package com.magenta.mc.client.android.log;

import android.util.Log;

import com.magenta.mc.client.log.ServerTime;

import net.sf.microlog.core.Level;
import net.sf.microlog.core.appender.ConsoleAppender;

public class AndroidConsoleAppender extends ConsoleAppender {

    public void doLog(String clientID, String name, long time, Level level, Object message, Throwable throwable) {
        name = "mc." + name.substring(name.lastIndexOf(".") + 1, name.length());
        String strMessage = message.toString() + " server time(" + ServerTime.get() + ")";
        switch (level.toInt()) {
            case Level.ERROR_INT:
                Log.e(name, strMessage, throwable);
                break;
            case Level.WARN_INT:
                Log.w(name, strMessage, throwable);
                break;
            case Level.INFO_INT:
                Log.i(name, strMessage, throwable);
                break;
            case Level.DEBUG_INT:
                Log.d(name, strMessage, throwable);
                break;
            case Level.TRACE_INT:
                Log.v(name, strMessage, throwable);
                break;
            default:
                Log.w(name, strMessage, throwable);
                break;
        }
    }
}