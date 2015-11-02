package me.play.analytics.spark.datasource.cassandra

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import com.datastax.spark.connector._
import java.nio.ByteBuffer
import org.apache.spark.storage.StorageLevel
import me.play.analytics.spark.tweet.JsonParser.{ parseTweetJson, parseTweetJsonToGetRetweetedCountAndUserDetail }
import org.apache.spark.rdd.RDD
import org.apache.spark.rdd.MapPartitionsRDD
import com.sun.xml.internal.ws.wsdl.writer.document.Import

/**
 * @author mangeeteden
 */

// Constants
object Constant {

  val APP_NAME = "Spark-Tweets-Analytics"
  val LOCATION_NOT_AVIALABLE = "LocNA"
  
  val CASSANDRA_HOST_KEY = "spark.cassandra.connection.host"
  val CASSANDRA_HOST_VALUE = "127.0.0.1"
  val CASSANDRA_KEYSPACE_NAME  = "play"
  val CASSANDRA_TABLE_NAME  = "tweets"
  
  val USER_HOME = System.getProperty("user.home")
}

/**
 * 1. Connects to Cassandra Cluster.
 * 2. Gets the CassandraRowRDD
 * 3. Parse the Tweets JSON to prepare PairRDD -> RDD[(location, tweet)]
 * 4. Perform the Anaylysis listed in README.md
 */
object TweetAnalytics {

  def main(args: Array[String]): Unit = {
    
    // setting Spark Configurations
    val sc = initSparkContext()

    // Getting Data from Cassandra (Tweets) and converting tweets from Blob -> String
    val tweets = fetchDataFromCassandra(sc)

    // parsing each tweet to pairRDD of (location, tweet)
    val location_tweet_pairRDD = tweets.map { tweet =>
      val parsedResponse = parseTweetJson(tweet)
      parsedResponse match {
        case Nil => (Constant.LOCATION_NOT_AVIALABLE, tweet)
        case _ =>
          val parsedTuple = parsedResponse(0)
          parsedTuple match {
            case (location, tweet) if location == null || location.isEmpty() => (Constant.LOCATION_NOT_AVIALABLE, tweet)
            case _ => parsedTuple
          }
      }
    }

    // Performance tuning
    location_tweet_pairRDD.repartition(3)
    location_tweet_pairRDD.persist(StorageLevel.MEMORY_AND_DISK_SER)

    // count number of tweets of every location and save the results in Output file ('<userHome>/countTweetsLocationWise')
    val location_initone = location_tweet_pairRDD.map { case (location, tweet) => (location, 1) }
    val location_tweetcount = location_initone.reduceByKey((a, b) => a + b)
    location_tweetcount.cache()
    val sortedlocation_tweetcount = location_tweetcount.sortByKey(false)
    location_tweetcount.saveAsTextFile(Constant.USER_HOME+"/sortedlocation_tweetcount")

    // locations(Top 5) with max tweets
    val sortedtweetcount_location = location_tweetcount.map(item => item.swap).sortByKey(false)
    println("Top 5 location of max Tweets:")
    sortedtweetcount_location.top(5).map(item => item.swap).foreach(println)

    // User details with Max ReTweeted
    val retweet_count_user = location_tweet_pairRDD.map {
      case (location, user) =>
        val parsedResponse = parseTweetJsonToGetRetweetedCountAndUserDetail(user)
        parsedResponse match {
          case Nil => (0, user)
          case _   => parsedResponse(0)
        }
    }
    val retweet_count_user_filtered = retweet_count_user.filter { case (retweeted_count, user) => retweeted_count != 0 }
    val retweet_count_user_filtered_sorted = retweet_count_user_filtered.sortByKey(false, 2)
    println(retweet_count_user_filtered_sorted.first())
  }

  /**
   * Initialize SparkContext with custom configurations.
   */
  def initSparkContext(): SparkContext = {
    val conf = new SparkConf(true).setAppName(Constant.APP_NAME).set(Constant.CASSANDRA_HOST_KEY, Constant.CASSANDRA_HOST_VALUE)
    new SparkContext(conf)
  }

  /**
   * Fetching tweets from Cassandra DB in for the raw text(Json).
   */
  def fetchDataFromCassandra(sc: SparkContext) = {
    val data = sc.cassandraTable(Constant.CASSANDRA_KEYSPACE_NAME, Constant.CASSANDRA_TABLE_NAME)
    data.map(row => new String(row.get[ByteBuffer]("tweet").array()))
  }

}