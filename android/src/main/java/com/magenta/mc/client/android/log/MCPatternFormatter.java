package com.magenta.mc.client.android.log;

import net.sf.microlog.core.Formatter;
import net.sf.microlog.core.Level;
import net.sf.microlog.core.format.command.CategoryFormatCommand;
import net.sf.microlog.core.format.command.ClientIdFormatCommand;
import net.sf.microlog.core.format.command.DateFormatCommand;
import net.sf.microlog.core.format.command.FormatCommandInterface;
import net.sf.microlog.core.format.command.MessageFormatCommand;
import net.sf.microlog.core.format.command.NoFormatCommand;
import net.sf.microlog.core.format.command.PriorityFormatCommand;
import net.sf.microlog.core.format.command.ThreadFormatCommand;
import net.sf.microlog.core.format.command.TimeFormatCommand;

import java.util.Vector;

public class MCPatternFormatter implements Formatter {

    public static final String PATTERN_PROPERTY = "pattern";
    public static final String DEFAULT_CONVERSION_PATTERN = "%r %c{1} [%P] %m %T";
    public static final char CLIENT_ID_CONVERSION_CHAR = 'i';
    public static final char CATEGORY_CONVERSION_CHAR = 'c';
    public static final char DATE_CONVERSION_CHAR = 'd';
    public static final char MESSAGE_CONVERSION_CHAR = 'm';
    public static final char PRIORITY_CONVERSION_CHAR = 'P';
    public static final char RELATIVE_TIME_CONVERSION_CHAR = 'r';
    public static final char THREAD_CONVERSION_CHAR = 't';
    public static final char THROWABLE_CONVERSION_CHAR = 'T';
    public static final char PERCENT_CONVERSION_CHAR = '%';
    private static final String[] PROPERTY_NAMES = new String[]{"pattern"};
    private String pattern = "%r %c{1} [%P] %m %T";
    private FormatCommandInterface[] commandArray;
    private boolean patternParsed = false;

    public MCPatternFormatter() {
    }

    public String format(String clientID, String name, long time, Level level, Object message, Throwable t) {
        if (!this.patternParsed && this.pattern != null) {
            this.parsePattern(this.pattern);
        }
        StringBuilder formattedStringBuffer = new StringBuilder(64);
        if (this.commandArray != null) {
            for (FormatCommandInterface currentConverter : this.commandArray) {
                if (currentConverter != null) {
                    formattedStringBuffer.append(currentConverter.execute(clientID, name, time, level, message, t));
                }
            }
        }
        return formattedStringBuffer.toString();
    }

    public String getPattern() {
        return this.pattern;
    }

    private void setPattern(String pattern) throws IllegalArgumentException {
        if (pattern == null) {
            throw new IllegalArgumentException("The pattern must not be null.");
        } else {
            this.pattern = pattern;
            this.parsePattern(this.pattern);
        }
    }

    private void parsePattern(String pattern) {
        int currentIndex = 0;
        int patternLength = pattern.length();
        Vector converterVector = new Vector(20);
        while (currentIndex < patternLength) {
            char currentChar = pattern.charAt(currentIndex);
            String specifier;
            if (currentChar == 37) {
                ++currentIndex;
                currentChar = pattern.charAt(currentIndex);
                int specifierLength;
                switch (currentChar) {
                    case '%':
                        NoFormatCommand noFormatCommand = new NoFormatCommand();
                        noFormatCommand.init("%");
                        converterVector.addElement(noFormatCommand);
                        break;
                    case 'P':
                        converterVector.addElement(new PriorityFormatCommand());
                        break;
                    case 'T':
                        converterVector.addElement(new MCThrowableFormatCommand());
                        break;
                    case 'c':
                        CategoryFormatCommand categoryFormatCommand = new CategoryFormatCommand();
                        specifier = this.extraxtSpecifier(pattern, currentIndex);
                        specifierLength = specifier.length();
                        if (specifierLength > 0) {
                            categoryFormatCommand.init(specifier);
                            currentIndex = currentIndex + specifierLength + 2;
                        }
                        converterVector.addElement(categoryFormatCommand);
                        break;
                    case 'd':
                        DateFormatCommand formatCommand = new DateFormatCommand();
                        specifier = this.extraxtSpecifier(pattern, currentIndex);
                        specifierLength = specifier.length();
                        if (specifierLength > 0) {
                            formatCommand.init(specifier);
                            currentIndex = currentIndex + specifierLength + 2;
                        }
                        converterVector.addElement(formatCommand);
                        break;
                    case 'i':
                        converterVector.addElement(new ClientIdFormatCommand());
                        break;
                    case 'm':
                        converterVector.addElement(new MessageFormatCommand());
                        break;
                    case 'r':
                        converterVector.addElement(new TimeFormatCommand());
                        break;
                    case 't':
                        converterVector.addElement(new ThreadFormatCommand());
                        break;
                    default:
                        System.err.println("Unrecognized conversion character " + currentChar);
                }
                ++currentIndex;
            } else {
                int percentIndex = pattern.indexOf("%", currentIndex);
                if (percentIndex != -1) {
                    specifier = pattern.substring(currentIndex, percentIndex);
                } else {
                    specifier = pattern.substring(currentIndex, patternLength);
                }
                NoFormatCommand noFormatCommand = new NoFormatCommand();
                noFormatCommand.init(specifier);
                converterVector.addElement(noFormatCommand);
                currentIndex += specifier.length();
            }
        }
        this.commandArray = new FormatCommandInterface[converterVector.size()];
        converterVector.copyInto(this.commandArray);
        this.patternParsed = true;
    }

    private String extraxtSpecifier(String pattern, int index) {
        String specifier = "";
        int beginIndex = pattern.indexOf(123, index);
        int endIndex = pattern.indexOf(125, index);
        if (beginIndex > 0 && endIndex > beginIndex) {
            specifier = pattern.substring(beginIndex + 1, endIndex);
        }
        return specifier;
    }

    public String[] getPropertyNames() {
        return PROPERTY_NAMES;
    }

    public void setProperty(String name, String value) {
        if (name.equals("pattern")) {
            this.setPattern(value);
        }
    }
}