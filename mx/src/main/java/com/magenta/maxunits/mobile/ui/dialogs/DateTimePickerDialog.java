package com.magenta.maxunits.mobile.ui.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.magenta.maxunits.mx.R;

import java.util.Calendar;
import java.util.Date;

/**
 * @author Sergey Grachev
 */
public class DateTimePickerDialog extends Dialog {
    private final Calendar calendar = Calendar.getInstance();
    private final Listener listener;

    protected DatePicker datePicker;
    protected TimePicker timePicker;

    public DateTimePickerDialog(final Context context, final Listener listener, final Date initDate) {
        super(context);
        this.listener = listener;
        if (initDate != null) {
            calendar.setTime(initDate);
        }
    }

    public DateTimePickerDialog(final Context context, final Listener listener) {
        super(context);
        this.listener = listener;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mx_datetime_picker);

        findViewById(R.id.mxSetButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                DateTimePickerDialog.this.listener.onSet(DateTimePickerDialog.this.calendar.getTime());
                DateTimePickerDialog.this.dismiss();
            }
        });

        findViewById(R.id.mxResetButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                DateTimePickerDialog.this.reset();
            }
        });

        findViewById(R.id.mxCancelButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                DateTimePickerDialog.this.cancel();
            }
        });

        datePicker = (DatePicker) findViewById(R.id.mxDate);
        datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
                new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(final DatePicker view, final int year, final int monthOfYear, final int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateTitle();
                    }
                });

        timePicker = (TimePicker) findViewById(R.id.mxTime);
        timePicker.setIs24HourView(true);
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(final TimePicker view, final int hourOfDay, final int minute) {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                updateTitle();
            }
        });

        updateTitle();
        updateFields();
    }

    private void updateTitle() {
        setTitle(calendar.getTime().toLocaleString());
    }

    private void updateFields() {
        datePicker.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
        timePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));
    }

    private void reset() {
        calendar.setTime(new Date());
        updateFields();
        updateTitle();
    }

    public interface Listener {
        void onSet(Date time);
    }
}
