package ru.tinkoff.gatling

import com.redis.RedisClientPool
import ru.tinkoff.gatling.config.SimulationConfig

package object redis {

  //redis config from simulation.conf
  private[redis] lazy val redisHost: String   = SimulationConfig.getStringParam("redisHost")
  private[redis] lazy val redisPort: Int   = SimulationConfig.getIntParam("redisPort")
  private[redis] lazy val init: RedisClientPool = new RedisClientPool(redisHost, redisPort)

  lazy val redis: RedisPersistent = RedisPersistent(init)

}
