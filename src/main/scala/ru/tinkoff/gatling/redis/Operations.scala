package ru.tinkoff.gatling.redis

import io.gatling.core.Predef._
import io.gatling.core.structure.{ChainBuilder, ScenarioBuilder}

object Operations {

  def redisDeleteKey(key: String, keys: Seq[Any]) = {
    OperationsManager.redisDeleteKey(key, keys)
  }

  def redisDeleteKeyMember(key: String, value: Any, values: Seq[Any]) = {
    OperationsManager.redisDeleteKeyMember(key, value, values)
  }

  def redisAddKeyMember(key: String, value: Any, values: Seq[Any]) = {
    OperationsManager.redisAddKeyMember(key, value, values)
  }

  //for usage in scenario
  implicit class ScenarioAppender(sb: ScenarioBuilder) {
    def redisDEL(key: String, keys: Any*): ScenarioBuilder =
      sb.exec(session => {
        redisDeleteKey(key, keys)
        session
      })

    def redisSREM(key: String, value: Any, values: Any*): ScenarioBuilder =
      sb.exec(session => {
        redisDeleteKeyMember(key, value, values)
        session
      })

    def redisSADD(key: String, value: Any, values: Any*): ScenarioBuilder =
      sb.exec(session => {
        redisAddKeyMember(key, value, values)
        session
      })
  }

  //for usage in chain builder
  implicit class ChainAppender(cb: ChainBuilder) {
    def redisDEL(key: String, keys: Any*): ChainBuilder =
      cb.exec(session => {
        redisDeleteKey(key, keys)
        session
      })

    def redisSREM(key: String, value: Any, values: Any*): ChainBuilder =
      cb.exec(session => {
        redisDeleteKeyMember(key, value, values)
        session
      })

    def redisSADD(key: String, value: Any, values: Any*): ChainBuilder =
      cb.exec(session => {
        redisAddKeyMember(key, value, values)
        session
      })
  }

}
