package com.magenta.maxunits.mobile.ui;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.magenta.maxunits.mobile.entity.Attribute;
import com.magenta.maxunits.mobile.utils.DateUtils;
import com.magenta.maxunits.mobile.utils.PhoneUtils;
import com.magenta.maxunits.mobile.utils.StringUtils;
import com.magenta.maxunits.mx.R;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public final class AttributesRenderer {

    private final Activity activity;
    private final LayoutInflater inflater;
    private final ViewGroup parentView;
    private final List<Attr> attributes = new ArrayList<Attr>();
    private final NumberFormat format = new DecimalFormat("#.#####");

    public AttributesRenderer(final Activity activity, final ViewGroup parentView) {
        this.activity = activity;
        this.inflater = activity.getLayoutInflater();
        this.parentView = parentView;
    }

    public AttributesRenderer clear() {
        parentView.removeAllViews();
        return this;
    }

    public AttributesRenderer render() {
        final List<View> views = new ArrayList<View>(attributes.size());
        for (final Attr attribute : attributes) {
            if (StringUtils.isBlank(attribute.value)) continue;
            final View view = inflater.inflate(R.layout.mx_attribute_table_item, null);
            String name = (!StringUtils.isBlank(attribute.name) && attribute.name.length() > 15) ? (attribute.name.substring(0, 15) + "...") : attribute.name;
            ((TextView) view.findViewById(R.id.attribute)).setText(name);
            final TextView valueView = ((TextView) view.findViewById(R.id.value));
            switch (attribute.type) {
                case PHONE: {
                    if (!StringUtils.isBlank(attribute.value)) {
                        PhoneUtils.assignPhone(valueView, attribute.value);
                    }
                    break;
                }
                case IMAGE: {
                    if (attribute.icon != null) {
                        ImageView img = (ImageView) view.findViewById(R.id.img);
                        img.setVisibility(View.VISIBLE);
                        img.setImageDrawable(attribute.icon);
                    }
                }
                default: {
                    if (attribute.color <= 0) {
                        valueView.setTextColor(attribute.color);
                    }
                    valueView.setText(attribute.value);
                }
            }
            if (views.size() > 0) {
                views.add(inflater.inflate(R.layout.mx_attribute_table_splitter, null));
            }
            if (attribute.listeners != null && attribute.listeners.length > 0) {
                for (final Object listener : attribute.listeners) {
                    if (listener instanceof View.OnLongClickListener) {
                        view.setOnLongClickListener((View.OnLongClickListener) listener);
                    } else if (listener instanceof View.OnClickListener) {
                        view.setOnClickListener((View.OnClickListener) listener);
                    }
                }
            }
            views.add(view);
        }
        for (final View view : views) {
            parentView.addView(view);
        }
        return this;
    }

    /**
     * @param listeners {@link View.OnLongClickListener}, {@link View.OnClickListener}
     */
    public AttributesRenderer add(final String name, final String value, final Object... listeners) {
        attributes.add(new Attr(name, value, listeners));
        return this;
    }

    /**
     * @param listeners {@link View.OnLongClickListener}, {@link View.OnClickListener}
     */
    public AttributesRenderer add(final int name, final String value, final Object... listeners) {
        attributes.add(new Attr(activity.getString(name), value, listeners));
        return this;
    }

    public AttributesRenderer add(final String name, final String value, final Integer color, final Object... listeners) {
        attributes.add(new Attr(name, value, color, Attr.Type.TEXT, null, listeners));
        return this;
    }

    public AttributesRenderer addAttributeWithImage(final String name, final String value, final Drawable img, final Object... listeners) {
        attributes.add(new Attr(name, value, Attr.Type.IMAGE, img, listeners));
        return this;
    }

    public AttributesRenderer addTime(final int name, final Date value) {
        attributes.add(new Attr(activity.getString(name), DateUtils.toStringTime(value)));
        return this;
    }

    public AttributesRenderer addTime(final int name, final String value, final TimeUnit unit) {
        try {
            final long valueLong = Long.parseLong(value);
            attributes.add(new Attr(activity.getString(name), DateUtils.toStringTime(unit.toSeconds(valueLong))));
        } catch (Exception e) {
            attributes.add(new Attr(activity.getString(name)));
        }
        return this;
    }

    public AttributesRenderer addTimeOfDate(final int name, final String value, final TimeUnit unit) {
        try {
            final long valueLong = Long.parseLong(value);
            attributes.add(new Attr(activity.getString(name), DateUtils.toStringTime(new Date(unit.toMillis(valueLong)))));
        } catch (Exception e) {
            attributes.add(new Attr(activity.getString(name)));
        }
        return this;
    }

    public AttributesRenderer addFloat(final int name, final String value) {
        try {
            attributes.add(new Attr(activity.getString(name), format.format(Float.parseFloat(value))));
        } catch (Exception e) {
            attributes.add(new Attr(activity.getString(name)));
        }
        return this;
    }

    public AttributesRenderer addFloat(int name, String value, String unit) {
        try {
            attributes.add(new Attr(activity.getString(name), String.format("%s %s", format.format(Float.parseFloat(value)), unit)));
        } catch (Exception e) {
            attributes.add(new Attr(activity.getString(name)));
        }
        return this;
    }

    public AttributesRenderer addPhone(final int name, final String value) {
        attributes.add(new Attr(activity.getString(name), value, Attr.Type.PHONE));
        return this;
    }

    public AttributesRenderer addTimeRange(final int name, final String start, final String end, final TimeUnit unit) {
        try {
            addTimeRange(name, Long.parseLong(start), Long.parseLong(end), unit);
        } catch (Exception e) {
            attributes.add(new Attr(activity.getString(name)));
        }
        return this;
    }

    public AttributesRenderer addTimeRange(final int name, final long start, final long end, final TimeUnit unit) {
        attributes.add(new Attr(activity.getString(name), String.format("%s - %s",
                DateUtils.toStringTime(new Date(unit.toMillis(start))),
                DateUtils.toStringTime(new Date(unit.toMillis(end))))));
        return this;
    }

    private AttributesRenderer addDateTime(final String name, final String value) {
        try {
            final long valueLong = Long.parseLong(value);
            attributes.add(new Attr(name, DateUtils.toString(new Date(valueLong))));
        } catch (NumberFormatException nfe) {
            if (StringUtils.isBlank(value)) {
                attributes.add(new Attr(name));
            } else {
                try {
                    attributes.add(new Attr(name, DateUtils.toString(new Date(value))));
                } catch (Exception e) {
                    attributes.add(new Attr(name));
                }
            }
        } catch (Exception e) {
            attributes.add(new Attr(name));
        }
        return this;
    }

    private AttributesRenderer addDouble(final String name, final String value) {
        try {
            final double valueDouble = Double.parseDouble(value);
            attributes.add(new Attr(name, String.format("%.2f", valueDouble)));
        } catch (Exception e) {
            attributes.add(new Attr(name));
        }
        return this;
    }

    private AttributesRenderer addBoolean(final String name, final String value) {
        this.attributes.add(new Attr(name, Boolean.valueOf(value) ? activity.getString(R.string.mx_yes) : activity.getString(R.string.mx_no)));
        return this;
    }

    public AttributesRenderer addAll(final Set<Attribute> attributes) {
        if (attributes == null || attributes.isEmpty()) {
            return this;
        }
        for (final Attribute attribute : attributes) {
            String title = attribute.getTitle();
            try {
                final JSONObject json = new JSONObject(attribute.getTitle());
                title = (String) json.get("en");
            } catch (Exception e) {
                final int titleResourceId = activity.getResources().getIdentifier(title, "string", activity.getPackageName());
                if (titleResourceId != 0) {
                    title = activity.getString(titleResourceId);
                }
            }
            if (title == null || title.length() == 0) {
                title = attribute.getName();
            }
            if (attribute.getUnit() != null) {
                String unit = attribute.getUnit().trim();
                if (unit.length() > 0) {
                    final int unitResourceId = activity.getResources().getIdentifier(unit, "string", activity.getPackageName());
                    if (unitResourceId != 0) {
                        unit = activity.getString(unitResourceId);
                    }
                    title += " (" + unit + ")";
                }
            }
            final String value = attribute.getValue();
            if ("BOOLEAN".equalsIgnoreCase(attribute.getTypeName())) {
                addBoolean(title, value);
            } else if ("DOUBLE".equalsIgnoreCase(attribute.getTypeName())) {
                addDouble(title, value);
            } else if ("DATETIME".equalsIgnoreCase(attribute.getTypeName())) {
                addDateTime(title, value);
            } else {
                add(title, value);
            }
        }
        return this;
    }

    public AttributesRenderer removeAll() {
        attributes.clear();
        return this;
    }

    private static final class Attr {
        private final String name;
        private final String value;
        private final int color;
        private final Type type;
        private final Drawable icon;
        private final Object[] listeners;

        public Attr(final String name, final String value, final Integer color, final Type type, final Drawable icon, final Object... listeners) {
            this.name = name;
            this.value = value;
            this.color = color;
            this.type = type;
            this.icon = icon;
            this.listeners = listeners;
        }

        public Attr(final String name, final String value, final Type type, final Drawable icon, final Object... listeners) {
            this(name, value, 1, type, icon, listeners);
        }

        public Attr(final String name, final String value, final Type type, final Object... listeners) {
            this(name, value, type, null, listeners);
        }

        public Attr(final String name, final String value, final Object... listeners) {
            this(name, value, Type.TEXT, listeners);
        }

        public Attr(final String name) {
            this(name, "");
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (!(o instanceof Attr)) return false;
            final Attr attribute = (Attr) o;
            return !(name != null ? !name.equals(attribute.name) : attribute.name != null);
        }

        @Override
        public int hashCode() {
            return name != null ? name.hashCode() : 0;
        }

        private enum Type {
            TEXT, PHONE, IMAGE
        }
    }
}