package ru.tinkoff.gatling.redis

import com.redis.RedisClient
import com.typesafe.scalalogging.StrictLogging

object OperationsManager extends StrictLogging{

  def redisDeleteKey(key: String, keys: Seq[Any])= {
    val connection = redis.init
    completeDeleteKey(connection, redis.deleteKey(connection, key, keys))
  }

  def redisDeleteKeyMember(key: String, value: Any, values: Seq[Any])= {
    val connection = redis.init
    completeDeleteKeyMember(connection, redis.deleteKeyMember(connection, key, value, values))
  }

  def redisAddKeyMember(key: String, value: Any, values: Seq[Any]): Unit = {
    val connection = redis.init
    completeAdd(connection, redis.addKeyMember(connection, key, value, values))
  }

  private def completeDeleteKey(connection: RedisClient, res: Option[Long]): Unit = {
    res match {
      case Some(i) =>
        logger.info(s"Key has been deleted from redis: $i")
        redis.close(connection)
      case None =>
        logger.error(s"Failed to delete key from redis")
        redis.close(connection)
    }
  }

  private def completeDeleteKeyMember(connection: RedisClient, res: Option[Long]): Unit = {
    res match {
      case Some(i) =>
        logger.info(s"Member has been deleted from key redis: $i")
        redis.close(connection)
      case None =>
        logger.error(s"Failed to delete member from key redis")
        redis.close(connection)
    }
  }

  private def completeAdd(connection: RedisClient, res: Option[Long]): Unit = {
    res match {
      case Some(i) =>
        logger.info(s"Member has been added to key redis: $i")
        redis.close(connection)
      case None =>
        logger.error(s"Failed to add member to key redis")
        redis.close(connection)
    }
  }

}
