package com.magenta.mc.client.android.ui.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.magenta.mc.client.android.R;
import com.magenta.mc.client.android.service.storage.entity.Job;
import com.magenta.mc.client.android.service.storage.entity.Stop;
import com.magenta.maxunits.mobile.entity.TaskState;
import com.magenta.mc.client.android.ui.view.TimeView;

import java.util.List;

public class JobsAdapter extends DistributionArrayAdapter<Job> {

    public JobsAdapter(Activity context, List<Job> list) {
        super(context, R.layout.item_job, list);
    }

    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = context.getLayoutInflater().inflate(R.layout.item_job, null);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.name = (TextView) view.findViewById(R.id.name);
            viewHolder.time = (TimeView) view.findViewById(R.id.time);
            viewHolder.date = (TextView) view.findViewById(R.id.date);
            viewHolder.address = (TextView) view.findViewById(R.id.address);
            viewHolder.status = (TextView) view.findViewById(R.id.status);
            viewHolder.totalJobs = (TextView) view.findViewById(R.id.total_jobs);
            viewHolder.failedJobs = (TextView) view.findViewById(R.id.failed_jobs);
            viewHolder.completeJobs = (TextView) view.findViewById(R.id.complete_jobs);
            viewHolder.suspendedJobs = (TextView) view.findViewById(R.id.suspended_jobs);
            viewHolder.jobs = (TextView) view.findViewById(R.id.jobs);
            viewHolder.statusIndicator = (ImageView) view.findViewById(R.id.status_indicator);
            ((LinearLayout.LayoutParams) view.findViewById(R.id.state_layout).getLayoutParams()).gravity = Gravity.CENTER_VERTICAL;
            view.setTag(viewHolder);
        } else {
            view = convertView;
        }
        Job job = list.get(position);
        ViewHolder holder = (ViewHolder) view.getTag();
        String runName = context.getString(R.string.run);
        if (job.getParameter("number") != null) {
            runName += " " + job.getParameter("number");
        }
        holder.name.setText(runName);
        holder.time.setHours(job.getDate().getHours()).setMinutes(job.getDate().getMinutes());
        holder.date.setText(DATE_FORMAT.format(job.getDate()));
        holder.status.setText(job.getStatusString(context));
        holder.address.setText(job.getAddressAsString());
        holder.totalJobs.setText(String.valueOf(job.getStops().size()));
        holder.statusIndicator.setImageResource(getImageResource(job));
        setStatistics(holder, job);
        return view;
    }

    private void setStatistics(ViewHolder holder, Job job) {
        int failed = 0;
        int complete = 0;
        int total = 0;
        int suspend = 0;
        for (Stop stop : (List<Stop>) job.getStops()) {
            total++;
            if (stop.isCancelled()) {
                failed++;
            } else if (stop.isCompleted()) {
                complete++;
            } else if (stop.getState() == TaskState.STOP_SUSPENDED) {
                suspend++;
            }
        }
        holder.jobs.setText(" " + (total - failed - complete - suspend));
        holder.suspendedJobs.setText(" " + suspend + " ");
        holder.completeJobs.setText(" " + complete + " ");
        holder.failedJobs.setText(" " + failed + " ");
    }

    private int getImageResource(Job job) {
        for (Stop stop : (List<Stop>) job.getStops()) {
            if (stop.getUpdateType() != Stop.NOT_CHANGED_STOP) {
                return R.drawable.mc_img_list_view_item_green_divider;
            }
        }
        return R.drawable.mc_img_list_view_item_gray_divider;
    }

    private class ViewHolder {
        protected TextView name;
        protected TimeView time;
        protected TextView date;
        protected TextView address;
        protected TextView status;
        protected TextView totalJobs;
        protected TextView failedJobs;
        protected TextView completeJobs;
        protected TextView suspendedJobs;
        protected TextView jobs;
        protected ImageView statusIndicator;
    }
}