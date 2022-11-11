package ru.tinkoff.gatling.javaapi;

import java.time.temporal.ChronoUnit;
import java.util.*;
import java.time.*;
import java.time.temporal.TemporalUnit;
import java.time.format.DateTimeFormatter;

import static scala.jdk.javaapi.CollectionConverters.asScala;

import ru.tinkoff.gatling.utils.phone.PhoneFormat;
import ru.tinkoff.gatling.utils.phone.TypePhone;
import scala.Function0;
import scala.Option;

import static ru.tinkoff.gatling.javaapi.internal.Feeders.*;

public final class Feeders {

    private Feeders() {
    }

    public static Iterator<Map<String, Object>> CurrentDateFeeder(String paramName, DateTimeFormatter datePattern, ZoneId timezone) {
        return toJavaFeeder(ru.tinkoff.gatling.feeders.CurrentDateFeeder.apply(paramName, datePattern, timezone));
    }

    public static Iterator<Map<String, Object>> CurrentDateFeeder(String paramName, DateTimeFormatter datePattern) {
        return CurrentDateFeeder(paramName, datePattern, ZoneId.systemDefault());
    }

    public static <T> Iterator<Map<String, Object>> CustomFeeder(String paramName, Function0<T> f) {
        return toJavaFeeder(
                ru.tinkoff.gatling.feeders.CustomFeeder.apply(paramName, f)
        );
    }

    public static Iterator<Map<String, Object>> RandomDateFeeder(
            String paramName,
            Integer positiveDaysDelta,
            Integer negativeDaysDelta,
            String datePattern,
            LocalDateTime dateFrom,
            TemporalUnit unit,
            ZoneId timezone
    ) {
        return toJavaFeeder(
                ru.tinkoff.gatling.feeders.RandomDateFeeder.apply(
                        paramName,
                        positiveDaysDelta,
                        negativeDaysDelta,
                        datePattern,
                        dateFrom,
                        unit,
                        timezone
                )
        );
    }

    public static Iterator<Map<String, Object>> RandomDateFeeder(
            String paramName,
            Integer positiveDaysDelta,
            Integer negativeDaysDelta
    ) {
        return RandomDateFeeder(
                paramName,
                positiveDaysDelta,
                negativeDaysDelta,
                "yyyy-MM-dd",
                LocalDateTime.now(),
                ChronoUnit.DAYS,
                ZoneId.systemDefault()
        );
    }

    public static Iterator<Map<String, Object>> RandomDateRangeFeeder(
            String paramNameFrom,
            String paramNameTo,
            Long offsetDate,
            String datePattern,
            LocalDateTime dateFrom,
            TemporalUnit unit,
            ZoneId timezone
    ) {
        return toJavaFeeder(ru.tinkoff.gatling.feeders.RandomDateRangeFeeder.apply(paramNameFrom, paramNameTo, offsetDate, datePattern, dateFrom, unit, timezone));
    }

    public static Iterator<Map<String, Object>> RandomDateRangeFeeder(
            String paramNameFrom,
            String paramNameTo,
            Long offsetDate
    ) {
        return RandomDateRangeFeeder(paramNameFrom, paramNameTo, offsetDate, "yyyy-MM-dd", LocalDateTime.now(), ChronoUnit.DAYS, ZoneId.systemDefault());
    }

    public static Iterator<Map<String, Object>> RandomDigitFeeder(String paramName) {
        return toJavaFeeder(ru.tinkoff.gatling.feeders.RandomDigitFeeder.apply(paramName));
    }

    public static Iterator<Map<String, Object>> RandomJurITNFeeder(String paramName) {
        return toJavaFeeder(ru.tinkoff.gatling.feeders.RandomJurITNFeeder.apply(paramName));
    }

    public static Iterator<Map<String, Object>> RandomKPPFeeder(String paramName) {
        return toJavaFeeder(ru.tinkoff.gatling.feeders.RandomKPPFeeder.apply(paramName));
    }

    public static Iterator<Map<String, Object>> RandomNatITNFeeder(String paramName) {
        return toJavaFeeder(ru.tinkoff.gatling.feeders.RandomNatITNFeeder.apply(paramName));
    }

    public static Iterator<Map<String, Object>> RandomOGRNFeeder(String paramName) {
        return toJavaFeeder(ru.tinkoff.gatling.feeders.RandomOGRNFeeder.apply(paramName));
    }

    public static Iterator<Map<String, Object>> RandomPANFeeder(String paramName, String... bins) {
        return toJavaFeeder(ru.tinkoff.gatling.feeders.RandomPANFeeder.apply(paramName, asScala(Arrays.asList(bins)).toSeq()));
    }

    public static Iterator<Map<String, Object>> RandomPhoneFeeder(String paramName) {
        return toJavaFeeder(ru.tinkoff.gatling.feeders.RandomPhoneFeeder.apply(paramName));
    }

    public static Iterator<Map<String, Object>> RandomPhoneFeeder(String paramName, PhoneFormat... formats) {
        return toJavaFeeder(ru.tinkoff.gatling.feeders.RandomPhoneFeeder.apply(paramName, asScala(Arrays.asList(formats)).toSeq()));
    }

    public static Iterator<Map<String, Object>> RandomPhoneFeeder(String paramName, TypePhone.TypePhone typePhone, PhoneFormat... formats) {
        return toJavaFeeder(ru.tinkoff.gatling.feeders.RandomPhoneFeeder.apply(paramName, typePhone, asScala(Arrays.asList(formats)).toSeq()));
    }

    public static Iterator<Map<String, Object>> RandomPhoneFeeder(String paramName, String formatsPath, TypePhone.TypePhone typePhone) {
        return toJavaFeeder(ru.tinkoff.gatling.feeders.RandomPhoneFeeder.apply(paramName, formatsPath, typePhone));
    }

