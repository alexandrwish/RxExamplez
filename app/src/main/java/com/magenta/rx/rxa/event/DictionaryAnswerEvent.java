package com.magenta.rx.rxa.event;

import com.magenta.rx.rxa.model.entity.DefinitionEntity;

import java.util.List;

public class DictionaryAnswerEvent {

    private final List<DefinitionEntity> entities;
    private final String word;

    public DictionaryAnswerEvent(String word, List<DefinitionEntity> definitionEntities) {
        this.entities = definitionEntities;
        this.word = word;
    }

    public List<DefinitionEntity> getEntities() {
        return entities;
    }

    public String getWord() {
        return word;
    }
}