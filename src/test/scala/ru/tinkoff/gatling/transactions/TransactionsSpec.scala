package ru.tinkoff.gatling.transactions

import io.gatling.core.Predef._
import io.gatling.core.session.Expression
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.transactions.fixtures
import io.gatling.transactions.fixtures.Evt
import org.scalatest.BeforeAndAfter
import org.scalatest.OptionValues._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import ru.tinkoff.gatling.transactions.Predef._
import ru.tinkoff.gatling.transactions.actions.builders._

object TransactionsSpec {
  private val now                          = System.currentTimeMillis()
  val transactionScenario: ScenarioBuilder =
    scenario("Test transaction scenario")
      .startTransaction("t1")
      .exec(s => s)
      .endTransaction("t1", now + 500)

  val startBuilder: StartTransactionActionBuilder = StartTransactionActionBuilder("t1")
  val endBuilder: EndTransactionActionBuilder     = EndTransactionActionBuilder("t1", now + 500)

  val transactionScenarioWithDefaultEndTime: ScenarioBuilder =
    scenario("Test transaction scenario").startTransaction("t1").exec(s => s).endTransaction("t1")

  val notOpenedTransactionScenario: ScenarioBuilder =
    scenario("Close not opened transaction").exec(s => s).endTransaction("t1")

  val incorrectEndTimeTransactionScenario: ScenarioBuilder =
    scenario("Incorrect end time transaction")
      .startTransaction("t1")
      .exec(s => s)
      .endTransaction("t1", 1L)

  val incorrectTransactionSequenceScenario: ScenarioBuilder =
    scenario("Incorrect transaction b")
      .startTransaction("t2")
      .exec(s => s)
      .endTransaction("t1")
      .endTransaction("t2")
}

class TransactionsSpec extends AnyFlatSpec with Matchers with Mocks with BeforeAndAfter {
  import TransactionsSpec._

  val tName: Symbol          = Symbol("tName")
  val tn: Expression[String] = "t1"

  "Scenario with startTransaction" should "contain start transaction action builder with specified name" in {
    transactionScenario.actionBuilders should contain(startBuilder)
  }

  "Scenario with endTransaction" should "contain end transaction action builder with specified name" in {
    transactionScenario.actionBuilders should contain(endBuilder)
  }

  before(fixtures.statsEngine.stop(testContext.coreComponents.controller, None))

  private val session                         = fixtures.emptySession(transactionScenario.name)
  private def runScenario(s: ScenarioBuilder) = {
    val actions = s.actionBuilders.foldLeft(fixtures.noAction)((next, builder) => builder.build(testContext, next))
    actions ! session
    Thread.sleep(200)
    actions
  }

  private val name     = Symbol("name")
  private val status   = Symbol("status")
  private val errorMsg = Symbol("errorMsg")

  "Scenario with transactions after run" should "write request with correct start/stop timestamps and name" in {
    runScenario(transactionScenarioWithDefaultEndTime)

    val requestRecord: Option[Evt] = fixtures.statsEngine.getEvents.find(_.evtType == "REQUEST")

    requestRecord shouldBe defined
    requestRecord.value should have(name("t1"), status("OK"), errorMsg(None))
    assert(requestRecord.value.startTimestamp <= requestRecord.value.endTimestamp)
  }

  "Scenario with not opened transactions after run" should "fail with transaction close error" in {
    runScenario(notOpenedTransactionScenario)

    val errorRecord: Option[Evt]   = fixtures.statsEngine.getEvents.find(_.evtType == "ERROR")
    val requestRecord: Option[Evt] = fixtures.statsEngine.getEvents.find(_.evtType == "REQUEST")

    requestRecord should not be defined
    errorRecord shouldBe defined
    errorRecord.value should have(
      name("Transaction 't1' close error"),
      status("KO"),
    )
    errorRecord.get.errorMsg.value shouldBe "transaction 't1' wasn't started"

  }

  "Scenario with a transaction that ended before it started after run" should "fail with illegal state error" in {
    runScenario(incorrectEndTimeTransactionScenario)

    val errorRecord: Option[Evt]   = fixtures.statsEngine.getEvents.find(_.evtType == "ERROR")
    val requestRecord: Option[Evt] = fixtures.statsEngine.getEvents.find(_.evtType == "REQUEST")

    requestRecord should not be defined
    errorRecord shouldBe defined
    errorRecord.value should have(
      name("Transaction 't1' illegal state"),
      status("KO"),
    )
    errorRecord.get.errorMsg.value shouldBe "transaction not be able end before they started"

  }

  "Scenario with incorrect transaction sequence after run" should "fail with transaction close error" in {
    runScenario(incorrectTransactionSequenceScenario)

    val errorRecord: Option[Evt]   = fixtures.statsEngine.getEvents.find(_.evtType == "ERROR")
    val requestRecord: Option[Evt] = fixtures.statsEngine.getEvents.find(_.evtType == "REQUEST")

    requestRecord should not be defined
    errorRecord shouldBe defined
    errorRecord.value should have(
      name("Transaction 't1' close error"),
      status("KO"),
    )
    errorRecord.get.errorMsg.value shouldBe "has unclosed transaction t2"

  }

}
