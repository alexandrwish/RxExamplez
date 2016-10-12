package com.magenta.rx.rxa.model.record;

public class Definition {

    private String text;
    private String pos;
    private Transcription[] tr;

    public Definition(String text, String pos, Transcription[] tr) {
        this.text = text;
        this.pos = pos;
        this.tr = tr;
    }

    public Definition() {
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPos() {
        return pos;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }

    public Transcription[] getTr() {
        return tr;
    }

    public void setTr(Transcription[] tr) {
        this.tr = tr;
    }
}