package ru.tinkoff.gatling.transactions

import io.gatling.core.Predef._
import io.gatling.core.session.Expression
import io.gatling.core.stats.StatsEngine
import io.gatling.core.structure.{ScenarioBuilder, ScenarioContext}
import org.scalatest.OptionValues._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import ru.tinkoff.gatling.transactions.Predef._
import ru.tinkoff.gatling.transactions.actions.builders._

import java.util.concurrent.ConcurrentLinkedQueue

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

class TransactionsSpec extends AnyFlatSpec with Matchers with Mocks {
  import TransactionsSpec._

  val tName: Symbol          = Symbol("tName")
  val tn: Expression[String] = "t1"

  "Scenario with startTransaction" should "contain start transaction action builder with specified name" in {
    transactionScenario.actionBuilders should contain(startBuilder)
  }

  "Scenario with endTransaction" should "contain end transaction action builder with specified name" in {
    transactionScenario.actionBuilders should contain(endBuilder)
  }

  private val session                                                       = fixtures.emptySession(transactionScenario.name)
  private def runScenario(s: ScenarioBuilder, testContext: ScenarioContext) = {
    val actions = s.actionBuilders.foldLeft(fixtures.noAction)((next, builder) => builder.build(testContext, next))
    actions ! session
    Thread.sleep(200)
    actions
  }

  private val name     = Symbol("name")
  private val status   = Symbol("status")
  private val errorMsg = Symbol("errorMsg")

  "Scenario with transactions after run" should "write request with correct start/stop timestamps and name" in new MockedGatlingCtx {
    (statsEngine.logResponse _)
      .when(*, *, *, *, *, *, *, *)
      .onCall { (_, _, c, d, e, f, _, h) => events.add(Evt("REQUEST", c, d, e, f.name, h)) }
      .once()

    runScenario(transactionScenarioWithDefaultEndTime, testContext)

    val requestRecord: Option[Evt] = getEvents.find(_.evtType == "REQUEST")

    requestRecord shouldBe defined
    requestRecord.value should have(name("t1"), status("OK"), errorMsg(None))
    assert(requestRecord.value.startTimestamp <= requestRecord.value.endTimestamp)
  }

  "Scenario with not opened transactions after run" should "fail with transaction close error" in new MockedGatlingCtx {
    (statsEngine.logCrash _)
      .when(*, *, *, *)
      .onCall { (_, _, c, d) =>
        events.add(Evt("ERROR", c, System.currentTimeMillis(), System.currentTimeMillis(), "KO", Some(d)))
      }
      .once()

    runScenario(notOpenedTransactionScenario, testContext)

    val errorRecord: Option[Evt]   = getEvents.find(_.evtType == "ERROR")
    val requestRecord: Option[Evt] = getEvents.find(_.evtType == "REQUEST")

    requestRecord should not be defined
    errorRecord shouldBe defined
    errorRecord.value should have(
      name("Transaction 't1' close error"),
      status("KO"),
    )
    errorRecord.get.errorMsg.value shouldBe "transaction 't1' wasn't started"

  }

  "Scenario with a transaction that ended before it started after run" should "fail with illegal state error" in new MockedGatlingCtx {
    (statsEngine.logCrash _)
      .when(*, *, *, *)
      .onCall { (_, _, c, d) =>
        events.add(Evt("ERROR", c, System.currentTimeMillis(), System.currentTimeMillis(), "KO", Some(d)))
      }
      .once()

    runScenario(incorrectEndTimeTransactionScenario, testContext)

    val errorRecord: Option[Evt]   = getEvents.find(_.evtType == "ERROR")
    val requestRecord: Option[Evt] = getEvents.find(_.evtType == "REQUEST")

    requestRecord should not be defined
    errorRecord shouldBe defined
    errorRecord.value should have(
      name("Transaction 't1' illegal state"),
      status("KO"),
    )
    errorRecord.get.errorMsg.value shouldBe "transaction not be able end before they started"

  }

  "Scenario with incorrect transaction sequence after run" should "fail with transaction close error" in new MockedGatlingCtx {
    (statsEngine.logCrash _)
      .when(*, *, *, *)
      .onCall { (_, _, c, d) =>
        events.add(Evt("ERROR", c, System.currentTimeMillis(), System.currentTimeMillis(), "KO", Some(d)))
      }
      .once()
    runScenario(incorrectTransactionSequenceScenario, testContext)

    val errorRecord: Option[Evt]   = getEvents.find(_.evtType == "ERROR")
    val requestRecord: Option[Evt] = getEvents.find(_.evtType == "REQUEST")

    requestRecord should not be defined
    errorRecord shouldBe defined
    errorRecord.value should have(
      name("Transaction 't1' close error"),
      status("KO"),
    )
    errorRecord.get.errorMsg.value shouldBe "has unclosed transaction t2"
  }

}
