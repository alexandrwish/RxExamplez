package com.magenta.rx.rxa.model.record;

public class Definition implements Texted {

    private String text;
    private String pos;
    private String ts;
    private Transcription[] tr;

    public Definition(String text, String pos, String ts, Transcription[] tr) {
        this.text = text;
        this.pos = pos;
        this.ts = ts;
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

    public String getTs() {
        return ts;
    }

    public void setTs(String ts) {
        this.ts = ts;
    }

    public Transcription[] getTr() {
        return tr;
    }

    public void setTr(Transcription[] tr) {
        this.tr = tr;
    }
}