package com.magenta.rx.kotlin.event

import com.magenta.rx.kotlin.record.RowResult

class CalcEvent(val result: RowResult, val report: Boolean, val name: String)