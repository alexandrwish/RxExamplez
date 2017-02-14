package com.magenta.mc.client.android.mc.log;

import net.sf.microlog.core.Level;
import net.sf.microlog.core.appender.ConsoleAppender;

/**
 * Author: Petr Popov
 * Created: 15.02.2011 16:05:38
 * <p/>
 * Copyright (c) 1999-2010 Magenta Corporation Ltd. All Rights Reserved.
 * Magenta Technology proprietary and confidential.
 * Use is subject to license terms.
 * <p/>
 */
public class MCConsoleAppender extends ConsoleAppender {
    public void doLog(String clientID, String name, long time, Level level,
                      Object message, Throwable throwable) {
        if (logOpen && formatter != null) {
            MCLoggerFactory.getInstance().getSystemOut().println(formatter.format(clientID,
                    name,
                    time,
                    level,
                    message + " server time: " + ServerTime.get(),
                    throwable));

        } else if (formatter == null) {
            MCLoggerFactory.getInstance().getSystemErr().println("Please set a formatter.");
        }
    }
}
