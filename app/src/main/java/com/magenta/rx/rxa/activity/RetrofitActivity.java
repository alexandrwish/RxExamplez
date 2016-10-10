package com.magenta.rx.rxa.activity;

import android.app.Activity;
import android.os.Bundle;

import com.magenta.rx.rxa.R;
import com.magenta.rx.rxa.RXApplication;
import com.magenta.rx.rxa.event.TranslateAnswerEvent;
import com.magenta.rx.rxa.presenter.RetrofitPresenter;
import com.magenta.rx.rxa.view.RetrofitViewHolder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class RetrofitActivity extends Activity {

    @Inject
    RetrofitViewHolder holder;

    @Inject
    RetrofitPresenter presenter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrofit);
        ButterKnife.bind(this);
        RXApplication.getInstance().addRetrofitComponent(this);
        EventBus.getDefault().register(this);
    }

    protected void onDestroy() {
        RXApplication.getInstance().removeRetrofitComponent();
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @OnClick(R.id.translate_btn)
    public void onClick() {
        presenter.onLoadClick(holder.getInput().getText().toString());
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    @SuppressWarnings("unused")
    public void onTranslateAnswer(TranslateAnswerEvent event) {
        holder.getInputText().setText(event.getText());
        holder.getTranslateText().setText(event.getTranslate());
        EventBus.getDefault().removeStickyEvent(event);
    }
}