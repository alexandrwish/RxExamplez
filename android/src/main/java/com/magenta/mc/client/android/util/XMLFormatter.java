package com.magenta.mc.client.android.util;

/**
 * Created by IntelliJ IDEA.
 * User: popov
 * Date: 19.08.2010
 * Time: 10:42:15
 * To change this template use File | Settings | File Templates.
 */
public class XMLFormatter {
    public static final int TAB_WIDTH = 4;

    private static String getTab(int length) {
        String result = "";
        for (int i = 0; i < length; i++) {
            result += " ";
        }
        return result;
    }

    public static String format(String input) {
        StringBuffer result = new StringBuffer();
        int spaceLength = 0;
        boolean isVSpaceAppended = false;
        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) == '<') {
                if (!isVSpaceAppended && i != 0 && input.charAt(i - 1) == '>') {
                    result.append("\n");
                }
                if (input.charAt(i + 1) != '/') {
                    spaceLength++;
                    result.append(getTab((spaceLength - 1) * TAB_WIDTH));
                } else {
                    if (i != 0 && input.charAt(i - 1) == '>')
                        result.append(getTab((spaceLength - 1) * TAB_WIDTH));
                    spaceLength--;
                }
                result.append("<");
            } else if (input.charAt(i) == '>') {
                result.append(">");
                if (input.length() != i + 1 && input.charAt(i + 1) == '<') {
                    result.append("\n");
                    isVSpaceAppended = true;
                }
            } else {
                isVSpaceAppended = false;
                result.append(input.charAt(i));
            }
        }
        return result.toString();
    }
}
