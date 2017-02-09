package com.magenta.maxunits.mobile.dlib.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.magenta.maxunits.distribution.R;
import com.magenta.maxunits.mobile.dlib.service.storage.entity.Stop;
import com.magenta.maxunits.mobile.dlib.utils.StopsComparator;

import java.util.Collections;
import java.util.List;

public class JobDetailStopsAdapter extends DistributionArrayAdapter<Stop> {

    public JobDetailStopsAdapter(Activity context, List<Stop> list) {
        super(context, R.layout.item_stop, list);
    }

    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = context.getLayoutInflater().inflate(R.layout.item_stop, null);
            viewHolder = new ViewHolder();
            viewHolder.time = (TextView) view.findViewById(R.id.time);
            viewHolder.date = (TextView) view.findViewById(R.id.date);
            viewHolder.address = (TextView) view.findViewById(R.id.address);
            viewHolder.referenceNumber = (TextView) view.findViewById(R.id.reference_number);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        Stop item = list.get(position);
        viewHolder.address.setText(item.getAddressAsString());
        viewHolder.time.setText(TIME_FORMAT.format(item.getDate()));
        viewHolder.date.setText(DATE_FORMAT.format(item.getDate()));
        viewHolder.referenceNumber.setText(item.getStopName());
        return view;
    }

    public void update(List<Stop> stops) {
        this.list.clear();
        this.list.addAll(stops);
        Collections.sort(this.list, StopsComparator.getInstance());
        notifyDataSetChanged();
    }

    private class ViewHolder {
        protected TextView time;
        protected TextView date;
        protected TextView address;
        protected TextView referenceNumber;
    }
}