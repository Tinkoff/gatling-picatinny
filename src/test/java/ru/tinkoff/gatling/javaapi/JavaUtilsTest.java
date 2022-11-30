package ru.tinkoff.gatling.javaapi;

import io.gatling.javaapi.core.ScenarioBuilder;
import ru.tinkoff.gatling.javaapi.utils.RandomPhoneGenerator;
import ru.tinkoff.gatling.javaapi.utils.RandomDataGenerators;
import ru.tinkoff.gatling.javaapi.utils.phone.*;
import ru.tinkoff.gatling.utils.jwt.JwtGeneratorBuilder;
import ru.tinkoff.gatling.utils.phone.PhoneFormat;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Arrays;
import java.util.List;

import static io.gatling.javaapi.core.CoreDsl.scenario;
import static ru.tinkoff.gatling.javaapi.utils.IntensityConverter.*;
import static ru.tinkoff.gatling.javaapi.utils.Jwt.jwt;
import static ru.tinkoff.gatling.javaapi.utils.Jwt.setJwt;

public class JavaUtilsTest {
    // RandomPhoneGenerator
    PhoneFormat ruMobileFormat = PhoneFormatBuilder.apply("+7", 10, Arrays.asList("945", "946"), "+X XXX XXX-XX-XX", Arrays.asList("qwe", "weew"));
    PhoneFormat ruCityPhoneFormat = PhoneFormatBuilder.apply("+7", 10, Arrays.asList("945", "946"), "+X XXX XXX-XX-XX");

    String randomPhone1 = RandomPhoneGenerator.randomPhone(List.of(ruMobileFormat, ruCityPhoneFormat), TypePhone.E164PhoneNumber());
    String randomPhone2 = RandomPhoneGenerator.randomPhone("pathToFormats", TypePhone.E164PhoneNumber());

    // RandomDataGenerators
    String randomString = RandomDataGenerators.randomString("asd", 2);
    String digitString = RandomDataGenerators.digitString(2);
    String hexString = RandomDataGenerators.hexString(3);
    String alphanumericString = RandomDataGenerators.alphanumericString(1);
    String randomOnlyLettersString = RandomDataGenerators.randomOnlyLettersString(1);
    String randomCyrillicString = RandomDataGenerators.randomCyrillicString(1);
    int randomDigit1 = RandomDataGenerators.randomDigit();
    int randomDigit2 = RandomDataGenerators.randomDigit(1);
    int randomDigit3 = RandomDataGenerators.randomDigit(1, 2);
    long randomDigit4 = RandomDataGenerators.randomDigit(3000000000L);
    long randomDigit5 = RandomDataGenerators.randomDigit(3000000000L, 3000000005L);
    double randomDigit6 = RandomDataGenerators.randomDigit(2.0);
    double randomDigit7 = RandomDataGenerators.randomDigit(2.5, 3.6);
    float randomDigit8 = RandomDataGenerators.randomDigit(2.5f);
    float randomDigit9 = RandomDataGenerators.randomDigit(2.5f, 3.6f);
    String randomUUID = RandomDataGenerators.randomUUID();
    String randomPAN = RandomDataGenerators.randomPAN(List.of("123", "234"));
    String randomOGRN = RandomDataGenerators.randomOGRN();
    String randomPSRNSP = RandomDataGenerators.randomPSRNSP();
    String randomKPP = RandomDataGenerators.randomKPP();
    String randomNatITN = RandomDataGenerators.randomNatITN();
    String randomJurITN = RandomDataGenerators.randomJurITN();
    String randomSNILS = RandomDataGenerators.randomSNILS();
    String randomRusPassport = RandomDataGenerators.randomRusPassport();
    String randomDate1 = RandomDataGenerators.randomDate(2, 1, "yyyy.MM.dd", LocalDateTime.now(), ChronoUnit.DAYS, ZoneId.of("Australia/Sydney"));
    String randomDate2 = RandomDataGenerators.randomDate(1, LocalDateTime.now(), ChronoUnit.DAYS, ZoneId.of("Australia/Sydney"));
    String randomDate3 = RandomDataGenerators.randomDate(1, "yyyy-MM-dd", LocalDateTime.now(), ChronoUnit.DAYS, ZoneId.of("Australia/Sydney"));
    String currentDate = RandomDataGenerators.currentDate(DateTimeFormatter.ofPattern("MM:dd"), ZoneId.of("Australia/Sydney"));

    // jwt
    static JwtGeneratorBuilder jwtGenerator = jwt("HS256", "jwtSecretToken")
            .defaultHeader()
            .payloadFromResource("/");

    public static ScenarioBuilder scn = scenario("Runs Scenario")
            .exec(setJwt(jwtGenerator, "jwtToken"));

    // IntensityConverter
    Double intensity = 30.0;
    String s_intensity = "30";
    Double intensity_prh = rph(intensity);
    Double intensity_prm = rpm(intensity);
    Double intensity_prs = rps(intensity);
    Double _intensity = getIntensityFromString(s_intensity);
}
