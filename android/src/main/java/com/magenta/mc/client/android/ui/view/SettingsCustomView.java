package com.magenta.mc.client.android.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.magenta.mc.client.android.McAndroidApplication;
import com.magenta.mc.client.android.R;
import com.magenta.mc.client.android.db.CacheDBHelper;
import com.magenta.mc.client.android.db.dao.TileCacheDAO;

import java.io.File;
import java.sql.SQLException;

public class SettingsCustomView extends LinearLayout {

    protected Button clearCache;

    public SettingsCustomView(Context context) {
        super(context);
        initView(context);
    }

    public SettingsCustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    protected void initView(final Context context) {
        ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_preferences, this);
        this.clearCache = (Button) findViewById(R.id.clear_cache);
        this.clearCache.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                try {
                    TileCacheDAO.getInstance().removeCacheTiles(System.currentTimeMillis());
                    updateInfoField();
                } catch (SQLException ignore) {
                }
            }
        });
        updateInfoField();
    }

    private void updateInfoField() {
        ((TextView) findViewById(R.id.cache_space)).setText((new File(McAndroidApplication.getInstance().getDBAdapter().getDB(CacheDBHelper.DATABASE_NAME).getPath()).length() / 1024 / 1024) + " mb");
    }
}