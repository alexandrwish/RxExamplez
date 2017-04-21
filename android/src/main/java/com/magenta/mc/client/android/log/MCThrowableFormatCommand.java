package com.magenta.mc.client.android.log;

import net.sf.microlog.core.Level;
import net.sf.microlog.core.format.command.FormatCommandInterface;

public class MCThrowableFormatCommand implements FormatCommandInterface {

    public MCThrowableFormatCommand() {
    }

    public void init(String initString) {
    }

    public String execute(String clientID, String name, long time, Level level, Object message, Throwable throwable) {
        String throwableMessage = "";
        if (throwable != null) {
            throwableMessage = this.throwableToString(throwable);
        }
        return throwableMessage;
    }

    private String throwableToString(Throwable t) {
        StringBuilder sb = new StringBuilder();
        sb.append(t.getMessage());
        sb.append('\n');
        for (int i = 0; i < t.getStackTrace().length; ++i) {
            sb.append('\t');
            sb.append(t.getStackTrace()[i]);
            sb.append('\n');
        }
        return sb.toString();
    }
}