    public static Iterator<Map<String, Object>> RandomPhoneFeeder(String paramName, String formatsPath) {
        return toJavaFeeder(ru.tinkoff.gatling.feeders.RandomPhoneFeeder.apply(paramName, formatsPath));
    }

    public static Iterator<Map<String, Object>> RandomPSRNSPFeeder(String paramName) {
        return toJavaFeeder(ru.tinkoff.gatling.feeders.RandomPSRNSPFeeder.apply(paramName));
    }

    public static Iterator<Map<String, Object>> RandomRangeStringFeeder(String paramName, Integer from, Integer to, String alphabet) {
        return toJavaFeeder(ru.tinkoff.gatling.feeders.RandomRangeStringFeeder.apply(paramName, from, to, alphabet));
    }

    public static Iterator<Map<String, Object>> RandomRangeStringFeeder(String paramName) {
        String alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789@#%\"&*()_-+={}<>?|:[].~";
        return RandomRangeStringFeeder(paramName, 10, 15, alphabet);
    }

    public static Iterator<Map<String, Object>> RandomRusPassportFeeder(String paramName) {
        return toJavaFeeder(ru.tinkoff.gatling.feeders.RandomRusPassportFeeder.apply(paramName));
    }

    public static Iterator<Map<String, Object>> RandomSNILSFeeder(String paramName) {
        return toJavaFeeder(ru.tinkoff.gatling.feeders.RandomSNILSFeeder.apply(paramName));
    }

    public static Iterator<Map<String, Object>> RandomStringFeeder(String paramName, Integer paramLength) {
        return toJavaFeeder(ru.tinkoff.gatling.feeders.RandomStringFeeder.apply(paramName, paramLength));
    }

    public static Iterator<Map<String, Object>> RandomStringFeeder(String paramName) {
        return RandomStringFeeder(paramName, 10);
    }

    public static Iterator<Map<String, Object>> RandomUUIDFeeder(String paramName) {
        return toJavaFeeder(ru.tinkoff.gatling.feeders.RandomUUIDFeeder.apply(paramName));
    }

    public static Iterator<Map<String, Object>> RegexFeeder(String paramName, String regex) {
        return toJavaFeeder(ru.tinkoff.gatling.feeders.RegexFeeder.apply(paramName, regex));
    }

    public static final class SeparatedValuesFeeder {
        private static final char commaSeparator = ',';
        private static final char SemicolonSeparator = ';';
        private static final char TabulationSeparator = '\t';

        public static Iterator<Map<String, Object>> apply(String paramName, String source, char separator) {
            return toJavaFeeder(ru.tinkoff.gatling.feeders.SeparatedValuesFeeder.apply(paramName, source, separator));
        }

        public static Iterator<Map<String, Object>> apply(String paramName, List<String> source, char separator) {
            return toJavaFeeder(ru.tinkoff.gatling.feeders.SeparatedValuesFeeder.apply(paramName, asScala(source).toSeq(), separator, io.gatling.core.Predef.configuration()));
        }

        public static Iterator<Map<String, Object>> apply(Optional<String> paramPrefix, List<Map<String, Object>> source, char separator) {
            Option<String> _paramPrefix = toScalaOption(paramPrefix);

            return toJavaFeeder(ru.tinkoff.gatling.feeders.SeparatedValuesFeeder.apply(_paramPrefix, toScala(source), separator, io.gatling.core.Predef.configuration()));
        }

        public static Iterator<Map<String, Object>> csv(String paramName, String source) {
            return apply(paramName, source, commaSeparator);
        }

        public static Iterator<Map<String, Object>> ssv(String paramName, String source) {
            return apply(paramName, source, SemicolonSeparator);
        }

        public static Iterator<Map<String, Object>> tsv(String paramName, String source) {
            return apply(paramName, source, TabulationSeparator);
        }

        public static Iterator<Map<String, Object>> csv(String paramName, List<String> source) {
            return apply(paramName, source, commaSeparator);
        }

        public static Iterator<Map<String, Object>> ssv(String paramName, List<String> source) {
            return apply(paramName, source, SemicolonSeparator);
        }

        public static Iterator<Map<String, Object>> tsv(String paramName, List<String> source) {
            return apply(paramName, source, TabulationSeparator);
        }

        public static Iterator<Map<String, Object>> csv(Optional<String> paramPrefix, List<Map<String, Object>> source) {
            return apply(paramPrefix, source, commaSeparator);
        }

        public static Iterator<Map<String, Object>> ssv(Optional<String> paramPrefix, List<Map<String, Object>> source) {
            return apply(paramPrefix, source, SemicolonSeparator);
        }

        public static Iterator<Map<String, Object>> tsv(Optional<String> paramPrefix, List<Map<String, Object>> source) {
            return apply(paramPrefix, source, TabulationSeparator);
        }

    }

    public static Iterator<Map<String, Object>> SequentialFeeder(String paramName, Integer start, Integer step) {
        return toJavaFeeder(ru.tinkoff.gatling.feeders.SequentialFeeder.apply(paramName, start, step));
    }

    public static Iterator<Map<String, Object>> SequentialFeeder(String paramName) {
        return SequentialFeeder(paramName, 0, 1);
    }

    public static Iterator<Map<String, Object>> VaultFeeder(
            String vaultUrl,
            String secretPath,
            String roleId,
            String secretId,
            List<String> keys
    ) {
        return toJavaFeeder(ru.tinkoff.gatling.feeders.VaultFeeder.apply(vaultUrl, secretPath, roleId, secretId, asScala(keys).toList()));
    }
}
