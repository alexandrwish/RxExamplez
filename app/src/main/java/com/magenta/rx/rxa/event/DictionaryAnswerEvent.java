package com.magenta.rx.rxa.event;

import com.magenta.rx.rxa.model.record.Definition;

import java.util.List;

public class DictionaryAnswerEvent {

    private final List<Definition> definitions;
    private final String word;

    public DictionaryAnswerEvent(String word, List<Definition> definitionEntities) {
        this.definitions = definitionEntities;
        this.word = word;
    }

    public List<Definition> getDefinitions() {
        return definitions;
    }

    public String getWord() {
        return word;
    }
}