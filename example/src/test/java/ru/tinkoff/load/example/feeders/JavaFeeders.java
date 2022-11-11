package ru.tinkoff.load.example.feeders;

import ru.tinkoff.gatling.javaapi.utils.phone.PhoneFormatBuilder;
import ru.tinkoff.gatling.javaapi.utils.phone.TypePhone;
import ru.tinkoff.gatling.utils.phone.PhoneFormat;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static ru.tinkoff.gatling.javaapi.Feeders.*;

public class JavaFeeders {
    private final LocalDateTime newYearDate = LocalDateTime.of(2020, 1, 1, 0, 0);
    private final LocalDateTime goToWorkDate = LocalDateTime.of(2020, 1, 9, 9, 0);
    private final DateTimeFormatter formatterShort = DateTimeFormatter.ofPattern("MM:dd");

    // date2pattern
    Iterator<Map<String, Object>> timeShort = CurrentDateFeeder("timeShort", formatterShort);

    // random date +/- 5 minutes with "Australia/Sydney" timezone
    ZoneId ausTZ = ZoneId.of("Australia/Sydney");
    Iterator<Map<String, Object>> timezoneRandom = CurrentDateFeeder("timeShort", formatterShort, ausTZ);

    // random date +/- 3 days from now
    Iterator<Map<String, Object>> simpleRandomDate = RandomDateFeeder("simpleDate", 3, 3);

    // random date from newYearDate  with specified date string pattern
    Iterator<Map<String, Object>> holidaysDate =
            RandomDateFeeder("holidays", 1, 5, "hh:mm:dd", newYearDate, ChronoUnit.MINUTES, ausTZ);

    // random time from 9:00 to 18:00
    Iterator<Map<String, Object>> firstWorkDayHours =
            RandomDateFeeder("firstWorkDayHours", 9 * 60, 0, "HH:mm", goToWorkDate, ChronoUnit.MINUTES, ausTZ);

    // feeder provide two params:
    // startOfVacation = LocalDateTime.now()
    // endOfVacation = random date from now() to 14 days in the future
    Iterator<Map<String, Object>> vacationDate =
            RandomDateRangeFeeder("startOfVacation", "endOfVacation", 14L, "yyyy-MM-dd", LocalDateTime.now(), ChronoUnit.DAYS, ausTZ);

    // random Int
    Iterator<Map<String, Object>> randomDigit = RandomDigitFeeder("randomDigit");
    Iterator<Map<String, Object>> randomRangeInt = CustomFeeder("randomRangeInt", () -> ThreadLocalRandom.current().nextInt(1, 100 + 1));

    // random phone
    // +7 country code is default
    Iterator<Map<String, Object>> randomPhone = RandomPhoneFeeder("randomPhone");

    // random USA phone
    PhoneFormat usaPhoneFormats =
            PhoneFormatBuilder.apply("+1", 10, Arrays.asList("484", "585", "610"), "+X XXX XXX-XX-XX", Arrays.asList("55", "81", "111"));

    Iterator<Map<String, Object>> randomUsaPhone = RandomPhoneFeeder("randomUsaPhone", usaPhoneFormats);

    String phoneFormatsFromFile = "phoneTemplates/ru.json";
    PhoneFormat ruMobileFormat =
            PhoneFormatBuilder.apply("+7", 10, Arrays.asList("945", "946"), "+X XXX XXX-XX-XX", Arrays.asList("55", "81", "111"));
    PhoneFormat ruCityPhoneFormat =
            PhoneFormatBuilder.apply("+7", 10, Arrays.asList("945", "946"), "+X XXX XXX-XX-XX");

    Iterator<Map<String, Object>> simplePhoneNumber = RandomPhoneFeeder("simplePhoneFeeder");
    Iterator<Map<String, Object>> randomPhoneNumberFromJson =
            RandomPhoneFeeder("randomPhoneNumberFile", phoneFormatsFromFile);
    Iterator<Map<String, Object>> randomPhoneNumber =
            RandomPhoneFeeder("randomPhoneNumber", ruMobileFormat, ruCityPhoneFormat);
    Iterator<Map<String, Object>> randomE164PhoneNumberFromJson =
            RandomPhoneFeeder("randomE164PhoneNumberFile", phoneFormatsFromFile, TypePhone.E164PhoneNumber());
    Iterator<Map<String, Object>> randomE164PhoneNumber =
            RandomPhoneFeeder("randomE164PhoneNumber", TypePhone.E164PhoneNumber(), ruMobileFormat, ruCityPhoneFormat);
    Iterator<Map<String, Object>> randomTollFreePhoneNumberFromJson =
            RandomPhoneFeeder("randomTollFreePhoneNumberFile", phoneFormatsFromFile, TypePhone.TollFreePhoneNumber());
    Iterator<Map<String, Object>> randomTollFreePhoneNumber =
            RandomPhoneFeeder("randomTollFreePhoneNumber", TypePhone.TollFreePhoneNumber(), ruMobileFormat);

