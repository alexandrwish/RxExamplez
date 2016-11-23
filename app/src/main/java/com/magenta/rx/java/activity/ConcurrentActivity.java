package com.magenta.rx.java.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.jakewharton.rxbinding.widget.RxTextView;
import com.magenta.rx.java.R;
import com.magenta.rx.java.RXApplication;
import com.magenta.rx.java.presenter.ConcurrentPresenter;
import com.magenta.rx.java.view.ConcurrentViewHolder;
import com.magenta.rx.kotlin.event.CalcEvent;
import com.magenta.rx.kotlin.event.CleanEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;
import rx.functions.Action1;

public class ConcurrentActivity extends Activity {

    @Inject
    ConcurrentViewHolder holder;
    @Inject
    ConcurrentPresenter presenter;

    private List<Subscription> subscriptions = new ArrayList<>(4);

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_concurrent);
        RXApplication.getInstance().getHolder().addConcurrentComponent(this);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        for (final EditText editText : holder.getParams()) {
            subscriptions.add(RxTextView.textChanges(editText).subscribe(new Action1<CharSequence>() {
                public void call(CharSequence charSequence) {
                    presenter.change(editText.getId(), charSequence);
                }
            }));
        }
    }

    protected void onDestroy() {
        RXApplication.getInstance().getHolder().removeConcurrentComponent();
        EventBus.getDefault().unregister(this);
        for (Subscription subscription : subscriptions) {
            subscription.unsubscribe();
        }
        super.onDestroy();
    }


    @OnClick(R.id.start_btn)
    public void onStartClick() {
        presenter.start(holder.isMultithreading(), holder.getSeekBar().getProgress());
    }

    @OnClick(R.id.clean_btn)
    public void onCleanClick() {
        presenter.clean();
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    @SuppressWarnings("unused")
    public void onCalcResult(CalcEvent event) {
        TableRow tr = new TableRow(this);
        tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        TextView x = new TextView(this);
        x.setText(new DecimalFormat("#.####").format(event.getResult().getX()));
        x.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
        tr.addView(x);
        TextView fx = new TextView(this);
        fx.setText(new DecimalFormat("#.####").format(event.getResult().getFx()));
        fx.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
        tr.addView(fx);
        TextView asin = new TextView(this);
        asin.setText(new DecimalFormat("#.####").format(event.getResult().getAsin()));
        asin.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
        tr.addView(asin);
        TextView sinf = new TextView(this);
        sinf.setText(new DecimalFormat("#.####").format(event.getResult().getSinF()));
        sinf.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
        tr.addView(sinf);
        holder.getScore().addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
        EventBus.getDefault().removeStickyEvent(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    @SuppressWarnings("unused")
    public void onCleanResult(CleanEvent event) {
        holder.getScore().removeAllViews();
    }
}