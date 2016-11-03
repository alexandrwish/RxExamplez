package com.magenta.maxunits.mobile.utils;

/**
 * @author Petr Popov
 *         Created: 28.12.11 12:19
 */
public class HtmlBuffer {

    private StringBuilder sb = new StringBuilder();

    public HtmlBuffer append(String... strings) {
        for (String str : strings) {
            if (str == null || "".equals(str)) {
                return this;
            }
        }
        for (String str : strings) {
            sb.append(str);
        }
        return this;
    }

    public HtmlBuffer paramLn(final String name, final String... values) {
        append("<b>", name, ":</b> ");
        append(values);
        append("<br>");
        return this;
    }

    public String toString() {
        return sb.toString();
    }

}
