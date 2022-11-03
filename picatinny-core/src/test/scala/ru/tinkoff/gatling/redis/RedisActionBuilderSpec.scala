package ru.tinkoff.gatling.redis

import com.redis.RedisClientPool
import io.gatling.core.Predef._
import io.gatling.core.session.Expression
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import ru.tinkoff.gatling.redis.RedisActionBuilder._

class RedisActionBuilderSpec extends AnyFlatSpec with Matchers {

  val redisPool                           = new RedisClientPool("localhost", 6379)
  val keyTest: Expression[String]         = "key"
  val keysTest: Seq[Expression[String]]   = Seq("keys")
  val valueTest: Expression[String]       = "value"
  val valuesTest: Seq[Expression[String]] = Seq("values")

  it should "return correct RedisDelActionBuilder" in {
    assert {
      exec(redisPool.DEL(keyTest, keysTest: _*)).actionBuilders.contains(RedisDelActionBuilder(redisPool, keyTest, keysTest))
    }
  }

  it should "return correct RedisSremActionBuilder" in {
    assert {
      exec(redisPool.SREM(keyTest, valueTest, valuesTest: _*)).actionBuilders
        .contains(RedisSremActionBuilder(redisPool, keyTest, valueTest, valuesTest))
    }
  }

  it should "return correct RedisSaddActionBuilder" in {
    assert {
      exec(redisPool.SADD(keyTest, valueTest, valuesTest: _*)).actionBuilders
        .contains(RedisSaddActionBuilder(redisPool, keyTest, valueTest, valuesTest))
    }
  }

}
