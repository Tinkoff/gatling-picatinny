package ru.tinkoff.gatling.transactions

case class Evt(
    evtType: String,
    name: String,
    startTimestamp: Long,
    endTimestamp: Long,
    status: String,
    errorMsg: Option[String],
)
