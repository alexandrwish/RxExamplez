package com.magenta.maxunits.mobile.dlib.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Matrix;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.magenta.maxunits.distribution.R;
import com.magenta.maxunits.mobile.dlib.activity.OrderItemActivity;
import com.magenta.maxunits.mobile.dlib.entity.OrderItemEntity;
import com.magenta.maxunits.mobile.dlib.entity.OrderItemStatus;
import com.magenta.maxunits.mobile.dlib.utils.StringUtils;

import java.util.List;

public class ExpandableOrderItemAdapter extends DistributionArrayAdapter<OrderItemEntity> {

    static final Integer MAX_BARCODE_LENGTH = 17;
    static final Integer MAX_NAME_LENGTH = 11;

    public ExpandableOrderItemAdapter(Activity context, List<OrderItemEntity> entities) {
        super(context, R.layout.item_expandable_order_item, entities);
    }

    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = context.getLayoutInflater().inflate(R.layout.item_expandable_order_item, null);
            viewHolder = new ViewHolder();
            viewHolder.expandable = (ImageView) view.findViewById(R.id.exp_btn);
            viewHolder.name = (TextView) view.findViewById(R.id.name);
            viewHolder.value = (TextView) view.findViewById(R.id.value);
            viewHolder.clear = (ImageView) view.findViewById(R.id.clear_btn);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        final OrderItemEntity entity = list.get(position);
        boolean canExpand = (entity.getName() != null && entity.getName().length() > MAX_NAME_LENGTH) || (entity.getBarcode() != null && entity.getBarcode().length() > MAX_BARCODE_LENGTH);
        viewHolder.expandable.setVisibility(canExpand ? View.VISIBLE : View.INVISIBLE);
        viewHolder.name.setText(getShortText(entity.getName(), MAX_NAME_LENGTH));
        viewHolder.value.setText(getShortText(entity.getBarcode(), MAX_BARCODE_LENGTH));
        viewHolder.expand = false;
        viewHolder.clear.setVisibility(entity.getStatus().equals(OrderItemStatus.ADDED_BY_DRIVER) ? View.VISIBLE : View.INVISIBLE);
        viewHolder.clear.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ((OrderItemActivity) context).removeOrderItem(entity);
            }
        });
        switch (entity.getStatus()) {
            case ADDED_BY_DRIVER: {
                viewHolder.value.setTextColor(Color.YELLOW);
                break;
            }
            case CHECKED: {
                viewHolder.value.setTextColor(Color.GREEN);
                break;
            }
            default: {
                viewHolder.value.setTextColor(Color.WHITE);
            }
        }
        return view;
    }

    private void rotateImage(ImageView view, boolean expand) {
        Matrix matrix = new Matrix();
        view.setScaleType(ImageView.ScaleType.MATRIX);
        matrix.postRotate(expand ? -90 : 0, view.getWidth() / 2, view.getHeight() / 2);
        view.setImageMatrix(matrix);
    }

    private String getShortText(String s, int length) {
        if (StringUtils.isBlank(s)) return "";
        if (s.length() <= length) return s;
        return s.substring(0, length - 3).trim() + "...";
    }

    public void onClick(View view, int i) {
        OrderItemEntity entity = list.get(i);
        if ((entity.getName() != null && entity.getName().length() > MAX_NAME_LENGTH) || (entity.getBarcode() != null && entity.getBarcode().length() > MAX_BARCODE_LENGTH)) {
            ViewHolder holder = (ViewHolder) view.getTag();
            holder.expand = !holder.expand;
            holder.name.setText(holder.expand ? entity.getName() : getShortText(entity.getName(), MAX_NAME_LENGTH));
            holder.value.setText(holder.expand ? entity.getBarcode() : getShortText(entity.getBarcode(), MAX_BARCODE_LENGTH));
            rotateImage(holder.expandable, holder.expand);
        }
    }

    private class ViewHolder {
        boolean expand;
        ImageView expandable;
        TextView name;
        TextView value;
        ImageView clear;
    }
}