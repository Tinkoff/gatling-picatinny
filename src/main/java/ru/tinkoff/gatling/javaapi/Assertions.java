package ru.tinkoff.gatling.javaapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import io.gatling.javaapi.core.Assertion;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static java.lang.Double.parseDouble;

public final class Assertions {

    private record RecordNFR(String key, HashMap<String, String> value) {}

    private record NFR(List<RecordNFR> nfr) {}

    private Assertions() {

    }

    private static NFR getNfr(String path) throws IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.findAndRegisterModules();
        NFR nfr = null;
        try {
            nfr = mapper.readValue(new File(path), NFR.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nfr;
    }

    private static String toUtf(String baseString) {
        return new String(baseString.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
    }

    private static String[] getGroupAndRequest(String key) {
        return key.split(" / ");
    }

    private static List<Assertion> buildAssertion(RecordNFR record) {
        return switch (toUtf(record.key())) {
            case "Процент ошибок" -> buildErrorAssertion(record);
            case "99 перцентиль времени выполнения" -> buildPercentileAssertion(record, 99.0);
            case "95 перцентиль времени выполнения" -> buildPercentileAssertion(record, 95.0);
            case "75 перцентиль времени выполнения" -> buildPercentileAssertion(record, 75.0);
            case "50 перцентиль времени выполнения" -> buildPercentileAssertion(record, 50.0);
            case "Максимальное время выполнения" -> buildMaxResponseTimeAssertion(record);
            default -> new LinkedList<>();
        };
    }

    private static List<Assertion> getListAssertions(List<Assertion> assertionList,
                                                     String key,
                                                     Assertion allAssertion,
                                                     Assertion detailsAssertion) {
        if (Objects.equals(key, "all")) {
            assertionList.add(allAssertion);
        }
        else {
            assertionList.add(detailsAssertion);
        }

        return assertionList;
    }

    private static List<Assertion> buildPercentileAssertion(RecordNFR record, Double percentile) {
        List<Assertion> assertionList = new LinkedList<>();

        for (Map.Entry<String, String> entry: record.value().entrySet() ) {
            String key = entry.getKey();
            String value = entry.getValue();

            assertionList.addAll(getListAssertions(
                    assertionList,
                    key,
                    global().responseTime().percentile(percentile).lt(Integer.valueOf(value)),
                    details(getGroupAndRequest(key)).responseTime().percentile(percentile).lt(Integer.valueOf(value)))
            );
        }

        return assertionList;
    }

    private static List<Assertion> buildErrorAssertion(RecordNFR record) {
        List<Assertion> assertionList = new LinkedList<>();

        for (Map.Entry<String, String> entry: record.value().entrySet() ) {
            String key = entry.getKey();
            String value = entry.getValue();

            assertionList.addAll(getListAssertions(
                    assertionList,
                    key,
                    global().failedRequests().percent().lt(parseDouble(value)),
                    details(getGroupAndRequest(key)).failedRequests().percent().lt(parseDouble(value)))
            );

        }
        return assertionList;
    }

    private static List<Assertion> buildMaxResponseTimeAssertion(RecordNFR record) {
        List<Assertion> assertionList = new LinkedList<>();

        for (Map.Entry<String, String> entry: record.value().entrySet() ) {
            String key = entry.getKey();
            String value = entry.getValue();

            assertionList.addAll(getListAssertions(
                    assertionList,
                    key,
                    global().responseTime().max().lt(Integer.valueOf(value)),
                    details(getGroupAndRequest(key)).responseTime().max().lt(Integer.valueOf(value)))
            );
        }
        return assertionList;
    }
    
    public static  List<Assertion> assertionFromYaml(String path) {

        List<Assertion> assertionList = new LinkedList<>();

        try {
            NFR nfr = getNfr(path);
            List<RecordNFR> recordNFRList = nfr.nfr();
            recordNFRList.forEach(recordNFR -> assertionList.addAll(buildAssertion(recordNFR)));
        } catch ( IOException e ) {
            throw new RuntimeException(e);
        }

        return assertionList;
    }
}
