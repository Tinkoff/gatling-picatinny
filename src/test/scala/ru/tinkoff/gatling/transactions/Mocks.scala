package ru.tinkoff.gatling.transactions
import akka.actor.{ActorRef, ActorSystem}
import io.gatling.commons.util.DefaultClock
import io.gatling.core.CoreComponents
import io.gatling.core.pause.Disabled
import io.gatling.core.protocol.{ProtocolComponentsRegistry, ProtocolKey}
import io.gatling.core.structure.ScenarioContext
import io.gatling.transactions.fixtures
import org.scalamock.scalatest.MockFactory
import org.scalatest.BeforeAndAfterAll

import scala.collection.mutable
import scala.concurrent.Await
import scala.concurrent.duration._

trait Mocks extends MockFactory with BeforeAndAfterAll {

  private val testActorSystem: ActorSystem = ActorSystem("TestSystem")
  val testTransactionsActor: ActorRef      = testActorSystem.actorOf(TransactionsActor.props(fixtures.statsEngine))

  private val protoComponents = new TransactionsComponents(new TransactionTracker(testTransactionsActor))

  private val testCoreComponents = new CoreComponents(
    testActorSystem,
    fixtures.fakeEventLoop,
    null,
    None,
    fixtures.statsEngine,
    new DefaultClock,
    fixtures.noAction,
    null,
  )

  val ProtocolComponentsRegistryMock: ProtocolComponentsRegistry =
    new ProtocolComponentsRegistry(testCoreComponents, Map.empty, mutable.Map.empty) {
      override def components[P, C](key: ProtocolKey[P, C]): C = protoComponents.asInstanceOf[C]
    }

  val testContext = new ScenarioContext(
    testCoreComponents,
    ProtocolComponentsRegistryMock,
    Disabled,
    false,
  )

  override protected def afterAll(): Unit = {
    fixtures.fakeEventLoop.shutdownGracefully()
    Await.ready(testActorSystem.terminate(), 10.seconds)
    super.afterAll()
  }
}
