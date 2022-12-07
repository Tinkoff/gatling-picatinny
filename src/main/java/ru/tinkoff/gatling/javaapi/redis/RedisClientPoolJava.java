package ru.tinkoff.gatling.javaapi.redis;

import io.gatling.javaapi.redis.RedisClientPool;
import ru.tinkoff.gatling.redis.RedisActionBuilder.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import static io.gatling.javaapi.core.internal.Expressions.toAnyExpression;
import static ru.tinkoff.gatling.javaapi.internal.Expression.toListExpression;

public final class RedisClientPoolJava {

    private final RedisClientPool redisClientPool;

    public RedisClientPoolJava(String host, int port) {
        this.redisClientPool = new RedisClientPool(host, port);
    }

    public RedisClientPoolJava(RedisClientPool redisClientPool) {
        this.redisClientPool = redisClientPool;
    }

    com.redis.RedisClientPool redisClientPoolAsScala() {
        Method m;
        com.redis.RedisClientPool invoke;

        try {
            m = RedisClientPool.class.getDeclaredMethod("asScala");
            m.setAccessible(true);
            invoke = (com.redis.RedisClientPool) m.invoke(redisClientPool);
        } catch (NoSuchMethodException e) {
            throw new RedisClientPoolJavaException(
                    "The " + RedisClientPool.class.getName() + "class does not have a asScala method", e);
        } catch (InvocationTargetException e) {
            throw new RedisClientPoolJavaException(
                    "asScala method of " + RedisClientPool.class.getName() + "class failed", e);
        } catch (IllegalAccessException e) {
            throw new RedisClientPoolJavaException(
                    "asScala method of " + RedisClientPool.class.getName() + "class not available", e);
        } catch (Exception e) {
            throw new RedisClientPoolJavaException("Unknown error ", e);
        }

        return invoke;
    }

    public RedisActionBuilder<RedisDelActionBuilder> DEL(String key, String... keys) {
        return new RedisActionBuilder<>(
                new ru.tinkoff.gatling.redis.RedisActionBuilder.RedisClientPoolOps(redisClientPoolAsScala()).DEL(
                        toAnyExpression(key), toListExpression(keys)
                )
        );
    }

    public RedisActionBuilder<RedisDelActionBuilder> DEL(String key, List<String> keys) {
        return DEL(key, keys.toArray(new String[0]));
    }

    public RedisActionBuilder<RedisDelActionBuilder> DEL(String key) {
        return DEL(key, Collections.emptyList());
    }

    public RedisActionBuilder<RedisSremActionBuilder> SREM(String key, String value, String... values) {
        return new RedisActionBuilder<>(
                new ru.tinkoff.gatling.redis.RedisActionBuilder.RedisClientPoolOps(redisClientPoolAsScala()).SREM(
                        toAnyExpression(key), toAnyExpression(value), toListExpression(values)
                )
        );
    }

    public RedisActionBuilder<RedisSremActionBuilder> SREM(String key, String value, List<String> values) {
        return SREM(key, value, values.toArray(new String[0]));
    }

    public RedisActionBuilder<RedisSremActionBuilder> SREM(String key, String value) {
        return SREM(key, value, Collections.emptyList());
    }


    public RedisActionBuilder<RedisSaddActionBuilder> SADD(String key, String value, String... values) {
        return new RedisActionBuilder<>(
                new ru.tinkoff.gatling.redis.RedisActionBuilder.RedisClientPoolOps(redisClientPoolAsScala()).SADD(
                        toAnyExpression(key), toAnyExpression(value), toListExpression(values)
                )
        );
    }

    public RedisActionBuilder<RedisSaddActionBuilder> SADD(String key, String value, List<String> values) {
        return SADD(key, value, values.toArray(new String[0]));
    }

    public RedisActionBuilder<RedisSaddActionBuilder> SADD(String key, String value) {
        return SADD(key, value, Collections.emptyList());

    }
}
