package com.magenta.mc.client.android.ui.adapter;

/**
 * Project: Santa-cruz
 * Author:  Alexandr Komarov
 * Created: 25.03.13 9:31
 * <p/>
 * Copyright (c) 1999-2013 Magenta Corporation Ltd. All Rights Reserved.
 * Magenta Technology proprietary and confidential.
 * Use is subject to license terms.
 * <p/>
 * $Id$
 */
public interface WheelAdapter {
    /**
     * Gets items count
     *
     * @return the count of wheel items
     */
    int getCount();

    /**
     * Gets a wheel item by index.
     *
     * @param index the item index
     * @return the wheel item text or null
     */
    String getItem(int index);

    /**
     * Gets maximum item length. It is used to determine the wheel width.
     * If -1 is returned there will be used the default wheel width.
     *
     * @return the maximum item length or -1
     */
    int getMaximumLength();
}