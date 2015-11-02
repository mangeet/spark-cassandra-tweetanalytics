# spark-cassandra-tweetanalytics

This is continuation project of #spray-akka-tweetstreaming. Tweets that are saved in Cassandra DB using Akka + Spray will be analyzed using Spark's Cassandra RDD.

We will calculate/analyze the following:

3.1 How many Tweets from each Distinct location.
3.2 Location with Max Tweets. ()
3.3 Tweet which is most ReTweeted and User details of that Tweet.

** Please note: This is an not an example of Data analytics of real-time streaming.
I will be soon publishing an example of Data Analysis of Real-Time Streaming.

Steps to Get-Started #

1. If you dont have data(tweets) available on your Casssadra DB instance, do follwoig steps first:
   
   1.1 Checkout #spray-akka-tweetstreaming(https://github.com/mangeet/spray-akka-tweetstreaming.git)
   1.2 run script ./bin/start-streaming.sh "<Search Term>" (This script will start streaming tweets of your favorite search term)
       Follow README.md of thsi project for set-up instructions.
   1.3 You can terminate the programme at any time you feel, data is enough accumulated to play around.
   1.4 Just in a short span of 5-10 minutes, almost ~100K tweets will available in DB to analyze.
       We can check count after each minute, and you can terminate the programme whenever you feel, data is enough
       to play around.

2.  Checkout project from GIHUB "https://github.com/mangeet/spark-cassandra-analytics.git"

3.  mvn package

4. ./bin/spark-submit --executor-memory=2g  --class me.play.analytics.spark.datasource.cassandra.TweetAnalytics <WORKSPACE>/spark-cassandra-analytics/target/spark-cassandra-analytics-0.0.1-SNAPSHOT.jar