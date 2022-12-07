package ru.tinkoff.gatling.javaapi.redis;


public class RedisClientPoolJavaException extends RuntimeException{
    RedisClientPoolJavaException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
