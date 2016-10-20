package com.magenta.rx.rxa.model.record;

public class Example implements Texted {

    private String text;
    private Transcription[] tr;

    public Example(String text, Transcription[] tr) {
        this.text = text;
        this.tr = tr;
    }

    public Example() {
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Transcription[] getTr() {
        return tr;
    }

    public void setTr(Transcription[] tr) {
        this.tr = tr;
    }
}