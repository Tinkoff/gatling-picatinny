package ru.tinkoff.gatling.javaapi;

import io.gatling.javaapi.core.ScenarioBuilder;
import ru.tinkoff.gatling.javaapi.profile.ProfileBuilderNew;


public class JavaProfileTest {
    ScenarioBuilder scn = ProfileBuilderNew
            .buildFromYaml("perf/profiles.yml")
            .selectProfile("MaxPerf")
            .toRandomScenario();
}
