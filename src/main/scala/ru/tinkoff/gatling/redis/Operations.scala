package ru.tinkoff.gatling.redis

import com.typesafe.scalalogging.StrictLogging
import io.gatling.commons.util.TypeCaster
import io.gatling.commons.validation._
import io.gatling.core.Predef._
import io.gatling.core.session.Expression
import io.gatling.core.session.el._
import io.gatling.core.structure.{ChainBuilder, ScenarioBuilder}

import scala.reflect.ClassTag

object Operations extends StrictLogging{

  def redisDeleteKey(key: String, keys: Seq[Any]): Unit = {
    OperationsManager.redisDeleteKey(key, keys)
  }

  def redisDeleteKeyMember(key: String, value: Any, values: Seq[Any]): Unit = {
    OperationsManager.redisDeleteKeyMember(key, value, values)
  }

  def redisAddKeyMember(key: String, value: Any, values: Seq[Any]): Unit = {
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

    def redisSADD(key: String, value: Expression[Any], values: Expression[Any]*): ScenarioBuilder =
      sb.exec(session => {
        redisAddKeyMember(key, value(session), newSeq(values, session, value))
        val vvv = value(session)
        session
      })

    def redisSADD3(key: String, value: Expression[String], values: String*): ScenarioBuilder =
      sb.exec(session =>
        for {
          resolvedValue <- value(session)
          _             <- redisAddKeyMember(key, resolvedValue, values).success
        } yield session)

    def redisSADD4(key: String, value: Expression[String], values: Expression[String]*): ScenarioBuilder =
      sb.exec(session =>
        for {
          resolvedValue <- value(session)
          resolvedValues <- values(session)
//          resolvedValuesEl <- resolvedValues.map(_(session))
//          _             <- redisAddKeyMember(key, resolvedValue, resolvedValuesEl).success
        } yield session)



    private def resolve[T: TypeCaster: ClassTag](e: Expression[T], session: Session): Validation[T] =
      e(session).flatMap {
        case s: String => s.el[T].apply(session)
        case o         => o.success
      }

    def redisSADD100[V: TypeCaster: ClassTag](key: String, value: Expression[V], values: Expression[V]*): ScenarioBuilder =
      sb.exec(s =>
        for {
          resolvedVal <- resolve(value, s)
          resolvedVals <- values
            .map(resolve(_, s))
            .foldLeft(Seq.empty[V].success)((r, v) => r.flatMap(seq => v.map(seq :+ _)))
          _ <- redisAddKeyMember(key, resolvedVal, resolvedVals).success
        } yield s)

    def redisSADD1000(key: String, value: Expression[Any], values: Expression[Any]*): ScenarioBuilder =
      sb.exec(s =>
        for {
          resolvedVal <- value(s)
          resolvedVals <- values
            .map(_(s))
            .foldLeft(Seq.empty[Any].success)((r, v) => r.flatMap(seq => v.map(seq :+ _)))
          _ <- redisAddKeyMember(key, resolvedVal, resolvedVals).success
        } yield s)
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

    def redisSADD(key: String, value: Expression[Any], values: Expression[String]*): ChainBuilder =
      cb.exec(session => {
        redisAddKeyMember(key, completeExpression(value(session)), newSeq(values, session, value))
        session
      })

//    def redisSADD2[T](key: String, value: Expression[T], values: Any*): ChainBuilder =
//      cb.exec(s => for{
//        resolvedValue <- value(s)
//        _ <- redisAddKeyMember(key, resolvedValue, values).success
//      } yield s)
  }
//  for(el <- values(session)) el(session)
  private def completeExpression(expression: Any): Any = {
    expression match {
      case Success(session) => session
    }
  }

  private def newSeq(values: Validation[Any], session: Session, value: Validation[Any]): Seq[Any] = {
    val newSeq = Seq[Any]()
    for(el <- values) newSeq :+ el(session)
    val v1 = value(session)
    val v2 = values(session)
    newSeq
    Console.println(newSeq)
    newSeq
  }

}
