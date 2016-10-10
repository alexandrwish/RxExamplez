package com.magenta.rx.rxa.model;

import java.util.Arrays;

public class TranslateAnswer {

    private String code;
    private String lang;
    private String[] text;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String[] getText() {
        return text;
    }

    public void setText(String[] text) {
        this.text = text;
    }

    public String toString() {
        return "{\"code\":" + code + ",\"lang\":" + lang + ",\"text\":[" + Arrays.toString(text) + "]}";
    }
}