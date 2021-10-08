package ru.tinkoff.gatling

import ru.tinkoff.gatling.config.ConfigManager.redisConfig

package object redis {

  //redis config from redis.conf
  private[redis] lazy val redisHost: String   = redisConfig.getString("redis.host")
  private[redis] lazy val redisPort: Int   = redisConfig.getInt("redis.port")

  //redis config from simulation.conf
  // private[redis] lazy val redisHost: String   = SimulationConfig.getStringParam("redisHost")
  // private[redis] lazy val redisPort: String   = SimulationConfig.getIntParam("redisPort")

  lazy val redis: RedisPersistent = RedisPersistent(redisHost, redisPort)

}
