package ru.tinkoff.gatling.transactions

import io.gatling.core.protocol.ProtocolComponents
import io.gatling.core.session.Session

class TransactionsComponents(val transactionTracker: TransactionTracker) extends ProtocolComponents {

  override def onStart: Session => Session = Session.Identity

  override def onExit: Session => Unit = ProtocolComponents.NoopOnExit
}
