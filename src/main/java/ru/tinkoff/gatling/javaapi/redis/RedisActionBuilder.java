package ru.tinkoff.gatling.javaapi.redis;

import io.gatling.javaapi.core.ActionBuilder;

/**
 *
 * @param <W> the type of wrapped RedisActionBuilder
 */
public final class RedisActionBuilder<W extends io.gatling.core.action.builder.ActionBuilder> implements ActionBuilder {

    private final W wrapped;

    @Override
    public io.gatling.core.action.builder.ActionBuilder asScala() {
        return this.wrapped;
    }

    RedisActionBuilder(W wrapped) {
        this.wrapped = wrapped;
    }

}
