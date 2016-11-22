package com.magenta.rx.java.view;

import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TableLayout;

import com.magenta.rx.java.R;
import com.magenta.rx.java.activity.ConcurrentActivity;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;

public class ConcurrentViewHolder {

    @BindView(R.id.multithreading)
    Switch multithreading;
    @BindView(R.id.seek_bar)
    SeekBar seekBar;
    @BindViews({R.id.xn, R.id.xk, R.id.dx, R.id.eps})
    List<EditText> params;
    @BindView(R.id.score_table)
    TableLayout score;

    private boolean checked;

    @Inject
    public ConcurrentViewHolder(ConcurrentActivity activity) {
        ButterKnife.bind(this, activity);
        multithreading.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checked = isChecked;
                seekBar.setEnabled(checked);
            }
        });
        seekBar.setMax(Runtime.getRuntime().availableProcessors() * 2);
        seekBar.setEnabled(false);
    }

    public boolean isMultithreading() {
        return checked;
    }

    public SeekBar getSeekBar() {
        return seekBar;
    }

    public List<EditText> getParams() {
        return params;
    }

    public TableLayout getScore() {
        return score;
    }
}