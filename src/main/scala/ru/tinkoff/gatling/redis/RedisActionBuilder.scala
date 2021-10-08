package ru.tinkoff.gatling.redis

import com.redis.RedisClientPool
import io.gatling.core.action.Action
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.session.Expression
import io.gatling.core.structure.ScenarioContext

object RedisActionBuilder {

  implicit class RedisClientPoolOps(clientPool: RedisClientPool) {

    def DEL(key: Expression[Any], keys: Expression[Any]*): RedisDelActionBuilder =
      RedisDelActionBuilder(clientPool, key, keys)

    def SREM(key: Expression[Any], value: Expression[Any], values: Expression[Any]*): RedisSremActionBuilder =
      RedisSremActionBuilder(clientPool, key, value, values)

    def SADD(key: Expression[Any], value: Expression[Any], values: Expression[Any]*): RedisSaddActionBuilder =
      RedisSaddActionBuilder(clientPool, key, value, values)

  }

  case class RedisDelActionBuilder(clientPool: RedisClientPool,
                                   key: Expression[Any],
                                   keys: Seq[Expression[Any]])
      extends ActionBuilder {
    override def build(ctx: ScenarioContext, next: Action): Action = RedisDelAction(ctx, next, clientPool, key, keys)
  }

  case class RedisSremActionBuilder(clientPool: RedisClientPool,
                                    key: Expression[Any],
                                    value: Expression[Any],
                                    values: Seq[Expression[Any]])
      extends ActionBuilder {
    override def build(ctx: ScenarioContext, next: Action): Action = RedisSremAction(ctx, next, clientPool, key, value, values)
  }

  case class RedisSaddActionBuilder(clientPool: RedisClientPool,
                                    key: Expression[Any],
                                    value: Expression[Any],
                                    values: Seq[Expression[Any]])
      extends ActionBuilder {
    override def build(ctx: ScenarioContext, next: Action): Action = RedisSaddAction(ctx, next, clientPool, key, value, values)
  }

}
