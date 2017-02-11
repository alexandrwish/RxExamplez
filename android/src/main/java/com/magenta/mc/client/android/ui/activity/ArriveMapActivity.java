package com.magenta.mc.client.android.ui.activity;

import android.content.Intent;
import android.view.MenuItem;
import android.view.View;

import com.magenta.mc.client.android.R;
import com.magenta.mc.client.android.util.IntentAttributes;

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

    public boolean onOptionsItemSelected(final MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.abort) {
            startActivity(new Intent(this, AbortActivity.class)
                    .putExtra(IntentAttributes.JOB_ID, currentJobId)
                    .putExtra(IntentAttributes.STOP_ID, currentStopId));
        } else if (i == R.id.fail) {
            startActivity(new Intent(this, AbortActivity.class)
                    .putExtra(AbortActivity.EXTRA_APPLY_FOR_RUN, true)
                    .putExtra(IntentAttributes.JOB_ID, currentJobId)
                    .putExtra(IntentAttributes.STOP_ID, currentStopId));
        } else if (i == R.id.suspend) {
            suspendStop();
        } else {
            return decorator.onMenuSelected(item) || super.onOptionsItemSelected(item);
        }
        return true;
    }
}