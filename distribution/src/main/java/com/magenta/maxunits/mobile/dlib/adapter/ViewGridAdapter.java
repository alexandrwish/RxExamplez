package com.magenta.maxunits.mobile.dlib.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

public class ViewGridAdapter<T extends View> extends BaseAdapter {

    List<T> buttons;

    public ViewGridAdapter(List<T> buttons) {
        super();
        this.buttons = buttons;
    }

    public int getCount() {
        return buttons.size();
    }

    public T getItem(int position) {
        return buttons.get(position);
    }

    public long getItemId(int position) {
        return getItem(position).getId();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        return getItem(position);
    }
}