package ru.tinkoff.gatling.profile.http

import ru.tinkoff.gatling.profile.ProfileConfig

case class HttpProfileConfig(name: String, profile: List[HttpRequestConfig]) extends ProfileConfig
