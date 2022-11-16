package ru.tinkoff.gatling.javaapi.utils.assertions;

import java.util.List;

public class NFR {

    private List<RecordNFR> nfr;

    public NFR() {

    }

    public NFR(List<RecordNFR> nfr) {
        super();
        this.nfr = nfr;
    }

    public List<RecordNFR> getNfr() {
        return nfr;
    }

    @Override
    public String toString() {
        return "NFR{" +
                "nfr=" + nfr +
                '}';
    }
}
