package ru.tinkoff.gatling.javaapi;

import java.time.Duration;

import static scala.compat.java8.DurationConverters.toJava;

public final class SimulationConfig {

    public static String getStringParam(String path) {
        return ru.tinkoff.gatling.config.SimulationConfig.getStringParam(path);
    }

    public static Integer getIntParam(String path) {
        return ru.tinkoff.gatling.config.SimulationConfig.getIntParam(path);
    }

    public static Double getDoubleParam(String path) {
        return ru.tinkoff.gatling.config.SimulationConfig.getDoubleParam(path);
    }

    public static Duration getDurationParam(String path) {
        return toJava(ru.tinkoff.gatling.config.SimulationConfig.getDurationParam(path));
    }

    public static Boolean getBooleanParam(String path) {
        return ru.tinkoff.gatling.config.SimulationConfig.getBooleanParam(path);
    }

    public static String baseUrl() {
        return ru.tinkoff.gatling.config.SimulationConfig.baseUrl();
    }

    public static String baseAuthUrl() {
        return ru.tinkoff.gatling.config.SimulationConfig.baseAuthUrl();
    }

    public static String wsBaseUrl() {
        return ru.tinkoff.gatling.config.SimulationConfig.wsBaseUrl();
    }

    public static Integer stagesNumber() {
        return ru.tinkoff.gatling.config.SimulationConfig.stagesNumber();
    }

    public static Duration rampDuration() {
        return toJava(ru.tinkoff.gatling.config.SimulationConfig.rampDuration());
    }

    public static Duration stageDuration() {
        return toJava(ru.tinkoff.gatling.config.SimulationConfig.stageDuration());
    }

    public static Duration testDuration() {
        return toJava(ru.tinkoff.gatling.config.SimulationConfig.testDuration());
    }

    public static Double intensity() {
        return ru.tinkoff.gatling.config.SimulationConfig.intensity();
    }
}
