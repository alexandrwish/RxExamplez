package com.magenta.rx.java.view;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.google.gson.Gson;
import com.magenta.rx.java.R;
import com.magenta.rx.java.activity.DictionaryActivity;
import com.magenta.rx.kotlin.record.Definition;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DefinitionListAdapter extends BaseExpandableListAdapter {

    private final LinkedHashMap<String, List<Definition>> entries;
    private final Activity activity;

    @Inject
    public DefinitionListAdapter(DictionaryActivity context) {
        this.entries = new LinkedHashMap<>();
        this.activity = context;
    }

    public int getGroupCount() {
        return entries.size();
    }

    public int getChildrenCount(int groupPosition) {
        return entries.get(getKeyByIndex(groupPosition)).size();
    }

    public Object getGroup(int groupPosition) {
        return entries.get(getKeyByIndex(groupPosition));
    }

    public Object getChild(int groupPosition, int childPosition) {
        return entries.get(getKeyByIndex(groupPosition)).get(childPosition);
    }

    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    public boolean hasStableIds() {
        return false;
    }

    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View view = convertView == null ? activity.getLayoutInflater().inflate(android.R.layout.simple_expandable_list_item_1, parent, false) : convertView;
        ((TextView) view.findViewById(android.R.id.text1)).setText(getKeyByIndex(groupPosition));
        return view;
    }

    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = activity.getLayoutInflater().inflate(R.layout.list_item_definition, parent, false);
            view.setTag(new ViewHolder(view));
        } else {
            view = convertView;
        }
        Definition definition = entries.get(getKeyByIndex(groupPosition)).get(childPosition);
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.text.setText(definition.getText());
        holder.pos.setText(definition.getPos());
        holder.ts.setText(definition.getTs());
        holder.def.init(new Gson().toJson(definition.getTr()));
        return view;
    }

    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    public void add(String word, List<Definition> definition) {
        entries.remove(word);
        entries.put(word, definition);
    }

    private String getKeyByIndex(int index) {
        Iterator<String> iterator = entries.keySet().iterator();
        while (iterator.hasNext()) {
            if (index-- > 0) {
                iterator.next();
            } else {
                return iterator.next();
            }
        }
        return null;
    }

    static class ViewHolder {

        @BindView(R.id.text)
        TextView text;
        @BindView(R.id.pos)
        TextView pos;
        @BindView(R.id.ts)
        TextView ts;
        @BindView(R.id.def)
        JSONView def;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}