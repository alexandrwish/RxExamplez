package com.magenta.mc.client.android.ui.activity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.magenta.mc.client.android.R;
import com.magenta.mc.client.android.ui.controls.SignatureView;

public class SignatureActivity extends DistributionActivity implements WorkflowActivity {

    public static final int REQUEST_CODE = SignatureActivity.class.hashCode();
    public static final String EXTRA_SIGNATURE = "SIGNATURE";
    public static final String EXTRA_CONTACT_NAME = "CONTACT_NAME";
    public static final String EXTRA_SIGNATURE_TIMESTAMP = "SIGNATURE_TIMESTAMP";

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
                        .putExtra(EXTRA_SIGNATURE_TIMESTAMP, System.currentTimeMillis())
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
            ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        } catch (Exception ignore) {
        }
    }
}