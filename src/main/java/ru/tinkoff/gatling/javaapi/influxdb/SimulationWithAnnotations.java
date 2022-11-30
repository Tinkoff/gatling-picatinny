package ru.tinkoff.gatling.javaapi.influxdb;

import io.gatling.javaapi.core.*;

import ru.tinkoff.gatling.influxdb.*;
import scala.Function0;

import java.util.ArrayList;
import java.util.List;

public class SimulationWithAnnotations extends Simulation {
    private final List<Function0<?>> _beforeSteps = new ArrayList<>();
    private final List<Function0<?>> _afterSteps = new ArrayList<>();

    public void before(Function0<?> function) {
        _beforeSteps.add(function);
    }

    public void after(Function0<?> function) {
        _afterSteps.add(function);
    }

    @Override
    public void before() {
        _beforeSteps.forEach(Function0::apply);
        AnnotationManager.addStatusAnnotation(Start$.MODULE$);
    }

    @Override
    public void after() {
        AnnotationManager.addStatusAnnotation(Stop$.MODULE$);
        _afterSteps.forEach(Function0::apply);
    }

}
