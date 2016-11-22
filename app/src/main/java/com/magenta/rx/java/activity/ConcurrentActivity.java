package com.magenta.rx.java.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;

import com.jakewharton.rxbinding.widget.RxTextView;
import com.magenta.rx.java.R;
import com.magenta.rx.java.RXApplication;
import com.magenta.rx.java.presenter.ConcurrentPresenter;
import com.magenta.rx.java.view.ConcurrentViewHolder;

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
//        EventBus.getDefault().register(this);
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
//        EventBus.getDefault().unregister(this);
        for (Subscription subscription : subscriptions) {
            subscription.unsubscribe();
        }
        super.onDestroy();
    }


    @OnClick(R.id.start_btn)
    public void onClick() {
        presenter.start(holder.isMultithreading(), holder.getSeekBar().getProgress());
    }
}