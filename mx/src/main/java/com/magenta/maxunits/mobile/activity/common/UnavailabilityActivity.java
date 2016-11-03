package com.magenta.maxunits.mobile.activity.common;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.magenta.maxunits.mobile.activity.ActivityDecorator;
import com.magenta.maxunits.mobile.activity.GenericActivity;
import com.magenta.maxunits.mobile.rpc.operations.CreateUnavailability;
import com.magenta.maxunits.mobile.ui.dialogs.DateTimePickerDialog;
import com.magenta.maxunits.mx.R;
import com.magenta.mc.client.MobileApp;
import com.magenta.mc.client.client.ConnectionListener;
import com.magenta.mc.client.settings.Settings;
import com.magenta.mc.client.setup.Setup;
import com.magenta.mc.client.util.Resources;

import java.text.ParseException;
import java.util.Date;

/**
 * @author Sergey Grachev
 */
public class UnavailabilityActivity extends GenericActivity {
    public static final int UNAVAILABILITY_REQUEST = 1;

    public static final String ATTR_TITLE = "title";

    private static final int DLG_START_DATE = 1;
    private static final int DLG_END_DATE = 2;

    private String title;
    private TextView startDateField;
    private TextView endDateField;

    {
        decorator = new ActivityDecorator(this);
    }

    @Override
    public String getCustomTitle() {
        return title;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void initActivity(final Bundle savedInstanceState) {
        final Bundle extras = getIntent().getExtras();
        if (extras != null) {
            title = extras.getString(ATTR_TITLE);
        }

        setContentView(R.layout.mx_unavailability);

        startDateField = (TextView) findViewById(R.id.mxStartDate);
        findViewById(R.id.mxSetStartDateButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DLG_START_DATE);
            }
        });

        endDateField = (TextView) findViewById(R.id.mxEndDate);
        findViewById(R.id.mxSetEndDateButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DLG_END_DATE);
            }
        });

        findViewById(R.id.mxOkButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final String nextAddress = ((TextView) findViewById(R.id.mxAddress)).getText().toString();
                final String nextPostcode = ((TextView) findViewById(R.id.mxPostcode)).getText().toString();
                final String reason = ((TextView) findViewById(R.id.mxReason)).getText().toString();

                if (reason.length() == 0) {
                    ((TextView) findViewById(R.id.mxReason)).setError(getString(R.string.mx_activity_unavailability_reasonRequired));
                }

                final Date startDate = parseDate(startDateField, R.string.mx_activity_unavailability_startDateRequired);
                final Date endDate = parseDate(endDateField, R.string.mx_activity_unavailability_endDateRequired);

                final boolean valid = startDate != null && endDate != null && reason.length() > 0;
                if (!valid) {
                    return;
                }

                if (!ConnectionListener.getInstance().isConnected()) {
                    if (Settings.get().isOfflineVersion()) {
                        setResult(RESULT_OK);
                        UnavailabilityActivity.this.finish();
                    }
                    return;
                }

                CreateUnavailability.create(
                        Resources.UTC_DATE_FORMAT.format(startDate), Resources.UTC_DATE_FORMAT.format(endDate),
                        nextAddress, nextPostcode, reason,
                        new CreateUnavailability.Callback() {
                            @Override
                            public void done(final String errorMessage) {
                                if (errorMessage == null) {
                                    setResult(RESULT_OK);
                                    UnavailabilityActivity.this.finish();
                                } else {
                                    Setup.get().getUI().getDialogManager().messageSafe(MobileApp.localize("Error"), errorMessage);
                                }
                            }
                        });
            }
        });

        findViewById(R.id.mxCancelButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                UnavailabilityActivity.this.finish();
            }
        });
    }

    private Date parseDate(final TextView dateField, final int errMsgId) {
        final String sdt = dateField.getText().toString().trim();
        if (sdt.length() == 0) {
            dateField.setError(getString(errMsgId));
        } else {
            try {
                return Resources.DATE_FORMAT.parse(sdt);
            } catch (ParseException e) {
                dateField.setError(
                        String.format(getString(R.string.mx_activity_unavailability_incorrectDateFormat),
                                Resources.DATE_FORMAT.toPattern()
                        ));
            }
        }
        return null;
    }

    @Override
    protected Dialog onCreateDialog(final int id) {
        switch (id) {
            case DLG_START_DATE:
                return new DateTimePickerDialog(this, new DateTimePickerDialog.Listener() {
                    @Override
                    public void onSet(final Date dateTime) {
                        startDateField.setText(Resources.DATE_FORMAT.format(dateTime));
                        startDateField.setError(null);
                    }
                });
            case DLG_END_DATE:
                return new DateTimePickerDialog(this, new DateTimePickerDialog.Listener() {
                    @Override
                    public void onSet(final Date dateTime) {
                        endDateField.setText(Resources.DATE_FORMAT.format(dateTime));
                        endDateField.setError(null);
                    }
                });
        }
        return null;
    }
}
