package ru.tinkoff.gatling.transactions

import akka.actor.ActorRef
import io.gatling.commons.stats.Status
import io.gatling.core.action.{Action, ChainableAction}
import io.gatling.core.session.{GroupBlock, Session}
import io.gatling.core.stats.StatsEngine
import io.gatling.core.stats.writer.UserEndMessage

import java.util.concurrent.ConcurrentLinkedQueue
import scala.jdk.CollectionConverters._
import scala.collection.mutable

object fixtures {
  val noAction: Action = new ChainableAction {
    override def next: Action = noAction

    override def name: String = "noop"

    override protected def execute(session: Session): Unit = ()
  }

  val fakeEventLoop = new FakeEventLoop

  case class Evt(evtType: String,
                 name: String,
                 startTimestamp: Long,
                 endTimestamp: Long,
                 status: String,
                 errorMsg: Option[String])
  final class InMemoryStatsEngine extends StatsEngine {

    private val events: ConcurrentLinkedQueue[Evt] = new ConcurrentLinkedQueue()

    override def start(): Unit = ()

    override def stop(controller: ActorRef, exception: Option[Exception]): Unit = events.clear()

    override def logUserStart(scenario: String, timestamp: Long): Unit = ()

    override def logUserEnd(userMessage: UserEndMessage): Unit = ()

    override def logResponse(scenario: String,
                             groups: List[String],
                             requestName: String,
                             startTimestamp: Long,
                             endTimestamp: Long,
                             status: Status,
                             responseCode: Option[String],
                             message: Option[String]): Unit = {
      events.add(Evt("REQUEST", requestName, startTimestamp, endTimestamp, status.name, message))
    }

    override def logGroupEnd(scenario: String, groupBlock: GroupBlock, exitTimestamp: Long): Unit = ()

    override def logCrash(scenario: String, groups: List[String], requestName: String, error: String): Unit =
      events.add(Evt("ERROR", requestName, System.currentTimeMillis(), System.currentTimeMillis(), "KO", Some(error)))

    def getEvents: List[Evt] = this.events.asScala.toList
  }

  val statsEngine = new InMemoryStatsEngine

  def emptySession(scenario: String): Session = Session(scenario, 1, fakeEventLoop)
}
