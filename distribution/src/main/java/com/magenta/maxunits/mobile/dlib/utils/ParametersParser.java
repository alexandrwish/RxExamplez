package com.magenta.maxunits.mobile.dlib.utils;

import java.util.HashMap;
import java.util.Map;

public class ParametersParser {

    public static Map fromString(final String parameters) {
        final Map result = new HashMap();
        final SimpleParser parser = new SimpleParser(parameters);
        while (!parser.nextToken().isEnd()) {
            final SimpleParser.Token name = parser.getToken();
            if (name.isId() || name.isString()) {
                final SimpleParser.Token d = parser.nextToken();
                if (d.isDelimiter() && d.getValueChar() == '=') {
                    final SimpleParser.Token value = parser.nextToken();
                    if (!value.isEnd() && !value.isDelimiter()) {
                        result.put(name.getValue(), value.getValue());
                    }
                }
            }
        }
        return result;
    }
}