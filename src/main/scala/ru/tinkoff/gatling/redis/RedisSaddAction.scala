package ru.tinkoff.gatling.redis

import com.redis.RedisClientPool
import io.gatling.commons.validation.SuccessWrapper
import io.gatling.core.action.{Action, ChainableAction}
import io.gatling.core.session.{Expression, Session}
import io.gatling.core.structure.ScenarioContext
import io.gatling.core.util.NameGen

case class RedisSaddAction(ctx: ScenarioContext, next: Action, clientPool: RedisClientPool, key: Expression[Any], value: Expression[Any], values: Seq[Expression[Any]])
  extends ChainableAction with NameGen {

  override val name: String = genName("redisSaddAction")

  private def addKeyMember(key: Any, value: Any, values: Seq[Any]): Option[Long] = {
    clientPool.withClient {
      client => {
        client.sadd(key, value, values: _*)
      }
    }
  }

  override def execute(session: Session): Unit = {
    try {
      for {
        resolvedKey <- key(session)
        resolvedVal <- value(session)
        resolvedVals <- values
          .map(_(session))
          .foldLeft(Seq.empty[Any].success)((r, v) => r.flatMap(seq => v.map(seq :+ _)))
      } yield addKeyMember(resolvedKey, resolvedVal, resolvedVals).success
      next ! session
    } catch {
      case e: Exception =>
        ctx.coreComponents.statsEngine.logCrash(
          session.scenario,
          session.groups,
          requestName = name,
          e.getMessage
        )
        next ! session
    }
  }

}