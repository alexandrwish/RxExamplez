package com.magenta.mc.client.android.mc.util;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created 30.07.2010
 *
 * @author Konstantin Pestrikov
 */
public class StrUtil {
    public static String[] split(String source, String separator) {
        final StringTokenizer tokenizer = new StringTokenizer(source, separator);
        if (!tokenizer.hasMoreTokens()) {
            return new String[]{source};
        }
        final List result = new ArrayList();
        while (tokenizer.hasMoreTokens()) {
            result.add(tokenizer.nextToken());
        }
        return (String[]) result.toArray(new String[result.size()]);
    }

    public static String rmSpaces(String source) {
        final StringTokenizer tokenizer = new StringTokenizer(source, " ");
        if (!tokenizer.hasMoreTokens()) {
            return source;
        }
        String result = "";
        while (tokenizer.hasMoreTokens()) {
            result += tokenizer.nextToken();
        }
        return result;
    }

    public static String replace(String string, String oldString, String newString) {
        if (string == null) {
            return null;
        }
        int i = 0;
        // Make sure that oldString appears at least once before doing any processing.
        if ((i = string.indexOf(oldString, i)) >= 0) {
            // Use char []'s, as they are more efficient to deal with.
            char[] string2 = string.toCharArray();
            char[] newString2 = newString.toCharArray();
            int oLength = oldString.length();
            StringBuffer buf = new StringBuffer(string2.length);
            buf.append(string2, 0, i).append(newString2);
            i += oLength;
            int j = i;
            // Replace all remaining instances of oldString with newString.
            while ((i = string.indexOf(oldString, i)) > 0) {
                buf.append(string2, j, i - j).append(newString2);
                i += oLength;
                j = i;
            }
            buf.append(string2, j, string2.length - j);
            return buf.toString();
        }
        return string;
    }

    public static void main(String[] args) {
        System.out.println(replace("lskdfjdfklsj/ldfksjdfklsj/lsfd", "/", "_fuck_"));
    }
}
