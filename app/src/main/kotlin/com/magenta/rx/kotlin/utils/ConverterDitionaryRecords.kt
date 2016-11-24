package com.magenta.rx.kotlin.utils

import com.magenta.rx.java.RXApplication
import com.magenta.rx.java.model.converter.DictionaryConverter
import com.magenta.rx.java.model.entity.*
import com.magenta.rx.kotlin.record.*

fun convert(synonym: SynonymEntity?) = Synonym(synonym?.text)

fun convert(meaning: MeaningEntity?) = Meaning(meaning?.text)

fun convert(entity: DictionaryEntity) = DictionaryAnswer(null, Array(entity.def?.size ?: 0, { i -> convert(entity.def[i]) }))

fun convert(example: ExampleEntity?) = Example(example?.text, Array(example?.tr?.size ?: 0, { i -> DictionaryConverter.toRecord(example!!.tr[i]) }))

fun convert(definition: DefinitionEntity?) = Definition(definition?.text, definition?.pos, definition?.ts, Array(definition?.tr?.size ?: 0, { i -> convert(definition!!.tr[i]) }))

fun convert(transcription: TranscriptionEntity?) = Transcription(transcription?.text, transcription?.pos,
        Array(transcription?.syn?.size ?: 0, { i -> convert(transcription!!.syn[i]) }),
        Array(transcription?.mean?.size ?: 0, { i -> convert(transcription!!.mean[i]) }),
        Array(transcription?.ex?.size ?: 0, { i -> convert(transcription!!.ex[i]) }))

fun toEntity(word: String, answer: DictionaryAnswer): DictionaryEntity {
    val application = RXApplication.getInstance()
    val dictionaryDao = application.session.dictionaryEntityDao
    val definitionDao = application.session.definitionEntityDao
    val transcriptionDao = application.session.transcriptionEntityDao
    val exampleDao = application.session.exampleEntityDao
    val meaningDao = application.session.meaningEntityDao
    val synonymDao = application.session.synonymEntityDao
    val entity = DictionaryEntity(word)
    dictionaryDao.insert(entity)
    for (definition in answer.def) {
        val definitionEntity = DefinitionEntity(null, word, definition.text, definition.pos, definition.ts)
        definitionDao.insert(definitionEntity)
        for (transcription in definition.tr!!) {
            val transcriptionEntity = TranscriptionEntity(null, definitionEntity.id, transcription.text, transcription.pos, null)
            transcriptionDao.insert(transcriptionEntity)
            for (example in transcription.ex!!) {
                val exampleEntity = ExampleEntity(null, transcriptionEntity.id, example.text)
                exampleDao.insert(exampleEntity)
                for (tr in example.tr!!) {
                    transcriptionDao.insert(TranscriptionEntity(null, null, tr.text, tr.pos, exampleEntity.id))
                }
            }
            for (meaning in transcription.mean!!) {
                meaningDao.insert(MeaningEntity(null, transcriptionEntity.id, meaning.text))
            }
            for (synonym in transcription.syn!!) {
                synonymDao.insert(SynonymEntity(null, transcriptionEntity.id, synonym.text))
            }
        }
    }
    return entity
}