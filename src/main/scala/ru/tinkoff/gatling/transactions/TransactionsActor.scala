package ru.tinkoff.gatling.transactions

import akka.actor.{Actor, ActorSystem, Props, Scheduler, Terminated}
import com.typesafe.scalalogging.LazyLogging
import io.gatling.commons.stats.{KO, OK}
import io.gatling.core.action.Action
import io.gatling.core.session.Session
import io.gatling.core.stats.StatsEngine

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.Duration

object TransactionsActor {
  def props(statsEngine: StatsEngine): Props =
    Props(new TransactionsActor(statsEngine))

  case class TransactionStarted(name: String, timestamp: Long)
  case class TransactionEnded(name: String, timestamp: Long, session: Session, next: Action)
}

class TransactionsActor(statsEngine: StatsEngine) extends Actor with LazyLogging {

  implicit def system: ActorSystem          = context.system
  def scheduler: Scheduler                  = system.scheduler
  implicit def dispatcher: ExecutionContext = system.dispatcher

  override def preStart(): Unit = context.setReceiveTimeout(Duration.Undefined)

  override def preRestart(reason: Throwable, message: Option[Any]): Unit =
    logger.error(s"Actor $this crashed on message $message", reason)

  override def unhandled(message: Any): Unit =
    message match {
      case _: Terminated => super.unhandled(message)
      case unknown       => throw new IllegalArgumentException(s"Actor $this doesn't support message $unknown")
    }

  private def crash(prefix: String, errorMsg: String, session: Session, next: Action): Unit = {
    statsEngine.logCrash(session.scenario, session.groups, prefix, errorMsg)
    next ! session.markAsFailed
  }

  private def executeNext(name: String, startTimestamp: Long, stopTimestamp: Long, session: Session, next: Action): Unit = {
    statsEngine.logResponse(
      session.scenario,
      session.groups,
      name,
      startTimestamp,
      stopTimestamp,
      if (session.isFailed) KO else OK,
      None,
      if (session.isFailed) Some(s"transaction '$name' failed") else None,
    )
    next ! session.logGroupRequestTimings(startTimestamp, stopTimestamp)
  }

  def onTransaction(transactionsStack: List[TransactionsActor.TransactionStarted]): Receive = {
    case t: TransactionsActor.TransactionStarted                            =>
      context.become(onTransaction(t :: transactionsStack))
    case TransactionsActor.TransactionEnded(name, timestamp, session, next) =>
      transactionsStack match {
        case Nil =>
          crash(s"Transaction '$name' close error", s"transaction '$name' wasn't started", session, next)

        case started :: newStack =>
          if (started.timestamp > timestamp) {
            crash(s"Transaction '$name' illegal state", s"transaction not be able end before they started", session, next)
          } else if (started.name == name) {
            executeNext(name, started.timestamp, timestamp, session, next)
          } else {
            crash(s"Transaction '$name' close error", s"has unclosed transaction ${started.name}", session, next)
          }
          context.become(onTransaction(newStack))
      }
  }

  override def receive: Receive = onTransaction(List.empty)
}
