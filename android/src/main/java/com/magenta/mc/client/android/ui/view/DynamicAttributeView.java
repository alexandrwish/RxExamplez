package com.magenta.mc.client.android.ui.view;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.magenta.mc.client.android.R;
import com.magenta.mc.client.android.db.dao.DistributionDAO;
import com.magenta.mc.client.android.entity.DynamicAttributeEntity;
import com.magenta.mc.client.android.entity.type.DynamicAttributeType;
import com.magenta.mc.client.android.ui.dialog.DateTimePickerDialog;
import com.magenta.mc.client.android.util.Attribute;
import com.magenta.mc.client.android.util.PhoneUtils;
import com.magenta.mc.client.android.util.StringUtils;
import com.magenta.mc.client.android.util.TextFilter;
import com.magenta.mc.client.android.util.TextWatcher;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class DynamicAttributeView {

    private Activity activity;
    private LayoutInflater inflater;
    private ViewGroup parentView;
    private List<Attribute> attributes = new ArrayList<>();

    public DynamicAttributeView(Activity activity, ViewGroup parentView) {
        this.activity = activity;
        this.inflater = activity.getLayoutInflater();
        this.parentView = parentView;
    }

    public DynamicAttributeView clear() {
        parentView.removeAllViews();
        return this;
    }

    public DynamicAttributeView render() {
        int countViews = 0;
        for (final Attribute attribute : attributes) {
            if (StringUtils.isBlank(attribute.getValue()) && !attribute.isEditable()) {
                continue;
            }
            boolean editable = attribute.isEditable() && !(attribute.getType().equals(DynamicAttributeType.DATETIME) || attribute.getType().equals(DynamicAttributeType.BOOLEAN));
            View view;
            final TextView valueView;
            if (editable) {
                view = inflater.inflate(R.layout.item_editable_attribute, null);
                valueView = ((EditText) view.findViewById(R.id.attribute_value_edit));
            } else if (attribute.getType().equals(DynamicAttributeType.BOOLEAN)) {
                view = inflater.inflate(R.layout.item_boolean_attribute, null);
                valueView = ((CheckBox) view.findViewById(R.id.attribute_value_check));
                if (!attribute.isEditable()) {
                    valueView.setEnabled(false);
                }
            } else {
                view = inflater.inflate(R.layout.item_attribute, null);
                valueView = ((TextView) view.findViewById(R.id.attribute_value));
            }
            ((TextView) view.findViewById(R.id.attribute_title)).setText(getTitle(attribute));
            switch (attribute.getType()) {
                case BOOLEAN: {
                    CheckBox checkBoxValue = (CheckBox) valueView;
                    checkBoxValue.setChecked(!StringUtils.isBlank(attribute.getValue()) && Boolean.valueOf(attribute.getValue()));
                    checkBoxValue.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            attribute.setValue(String.valueOf(b));
                            updateDynamicAttribute(attribute);
                        }
                    });
                    break;
                }
                case PHONE: {
                    if (!StringUtils.isBlank(attribute.getValue())) {
                        PhoneUtils.assignPhone(valueView, attribute.getValue());
                    }
                    break;
                }
                case DATETIME:
                case IMAGE: {
                    if (attribute.getIcon() != null) {
                        ImageView img = (ImageView) view.findViewById(R.id.attribute_img);
                        img.setVisibility(View.VISIBLE);
                        img.setImageDrawable(attribute.getIcon());
                    }
                }
                default: {
                    valueView.setText(getValue(attribute));
                    valueView.clearFocus();
                    if (editable) {
                        TextFilter filter = null;
                        switch (attribute.getType()) {
                            case DOUBLE:
                                filter = new TextFilter(TextFilter.TextFilterType.DOUBLE, 11);
                            case INTEGER: {
                                if (filter == null) {
                                    filter = new TextFilter(TextFilter.TextFilterType.NUMBER, 6);
                                }
                                valueView.setFilters(new InputFilter[]{filter});
                                break;
                            }
                            default: {
                                valueView.setFilters(new InputFilter[]{new TextFilter(TextFilter.TextFilterType.TEXT, 1000)});
                            }
                        }
                        if (!attribute.getType().equals(DynamicAttributeType.DATETIME))
                            valueView.addTextChangedListener(new TextWatcher() {
                                public void afterTextChanged(Editable editable) {
                                    attribute.setValue(editable.toString());
                                    updateDynamicAttribute(attribute);
                                }
                            });
                    }
                }
            }
            for (Object listener : attribute.getListeners()) {
                if (listener instanceof View.OnLongClickListener) {
                    view.setOnLongClickListener((View.OnLongClickListener) listener);
                } else if (listener instanceof View.OnClickListener) {
                    view.setOnClickListener((View.OnClickListener) listener);
                }
            }
            if (countViews++ > 0) {
                parentView.addView(inflater.inflate(R.layout.mx_attribute_table_splitter, null));
            }
            parentView.addView(view);
        }
        try {
            activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        } catch (Exception ignore) {
        }
        return this;
    }

    private void updateDynamicAttribute(Attribute attribute) {
        try {
            DistributionDAO.getInstance().updateDynamicAttribute(attribute.getId(), attribute.getValue());
        } catch (SQLException ignore) {
        }
    }

    public DynamicAttributeView addAll(final Collection<DynamicAttributeEntity> entities) {
        if (entities == null || entities.isEmpty()) {
            return this;
        }
        for (final DynamicAttributeEntity entity : entities) {
            final Attribute attribute = Attribute.fromEntity(entity, activity);
            if (entity.isPdaEditable()) {
                attribute.addListeners(new View.OnClickListener() {
                    public void onClick(final View view) {
                        if (entity.getTypeName().equals(DynamicAttributeType.DATETIME)) {
                            new DateTimePickerDialog(activity, new DateTimePickerDialog.Listener() {
                                public void onSet(Date time) {
                                    attribute.setValue(time.toString());
                                    updateDynamicAttribute(attribute);
                                    clear().render();
                                }
                            }, StringUtils.isBlank(attribute.getValue()) ? new Date() : new Date(attribute.getValue())) {
                                protected void onCreate(Bundle savedInstanceState) {
                                    super.onCreate(savedInstanceState);
                                    datePicker.setDescendantFocusability(DatePicker.FOCUS_BLOCK_DESCENDANTS);
                                    timePicker.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);
                                }
                            }.show();
                        }
                    }
                });
            }
            add(attribute);
        }
        return this;
    }

    public DynamicAttributeView add(Attribute attribute) {
        attributes.add(attribute);
        return this;
    }

    private String getTitle(Attribute attribute) {
        StringBuilder title = new StringBuilder(attribute.getTitle() != null ? attribute.getTitle() : "");
        if (!StringUtils.isBlank(attribute.getUnit())) {
            title.append("(").append(attribute.getUnit()).append(")");
        }
        if (attribute.isRequired()) {
            title.append("*");
        }
        return title.length() > 18 ? title.toString().substring(0, 15) + "..." : title.toString();
    }

    private CharSequence getValue(Attribute attribute) {
        String value = "";
        if (!StringUtils.isBlank(attribute.getValue())) {
            switch (attribute.getType()) {
                case BOOLEAN: {
                    value = activity.getString(Boolean.valueOf(attribute.getValue()) ? R.string.mx_yes : R.string.mx_no);
                    break;
                }
                case DATETIME: {
                    value = StringUtils.formatDateTime(attribute.getValue());
                    break;
                }
                case DOUBLE: {
                    value = StringUtils.formatDouble(attribute.getValue());
                    break;
                }
                default: {
                    value = attribute.getValue();
                }
            }
        }
        return value;
    }
}