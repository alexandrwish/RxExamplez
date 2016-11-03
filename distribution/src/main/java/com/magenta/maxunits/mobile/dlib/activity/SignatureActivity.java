package com.magenta.maxunits.mobile.dlib.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.magenta.maxunits.distribution.R;
import com.magenta.maxunits.mobile.activity.WorkflowActivity;
import com.magenta.maxunits.mobile.ui.controls.SignatureView;

public class SignatureActivity extends DistributionActivity implements WorkflowActivity {

    public static final int REQUEST_CODE = SignatureActivity.class.hashCode();
    public static final String EXTRA_SIGNATURE = "SIGNATURE";
    public static final String EXTRA_CONTACT_NAME = "CONTACT_NAME";

    SignatureView signature;

    public String getCustomTitle() {
        return getString(R.string.pod);
    }

    public void initActivity(Bundle savedInstanceState) {
        super.initActivity(savedInstanceState);
        setContentView(R.layout.activity_signature);
        final EditText name = (EditText) findViewById(R.id.signature_name);
        name.setText(getIntent().getStringExtra(EXTRA_CONTACT_NAME));
        String savedSignature = (String) getLastNonConfigurationInstance();
        signature = new SignatureView(this, savedSignature == null ? getIntent().getStringExtra(EXTRA_SIGNATURE) : savedSignature);
        ((RelativeLayout) findViewById(R.id.signature)).addView(signature);
        initPaint();
        findViewById(R.id.signature_clear_button).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                signature.clear();
            }
        });
        findViewById(R.id.signature_done_button).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SignatureActivity.this.setResult(RESULT_OK, new Intent()
                        .putExtra(EXTRA_SIGNATURE, signature.sign())
                        .putExtra(EXTRA_CONTACT_NAME, name.getText().toString())
                );
                finish();
            }
        });
    }

    protected void initPaint() {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setColor(0xFFFF0000);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(12);
    }

    public Object onRetainNonConfigurationInstance() {
        return signature.sign();
    }

    protected void onStart() {
        super.onStart();
        try {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        } catch (Exception ignore) {
        }
    }
}