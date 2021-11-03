package ru.tinkoff.gatling.transactions

import akka.actor.Props
import io.gatling.commons.stats.{KO, OK}
import io.gatling.core.action.Action
import io.gatling.core.akka.BaseActor
import io.gatling.core.session.Session
import io.gatling.core.stats.StatsEngine

object TransactionsActor {
  def props(statsEngine: StatsEngine): Props =
    Props(new TransactionsActor(statsEngine))

  case class TransactionStarted(name: String, timestamp: Long)
  case class TransactionEnded(name: String, timestamp: Long, session: Session, next: Action)
}

class TransactionsActor(statsEngine: StatsEngine) extends BaseActor {

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
