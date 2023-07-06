package org.example;

import java.util.HashMap;
import java.util.Map;

public enum BotCurrency {
    UAH("UAH_source", "UAH_target"),
    USD("USD_source", "USD_target"),
    ARS("ARS_source", "ARS_target");

    private static final Map<String, BotCurrency> sourceLookup = new HashMap<>();
    private static final Map<String, BotCurrency> targetLookup = new HashMap<>();

    static {
        for (BotCurrency currency : values()) {
            sourceLookup.put(currency.getSourceCallback(), currency);
            targetLookup.put(currency.getTargetCallback(), currency);
        }
    }
    private final String sourceCallback;
    private final String targetCallback;

    BotCurrency(String sourceCallback, String targetCallback) {
        this.sourceCallback = sourceCallback;
        this.targetCallback = targetCallback;
    }

    public String getSourceCallback() {
        return sourceCallback;
    }

    public String getTargetCallback() {
        return targetCallback;
    }

    public static BotCurrency getSourceCurrency(String sourceCallback) {
        return sourceLookup.get(sourceCallback);
    }

    public static BotCurrency getTargetCurrency(String targetCallback) {
        return targetLookup.get(targetCallback);
    }
}
