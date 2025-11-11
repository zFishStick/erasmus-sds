package com.sds2.classes;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;

@Embeddable
public class Address {
    private String countryCode;

    @ElementCollection
    private List<String> lines = new ArrayList<>();

    public Address() {
    }

    public Address(String countryCode, List<String> lines) {
        this.countryCode = countryCode;
        if (lines != null) {
            this.lines = new ArrayList<>(lines);
        }
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public List<String> getLines() {
        return lines;
    }

    public void setLines(List<String> lines) {
        this.lines = (lines != null) ? new ArrayList<>(lines) : new ArrayList<>();
    }
}

