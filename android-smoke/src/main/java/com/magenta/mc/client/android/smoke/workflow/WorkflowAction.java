package com.magenta.mc.client.android.smoke.workflow;

import android.view.View;

/**
 * Project: Santa-cruz
 * Author:  Alexandr Komarov
 * Created: 18.03.13 17:16
 * <p/>
 * Copyright (c) 1999-2013 Magenta Corporation Ltd. All Rights Reserved.
 * Magenta Technology proprietary and confidential.
 * Use is subject to license terms.
 * <p/>
 * $Id$
 */
public abstract class WorkflowAction implements View.OnClickListener {

    public Runnable getFutureRunnable(final View v) {
        return new Runnable() {
            @Override
            public void run() {

            }
        };
    }
}
