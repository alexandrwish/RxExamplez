package com.magenta.mc.client.android.mc.log;

import net.sf.microlog.core.Level;
import net.sf.microlog.core.format.command.FormatCommandInterface;

/**
 * Author: Petr Popov
 * Created: 15.02.2011 16:09:39
 * <p/>
 * Copyright (c) 1999-2010 Magenta Corporation Ltd. All Rights Reserved.
 * Magenta Technology proprietary and confidential.
 * Use is subject to license terms.
 * <p/>
 */
public class MCThrowableFormatCommand implements FormatCommandInterface {

    /**
     * @see net.sf.microlog.core.format.command.FormatCommandInterface#init(String)
     */
    public void init(String initString) {
        // Do nothing.
    }

    /**
     * Set the log data.
     *
     * @see FormatCommandInterface#execute(String, String, long, net.sf.microlog.core.Level, Object, Throwable)
     */
    public String execute(String clientID, String name, long time, Level level, Object message, Throwable throwable) {

        String throwableMessage = "";
        if (throwable != null) {
            throwableMessage = throwableToString(throwable);
        }

        return throwableMessage;
    }

    private String throwableToString(Throwable t) {
        StringBuffer sb = new StringBuffer();
        sb.append(t.getMessage());
        sb.append('\n');
        for (int i = 0; i < t.getStackTrace().length; i++) {
            sb.append('\t');
            sb.append(t.getStackTrace()[i]);
            sb.append('\n');
        }
        return sb.toString();
    }

}
