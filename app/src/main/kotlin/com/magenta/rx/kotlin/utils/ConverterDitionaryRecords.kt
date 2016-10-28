package com.magenta.rx.kotlin.utils

import com.magenta.rx.java.model.converter.DictionaryConverter
import com.magenta.rx.java.model.entity.*
import com.magenta.rx.java.model.record.*

fun convert(synonym: SynonymEntity) = Synonym(synonym.text)

fun convert(meaning: MeaningEntity) = Meaning(meaning.text)

fun convert(definition: DefinitionEntity) = Definition(definition.text, definition.pos, definition.ts, if (definition.tr == null) Array(0, { i -> Transcription() }) else Array(definition.tr.size, { i -> convert(definition.tr[i]) }))

fun convert(example: ExampleEntity) = Example(example.text, if (example.tr == null) Array(0, { i -> Transcription() }) else Array(example.tr.size, { i -> DictionaryConverter.toRecord(example.tr[i]) }))

fun convert(transcription: TranscriptionEntity) = Transcription(transcription.text, transcription.pos,
        if (transcription.syn == null) Array(0, { i -> Synonym() }) else Array(transcription.syn.size, { i -> convert(transcription.syn[i]) }),
        if (transcription.mean == null) Array(0, { i -> Meaning() }) else Array(transcription.mean.size, { i -> convert(transcription.mean[i]) }),
        if (transcription.ex == null) Array(0, { i -> Example() }) else Array(transcription.ex.size, { i -> convert(transcription.ex[i]) }))