package ru.tinkoff.gatling.javaapi.utils;

import io.gatling.javaapi.core.Session;
import ru.tinkoff.gatling.utils.jwt.*;
import ru.tinkoff.gatling.utils.jwt.package$;

import java.util.function.Function;

public final class Jwt {

    public static JwtGeneratorBuilder jwt(String algorithm, String secret) {
        return new JwtGeneratorBuilder(new Header(""), new Payload(""), algorithm, secret);
    }

    public static Function<Session, Session> setJwt(JwtGeneratorBuilder generator, String tokenName) {
        return session -> new Session(
                    package$.MODULE$.SessionAppender(session.asScala()).setJwt(generator, tokenName)
            );
    }
}
