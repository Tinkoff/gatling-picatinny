package ru.tinkoff.gatling.javaapi.influxdb;

import io.gatling.javaapi.core.*;
import io.razem.influxdbclient.*;

import java.math.BigDecimal;
import java.util.function.Function;
import java.util.ArrayList;

import static scala.jdk.CollectionConverters.IterableHasAsScala;
import static io.gatling.javaapi.core.internal.Expressions.*;


public final class Annotations {
    public static Tag Tag(String key, String value) {
        return Tag.apply(key, value);
    }

    public static Field Field(String key, String value) {
        return StringField$.MODULE$.apply(key, value);
    }

    public static Field Field(String key, double value) {
        return DoubleField$.MODULE$.apply(key, value);
    }

    public static Field Field(String key, long value) {
        return LongField$.MODULE$.apply(key, value);
    }

    public static Field Field(String key, boolean value) {
        return BooleanField$.MODULE$.apply(key, value);
    }

    public static Field Field(String key, BigDecimal value) {
        return BigDecimalField$.MODULE$.apply(key, new scala.math.BigDecimal(value));
    }

    public static Point Point(String key) {
        return new Point(
                key,
                System.currentTimeMillis() * 1000000,
                IterableHasAsScala(new ArrayList<Tag>()).asScala().toSeq(),
                IterableHasAsScala(new ArrayList<Field>()).asScala().toSeq()
        );
    }

    public static Point Point(String key, long timestamp) {
        return new Point(
                key,
                timestamp,
                IterableHasAsScala(new ArrayList<Tag>()).asScala().toSeq(),
                IterableHasAsScala(new ArrayList<Field>()).asScala().toSeq()
        );
    }

    public static Point Point(String key, long timestamp, Iterable<Tag> tags, Iterable<Field> fields) {
        return new io.razem.influxdbclient.Point(
                key,
                timestamp,
                IterableHasAsScala(tags).asScala().toSeq(),
                IterableHasAsScala(fields).asScala().toSeq()
        );
    }

    public static PopulationBuilder userDataPoint(String uniqScpName, Point point) {
        return new PopulationBuilder(ru.tinkoff.gatling.influxdb.Annotations.userDataPoint(uniqScpName, point));
    }

    public static PopulationBuilder userDataPoint(String uniqScpName,
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

    public static Function<Session, Session> userDataPoint(Point point) {
        return (Session session) -> {
            ru.tinkoff.gatling.influxdb.Annotations.influxDataPoint(point);
            return session;
        };
    }

    public static Function<Session, Session> userDataPoint(String tagKey,
                                                           String tagValue,
                                                           String fieldKey,
                                                           String fieldValue) {
        return (Session session) -> {
            toStringExpression(tagValue).apply(session.asScala()).onSuccess((tag) -> {
                toStringExpression(fieldValue).apply(session.asScala()).onSuccess((field) ->
                {
                    ru.tinkoff.gatling.influxdb.Annotations.influxDataPoint(tagKey, tag, fieldKey, field);
                    return 0;
                });
                return 0;
            });
            return session;
        };
    }

}
