package ru.tinkoff.gatling.redis

import com.redis.RedisClientPool

private[redis] case class RedisPersistent(init: RedisClientPool) {

  def deleteKey(key: Any, keys: Seq[Any]): Option[Long] = {
    init.withClient {
      client => {
        client.del(key, keys: _*)
      }
    }
  }

  def deleteKeyMember(key: Any, value: Any, values: Seq[Any]): Option[Long] = {
    init.withClient {
      client => {
        client.srem(key, value, values: _*)
      }
    }
  }

  def addKeyMember(key: Any, value: Any, values: Seq[Any]): Option[Long] = {
    init.withClient {
      client => {
        client.sadd(key, value, values: _*)
      }
    }
  }

}
