package com.magenta.rx.rxa.activity;

import android.app.Activity;
import android.os.Bundle;

import com.magenta.rx.rxa.R;
import com.magenta.rx.rxa.RXApplication;
import com.magenta.rx.rxa.event.DictionaryAnswerEvent;
import com.magenta.rx.rxa.presenter.DictionaryPresenter;
import com.magenta.rx.rxa.view.DefinitionListAdapter;
import com.magenta.rx.rxa.view.DictionaryViewHolder;

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
        RXApplication.getInstance().addDictionaryComponent(this);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        holder.getDictionary().setAdapter(adapter);
        presenter.init();
    }

    protected void onDestroy() {
        RXApplication.getInstance().removeDictionaryComponent();
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @OnClick(R.id.load)
    @SuppressWarnings("unused")
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