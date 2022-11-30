package ru.tinkoff.gatling.javaapi.utils;

import static scala.jdk.javaapi.CollectionConverters.asScala;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalUnit;
import java.util.Arrays;
import java.util.List;

public final class RandomDataGenerators {
    public static String randomString(String alphabet, int n) {
        return ru.tinkoff.gatling.utils.RandomDataGenerators.randomString(alphabet, n);
    }

    public static String digitString(int n) {
        return ru.tinkoff.gatling.utils.RandomDataGenerators.digitString(n);
    }

    public static String hexString(int n) {
        return ru.tinkoff.gatling.utils.RandomDataGenerators.hexString(n);
    }

    public static String alphanumericString(int stringLength) {
        return ru.tinkoff.gatling.utils.RandomDataGenerators.alphanumericString(stringLength);
    }

    public static String randomOnlyLettersString(int stringLength) {
        return ru.tinkoff.gatling.utils.RandomDataGenerators.randomOnlyLettersString(stringLength);
    }

    public static String randomCyrillicString(int n) {
        return ru.tinkoff.gatling.utils.RandomDataGenerators.randomCyrillicString(n);
    }

    public static int randomDigit() {
        return ru.tinkoff.gatling.utils.RandomDataGenerators.randomDigit();
    }

    public static int randomDigit(int max) {
        return ru.tinkoff.gatling.utils.RandomDataGenerators.randomDigit(max);
    }

    public static int randomDigit(int min, int max) {
        return ru.tinkoff.gatling.utils.RandomDataGenerators.randomDigit(min, max);
    }

    public static long randomDigit(long max) {
        return ru.tinkoff.gatling.utils.RandomDataGenerators.randomDigit(max);
    }

    public static long randomDigit(long min, long max) {
        return ru.tinkoff.gatling.utils.RandomDataGenerators.randomDigit(min, max);
    }

    public static double randomDigit(double max) {
        return ru.tinkoff.gatling.utils.RandomDataGenerators.randomDigit(max);
    }

    public static double randomDigit(double min, double max) {
        return ru.tinkoff.gatling.utils.RandomDataGenerators.randomDigit(min, max);
    }

    public static float randomDigit(float max) {
        return ru.tinkoff.gatling.utils.RandomDataGenerators.randomDigit(max);
    }

    public static float randomDigit(float min, float max) {
        return ru.tinkoff.gatling.utils.RandomDataGenerators.randomDigit(min, max);
    }

    public static String randomUUID() {
        return ru.tinkoff.gatling.utils.RandomDataGenerators.randomUUID();
    }

    public static String randomPAN(List<String> bins) {
        return ru.tinkoff.gatling.utils.RandomDataGenerators.randomPAN(asScala(bins).toSeq());
    }

    public static String randomOGRN() {
        return ru.tinkoff.gatling.utils.RandomDataGenerators.randomOGRN();
    }

    public static String randomPSRNSP() {
        return ru.tinkoff.gatling.utils.RandomDataGenerators.randomPSRNSP();
    }

    public static String randomKPP() {
        return ru.tinkoff.gatling.utils.RandomDataGenerators.randomKPP();
    }

    public static String randomNatITN() {
        return ru.tinkoff.gatling.utils.RandomDataGenerators.randomNatITN();
    }

    public static String randomJurITN() {
        return ru.tinkoff.gatling.utils.RandomDataGenerators.randomJurITN();
    }

    public static String randomSNILS() {
        return ru.tinkoff.gatling.utils.RandomDataGenerators.randomSNILS();
    }

    public static String randomRusPassport() {
        return ru.tinkoff.gatling.utils.RandomDataGenerators.randomRusPassport();
    }

    public static String randomDate(
            int positiveDelta,
            int negativeDelta,
            String datePattern,
            LocalDateTime dateFrom,
            TemporalUnit unit,
            ZoneId timezone) {
        return ru.tinkoff.gatling.utils.RandomDataGenerators.randomDate(positiveDelta, negativeDelta, datePattern, dateFrom, unit, timezone);
    }

    public static String randomDate(
            long offsetDate,
            LocalDateTime dateFrom,
            TemporalUnit unit,
            ZoneId timezone
    ) {
        return ru.tinkoff.gatling.utils.RandomDataGenerators.randomDate(offsetDate, "yyyy-MM-dd", dateFrom, unit, timezone);
    }

    public static String randomDate(
            long offsetDate,
            String datePattern,
            LocalDateTime dateFrom,
            TemporalUnit unit,
            ZoneId timezone
    ) {
        return ru.tinkoff.gatling.utils.RandomDataGenerators.randomDate(offsetDate, datePattern, dateFrom, unit, timezone);
    }

    public static String currentDate(DateTimeFormatter datePattern, ZoneId timezone){
        return ru.tinkoff.gatling.utils.RandomDataGenerators.currentDate(datePattern, timezone);
    }
}
