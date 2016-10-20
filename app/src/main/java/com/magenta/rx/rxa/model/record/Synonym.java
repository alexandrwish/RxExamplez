package com.magenta.rx.rxa.model.record;

public class Synonym implements Texted {

    private String text;

    public Synonym(String text) {
        this.text = text;
    }

    public Synonym() {
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}