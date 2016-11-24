package com.magenta.rx.java.model.converter;

import com.magenta.rx.java.model.entity.TranscriptionEntity;
import com.magenta.rx.kotlin.record.Transcription;
import com.magenta.rx.kotlin.utils.ConverterDitionaryRecordsKt;

@Deprecated
public class DictionaryConverter {

    //hack for recursive call
    public static Transcription toRecord(TranscriptionEntity transcription) {
        return ConverterDitionaryRecordsKt.convert(transcription);
    }
}