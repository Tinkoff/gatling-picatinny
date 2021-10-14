package ru.tinkoff.gatling.redis

import com.typesafe.scalalogging.StrictLogging

object OperationsManager extends StrictLogging{

  def redisDeleteKey(key: String, keys: Seq[Any]): Unit = {
    completeOperation(redis.deleteKey(key, keys))
  }

  def redisDeleteKeyMember(key: String, value: Any, values: Seq[Any]): Unit = {
    completeOperation(redis.deleteKeyMember(key, value, values))
  }

  def redisAddKeyMember(key: String, value: Any, values: Seq[Any]): Unit = {
    completeOperation(redis.addKeyMember(key, value, values))
  }

  private def completeOperation(res: Option[Long]): Unit = {
    res match {
      case Some(i) =>
        logger.info(s"Operation complete: $i")
      case None =>
        logger.error(s"Operation failed")
    }
  }

}
