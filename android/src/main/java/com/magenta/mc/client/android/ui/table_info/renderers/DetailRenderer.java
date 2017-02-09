package com.magenta.mc.client.android.ui.table_info.renderers;

import android.view.View;

public abstract class DetailRenderer<V extends View, S> {

    protected int[] hide;
    private boolean visibleOnlyInExpandedView;
    private int viewId;

    public DetailRenderer(int appropriateViewId, int[] hide) {
        this(appropriateViewId, hide, false);
    }

    public DetailRenderer(int viewId, int[] hide, boolean visibleOnlyInExpandedView) {
        this.viewId = viewId;
        this.hide = hide;
        this.visibleOnlyInExpandedView = visibleOnlyInExpandedView;
    }

    public int getViewId() {
        return viewId;
    }

    public int[] getHidIds() {
        return hide;
    }

    public boolean isVisibleOnlyInExpandedView() {
        return visibleOnlyInExpandedView;
    }

    /**
     * Render appropriate property to view
     *
     * @param view   target for rendering
     * @param source for rendering
     * @return if appropriate property is not empty
     */
    public abstract void render(V view, S source);

    /**
     * @return true if source has non empty appropriate value
     */
    public abstract boolean isRendered();
}