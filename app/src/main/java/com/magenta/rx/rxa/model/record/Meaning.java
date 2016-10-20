package com.magenta.rx.rxa.model.record;

public class Meaning implements Texted {

    private String text;

    public Meaning(String text) {
        this.text = text;
    }

    public Meaning() {
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}