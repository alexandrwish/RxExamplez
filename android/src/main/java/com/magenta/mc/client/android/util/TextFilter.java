package com.magenta.mc.client.android.util;

import android.text.InputFilter;
import android.text.Spanned;

public class TextFilter implements InputFilter {

    private final TextFilterType type;
    private final int length;

    public TextFilter(TextFilterType type, int length) {
        this.type = type;
        this.length = length;
    }

    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        String charStr = source.toString();
        String str = dest.toString();
        switch (type) {
            case DOUBLE:
            case NUMBER:
            case PERCENT:
            case MONEY: {
                return (charStr.matches("[-]|\\d|\\.") || charStr.matches(type.getRegExp())) && (str.substring(0, dend) + charStr + str.substring(dend, str.length())).matches(type.getRegExp()) ? null : "";
            }
            case TIME:
            case DATE: {
                return charStr.matches(type.getRegExp()) && (str.substring(0, dend) + charStr + str.substring(dend, str.length())).matches(type.getRegExp()) ? null : "";
            }
            case TEXT: {
                return filterLength(length, source, start, end, dest, dstart, dend);
            }

        }
        return null;
    }

    private CharSequence filterLength(int max, CharSequence charSequence, int i, int i2, Spanned spanned, int i3, int i4) {
        if (charSequence.toString().matches(type.getRegExp())) {
            int keep = max - (spanned.length() - (i3 - i4));
            if (keep <= 0) {
                return "";
            } else if (keep >= i2 - i) {
                return null;
            } else {
                return spanned.subSequence(i, i + keep);
            }
        } else {
            return "";
        }
    }

    public enum TextFilterType {
        NUMBER("[-]?\\d{0,6}"),
        DOUBLE("[-]?\\d{0,6}+(?:\\.(?:[0-9]{0,5})?)?"),
        PERCENT("[-]?\\d{0,2}(?:\\.(?:[0-9]{0,2})?)?"),
        MONEY("[-]?\\d{0,6}(?:\\.(?:[0-9]{0,2})?)?"),
        TEXT(".{0,255}"),
        DATE("\\d{0,2}(?:\\/(?:\\d{0,2}(?:\\/(?:\\d{0,4})?)?)?)?"),
        TIME("\\d{0,2}(?:\\:(?:\\d{0,2})?)?");

        private final String regExp;

        TextFilterType(String regExp) {
            this.regExp = regExp;
        }

        public String getRegExp() {
            return regExp;
        }
    }
}