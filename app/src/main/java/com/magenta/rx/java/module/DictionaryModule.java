package com.magenta.rx.java.module;

import com.magenta.rx.java.activity.DictionaryActivity;
import com.magenta.rx.java.component.ActivityScope;
import com.magenta.rx.java.model.record.Definition;
import com.magenta.rx.java.presenter.DictionaryPresenter;
import com.magenta.rx.java.view.DefinitionListAdapter;
import com.magenta.rx.java.view.DictionaryViewHolder;
import com.magenta.rx.kotlin.loader.DictionaryLoader;

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