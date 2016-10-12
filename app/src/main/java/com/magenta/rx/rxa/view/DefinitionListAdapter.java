package com.magenta.rx.rxa.view;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.magenta.rx.rxa.R;
import com.magenta.rx.rxa.model.entity.DefinitionEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DefinitionListAdapter extends BaseExpandableListAdapter {

    private final List<HashMap.Entry<String, List<DefinitionEntity>>> entries;
    private final Activity activity;

    public DefinitionListAdapter(List<HashMap.Entry<String, List<DefinitionEntity>>> entries, Activity context) {
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
        DefinitionEntity definitionEntity = entries.get(groupPosition).getValue().get(childPosition);
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.text.setText(definitionEntity.getText());
        holder.pos.setText(definitionEntity.getPos());
        holder.def.setAdapter(new TranscriptionListAdapter(definitionEntity.getTr(), activity));
        return view;
    }

    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    public void add(String word, List<DefinitionEntity> definitionEntities) {
        entries.add(new HashMap.SimpleEntry<>(word, definitionEntities));
    }

    public void addAll(Map<String, List<DefinitionEntity>> map) {
        map.clear();
        for (Map.Entry<String, List<DefinitionEntity>> entry : map.entrySet()) {
            this.entries.add(entry);
        }
    }

    static class ViewHolder {

        @BindView(R.id.text)
        TextView text;
        @BindView(R.id.pos)
        TextView pos;
        @BindView(R.id.def)
        ListView def;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}