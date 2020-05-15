package ru.tinkoff.gatling.influxdb

sealed trait Status

case object Start extends Status

case object Stop extends Status