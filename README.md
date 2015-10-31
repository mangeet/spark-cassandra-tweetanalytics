# spark-cassandra-analytics

This is continuation project of #spray-akka-tweetstreaming. Tweets that are saved in Cassandra DB using Akka + Spray will be analyzed using Spark's Cassandra RDD.

We will calculate/analyze the following:

3.1 How many Tweets from each Distinct location.
3.2 Location with Max Tweets. ()
3.3 Tweet which is most ReTweeted and User details of that Tweet.

** Please note: This is an not an example of Data analytics of real-time streaming.
I will be soon publishing an example of Data Analysis of Real-Time Streaming.

Steps to Get-Started #

1. Run programme to #spray-akka-tweetstreaming(https://github.com/mangeet/spray-akka-tweetstreaming.git)
   me.play.streaming.tweet.App.scala 
   
   which will accumulate/save all data(tweets) in CassandraDB for query of your choice. I am taking query 
   "Happy Halloween"

2. Just in a short span of 5-10 minutes, almost ~100K tweets will available in DB to analyze.
   We can check count after each minute, and you can terminate the programme whenever you feel, data is enough
   to play around.

3.  Checkout project from GIHUB ""

4.  mvn package

5. ./bin/spark-submit --executor-memory=2g  --class me.play.analytics.spark.datasource.cassandra.App <WORKSPACE>/spark-cassandra-analytics/target/spark-cassandra-analytics-0.0.1-SNAPSHOT.jar