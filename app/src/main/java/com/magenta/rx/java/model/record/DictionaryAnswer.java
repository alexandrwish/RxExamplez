package com.magenta.rx.java.model.record;

public class DictionaryAnswer {

    private Object head;
    private Definition[] def;

    public DictionaryAnswer(String head, Definition[] def) {
        this.head = head;
        this.def = def;
    }

    public DictionaryAnswer() {
    }

    public Object getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public Definition[] getDef() {
        return def;
    }

    public void setDef(Definition[] def) {
        this.def = def;
    }
}