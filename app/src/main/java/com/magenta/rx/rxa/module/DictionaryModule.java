package com.magenta.rx.rxa.module;

import com.magenta.rx.rxa.activity.DictionaryActivity;
import com.magenta.rx.rxa.component.ActivityScope;
import com.magenta.rx.rxa.model.loader.DictionaryLoader;
import com.magenta.rx.rxa.model.record.Definition;
import com.magenta.rx.rxa.presenter.DictionaryPresenter;
import com.magenta.rx.rxa.view.DefinitionListAdapter;
import com.magenta.rx.rxa.view.DictionaryViewHolder;

import java.util.LinkedHashMap;
import java.util.List;

import dagger.Module;
import dagger.Provides;

@Module
public class DictionaryModule {

    private final DictionaryActivity activity;

    public DictionaryModule(DictionaryActivity activity) {
        this.activity = activity;
    }

    @Provides
    @ActivityScope
    public DictionaryActivity provideRetrofitActivity() {
        return activity;
    }

    @Provides
    @ActivityScope
    public DictionaryViewHolder provideDictionaryViewHolder() {
        return new DictionaryViewHolder(activity);
    }

    @Provides
    @ActivityScope
    public DictionaryPresenter provideDictionaryPresenter() {
        return new DictionaryPresenter();
    }

    @Provides
    @ActivityScope
    public DictionaryLoader provideDictionaryLoader() {
        return new DictionaryLoader();
    }

    @Provides
    @ActivityScope
    protected DefinitionListAdapter provideDictionaryListAdapter() {
        return new DefinitionListAdapter(new LinkedHashMap<String, List<Definition>>(), activity);
    }
}