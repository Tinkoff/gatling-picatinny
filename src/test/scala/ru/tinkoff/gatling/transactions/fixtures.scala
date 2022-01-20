package ru.tinkoff.gatling.transactions

import io.gatling.core.action.{Action, ChainableAction}
import io.gatling.core.session.Session

object fixtures {
  val noAction: Action = new ChainableAction {
    override def next: Action = noAction

    override def name: String = "noop"

    override protected def execute(session: Session): Unit = ()
  }

  val fakeEventLoop = new FakeEventLoop

  def emptySession(scenario: String): Session = Session(scenario, 1, fakeEventLoop)
}
