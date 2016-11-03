package com.magenta.maxunits.mobile.dlib.adapter;

import android.app.Activity;
import android.widget.ArrayAdapter;

import java.text.SimpleDateFormat;
import java.util.List;

public class DistributionArrayAdapter<T> extends ArrayAdapter<T> {

    protected static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("d MMM");
    protected static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");

    protected List<T> list;
    protected Activity context;

    public DistributionArrayAdapter(Activity context, int layoutID, List<T> list) {
        super(context, layoutID, list);
        this.context = context;
        this.list = list;
    }

    public void changeList(List<T> list) {
        this.list.clear();
        this.list.addAll(list);
    }
}