package ru.tinkoff.gatling.javaapi;

import static ru.tinkoff.gatling.javaapi.Feeders.*;
import ru.tinkoff.gatling.javaapi.utils.phone.TypePhone;

import static io.gatling.javaapi.core.CoreDsl.*;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import ru.tinkoff.gatling.javaapi.utils.phone.PhoneFormatBuilder;
import ru.tinkoff.gatling.utils.phone.PhoneFormat;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;


public class JavaFeedersTest extends Simulation {
    Iterator<Map<String, Object>> currentDateFeeder = CurrentDateFeeder("timeShort", DateTimeFormatter.ofPattern("MM:dd"));
    Iterator<Map<String, Object>> currentDateFeeder1 = CurrentDateFeeder("timeShort", DateTimeFormatter.ofPattern("MM:dd"), ZoneId.systemDefault());

    private String myFunction() {
        byte[] array = new byte[7];
        new Random().nextBytes(array);
        return new String(array, StandardCharsets.UTF_8);
    }

    // custom feeder from provided function
    Iterator<Map<String, Object>> customFeeder = CustomFeeder("myParam", this::myFunction);

    Iterator<Map<String, Object>> randomDateFeeder = RandomDateFeeder("randomDateFeeder",1, 5, "hh:mm:dd", LocalDateTime.of(2020, 1, 9, 9, 0), ChronoUnit.MINUTES, ZoneId.of("Australia/Sydney"));
    Iterator<Map<String, Object>> randomDateFeeder1 = RandomDateFeeder("randomDateFeeder",1, 5);


    Iterator<Map<String, Object>> randomDateRangeFeeder = RandomDateRangeFeeder("startOfVacation", "endOfVacation", 14L, "yyyy-MM-dd", LocalDateTime.now(), ChronoUnit.DAYS, ZoneId.of("Australia/Sydney"));
    Iterator<Map<String, Object>> randomDateRangeFeeder1 = RandomDateRangeFeeder("startOfVacation", "endOfVacation", 14L);

    Iterator<Map<String, Object>> randomDigitFeeder = RandomDigitFeeder("randomDigitFeeder");
    Iterator<Map<String, Object>> randomJurITNFeeder = RandomJurITNFeeder("randomJurITNFeeder");
    Iterator<Map<String, Object>> randomKPPFeeder = RandomKPPFeeder("randomKPPFeeder");
    Iterator<Map<String, Object>> randomNatITNFeeder = RandomNatITNFeeder("randomNatITNFeeder");
    Iterator<Map<String, Object>> randomOGRNFeeder = RandomOGRNFeeder("RandomOGRNFeeder");
    Iterator<Map<String, Object>> randomPANFeeder = RandomPANFeeder("RandomPANFeeder", "411", "445");

    PhoneFormat ruMobileFormat = PhoneFormatBuilder.apply("+7", 10, Arrays.asList("945", "946"), "+X XXX XXX-XX-XX", Arrays.asList("qwe", "weew"));
    PhoneFormat ruCityPhoneFormat = PhoneFormatBuilder.apply("+7", 10, Arrays.asList("945", "946"), "+X XXX XXX-XX-XX");
    Iterator<Map<String, Object>> randomPhoneFeeder = RandomPhoneFeeder("randomPhoneFeeder");
    Iterator<Map<String, Object>> randomPhoneFeeder1 = RandomPhoneFeeder("randomPhoneFeeder", ruMobileFormat, ruCityPhoneFormat);
    Iterator<Map<String, Object>> randomPhoneFeeder2 = RandomPhoneFeeder("randomPhoneFeeder", TypePhone.E164PhoneNumber(), ruMobileFormat, ruCityPhoneFormat);
    Iterator<Map<String, Object>> randomPhoneFeeder3 = RandomPhoneFeeder("randomPhoneFeeder", "phoneTemplates/ru.json", TypePhone.PhoneNumber());
    Iterator<Map<String, Object>> randomPhoneFeeder4 = RandomPhoneFeeder("randomPhoneFeeder", "phoneTemplates/ru.json");

    Iterator<Map<String, Object>> randomPSRNSPFeeder = RandomPSRNSPFeeder("randomPSRNSPFeeder");

    Iterator<Map<String, Object>> randomRangeStringFeeder = RandomRangeStringFeeder("randomRangeStringFeeder");
    Iterator<Map<String, Object>> randomRangeStringFeeder1 = RandomRangeStringFeeder("randomRangeStringFeeder", 1, 5, "abc");

    Iterator<Map<String, Object>> randomRusPassportFeeder = RandomRusPassportFeeder("randomRusPassportFeeder");
    Iterator<Map<String, Object>> randomSNILSFeeder = RandomSNILSFeeder("randomSNILSFeeder");

    Iterator<Map<String, Object>> randomStringFeeder = RandomStringFeeder("randomStringFeeder");
    Iterator<Map<String, Object>> randomStringFeeder1 = RandomStringFeeder("randomStringFeeder", 1);

