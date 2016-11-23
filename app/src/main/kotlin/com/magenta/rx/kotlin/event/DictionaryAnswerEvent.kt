package com.magenta.rx.kotlin.event

import com.magenta.rx.kotlin.record.Definition

class DictionaryAnswerEvent(val word: String, val definitions: List<Definition>)