package com.magenta.rx.rxa.view;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.magenta.rx.rxa.R;
import com.magenta.rx.rxa.model.entity.TranscriptionEntity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TranscriptionListAdapter extends BaseAdapter {

    private final List<TranscriptionEntity> entities;
    private final Activity activity;

    public TranscriptionListAdapter(List<TranscriptionEntity> entities, Activity activity) {
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
            view = activity.getLayoutInflater().inflate(R.layout.transcription_list_item, parent, false);
            view.setTag(new ViewHolder(view));
        } else {
            view = convertView;
        }
        TranscriptionEntity entity = entities.get(position);
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.text.setText(entity.getText());
        holder.pos.setText(entity.getPos());
        holder.sun.setAdapter(new ArrayAdapter<>(activity, entity.getSyn()));
        holder.mean.setAdapter(new ArrayAdapter<>(activity, entity.getMean()));
        holder.ex.setAdapter(new ExampleListAdapter(activity, entity.getEx()));
        return view;
    }

    static class ViewHolder {

        @BindView(R.id.text)
        TextView text;
        @BindView(R.id.pos)
        TextView pos;
        @BindView(R.id.sun)
        ListView sun;
        @BindView(R.id.mean)
        ListView mean;
        @BindView(R.id.ex)
        ListView ex;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}