    Iterator<Map<String, Object>> randomUUIDFeeder = RandomUUIDFeeder("randomUUIDFeeder");
    Iterator<Map<String, Object>> regexFeeder = RegexFeeder("regexFeeder", "[a-z]");

    Iterator<Map<String, Object>> separatedValuesFeeder = SeparatedValuesFeeder.apply("someValues", "v21;v22;v23", ';');
    List<Map<String, Object>> source = Arrays.asList(Map.of("HOSTS","host11,host12"), Map.of("USERS", "user21,user22,user23"));
    Iterator<Map<String, Object>> separatedValuesFeeder1 = SeparatedValuesFeeder.apply("someValues", Arrays.asList("1,two", "3,4"), ',');
    Iterator<Map<String, Object>> separatedValuesFeeder2 = SeparatedValuesFeeder.apply(Optional.of("asd"), source, ',');
    Iterator<Map<String, Object>> separatedValuesFeeder3 = SeparatedValuesFeeder.csv("someValues", "v21;v22;v23");
    Iterator<Map<String, Object>> separatedValuesFeeder4 = SeparatedValuesFeeder.ssv("someValues", "v21;v22;v23");
    Iterator<Map<String, Object>> separatedValuesFeeder5 = SeparatedValuesFeeder.tsv("someValues", "v21;v22;v23");
    Iterator<Map<String, Object>> separatedValuesFeeder6 = SeparatedValuesFeeder.csv("someValues", Arrays.asList("1,two", "3,4"));
    Iterator<Map<String, Object>> separatedValuesFeeder7 = SeparatedValuesFeeder.ssv("someValues", Arrays.asList("1,two", "3,4"));
    Iterator<Map<String, Object>> separatedValuesFeeder8 = SeparatedValuesFeeder.tsv("someValues", Arrays.asList("1,two", "3,4"));
    Iterator<Map<String, Object>> separatedValuesFeeder9 = SeparatedValuesFeeder.csv(Optional.of("asd"), source);
    Iterator<Map<String, Object>> separatedValuesFeeder10 = SeparatedValuesFeeder.ssv(Optional.of("asd"), source);
    Iterator<Map<String, Object>> separatedValuesFeeder11 = SeparatedValuesFeeder.tsv(Optional.of("asd"), source);

    Iterator<Map<String, Object>> sequentialFeeder = SequentialFeeder("sequentialFeeder");
    Iterator<Map<String, Object>> sequentialFeeder1 = SequentialFeeder("sequentialFeeder", 0, 1);

    private final String vaultUrl = System.getenv("vaultUrl");
    private final String secretPath = System.getenv("secretPath");
    private final String roleId = System.getenv("roleId");
    private final String secretId = System.getenv("secretId");
    private final List<String> keys = Arrays.asList("k1", "k2", "k3");
    Iterator<Map<String, Object>> vaultFeeder = VaultFeeder(vaultUrl, secretPath, roleId, secretId, keys);

    private ScenarioBuilder scenario =
            scenario("scenario")
                    .feed(currentDateFeeder)
                    .feed(currentDateFeeder1)
                    .feed(customFeeder)
                    .feed(randomDateFeeder)
                    .feed(randomDateFeeder1)
                    .feed(randomDateRangeFeeder)
                    .feed(randomDateRangeFeeder1)
                    .feed(randomDigitFeeder)
                    .feed(randomJurITNFeeder)
                    .feed(randomKPPFeeder)
                    .feed(randomNatITNFeeder)
                    .feed(randomOGRNFeeder)
                    .feed(randomPANFeeder)
                    .feed(randomPhoneFeeder)
                    .feed(randomPhoneFeeder1)
                    .feed(randomPhoneFeeder2)
                    .feed(randomPhoneFeeder3)
                    .feed(randomPhoneFeeder4)
                    .feed(randomPSRNSPFeeder)
                    .feed(randomRangeStringFeeder)
                    .feed(randomRangeStringFeeder1)
                    .feed(randomRusPassportFeeder)
                    .feed(randomSNILSFeeder)
                    .feed(randomStringFeeder)
                    .feed(randomStringFeeder1)
                    .feed(randomUUIDFeeder)
                    .feed(regexFeeder)
                    .feed(separatedValuesFeeder)
                    .feed(separatedValuesFeeder1)
                    .feed(separatedValuesFeeder2)
                    .feed(separatedValuesFeeder3)
                    .feed(separatedValuesFeeder4)
                    .feed(separatedValuesFeeder5)
                    .feed(separatedValuesFeeder6)
                    .feed(separatedValuesFeeder7)
                    .feed(separatedValuesFeeder8)
                    .feed(separatedValuesFeeder9)
                    .feed(separatedValuesFeeder10)
                    .feed(separatedValuesFeeder11)
                    .feed(sequentialFeeder)
                    .feed(sequentialFeeder1)
                    .feed(vaultFeeder);
}
