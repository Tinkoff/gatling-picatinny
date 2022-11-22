package ru.tinkoff.gatling.javaapi.profile;


public final class ProfileBuilderNew {

    public static YamlBuilder buildFromYaml(String path) {
        return new YamlBuilder(ru.tinkoff.gatling.profile.ProfileBuilderNew.buildFromYaml(path));
    }
}