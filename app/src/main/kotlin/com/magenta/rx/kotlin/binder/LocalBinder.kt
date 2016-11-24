package com.magenta.rx.kotlin.binder

import android.app.Service
import android.os.Binder

class LocalBinder<out T : Service>(val service: T) : Binder()