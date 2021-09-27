package ru.tinkoff.gatling.transactions.actions

import io.gatling.core.action.{Action, ChainableAction}
import io.gatling.core.controller.throttle.Throttler
import io.gatling.core.session.{Expression, Session}
import io.gatling.core.structure.ScenarioContext
import io.gatling.core.util.NameGen
import ru.tinkoff.gatling.transactions.TransactionsProtocol

class EndTransactionAction(transactionName: Expression[String],
                           stopTime: Expression[Long],
                           ctx: ScenarioContext,
                           val next: Action)
    extends ChainableAction with NameGen {

  override def name: String                = genName("endTransactionAction")
  private val components                   = ctx.protocolComponentsRegistry.components(TransactionsProtocol.key)
  private val throttler: Option[Throttler] = ctx.coreComponents.throttler

  override protected def execute(session: Session): Unit =
    for {
      resolvedName  <- transactionName(session)
      stopTimestamp <- stopTime(session)
    } yield
      throttler.fold(components.transactionTracker.endTransaction(resolvedName, stopTimestamp, session, next))(
        _.throttle(session.scenario, () => {
          components.transactionTracker.endTransaction(resolvedName, stopTimestamp, session, next)
        }))
}
