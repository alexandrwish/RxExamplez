package com.magenta.rx.rxa.view;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.magenta.rx.rxa.R;
import com.magenta.rx.rxa.model.record.Definition;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DefinitionListAdapter extends BaseExpandableListAdapter {

    private final List<HashMap.Entry<String, List<Definition>>> entries;
    private final Activity activity;

    public DefinitionListAdapter(List<HashMap.Entry<String, List<Definition>>> entries, Activity context) {
        this.entries = entries;
        this.activity = context;
    }

    public int getGroupCount() {
        return entries.size();
    }

    public int getChildrenCount(int groupPosition) {
        return entries.get(groupPosition).getValue().size();
    }

    public Object getGroup(int groupPosition) {
        return entries.get(groupPosition);
    }

    public Object getChild(int groupPosition, int childPosition) {
        return entries.get(groupPosition).getValue().get(childPosition);
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
        ((TextView) view.findViewById(android.R.id.text1)).setText(entries.get(groupPosition).getKey());
        return view;
    }

    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = activity.getLayoutInflater().inflate(R.layout.definition_list_item, parent, false);
            view.setTag(new ViewHolder(view));
        } else {
            view = convertView;
        }
        Definition definition = entries.get(groupPosition).getValue().get(childPosition);
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.text.setText(definition.getText());
        holder.pos.setText(definition.getPos());
        holder.def.init(new Gson().toJson(definition.getTr()));
        return view;
    }

    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    public void add(String word, List<Definition> definition) {
        entries.add(new HashMap.SimpleEntry<>(word, definition));
    }

    static class ViewHolder {

        @BindView(R.id.text)
        TextView text;
        @BindView(R.id.pos)
        TextView pos;
        @BindView(R.id.def)
        JSONView def;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}