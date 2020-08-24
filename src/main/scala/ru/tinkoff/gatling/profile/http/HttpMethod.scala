package ru.tinkoff.gatling.profile.http

sealed trait HttpMethod
case object GET extends HttpMethod
case object POST extends HttpMethod
case object PUT extends HttpMethod
case object DELETE extends HttpMethod