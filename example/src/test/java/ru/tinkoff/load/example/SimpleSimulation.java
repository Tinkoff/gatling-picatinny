package ru.tinkoff.load.example;

import ru.tinkoff.gatling.javaapi.influxdb.SimulationWithAnnotations;

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
