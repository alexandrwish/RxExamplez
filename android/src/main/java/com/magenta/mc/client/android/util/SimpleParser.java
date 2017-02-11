package com.magenta.mc.client.android.util;

public class SimpleParser {

    private String text;
    private char currentChar;
    private int currentPos;
    private Token token;

    public SimpleParser(String text) {
        this.text = text.trim();
        reset();
    }

    public void reset() {
        currentPos = 0;
        currentChar = 0;
        token = null;
        nextChar();
    }

    private char nextChar() {
        if (currentPos < text.length()) {
            currentChar = text.charAt(currentPos++);
        } else {
            currentChar = 0;
        }
        return currentChar;
    }

    private char prevChar() {
        if (currentPos > 1) {
            currentChar = text.charAt(currentPos--);
        } else {
            currentChar = 0;
        }
        return currentChar;
    }

    private boolean isIDChar(char c) {
        return Character.isLetterOrDigit(c) || c == '-' || c == '_';
    }

    private boolean isStringChar(char c) {
        return c == '"';
    }

    private boolean isDelimiterChar(char c) {
        return c == '=' || c == ';';
    }

    private Token parseID() {
        final StringBuilder sb = new StringBuilder();
        while (isIDChar(currentChar)) {
            sb.append(currentChar);
            nextChar();
        }
        return new Token(Token.ID, sb.toString());
    }

    private Token parseString() {
        nextChar();
        final StringBuilder sb = new StringBuilder();
        while (hasNext() && !isStringChar(currentChar)) {
            // mask
            if (currentChar == '\\') {
                nextChar();
                if (isStringChar(currentChar)) {
                    sb.append('"');
                } else {
                    sb.append('\'');
                    sb.append(currentChar);
                }
            } else {
                sb.append(currentChar);
            }
            nextChar();
        }
        nextChar();
        return new Token(Token.STRING, sb.toString());
    }

    private Token parseDelimiter() {
        final Token token = new Token(Token.DELIMITER, currentChar);
        nextChar();
        return token;
    }

    public Token nextToken() {
        while (hasNext()) {
            if (isIDChar(currentChar)) {
                return token = parseID();
            }
            if (isStringChar(currentChar)) {
                return token = parseString();
            }
            if (isDelimiterChar(currentChar)) {
                return token = parseDelimiter();
            }
            nextChar();
        }
        return new Token(Token.END);
    }

    public boolean hasNext() {
        return currentChar != 0;
    }

    public Token getToken() {
        return token;
    }

    public static class Token {

        public static final int END = -1;
        public static final int DELIMITER = 0;
        public static final int STRING = 1;
        public static final int ID = 2;

        private String value;
        private char valueChar;
        private int type;

        public Token(int type, String value) {
            this.type = type;
            this.value = value;
        }

        public Token(int type, char value) {
            this.type = type;
            this.valueChar = value;
        }

        public Token(int type) {
            this.type = type;
        }

        public String getValue() {
            return value;
        }

        public int getType() {
            return type;
        }

        public char getValueChar() {
            return valueChar;
        }

        public boolean isString() {
            return STRING == type;
        }

        public boolean isDelimiter() {
            return DELIMITER == type;
        }

        public boolean isId() {
            return ID == type;
        }

        public boolean isEnd() {
            return END == type;
        }
    }
}