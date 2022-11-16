package ru.tinkoff.gatling.javaapi.utils.assertions;

import java.util.HashMap;

public class RecordNFR {

    private String key;
    private HashMap<String, String> value;

    public RecordNFR() {

    }
    public RecordNFR(String key, HashMap<String, String> value) {
        super();
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public HashMap<String, String> getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Record{" +
                "key='" + key + '\'' +
                ", value=" + value +
                '}';
    }

}
