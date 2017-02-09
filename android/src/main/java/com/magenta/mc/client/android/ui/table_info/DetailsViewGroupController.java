package com.magenta.mc.client.android.ui.table_info;

import android.content.res.TypedArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.magenta.mc.client.android.R;
import com.magenta.mc.client.android.ui.table_info.renderers.DetailRenderer;

public class DetailsViewGroupController<T> {

    private ViewGroup viewGroup;
    private DetailRenderer[] renderers;
    private Button expandCollapseButton;
    private ImageView expandCollapseIcon;
    private boolean expanded;

    public DetailsViewGroupController(ViewGroup viewGroup, DetailRenderer[] renderers) {
        this(viewGroup, renderers, null, null);
    }

    public DetailsViewGroupController(ViewGroup viewGroup, DetailRenderer[] renderers, Button expandCollapseButton, ImageView expandCollapseIcon) {
        this.viewGroup = viewGroup;
        this.renderers = renderers;
        if (expandCollapseButton != null) {
            this.expandCollapseButton = expandCollapseButton;
            this.expandCollapseIcon = expandCollapseIcon;
            View.OnClickListener expandCollapse = new View.OnClickListener() {
                public void onClick(View v) {
                    setExpanded(!expanded);
                }
            };
            this.expandCollapseButton.setOnClickListener(expandCollapse);
            viewGroup.setOnClickListener(expandCollapse);
        }
    }

    public void fill(T source, boolean expanded) {
        for (DetailRenderer renderer : renderers) {
            View valueView = viewGroup.findViewById(renderer.getViewId());
            renderer.render(valueView, source);
            processVisibility(renderer, expanded);
        }
    }

    public void fill(T source) {
        this.fill(source, expandCollapseButton == null || expanded);
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
        int background;
        if (expanded) {
            TypedArray ta = viewGroup.getContext().obtainStyledAttributes(new int[]{R.attr.defaultIconUpDrawable});
            background = ta.getResourceId(0, R.drawable.mc_button_collapse);
        } else {
            TypedArray ta = viewGroup.getContext().obtainStyledAttributes(new int[]{R.attr.defaultIconDownDrawable});
            background = ta.getResourceId(0, R.drawable.mc_button_expand);
        }
        expandCollapseIcon.setBackgroundResource(background);
        changeView(viewGroup, expanded);
    }

    private void processVisibility(DetailRenderer renderer, boolean expanded) {
        int visibility = View.VISIBLE;
        if (!renderer.isRendered() || !expanded && renderer.isVisibleOnlyInExpandedView()) {
            visibility = View.GONE;
        }
        View valueView = viewGroup.findViewById(renderer.getViewId());
        valueView.setVisibility(visibility);
        for (int id : renderer.getHidIds()) {
            viewGroup.findViewById(id).setVisibility(visibility);
        }
    }

    private void changeView(View view, boolean expanded) {
        for (DetailRenderer renderer : renderers) {
            processVisibility(renderer, expanded);
        }
    }
}