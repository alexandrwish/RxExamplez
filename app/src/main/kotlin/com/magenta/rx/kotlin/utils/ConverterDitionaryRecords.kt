package com.magenta.rx.kotlin.utils

import com.magenta.rx.java.model.converter.DictionaryConverter
import com.magenta.rx.java.model.entity.*
import com.magenta.rx.kotlin.record.*

fun convert(synonym: SynonymEntity?) = Synonym(synonym?.text)

fun convert(meaning: MeaningEntity?) = Meaning(meaning?.text)

fun convert(definition: DefinitionEntity?) = Definition(definition?.text, definition?.pos, definition?.ts, Array(definition?.tr?.size ?: 0, { i -> convert(definition!!.tr[i]) }))

fun convert(example: ExampleEntity?) = Example(example?.text, Array(example?.tr?.size ?: 0, { i -> DictionaryConverter.toRecord(example!!.tr[i]) }))

fun convert(transcription: TranscriptionEntity?) = Transcription(transcription?.text, transcription?.pos,
        Array(transcription?.syn?.size ?: 0, { i -> convert(transcription!!.syn[i]) }),
        Array(transcription?.mean?.size ?: 0, { i -> convert(transcription!!.mean[i]) }),
        Array(transcription?.ex?.size ?: 0, { i -> convert(transcription!!.ex[i]) }))