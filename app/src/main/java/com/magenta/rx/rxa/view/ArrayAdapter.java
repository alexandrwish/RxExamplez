package com.magenta.rx.rxa.view;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.magenta.rx.rxa.model.record.Texted;

import java.util.List;

public class ArrayAdapter<T extends Texted> extends BaseAdapter {

    private final List<T> entities;
    private final Activity activity;

    public ArrayAdapter(Activity activity, List<T> entities) {
        this.entities = entities;
        this.activity = activity;
    }

    public int getCount() {
        return entities.size();
    }

    public Object getItem(int position) {
        return entities.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView == null ? activity.getLayoutInflater().inflate(android.R.layout.simple_list_item_1, parent, false) : convertView;
        ((TextView) view.findViewById(android.R.id.text1)).setText(entities.get(position).getText());
        return view;
    }
}