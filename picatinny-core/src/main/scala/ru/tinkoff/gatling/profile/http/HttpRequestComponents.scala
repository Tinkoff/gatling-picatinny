package ru.tinkoff.gatling.profile.http

sealed trait HttpMethodConfig
case object GET    extends HttpMethodConfig
case object POST   extends HttpMethodConfig
case object PUT    extends HttpMethodConfig
case object DELETE extends HttpMethodConfig
