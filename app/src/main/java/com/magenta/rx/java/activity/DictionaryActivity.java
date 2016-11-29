package com.magenta.rx.java.activity;

import android.app.Activity;
import android.os.Bundle;

import com.magenta.rx.java.R;
import com.magenta.rx.java.RXApplication;
import com.magenta.rx.java.view.DefinitionListAdapter;
import com.magenta.rx.java.view.DictionaryViewHolder;
import com.magenta.rx.kotlin.event.DictionaryAnswerEvent;
import com.magenta.rx.kotlin.presenter.DictionaryPresenter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class DictionaryActivity extends Activity {

    @Inject
    DictionaryViewHolder holder;
    @Inject
    DefinitionListAdapter adapter;
    @Inject
    DictionaryPresenter presenter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictionary);
        RXApplication.getInstance().getHolder().addDictionaryComponent(this);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        holder.getDictionary().setAdapter(adapter);
    }

    protected void onDestroy() {
        RXApplication.getInstance().getHolder().removeDictionaryComponent();
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @OnClick(R.id.load)
    public void onClick() {
        presenter.onLoadClick(holder.getText().getText().toString());
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onDictionaryAnswer(DictionaryAnswerEvent event) {
        adapter.add(event.getWord(), event.getDefinitions());
        adapter.notifyDataSetChanged();
        EventBus.getDefault().removeStickyEvent(event);
    }
}