package com.magenta.maxunits.mobile.ui;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * @author Sergey Grachev
 */
public class LeftRightGestureDetector extends GestureDetector {
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;

    public LeftRightGestureDetector(final Context context, final Listener listener) {
        super(context, new SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                try {
                    if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                        return false;

                    if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                        listener.onLeft();
                        return true;
                    } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                        listener.onRight();
                        return true;
                    }
                } catch (Exception e) {
                    // nothing
                }
                return false;
            }
        });
    }

    public interface Listener {
        void onLeft();

        void onRight();
    }

    public static class ListenerAdapter implements Listener {
        public void onLeft() {
        }

        public void onRight() {
        }
    }
}
