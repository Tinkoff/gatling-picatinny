package ru.tinkoff.gatling.javaapi.influxdb;

import io.gatling.javaapi.core.*;
import io.razem.influxdbclient.Point;

import java.util.function.Function;


public class Annotations {

    public PopulationBuilder userDataPoint(String uniqScpName, Point point) {
        return new PopulationBuilder(ru.tinkoff.gatling.influxdb.Annotations.userDataPoint(uniqScpName, point));
    }

    public PopulationBuilder userDataPoint(String uniqScpName,
                                           String tagKey,
                                           String tagValue,
                                           String fieldKey,
                                           String fieldValue) {
        return new PopulationBuilder(
                ru.tinkoff.gatling.influxdb.Annotations.userDataPoint(
                        uniqScpName,
                        tagKey,
                        tagValue,
                        fieldKey,
                        fieldValue)
        );
    }

    public Function<Session, Session> userDataPoints(Point point) {
        return (Session session) -> {
            ru.tinkoff.gatling.influxdb.Annotations.influxDataPoint(point);
            return session;
        };
    }

    public static Function<Session, Session> userDataPoints(String tagKey,
                                                            String tagValue,
                                                            String fieldKey,
                                                            String fieldValue){
        return (Session session) -> {
            String tag = tagValue;
            String field = fieldValue;
            if (session.contains(tag)) tag = session.getString(tag);
            if (session.contains(field)) field = session.getString(field);
            ru.tinkoff.gatling.influxdb.Annotations.influxDataPoint(tagKey, tag, fieldKey, field);
            return session;
        };
    }

}
