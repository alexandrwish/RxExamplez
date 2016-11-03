package com.magenta.mc.client.android.smoke.activity.delegate;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.magenta.mc.client.android.smoke.R;
import com.magenta.mc.client.android.smoke.activity.SmokeActivityInterface;
import com.magenta.mc.client.android.ui.delegate.McActivityDelegate;
import com.magenta.mc.client.client.DriverStatus;
import com.magenta.mc.client.log.MCLoggerFactory;
import com.magenta.mc.client.setup.Setup;

/**
 * Project: Santa-cruz
 * Author:  Alexandr Komarov
 * Created: 27.12.13 10:11
 * <p/>
 * Copyright (c) 1999-2013 Magenta Corporation Ltd. All Rights Reserved.
 * Magenta Technology proprietary and confidential.
 * Use is subject to license terms.
 * <p/>
 * $Id$
 */

public class SmokeActivityDelegate extends McActivityDelegate {

    protected boolean visible;

    protected Handler handler = new Handler(Looper.getMainLooper());

    private SmokeActivityInterface getSmokeActivity() {
        return (SmokeActivityInterface) getActivity();
    }

    @Override
    public void onResume() {
        super.onResume();
        changeVisibleFlag(true);
        LOG.info(getActivity().getLocalClassName() + ": show info dialog if needed");
        Setup.get().getUI().getDialogManager().ShowDialogsAgain(getActivity());
    }

    @Override
    public void onPause() {
        super.onPause();
        changeVisibleFlag(false);
    }

    @Override
    public void setDriverStatus(final DriverStatus driverStatus) {
        super.setDriverStatus(driverStatus);
        if (isHasTitleBar()) {
            getHandler().post(new Runnable() {
                @Override
                public void run() {
                    ImageView imageView = (ImageView) getActivity().findViewById(R.id.mcTitleBarRightIndicator);
                    if (imageView != null) {
                        imageView.setImageResource(driverStatus.isOnline()
                                ? R.drawable.mc_img_indicator_green_light
                                : R.drawable.mc_img_indicator_red_light);
                    }
                }
            });
        }
    }

    protected boolean isHasTitleBar() {
        return getSmokeActivity().isHasTitleBar();
    }

    public boolean onCreateOptionMenu(Menu menu) {
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(getMenu(), menu);
        return true;
    }

    public void buildTitleBarMenu(Integer menu, int buttonId, int iconId) {
        final ImageButton button = (ImageButton) getActivity().findViewById(buttonId);

        boolean hideButton = true;
        if (menu != null && button != null) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    getActivity().registerForContextMenu(v);
                    getActivity().openContextMenu(v);
                    getActivity().unregisterForContextMenu(v);
                }
            });
            hideButton = false;
        }

        if (hideButton) {
            final ImageView icon = (ImageView) getActivity().findViewById(iconId);
            if (icon != null) {
                icon.setVisibility(View.GONE);
            }
            if (button != null) {
                button.setEnabled(false);
            }
        }
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.mcTitleBarLeftButton) {
            inflateContextMenu(menu, getTitleBarLeftMenu());
        } else if (v.getId() == R.id.mcTitleBarRightButton) {
            inflateContextMenu(menu, getTitleBarRightMenu());
        }
    }

    private void inflateContextMenu(ContextMenu menu, Integer menuId) {
        if (menuId != null) {
            final MenuInflater inflater = getActivity().getMenuInflater();
            inflater.inflate(menuId, menu);
        }
    }


    /**
     * @return null if not has menu or resource id
     */
    protected Integer getTitleBarLeftMenu() {
        return getSmokeActivity().getTitleBarLeftMenu();
    }

    /**
     * @return null if not has menu or resource id
     */
    protected Integer getTitleBarRightMenu() {
        return getSmokeActivity().getTitleBarRightMenu();
    }

    /**
     * @return null if not has menu or resource id
     */
    protected Integer getMenu() {
        return getSmokeActivity().getMenu();
    }

    private void changeVisibleFlag(boolean isVisible) {
        visible = isVisible;
    }

    public boolean isVisible() {
        return visible;
    }

    public Handler getHandler() {
        return handler;
    }

    public void onCreate(Bundle savedInstanceState) {
        final boolean featureSupported = getActivity().requestWindowFeature(isHasTitleBar() ? Window.FEATURE_CUSTOM_TITLE : Window.FEATURE_NO_TITLE);

        getSmokeActivity().initActivity(savedInstanceState);

        if (featureSupported && isHasTitleBar()) {
            getActivity().getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.mc_titlebar);
        }

        getActivity().setTitle(getSmokeActivity().getCustomTitle());

        buildTitleBarMenu(getTitleBarLeftMenu(), R.id.mcTitleBarLeftButton, R.id.mcTitleBarLeftIcon);
        buildTitleBarMenu(getTitleBarRightMenu(), R.id.mcTitleBarRightButton, R.id.mcTitleBarRightIcon);
        MCLoggerFactory.getLogger(getClass()).trace(getActivity().getLocalClassName() + ": onCreate.");
    }
}
