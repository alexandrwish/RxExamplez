package com.magenta.rx.java.view;

import android.widget.EditText;
import android.widget.ExpandableListView;

import com.magenta.rx.java.R;
import com.magenta.rx.java.activity.DictionaryActivity;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DictionaryViewHolder {

    @BindView(R.id.text)
    EditText text;
    @BindView(R.id.dictionary)
    ExpandableListView dictionary;

    @Inject
    public DictionaryViewHolder(DictionaryActivity activity) {
        ButterKnife.bind(this, activity);
    }

    public EditText getText() {
        return text;
    }

    public ExpandableListView getDictionary() {
        return dictionary;
    }
}