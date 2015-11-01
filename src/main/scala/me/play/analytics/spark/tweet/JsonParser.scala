package me.play.analytics.spark.tweet

import scala.util.parsing.json.JSON

/**
 * @author mangeeteden
 */

class CC[T] { def unapply(a: Any): Option[T] = Some(a.asInstanceOf[T]) }

object M extends CC[Map[String, Any]]
object L extends CC[List[Any]]
object S extends CC[String]
object D extends CC[Double]
object B extends CC[Boolean]
object I extends CC[Int]

object JsonParser {
  
  def parseTweetJson(tweet: String): List[(String, String)] = {
    for {
      Some(M(map)) <- List(JSON.parseFull(tweet))
      
      if map.contains("user")
      M(user) = map("user")
      
      if user.contains("location")
      S(location) = user("location")
    } yield {
      (location, tweet)
    } 
  }
  
  def parseTweetJsonToGetRetweetedCountAndUserDetail(tweet: String): List[(Int, Map[String, Any])] = {
    for {
      Some(M(map)) <- List(JSON.parseFull(tweet))
      
      if map.contains("user")
      M(user) = map("user")
      
      D(retweet_count) = map("retweet_count")
    } yield {
      (retweet_count.toInt, user)
    } 
  }
}

