package com.magenta.mc.client.android.ui.dialog;

import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.magenta.mc.client.android.R;

public class TextInputDialog extends WideDialog {

    private Button okButton;
    private EditText textInput;
    private View.OnClickListener okButtonListener;

    private String text;

    public TextInputDialog(Context context) {
        super(R.layout.dialog_text_input, context);
    }

    public TextInputDialog(Context context, int theme) {
        super(R.layout.dialog_text_input, context, theme);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        okButton = (Button) findViewById(R.id.ok_button);
        textInput = (EditText) findViewById(R.id.text_input);
        okButton.setOnClickListener(okButtonListener);
        textInput.setText(text);
        textInput.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
    }

    public void setOkButtonOnClickListener(View.OnClickListener l) {
        this.okButtonListener = l;
    }

    public String getText() {
        return textInput.getText().toString();
    }

    public void setText(String text) {
        this.text = text;
    }
}