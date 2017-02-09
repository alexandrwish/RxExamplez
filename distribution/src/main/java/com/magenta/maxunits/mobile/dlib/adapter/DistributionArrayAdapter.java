package com.magenta.maxunits.mobile.dlib.adapter;

import android.app.Activity;
import android.widget.ArrayAdapter;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class DistributionArrayAdapter<T> extends ArrayAdapter<T> {

    static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("d MMM", Locale.UK);
    static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm", Locale.UK);

    protected List<T> list;
    protected Activity context;

    DistributionArrayAdapter(Activity context, int layoutID, List<T> list) {
        super(context, layoutID, list);
        this.context = context;
        this.list = list;
    }

    public void changeList(List<T> list) {
        this.list.clear();
        this.list.addAll(list);
    }
}