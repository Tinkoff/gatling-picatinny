package ru.tinkoff.gatling.javaapi.assertions;

import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.core.CoreDsl.*;
import static ru.tinkoff.gatling.javaapi.Assertions.assertionFromYaml;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

public class JavaAssertionsCompileTest extends Simulation {

    HttpProtocolBuilder httpProtocol =
            http.baseUrl("https://computer-database.gatling.io")
                    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                    .acceptLanguageHeader("en-US,en;q=0.5")
                    .acceptEncodingHeader("gzip, deflate")
                    .userAgentHeader(
                            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0"
                    );

    {
            setUp(
                    scenario("Users")
                            .exec(http("Home").get("/"))
                            .injectOpen(rampUsers(10).during(10))
            ).protocols(httpProtocol).assertions(assertionFromYaml("src/test/resources/nfr.yml"));
    }
}
