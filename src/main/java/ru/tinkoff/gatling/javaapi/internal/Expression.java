package ru.tinkoff.gatling.javaapi.internal;

import io.gatling.commons.validation.Validation;
import io.gatling.core.session.Session;
import io.gatling.javaapi.core.internal.Expressions;
import scala.Function1;
import scala.collection.immutable.Seq;
import scala.jdk.javaapi.CollectionConverters;

import java.util.Arrays;

public final class Expression {
    private Expression() {}

    public static Seq<Function1<Session, Validation<Object>>> toListExpression(String... expressions) {
        return CollectionConverters.asScala(Arrays.stream(expressions).map(Expressions::toAnyExpression).toList()).toSeq();
    }
}
