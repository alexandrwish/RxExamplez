package com.magenta.rx.rxa.model.record;

public class Transcription implements Texted {

    private String text;
    private String pos;
    private Synonym[] syn;
    private Meaning[] mean;
    private Example[] ex;

    public Transcription(String text, String pos, Synonym[] syn, Meaning[] mean, Example[] ex) {
        this.text = text;
        this.pos = pos;
        this.syn = syn;
        this.mean = mean;
        this.ex = ex;
    }

    public Transcription() {
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

    public Synonym[] getSyn() {
        return syn;
    }

    public void setSyn(Synonym[] syn) {
        this.syn = syn;
    }

    public Meaning[] getMean() {
        return mean;
    }

    public void setMean(Meaning[] mean) {
        this.mean = mean;
    }

    public Example[] getEx() {
        return ex;
    }

    public void setEx(Example[] ex) {
        this.ex = ex;
    }
}