    // random alphanumeric String with specified length
    Iterator<Map<String, Object>> randomString =
            RandomStringFeeder("randomString", 16);

    // random String generated from specified alphabet (or alphanumeric as default)
    // with random length in specified interval from 1 to 10
    Iterator<Map<String, Object>> randomRangeString =
            RandomRangeStringFeeder("randomRangeString", 1, 10, "qwertyuiop*+-123");

    // random UUID
    Iterator<Map<String, Object>> randomUuid =
            RandomUUIDFeeder("randomUuid");

    // sequence of Long numbers from one to Long.MaxValue with specified step = 2
    Iterator<Map<String, Object>> sequenceLong =
            SequentialFeeder("sequenceLong", 1, 2);

    private String myFunction() {
        byte[] array = new byte[7];
        new Random().nextBytes(array);
        return new String(array, StandardCharsets.UTF_8);
    }

    // custom feeder from provided function
    Iterator<Map<String, Object>> myCustomFeeder = CustomFeeder("myParam", this::myFunction);

    Iterator<Map<String, Object>> digitFeeder = RandomDigitFeeder("digit");
    Iterator<Map<String, Object>> stringFeeder = RandomStringFeeder("string");
    Iterator<Map<String, Object>> phoneFeeder = RandomStringFeeder("phone");

    // Vault HC feeder
    private final String vaultUrl = System.getenv("vaultUrl");
    private final String secretPath = System.getenv("secretPath");
    private final String roleId = System.getenv("roleId");
    private final String secretId = System.getenv("secretId");
    private final List<String> keys = Arrays.asList("k1", "k2", "k3");
    Iterator<Map<String, Object>> vaultFeeder = VaultFeeder(vaultUrl, secretPath, roleId, secretId, keys);

    // Get separated values feeder from the source
    // SeparatedValuesFeeder will return Iterator(Map(HOSTS -> host11), Map(HOSTS -> host12), Map(USERS -> user11), Map(HOSTS -> host21), Map(HOSTS -> host22), Map(USERS -> user21), Map(USERS -> user22), Map(USERS -> user23))
    List<Map<String, Object>> vaultData = Arrays.asList(Map.of("HOSTS","host11,host12"), Map.of("USERS", "user21,user22,user23"));

    Iterator<Map<String, Object>> separatedValuesFeeder =
        SeparatedValuesFeeder.apply(Optional.empty(), vaultData, ',');

    // string sequentially generated from the specified pattern
    Iterator<Map<String, Object>> regexString = RegexFeeder("regex", "[a-zA-Z0-9]{8}");

    // random PAN
    Iterator<Map<String, Object>> feederWithoutBinPAN = RandomPANFeeder("feederWithoutBinPAN");
    Iterator<Map<String, Object>> feederPAN = RandomPANFeeder("feederPAN", "421345", "541673");

    // random ITN
    Iterator<Map<String, Object>> feederNatITN = RandomNatITNFeeder("feederNatITN");
    Iterator<Map<String, Object>> feederJurITN = RandomJurITNFeeder("feederJurITN");

    // random OGRN
    Iterator<Map<String, Object>> feederOGRN = RandomOGRNFeeder("feederOGRN");

    // random PSRNSP
    Iterator<Map<String, Object>> feederPSRNSP = RandomPSRNSPFeeder("feederPSRNSP");

    // random KPP
    Iterator<Map<String, Object>> feederKPP = RandomKPPFeeder("feederKPP");

    // random SNILS
    Iterator<Map<String, Object>> feederSNILS = RandomSNILSFeeder("randomSNILS");

    // random russian passport
    Iterator<Map<String, Object>> feederRusPassport = RandomRusPassportFeeder("feederRusPassport");

}
