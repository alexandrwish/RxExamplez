package com.magenta.mc.client.android.integrator;

public final class IntentResult {

    private final String contents;

    IntentResult() {
        this(null);
    }

    IntentResult(String contents) {
        this.contents = contents;
    }

    /**
     * @return raw content of barcode
     */
    public String getContents() {
        return contents;
    }
}