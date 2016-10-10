package com.magenta.rx.rxa.event;

public class TranslateAnswerEvent {

    private final String text;
    private final String translate;

    public TranslateAnswerEvent(String text, String translate) {
        this.text = text;
        this.translate = translate;
    }

    public String getText() {
        return text;
    }

    public String getTranslate() {
        return translate;
    }
}