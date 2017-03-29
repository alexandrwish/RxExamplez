package com.magenta.mc.client.android.ui.activity;

import android.content.Intent;
import android.view.View;

import com.magenta.mc.client.android.common.IntentAttributes;

public class ArriveMapActivity extends AbstractArriveMapActivity {

    protected void initButtons() {
        super.initButtons();
        doneButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(final View v) {
                Intent intent = new Intent(ArriveMapActivity.this, CompleteActivity.class);
                intent.putExtra(IntentAttributes.JOB_ID, currentJobId);
                intent.putExtra(IntentAttributes.STOP_ID, currentStopId);
                startActivity(intent);
            }
        });
    }
}