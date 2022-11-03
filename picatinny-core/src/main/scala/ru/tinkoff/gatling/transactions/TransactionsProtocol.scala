package ru.tinkoff.gatling.transactions

import io.gatling.core.CoreComponents
import io.gatling.core.config.GatlingConfiguration
import io.gatling.core.protocol.{Protocol, ProtocolKey}

object TransactionsProtocol {
  val key: ProtocolKey[TransactionsProtocol, TransactionsComponents] =
    new ProtocolKey[TransactionsProtocol, TransactionsComponents] {
      override def protocolClass: Class[Protocol] = classOf[TransactionsProtocol].asInstanceOf[Class[Protocol]]

      override def defaultProtocolValue(configuration: GatlingConfiguration): TransactionsProtocol = new TransactionsProtocol

      override def newComponents(coreComponents: CoreComponents): TransactionsProtocol => TransactionsComponents =
        _ => {
          val transactionsActor = coreComponents.actorSystem.actorOf(TransactionsActor.props(coreComponents.statsEngine))
          new TransactionsComponents(new TransactionTracker(transactionsActor))
        }
    }
}

final class TransactionsProtocol extends Protocol {
  type Components = TransactionsComponents
}
