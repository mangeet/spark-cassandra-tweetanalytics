#bin/.sh

echo "Building project 'spark-cassandra-tweetanalytics'"
mvn package

echo "Start analyzing tweets.."
./$SPARK_HOME/bin/spark-submit --executor-memory=2g  --class me.play.analytics.spark.datasource.cassandra.TweetAnalytics target/spark-cassandra-analytics-0.0.1-SNAPSHOT.jar