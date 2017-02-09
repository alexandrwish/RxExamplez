package com.magenta.mc.client.android.ui.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.magenta.mc.client.android.R;
import com.magenta.mc.client.android.ui.view.NumberInput;

public class NumberInputDialog extends WideDialog {

    private int iNumber;
    private double dNumber;
    private Button okButton;
    private NumberInput numberInput;
    private View.OnClickListener okButtonListener;

    public NumberInputDialog(Context context) {
        super(R.layout.dialog_number_input, context);
    }

    public NumberInputDialog(Context context, int theme) {
        super(R.layout.dialog_number_input, context, theme);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        okButton = (Button) findViewById(R.id.ok_button);
        numberInput = (NumberInput) findViewById(R.id.number_input);
        if (dNumber < 0) {
            numberInput.initWithNumber(iNumber);
        } else {
            numberInput.initWithNumber(dNumber);
        }
        okButton.setOnClickListener(okButtonListener);
    }

    public void setOkButtonOnClickListener(View.OnClickListener l) {
        this.okButtonListener = l;
    }

    public void setNumber(int number) {
        iNumber = number;
        dNumber = -100;
    }

    public void setNumber(double number) {
        dNumber = number;
        iNumber = -100;
    }

    public double getDoubleNumber() {
        return numberInput.getDoubleNumber();
    }

    public long getIntegerNumber() {
        return numberInput.getIntegerNumber();
    }
}