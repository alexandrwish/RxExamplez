package com.magenta.rx.rxa.view;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.magenta.rx.rxa.R;
import com.magenta.rx.rxa.model.entity.ExampleEntity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ExampleListAdapter extends BaseAdapter {

    private final List<ExampleEntity> entities;
    private final Activity activity;

    public ExampleListAdapter(Activity activity, List<ExampleEntity> entities) {
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
        View view;
        if (convertView == null) {
            view = activity.getLayoutInflater().inflate(R.layout.example_list_item, parent, false);
            view.setTag(new ViewHolder(view));
        } else {
            view = convertView;
        }
        ExampleEntity entity = entities.get(position);
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.text.setText(entity.getText());
        holder.ex.setAdapter(new ArrayAdapter<>(activity, entity.getTr()));
        return view;
    }

    static class ViewHolder {

        @BindView(R.id.text)
        TextView text;
        @BindView(R.id.ex)
        ListView ex;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}