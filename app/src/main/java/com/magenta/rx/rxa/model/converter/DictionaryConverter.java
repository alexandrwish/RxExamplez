package com.magenta.rx.rxa.model.converter;

import com.magenta.rx.rxa.RXApplication;
import com.magenta.rx.rxa.model.entity.DefinitionEntity;
import com.magenta.rx.rxa.model.entity.DefinitionEntityDao;
import com.magenta.rx.rxa.model.entity.DictionaryEntity;
import com.magenta.rx.rxa.model.entity.DictionaryEntityDao;
import com.magenta.rx.rxa.model.entity.ExampleEntity;
import com.magenta.rx.rxa.model.entity.ExampleEntityDao;
import com.magenta.rx.rxa.model.entity.MeaningEntity;
import com.magenta.rx.rxa.model.entity.MeaningEntityDao;
import com.magenta.rx.rxa.model.entity.SynonymEntity;
import com.magenta.rx.rxa.model.entity.SynonymEntityDao;
import com.magenta.rx.rxa.model.entity.TranscriptionEntity;
import com.magenta.rx.rxa.model.entity.TranscriptionEntityDao;
import com.magenta.rx.rxa.model.record.Definition;
import com.magenta.rx.rxa.model.record.DictionaryAnswer;
import com.magenta.rx.rxa.model.record.Example;
import com.magenta.rx.rxa.model.record.Meaning;
import com.magenta.rx.rxa.model.record.Synonym;
import com.magenta.rx.rxa.model.record.Transcription;

public class DictionaryConverter {

    public static DictionaryEntity toEntity(String word, DictionaryAnswer answer) {
        final DictionaryEntityDao dictionaryDao = RXApplication.getInstance().getSession().getDictionaryEntityDao();
        final DefinitionEntityDao definitionDao = RXApplication.getInstance().getSession().getDefinitionEntityDao();
        final TranscriptionEntityDao transcriptionDao = RXApplication.getInstance().getSession().getTranscriptionEntityDao();
        final ExampleEntityDao exampleDao = RXApplication.getInstance().getSession().getExampleEntityDao();
        final MeaningEntityDao meaningDao = RXApplication.getInstance().getSession().getMeaningEntityDao();
        final SynonymEntityDao synonymDao = RXApplication.getInstance().getSession().getSynonymEntityDao();
        DictionaryEntity entity = new DictionaryEntity(word);
        dictionaryDao.insert(entity);
        for (Definition definition : answer.getDef()) {
            DefinitionEntity definitionEntity = new DefinitionEntity(null, word, definition.getText(), definition.getPos(), definition.getTs());
            definitionDao.insert(definitionEntity);
            if (definition.getTr() != null) {
                for (Transcription transcription : definition.getTr()) {
                    TranscriptionEntity transcriptionEntity = new TranscriptionEntity(null, definitionEntity.getId(), transcription.getText(), transcription.getPos(), null);
                    transcriptionDao.insert(transcriptionEntity);
                    if (transcription.getEx() != null) {
                        for (Example example : transcription.getEx()) {
                            ExampleEntity exampleEntity = new ExampleEntity(null, transcriptionEntity.getId(), example.getText());
                            exampleDao.insert(exampleEntity);
                            if (example.getTr() != null) {
                                for (Transcription tr : example.getTr()) {
                                    transcriptionDao.insert(new TranscriptionEntity(null, null, tr.getText(), tr.getPos(), exampleEntity.getId()));
                                }
                            }
                        }
                        if (transcription.getMean() != null) {
                            for (Meaning meaning : transcription.getMean()) {
                                meaningDao.insert(new MeaningEntity(null, transcriptionEntity.getId(), meaning.getText()));
                            }
                        }
                        if (transcription.getSyn() != null) {
                            for (Synonym synonym : transcription.getSyn()) {
                                synonymDao.insert(new SynonymEntity(null, transcriptionEntity.getId(), synonym.getText()));
                            }
                        }
                    }
                }
            }
        }
        return entity;
    }

    public static DictionaryAnswer fromEntity(DictionaryEntity entity) {
        DictionaryAnswer answer = new DictionaryAnswer();
        Definition[] definitions = new Definition[entity.getDef() != null ? entity.getDef().size() : 0];
        for (int i = 0; i < definitions.length; i++) {
            definitions[i] = toRecord(entity.getDef().get(i));
        }
        answer.setDef(definitions);
        return answer;
    }

    private static Definition toRecord(DefinitionEntity definition) {
        Transcription[] transcriptions = new Transcription[definition.getTr() != null ? definition.getTr().size() : 0];
        for (int i = 0; i < transcriptions.length; i++) {
            transcriptions[i] = toRecord(definition.getTr().get(i));
        }
        return new Definition(definition.getText(), definition.getPos(), definition.getTs(), transcriptions);
    }

    private static Transcription toRecord(TranscriptionEntity transcription) {
        Synonym[] synonyms = new Synonym[transcription.getSyn() != null ? transcription.getSyn().size() : 0];
        for (int i = 0; i < synonyms.length; i++) {
            synonyms[i] = toRecord(transcription.getSyn().get(i));
        }
        Meaning[] meanings = new Meaning[transcription.getMean() != null ? transcription.getMean().size() : 0];
        for (int i = 0; i < meanings.length; i++) {
            meanings[i] = toRecord(transcription.getMean().get(i));
        }
        Example[] examples = new Example[transcription.getEx() != null ? transcription.getEx().size() : 0];
        for (int i = 0; i < examples.length; i++) {
            examples[i] = toRecord(transcription.getEx().get(i));
        }
        return new Transcription(transcription.getText(), transcription.getPos(), synonyms, meanings, examples);
    }

    private static Example toRecord(ExampleEntity example) {
        Transcription[] transcriptions = new Transcription[example.getTr() != null ? example.getTr().size() : 0];
        for (int i = 0; i < transcriptions.length; i++) {
            transcriptions[i] = toRecord(example.getTr().get(i));
        }
        return new Example(example.getText(), transcriptions);
    }

    private static Synonym toRecord(SynonymEntity synonym) {
        return new Synonym(synonym.getText());
    }

    private static Meaning toRecord(MeaningEntity meaning) {
        return new Meaning(meaning.getText());
    }
}