package com.magenta.mc.client.android.ui.table_info.renderers;

import android.widget.TextView;

public abstract class TextRenderer<V extends TextView, S> extends DetailRenderer<V, S> {

    protected boolean rendered;

    public TextRenderer(int viewId, int[] hide, boolean isVisibleOnlyInExpandedMode) {
        super(viewId, hide, isVisibleOnlyInExpandedMode);
    }

    public void render(V view, S source) {
        String text = extractText(source);
        if (text != null && !"".equals(text)) {
            view.setText(text);
            rendered = true;
        } else {
            rendered = false;
        }
    }

    public boolean isRendered() {
        return rendered;
    }

    public abstract String extractText(S source);
}