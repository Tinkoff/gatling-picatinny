package ru.tinkoff.gatling.javaapi;

import ru.tinkoff.gatling.javaapi.influxdb.SimulationWithAnnotations;

import static io.gatling.javaapi.core.CoreDsl.*;
import static ru.tinkoff.gatling.javaapi.influxdb.Annotations.*;


public class JavaInfluxTest extends SimulationWithAnnotations {
    public Void function(){
        System.out.println("Some action");
        return null;
    }

    {
        before(() -> {
            System.out.println("Some action");
            return 0;
        });
        before(this::function);

        setUp(
                scenario("Java Influx")
                        .exec(userDataPoint(Point("gatling")))
                        .exec(userDataPoint(
                                "status",
                                "check",
                                "testField",
                                "fieldValue")
                        )
                        .injectOpen(atOnceUsers(1))
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

        after(() -> {
            System.out.println("Some action");
            return 0;
        });
        after(this::function);
    }
}
