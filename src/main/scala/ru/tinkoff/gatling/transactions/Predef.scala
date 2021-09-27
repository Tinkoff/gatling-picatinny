package ru.tinkoff.gatling.transactions

import io.gatling.commons.validation.SuccessWrapper
import io.gatling.core.Predef.Simulation
import io.gatling.core.protocol.Protocol
import io.gatling.core.session.Expression
import io.gatling.core.structure.{PopulationBuilder, ScenarioBuilder}
import ru.tinkoff.gatling.transactions.actions.builders._

object Predef {
  abstract class SimulationWithTransactions extends Simulation {
    override def setUp(populationBuilders: List[PopulationBuilder]): SetUp = {
      super.setUp(populationBuilders)
      new SetUpWithTransactions
    }

    private final class SetUpWithTransactions extends SetUp {
      override def protocols(ps: Iterable[Protocol]): SetUp = super.protocols(ps ++ Seq(new TransactionsProtocol))
    }
  }

  implicit class TransactionsOps(val scenarioBuilder: ScenarioBuilder) extends AnyVal {
    def startTransaction(tName: Expression[String]): ScenarioBuilder =
      scenarioBuilder.exec(StartTransactionActionBuilder(tName))

    def endTransaction(tName: Expression[String],
                       stopTime: Expression[Long] = _ => System.currentTimeMillis().success): ScenarioBuilder =
      scenarioBuilder.exec(EndTransactionActionBuilder(tName, stopTime))
  }
}
