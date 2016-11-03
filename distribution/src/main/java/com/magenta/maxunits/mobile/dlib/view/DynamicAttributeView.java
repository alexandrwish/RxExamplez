package com.magenta.maxunits.mobile.dlib.view;

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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.magenta.maxunits.distribution.R;
import com.magenta.maxunits.mobile.dlib.db.dao.DistributionDAO;
import com.magenta.maxunits.mobile.dlib.entity.DynamicAttributeEntity;
import com.magenta.maxunits.mobile.dlib.entity.DynamicAttributeType;
import com.magenta.maxunits.mobile.dlib.entity.LocalizeStringEntity;
import com.magenta.maxunits.mobile.dlib.utils.Attribute;
import com.magenta.maxunits.mobile.dlib.utils.TextFilter;
import com.magenta.maxunits.mobile.dlib.utils.TextWatcher;
import com.magenta.maxunits.mobile.mc.MxSettings;
import com.magenta.maxunits.mobile.ui.dialogs.DateTimePickerDialog;
import com.magenta.maxunits.mobile.utils.PhoneUtils;
import com.magenta.maxunits.mobile.utils.StringUtils;
import com.magenta.mc.client.setup.Setup;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class DynamicAttributeView {

    Activity activity;
    LayoutInflater inflater;
    ViewGroup parentView;
    List<Attribute> attributes = new ArrayList<Attribute>();

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
        final List<View> views = new ArrayList<View>(attributes.size());
        for (final Attribute attribute : attributes) {
            if (StringUtils.isBlank(attribute.getValue()) && !attribute.isEditable()) continue;
            boolean editable = attribute.isEditable() && !(attribute.getType().equals(DynamicAttributeType.DATETIME) || attribute.getType().equals(DynamicAttributeType.BOOLEAN));
            View view;
            if (editable) {
                view = inflater.inflate(R.layout.item_editable_attribute, null);
            } else if (attribute.getType().equals(DynamicAttributeType.BOOLEAN)) {
                view = inflater.inflate(R.layout.item_boolean_attribute, null);
            } else {
                view = inflater.inflate(R.layout.item_attribute, null);
            }
            ((TextView) view.findViewById(R.id.attribute_title)).setText(getTitle(attribute));
            final TextView valueView = ((TextView) view.findViewById(R.id.attribute_value));
            switch (attribute.getType()) {
                case BOOLEAN: {
                    ((CheckBox) valueView).setChecked(!StringUtils.isBlank(attribute.getValue()) && Boolean.valueOf(attribute.getValue()));
                    ((CheckBox) valueView).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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
            if (views.size() > 0) {
                views.add(inflater.inflate(R.layout.mx_attribute_table_splitter, null));
            }
            views.add(view);
        }
        for (View view : views) {
            parentView.addView(view);
        }
        try {
            activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        } catch (Exception ignore) {
        }
        return this;
    }

    protected void updateDynamicAttribute(Attribute attribute) {
        try {
            DistributionDAO.getInstance(activity.getApplicationContext()).updateDynamicAttribute(attribute.getId(), attribute.getValue());
        } catch (SQLException ignore) {
        }
    }

    public DynamicAttributeView addAll(final Collection<DynamicAttributeEntity> entities) {
        if (entities != null) {
            for (final DynamicAttributeEntity entity : entities) {
                LocalizeStringEntity.LocalizeStringType type = LocalizeStringEntity.LocalizeStringType.getType(((MxSettings) Setup.get().getSettings()).getLocale());
                final Attribute attribute = new Attribute(
                        entity.getId(),
                        entity.getTitle().getLocalizeString(type),
                        entity.getUnit(),
                        entity.isPdaRequired(),
                        entity.isPdaEditable(),
                        entity.getValue(),
                        entity.getTypeName().equals(DynamicAttributeType.DATETIME) ? activity.getResources().getDrawable(android.R.drawable.ic_menu_today) : null,
                        entity.getTypeName());
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
                attributes.add(attribute);
            }
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