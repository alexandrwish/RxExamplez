package com.magenta.rx.java.model.converter;

import com.magenta.rx.java.RXApplication;
import com.magenta.rx.java.model.entity.DefinitionEntity;
import com.magenta.rx.java.model.entity.DefinitionEntityDao;
import com.magenta.rx.java.model.entity.DictionaryEntity;
import com.magenta.rx.java.model.entity.DictionaryEntityDao;
import com.magenta.rx.java.model.entity.ExampleEntity;
import com.magenta.rx.java.model.entity.ExampleEntityDao;
import com.magenta.rx.java.model.entity.MeaningEntity;
import com.magenta.rx.java.model.entity.MeaningEntityDao;
import com.magenta.rx.java.model.entity.SynonymEntity;
import com.magenta.rx.java.model.entity.SynonymEntityDao;
import com.magenta.rx.java.model.entity.TranscriptionEntity;
import com.magenta.rx.java.model.entity.TranscriptionEntityDao;
import com.magenta.rx.kotlin.record.Definition;
import com.magenta.rx.kotlin.record.DictionaryAnswer;
import com.magenta.rx.kotlin.record.Example;
import com.magenta.rx.kotlin.record.Meaning;
import com.magenta.rx.kotlin.record.Synonym;
import com.magenta.rx.kotlin.record.Transcription;
import com.magenta.rx.kotlin.utils.ConverterDitionaryRecordsKt;

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
        Definition[] definitions = new Definition[entity.getDef() != null ? entity.getDef().size() : 0];
        for (int i = 0; i < definitions.length; i++) {
            definitions[i] = ConverterDitionaryRecordsKt.convert(entity.getDef().get(i));
        }
        return new DictionaryAnswer(null, definitions);
    }

    //hack for recursive call
    public static Transcription toRecord(TranscriptionEntity transcription) {
        return ConverterDitionaryRecordsKt.convert(transcription);
    }
}