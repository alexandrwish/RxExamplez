package com.magenta.rx.java.model.comporator;

import java.util.Comparator;

public class JsonDictionaryKeyComparator implements Comparator<String> {

    private static JsonDictionaryKeyComparator instance;

    public static JsonDictionaryKeyComparator getInstance() {
        return instance == null ? instance = new JsonDictionaryKeyComparator() : instance;
    }

    private JsonDictionaryKeyComparator() {
    }

    public int compare(String o1, String o2) {
        return o1.equalsIgnoreCase("text") ? -1 : (o2.equalsIgnoreCase("text") ? 1 : (o1.equalsIgnoreCase("pos") ? -1 : (o2.equalsIgnoreCase("pos") ? 1 : 0)));
    }
}