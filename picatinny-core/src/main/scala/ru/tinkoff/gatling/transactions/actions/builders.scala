package ru.tinkoff.gatling.transactions.actions
import io.gatling.core.action.Action
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.session.Expression
import io.gatling.core.structure.ScenarioContext

object builders {
  final case class StartTransactionActionBuilder(tName: Expression[String]) extends ActionBuilder {
    override def build(ctx: ScenarioContext, next: Action): Action = new StartTransactionAction(tName, ctx, next)
  }

  final case class EndTransactionActionBuilder(tName: Expression[String], stopTime: Expression[Long]) extends ActionBuilder {
    override def build(ctx: ScenarioContext, next: Action): Action = new EndTransactionAction(tName, stopTime, ctx, next)
  }
}
