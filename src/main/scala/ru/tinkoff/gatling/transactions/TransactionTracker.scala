package ru.tinkoff.gatling.transactions

import akka.actor.ActorRef
import io.gatling.core.action.Action
import io.gatling.core.session.Session

class TransactionTracker(transactionActor: ActorRef) {
  def startTransaction(name: String, timestamp: Long): Unit =
    transactionActor ! TransactionsActor.TransactionStarted(name, timestamp)
  def endTransaction(name: String, timestamp: Long, session: Session, next: Action): Unit =
    transactionActor ! TransactionsActor.TransactionEnded(name, timestamp, session, next)
}
