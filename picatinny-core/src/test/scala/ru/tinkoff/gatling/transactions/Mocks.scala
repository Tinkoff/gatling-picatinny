package ru.tinkoff.gatling.transactions
import akka.actor.{ActorRef, ActorSystem}
import io.gatling.commons.util.DefaultClock
import io.gatling.core.CoreComponents
import io.gatling.core.pause.Disabled
import io.gatling.core.protocol.{ProtocolComponentsRegistry, ProtocolKey}
import io.gatling.core.stats.StatsEngine
import io.gatling.core.structure.ScenarioContext
import org.scalamock.scalatest.MockFactory
import org.scalatest.{BeforeAndAfterAll, stats}

import java.util.concurrent.ConcurrentLinkedQueue
import scala.collection.mutable
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import scala.jdk.CollectionConverters._

trait Mocks extends MockFactory with BeforeAndAfterAll {

  trait MockedGatlingCtx {
    private val testActorSystem: ActorSystem = ActorSystem("TestSystem")

    val events: ConcurrentLinkedQueue[Evt] = new ConcurrentLinkedQueue()

    def getEvents: List[Evt] = this.events.asScala.toList

    val statsEngine = stub[StatsEngine]

    val testTransactionsActor: ActorRef = testActorSystem.actorOf(TransactionsActor.props(statsEngine))

    private val protoComponents = new TransactionsComponents(new TransactionTracker(testTransactionsActor))

    private val testCoreComponents = new CoreComponents(
      testActorSystem,
      fixtures.fakeEventLoop,
      null,
      None,
      statsEngine,
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

    def stop = Await.result(testActorSystem.terminate(), 10.seconds)
  }

  override protected def afterAll(): Unit = {
    fixtures.fakeEventLoop.shutdownGracefully()
    super.afterAll()
  }
}
