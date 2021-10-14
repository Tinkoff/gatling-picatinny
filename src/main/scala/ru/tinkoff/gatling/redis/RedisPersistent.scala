package ru.tinkoff.gatling.redis

import com.redis.RedisClientPool

private[redis] case class RedisPersistent(init: RedisClientPool) {

  def deleteKey(key: String, keys: Seq[Any]): Option[Long] = {
    init.withClient {
      client => {
        client.del(key, keys: _*)
      }
    }
  }

  def deleteKeyMember(key: String, value: Any, values: Seq[Any]): Option[Long] = {
    init.withClient {
      client => {
        client.srem(key, value, values: _*)
      }
    }
  }

  def addKeyMember(key: String, value: Any, values: Seq[Any]): Option[Long] = {
    init.withClient {
      client => {
        client.sadd(key, value, values: _*)
      }
    }
  }

}
