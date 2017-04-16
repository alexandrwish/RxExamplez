package com.magenta.mc.client.android.ui.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.magenta.mc.client.android.R;
import com.magenta.mc.client.android.entity.AbstractStop;
import com.magenta.mc.client.android.entity.Stop;
import com.magenta.mc.client.android.ui.map.MapAddress;
import com.magenta.mc.client.android.ui.view.TimeWindowView;
import com.magenta.mc.client.android.util.DateUtils;
import com.magenta.mc.client.android.util.DistributionUtils;
import com.magenta.mc.client.android.util.PhoneUtils;
import com.magenta.mc.client.android.util.StopsComparator;
import com.magenta.mc.client.android.util.StringUtils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class StopsAdapter extends DistributionArrayAdapter<Stop> {

    private List<Stop> jobStops;

    public StopsAdapter(Activity context, List<Stop> list) {
        super(context, R.layout.item_inactive_stop, list);
        this.jobStops = list.get(0).getParentJob().getStops();
        sort();
    }

    public void update(final List<Stop> stops) {
        super.changeList(stops);
        this.jobStops = list.isEmpty() ? new LinkedList<Stop>() : list.get(0).getParentJob().getStops();
        sort();
        notifyDataSetChanged();
    }

    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = context.getLayoutInflater().inflate(R.layout.item_inactive_stop, null);
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.address = (TextView) view.findViewById(R.id.address);
            viewHolder.customerInfo = (TextView) view.findViewById(R.id.customer_info);
            viewHolder.client = (TextView) view.findViewById(R.id.client);
            viewHolder.phone = (ImageView) view.findViewById(R.id.phone_img);
            viewHolder.name = (TextView) view.findViewById(R.id.name);
            viewHolder.time = (TextView) view.findViewById(R.id.time);
            viewHolder.time_window = (TimeWindowView) view.findViewById(R.id.time_window);
            viewHolder.status = (TextView) view.findViewById(R.id.status);
            viewHolder.statusIndicator = (ImageView) view.findViewById(R.id.status_indicator);
            viewHolder.list_item = (LinearLayout) view.findViewById(R.id.stop_list_item);
            viewHolder.stopNumber = (TextView) view.findViewById(R.id.stop_number);
            viewHolder.priority = (ImageView) view.findViewById(R.id.priority_img);
            view.setTag(viewHolder);
        } else {
            view = convertView;
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        AbstractStop stop = list.get(position);
        MapAddress destinationAddress = MapAddress.from(stop.getAddress());
        String customerInfo = stop.getCustomerInfo();
        if (StringUtils.isBlank(customerInfo)) {
            holder.customerInfo.setVisibility(View.GONE);
        } else {
            holder.customerInfo.setText(customerInfo);
            holder.customerInfo.setVisibility(View.VISIBLE);
        }
        holder.name.setText(stop.getStopName());
        holder.address.setText(destinationAddress != null ? destinationAddress.getFull() : "");
        holder.time.setText(DateUtils.toStringTime(stop.getDate()));
        holder.status.setText(stop.getStatusString(context));
        holder.client.setText(stop.getContactPerson());
        holder.stopNumber.setText(jobStops.indexOf(stop) + 1 + "/" + jobStops.size());
        holder.priority.setImageDrawable(DistributionUtils.getStopPriorityIcon(context, stop));
        PhoneUtils.assignPhone(holder.phone, stop.getContactPhone());
        setUpdateState(stop, holder);
        setTimeWindow(stop, holder);
        if (stop.isCompleted()) {
            view.setBackgroundResource(R.drawable.mc_list_view_item_state_finished_bg);
        } else {
            view.setBackgroundResource(R.drawable.mc_list_view_item_bg);
        }
        return view;
    }

    private void setTimeWindow(AbstractStop stop, ViewHolder holder) {
        String[] times = stop.getTimeWindowAsString().replaceAll(" ", "").split("[-]");
        holder.time_window.setLeftBound(times[0]).setRightBound(times[1]).setVisibilityForDivider(View.GONE);
    }

    private void setUpdateState(AbstractStop stop, ViewHolder holder) {
        switch (stop.getUpdateType()) {
            case Stop.CANCEL_STOP: {
                holder.statusIndicator.setImageResource(R.drawable.mc_img_list_view_item_red_divider);
                break;
            }
            case Stop.NOT_CHANGED_STOP: {
                holder.statusIndicator.setImageResource(R.drawable.mc_img_list_view_item_gray_divider);
                break;
            }
            case Stop.UPDATE_STOP: {
                holder.statusIndicator.setImageResource(R.drawable.mc_img_list_view_item_green_divider);
                break;
            }
        }
    }

    private void sort() {
        Collections.sort(this.list, StopsComparator.getInstance());
        Collections.sort(this.jobStops, StopsComparator.getInstance());
    }

    private class ViewHolder {
        protected TextView name;
        protected TextView customerInfo;
        protected TextView address;
        protected TextView client;
        protected TextView time;
        protected TimeWindowView time_window;
        protected TextView status;
        protected TextView stopNumber;
        protected ImageView statusIndicator;
        protected ImageView priority;
        protected ImageView phone;
        protected LinearLayout list_item;
    }
}