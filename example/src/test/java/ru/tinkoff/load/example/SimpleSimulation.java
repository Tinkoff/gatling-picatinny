package ru.tinkoff.load.example;

import io.gatling.javaapi.redis.RedisClientPool;
import ru.tinkoff.gatling.javaapi.influxdb.SimulationWithAnnotations;
import ru.tinkoff.gatling.javaapi.redis.RedisClientPoolJava;

import java.util.List;

import static io.gatling.javaapi.core.CoreDsl.*;
import static ru.tinkoff.gatling.javaapi.influxdb.Annotations.*;

/** class SimulationWithAnnotations allows you to write Start annotation to influxdb before starting the simulation and Stop annotation after
 * completion of the simulation
 */

public class SimpleSimulation extends SimulationWithAnnotations {
    public Void function(){
        System.out.println("Some action");
        return null;
    }

    {
        /** how to add custom actions before start of simulation
         * no need to override "before" function
         */
        before(() -> {
            System.out.println("Some action");
            return 0;
        });
        before(this::function);

        // Create redis client
        RedisClientPool redisClientPool =
                new RedisClientPool("localhost", 6379)
                        .withDatabase(1)
                        .withBatchMode(true);

        // Create redisClientPoolJava
        RedisClientPoolJava redisClientPoolJava = new RedisClientPoolJava(redisClientPool);

        setUp(
                /** how to add custom points to influxdb during scenario execution
                 */
                scenario("Java Influx")
                        .exec(userDataPoint(Point("gatling").addField("status", "check")))
                        .exec(userDataPoint(
                                "status",
                                "check",
                                "testField",
                                "fieldValue")
                        )
                        .exec(redisClientPoolJava.SADD("key", "values", "values", "values1")) //add the specified members to the set stored at key
                        .exec(redisClientPoolJava.SADD("key", "values", List.of("values", "values1"))) //add the specified members to the set stored at key
                        .exec(redisClientPoolJava.DEL("key", "keys")) //removes the specified keys
                        .exec(redisClientPoolJava.SREM("key", "values", "values")) //remove the specified members from the set stored at key
                        .injectOpen(atOnceUsers(1))
                        /** how to add custom points to influxdb after scenario execution
                         */
                        .andThen(
                                userDataPoint("myUniqueScenario", Point("gatling", 1L))
                        )
                        .andThen(
                                userDataPoint(
                                        "myUniqueScenario2",
                                        "status",
                                        "check2",
                                        "testField",
                                        "fieldValue2"
                                )
                        )
        ).protocols();

        /** how to add custom actions after end of simulation
         * No need to override "after" function
         */
        after(() -> {
            System.out.println("Some action");
            return 0;
        });
        after(this::function);
    }
}
