package ru.tinkoff.gatling.influxdb

trait Status

case object Start extends Status

case object Stop extends Status
