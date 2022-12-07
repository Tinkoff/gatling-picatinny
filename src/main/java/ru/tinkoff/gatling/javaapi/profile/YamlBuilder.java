package ru.tinkoff.gatling.javaapi.profile;

import ru.tinkoff.gatling.profile.Yaml;

public record YamlBuilder(Yaml yaml) {

    public OneProfileBuilder selectProfile(String profileName) {
        return new OneProfileBuilder(yaml.selectProfile(profileName));
    }
}
