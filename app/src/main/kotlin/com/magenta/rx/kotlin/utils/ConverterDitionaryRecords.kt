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
    answer.def.forEach { definition ->
        val definitionEntity = DefinitionEntity(null, word, definition.text, definition.pos, definition.ts)
        definitionDao.insert(definitionEntity)
        definition.tr?.forEach { transcription ->
            val transcriptionEntity = TranscriptionEntity(null, definitionEntity.id, transcription.text, transcription.pos, null)
            transcriptionDao.insert(transcriptionEntity)
            transcription.ex?.forEach { example ->
                val exampleEntity = ExampleEntity(null, transcriptionEntity.id, example.text)
                exampleDao.insert(exampleEntity)
                example.tr?.forEach { transcriptionDao.insert(TranscriptionEntity(null, null, it.text, it.pos, exampleEntity.id)) }
            }
            transcription.mean?.forEach { meaningDao.insert(MeaningEntity(null, transcriptionEntity.id, it.text)) }
            transcription.mean?.forEach { synonymDao.insert(SynonymEntity(null, transcriptionEntity.id, it.text)) }
        }
    }
    return entity
}