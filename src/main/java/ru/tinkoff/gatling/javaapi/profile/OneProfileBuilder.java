package ru.tinkoff.gatling.javaapi.profile;

import io.gatling.javaapi.core.*;
import ru.tinkoff.gatling.profile.OneProfile;
import scala.Tuple2;
import scala.jdk.javaapi.CollectionConverters;

import java.util.List;
import java.util.stream.DoubleStream;

import static io.gatling.javaapi.core.CoreDsl.scenario;

public record OneProfileBuilder(OneProfile oneProfile) {

    public ScenarioBuilder toRandomScenario() {
        List<Tuple2<Double, ChainBuilder>> requests = CollectionConverters
                .asJava(oneProfile.profile())
                .stream()
                .map(ru.tinkoff.gatling.javaapi.internal.ProfileBuilderNew::toTuple)
                .toList();

        double intensitySum = requests
                .stream()
                .flatMapToDouble(request -> DoubleStream.of(request._1))
                .sum();

        List<Choice.WithWeight> prepRequests = requests
                .stream()
                .map(request -> Choice.withWeight(100 * request._1 / intensitySum, request._2))
                .toList();

        return scenario(oneProfile.name()).randomSwitch().on(prepRequests);
    }
}
