package com.magenta.rx.java.view;

import android.app.Activity;
import android.widget.EditText;
import android.widget.TextView;

import com.magenta.rx.java.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RetrofitViewHolder {

    @BindView(R.id.input)
    EditText input;
    @BindView(R.id.input_text)
    TextView inputText;
    @BindView(R.id.translate_text)
    TextView translateText;

    public RetrofitViewHolder(Activity activity) {
        ButterKnife.bind(this, activity);
    }

    public EditText getInput() {
        return input;
    }

    public TextView getInputText() {
        return inputText;
    }

    public TextView getTranslateText() {
        return translateText;
    }
}