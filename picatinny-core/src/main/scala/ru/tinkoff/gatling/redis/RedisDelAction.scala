package ru.tinkoff.gatling.redis

import com.redis.RedisClientPool
import io.gatling.commons.validation.{Failure, Success, SuccessWrapper}
import io.gatling.core.action.{Action, ChainableAction}
import io.gatling.core.session.{Expression, Session}
import io.gatling.core.structure.ScenarioContext
import io.gatling.core.util.NameGen

case class RedisDelAction(
    ctx: ScenarioContext,
    next: Action,
    clientPool: RedisClientPool,
    key: Expression[Any],
    keys: Seq[Expression[Any]],
) extends ChainableAction with NameGen {

  override val name: String = genName("redisDelAction")

  private def redisDeleteKey(key: Any, keys: Seq[Any]): Option[Long] =
    clientPool.withClient { client =>
      {
        client.del(key, keys: _*)
      }
    }

  override def execute(session: Session): Unit =
    try {
      for {
        resolvedKey  <- key(session)
        resolvedKeys <- keys
                          .map(_(session) match {
                            case Success(v) => v
                            case Failure(e) => logger.debug(e)
                          })
                          .success
      } yield redisDeleteKey(resolvedKey, resolvedKeys)
      next ! session
    } catch {
      case e: Exception =>
        ctx.coreComponents.statsEngine.logCrash(
          session.scenario,
          session.groups,
          requestName = name,
          e.getMessage,
        )
        next ! session
    }

}
