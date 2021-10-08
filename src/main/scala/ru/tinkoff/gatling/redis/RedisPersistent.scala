package ru.tinkoff.gatling.redis

import com.redis.RedisClient

private[redis] case class RedisPersistent(host: String, port: Int) {

  private[redis] def init: RedisClient = new RedisClient(host, port)
  private[redis] def close(connect: RedisClient): Unit = connect.close()

  def deleteKey(redis: RedisClient, key: String, keys: Seq[Any]): Option[Long]  = redis.del(key, keys: _*)
  def deleteKeyMember(redis: RedisClient, key: String, value: Any, values: Seq[Any]): Option[Long]  = redis.srem(key, value, values: _*)
  def addKeyMember(redis: RedisClient, key: String, value: Any, values: Seq[Any]): Option[Long]  = redis.sadd(key, value, values: _*)

}
