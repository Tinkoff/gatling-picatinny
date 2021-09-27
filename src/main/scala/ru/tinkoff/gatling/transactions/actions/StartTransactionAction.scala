package ru.tinkoff.gatling.transactions.actions

import io.gatling.commons.validation._
import io.gatling.core.action.{Action, ChainableAction}
import io.gatling.core.controller.throttle.Throttler
import io.gatling.core.session.{Expression, Session}
import io.gatling.core.structure.ScenarioContext
import io.gatling.core.util.NameGen
import ru.tinkoff.gatling.transactions.TransactionsProtocol

class StartTransactionAction(transactionName: Expression[String], ctx: ScenarioContext, val next: Action)
    extends ChainableAction with NameGen {

  override def name: String                = genName("startTransactionAction")
  private val components                   = ctx.protocolComponentsRegistry.components(TransactionsProtocol.key)
  private val throttler: Option[Throttler] = ctx.coreComponents.throttler

  private def startAndNext(tName: String, startTimestamp: Long, session: Session): Unit = {
    components.transactionTracker.startTransaction(tName, startTimestamp)
    next ! session
  }

  override protected def execute(session: Session): Unit =
    for {
      resolvedName   <- transactionName(session)
      startTimestamp <- ctx.coreComponents.clock.nowMillis.success
    } yield
      throttler.fold(startAndNext(resolvedName, startTimestamp, session))(_.throttle(session.scenario, () => {
        startAndNext(resolvedName, startTimestamp, session)
      }))
}
