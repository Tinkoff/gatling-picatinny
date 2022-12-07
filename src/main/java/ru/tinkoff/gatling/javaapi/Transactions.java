package ru.tinkoff.gatling.javaapi;

import io.gatling.core.action.builder.ActionBuilder;
import io.gatling.core.structure.ChainBuilder;
import io.gatling.javaapi.core.internal.Expressions;
import ru.tinkoff.gatling.transactions.actions.builders;
import scala.jdk.javaapi.CollectionConverters;

import java.util.ArrayList;

public final class Transactions {

    static ActionBuilder startTransactionActionBuilder(String tname) {
        return new builders.StartTransactionActionBuilder(Expressions.toStringExpression(tname));
    }

    static ActionBuilder endTransactionActionBuilder(String tname, Long time) {
        return new builders.EndTransactionActionBuilder(Expressions.toStringExpression(tname), Expressions.toStaticValueExpression(time));
    }

    static ActionBuilder endTransactionActionBuilder(String tname) {
        return new builders.EndTransactionActionBuilderWithoutTime(Expressions.toStringExpression(tname));
    }

    public static scala.collection.immutable.List<ActionBuilder> getCollection(ActionBuilder myAction) {

        java.util.Collection<ActionBuilder> collection = new ArrayList<>();
        collection.add(myAction);

        return CollectionConverters.asScala(collection).toList();
    }

    public static io.gatling.javaapi.core.ChainBuilder startTransaction(String name) {
        ActionBuilder action = startTransactionActionBuilder(name);
        ChainBuilder scalaChain = new ChainBuilder(getCollection(action));
        return io.gatling.javaapi.core.ChainBuilder.EMPTY.make(x -> scalaChain);
    }

    public static io.gatling.javaapi.core.ChainBuilder endTransaction(String name, Long time) {
        ActionBuilder action = endTransactionActionBuilder(name, time);
        ChainBuilder scalaChain = new ChainBuilder(getCollection(action));
        return io.gatling.javaapi.core.ChainBuilder.EMPTY.make(x -> scalaChain);
    }

    public static io.gatling.javaapi.core.ChainBuilder endTransaction(String name) {
        ActionBuilder action = endTransactionActionBuilder(name);
        ChainBuilder scalaChain = new ChainBuilder(getCollection(action));
        return io.gatling.javaapi.core.ChainBuilder.EMPTY.make(x -> scalaChain);
    }
}
