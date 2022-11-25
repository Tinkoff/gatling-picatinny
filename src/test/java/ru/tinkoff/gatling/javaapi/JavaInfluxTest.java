package ru.tinkoff.gatling.javaapi;

import ru.tinkoff.gatling.javaapi.influxdb.SimulationWithAnnotations;

import static io.gatling.javaapi.core.CoreDsl.*;
import static ru.tinkoff.gatling.javaapi.influxdb.Annotations.*;
import io.razem.influxdbclient.Point;


public class JavaInfluxTest extends SimulationWithAnnotations {
    static Point makePoint(long value){
        return new Point("",1L, null, null)
                .addTag("","")
                .addField("value", value);
    }
    {
        setUp(
                scenario("Java Influx")
                        .exec(userDataPoint(makePoint(100L)))
                        .injectOpen(atOnceUsers(1))
                        .andThen(
                                userDataPoint("myUniqueScenario", makePoint(200L))
                        )
        ).protocols();
    }
}
