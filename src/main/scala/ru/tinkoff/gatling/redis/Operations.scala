package ru.tinkoff.gatling.redis

import com.typesafe.scalalogging.StrictLogging
import io.gatling.commons.validation._
import io.gatling.core.session.Expression
import io.gatling.core.structure.{ChainBuilder, ScenarioBuilder}

object Operations extends StrictLogging{

  def redisDeleteKey(key: Any, keys: Seq[Any]): Unit = {
    OperationsManager.redisDeleteKey(key, keys)
  }

  def redisDeleteKeyMember(key: Any, value: Any, values: Seq[Any]): Unit = {
    OperationsManager.redisDeleteKeyMember(key, value, values)
  }

  def redisAddKeyMember(key: Any, value: Any, values: Seq[Any]): Unit = {
    OperationsManager.redisAddKeyMember(key, value, values)
  }

  //for usage in scenario
  implicit class ScenarioAppender(sb: ScenarioBuilder) {
    def redisDEL(key: Expression[Any], keys: Expression[Any]*): ScenarioBuilder =
      sb.exec(session =>
        for {
          resolvedVal <- key(session)
          resolvedVals <- keys
            .map(_(session))
            .foldLeft(Seq.empty[Any].success)((r, v) => r.flatMap(seq => v.map(seq :+ _)))
          _ <- redisDeleteKey(resolvedVal, resolvedVals).success
        } yield session)

    def redisSREM(key: Any, value: Expression[Any], values: Expression[Any]*): ScenarioBuilder =
      sb.exec(session =>
        for {
          resolvedVal <- value(session)
          resolvedVals <- values
            .map(_(session))
            .foldLeft(Seq.empty[Any].success)((r, v) => r.flatMap(seq => v.map(seq :+ _)))
          _ <- redisDeleteKeyMember(key, resolvedVal, resolvedVals).success
        } yield session)

    def redisSADD(key: Any, value: Expression[Any], values: Expression[Any]*): ScenarioBuilder =
      sb.exec(session =>
        for {
          resolvedVal <- value(session)
          resolvedVals <- values
            .map(_(session))
            .foldLeft(Seq.empty[Any].success)((r, v) => r.flatMap(seq => v.map(seq :+ _)))
          _ <- redisAddKeyMember(key, resolvedVal, resolvedVals).success
        } yield session)
  }

  //for usage in chain builder
  implicit class ChainAppender(cb: ChainBuilder) {
    def redisDEL(key: Expression[Any], keys: Expression[Any]*): ChainBuilder =
      cb.exec(session =>
        for {
          resolvedVal <- key(session)
          resolvedVals <- keys
            .map(_(session))
            .foldLeft(Seq.empty[Any].success)((r, v) => r.flatMap(seq => v.map(seq :+ _)))
          _ <- redisDeleteKey(resolvedVal, resolvedVals).success
        } yield session)

    def redisSREM(key: Any, value: Expression[Any], values: Expression[Any]*): ChainBuilder =
      cb.exec(session =>
        for {
          resolvedVal <- value(session)
          resolvedVals <- values
            .map(_(session))
            .foldLeft(Seq.empty[Any].success)((r, v) => r.flatMap(seq => v.map(seq :+ _)))
          _ <- redisDeleteKeyMember(key, resolvedVal, resolvedVals).success
        } yield session)

    def redisSADD(key: Any, value: Expression[Any], values: Expression[Any]*): ChainBuilder =
      cb.exec(session =>
        for {
          resolvedVal <- value(session)
          resolvedVals <- values
            .map(_(session))
            .foldLeft(Seq.empty[Any].success)((r, v) => r.flatMap(seq => v.map(seq :+ _)))
          _ <- redisAddKeyMember(key, resolvedVal, resolvedVals).success
        } yield session)
  }
}
