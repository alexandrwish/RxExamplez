package com.magenta.mc.client.ui;

import com.magenta.mc.client.MobileApp;
import com.magenta.mc.client.components.MCTimerTask;
import com.magenta.mc.client.components.dialogs.SynchronousCallback;
import com.magenta.mc.client.log.MCLoggerFactory;
import com.magenta.mc.client.setup.Setup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: const
 * Date: 28.06.11
 * Time: 11:19
 * To change this template use File | Settings | File Templates.
 */
public abstract class TaskUpdateController {

    private final List taskActions = new ArrayList();
    protected boolean isInfoDialogVisible;
    private long WAIT_TIME = 1000;

    public void processTaskAction(final String referenceId, final int actionType) {
        MobileApp.runTask(new Runnable() {
            public void run() {
                scheduleCumulativeAction(new TaskUpdateAction(referenceId, actionType), new Runnable() {
                    public void run() {
                        process();
                    }
                }, WAIT_TIME);
            }
        });
    }

    private void process() {
        playIncomingMessageSound();

        showDialog();
        recognizeScreenSwitchTo();
    }

    protected void showDialog() {
        if (!isInfoDialogVisible) {
            MCLoggerFactory.getLogger(getClass()).info("Show info dialog");
            isInfoDialogVisible = true;
            Setup.get().getUI().getDialogManager().asyncMessageSafe(
                    MobileApp.localize("msg.schedule.updated.title"),
                    MobileApp.localize("msg.schedule.updated.message"),
                    new SynchronousCallback() {
                        public void done(boolean ok) {
                            isInfoDialogVisible = false;
                            switchToNewScreen();
                        }
                    }
            );
        }
    }

    protected List getTaskActions() {
        return taskActions;
    }

    protected abstract void recognizeScreenSwitchTo();

    protected abstract void switchToNewScreen();

    protected abstract void playIncomingMessageSound();

    /*
       we receive incoming / canceled / updated jobs one by one.
       waiting 'timeout' milliseconds for the next job to arrive
       not to show multiple notifications upon receiving multiple jobs
    */
    private void scheduleCumulativeAction(final TaskUpdateAction nextElement, Runnable action, long timeout) {
        synchronized (taskActions) {
            int prevCount = taskActions.size();
            taskActions.add(nextElement);
            if (prevCount > 0) {
                // previous "addition is still waiting, don't schedule new timer"
                return;
            }
        }
        executeCumulativeAction(action, timeout);
    }

    private void executeCumulativeAction(final Runnable action, final long timeout) {
        final int previousAddedCount = taskActions.size();
        MobileApp.getInstance().getTimer().schedule(new MCTimerTask() {
            public void runTask() {
                synchronized (taskActions) {
                    if (previousAddedCount < taskActions.size()) {
                        // more jobs added, waiting again
                        executeCumulativeAction(action, timeout);
                    } else {
                        action.run();
                        taskActions.clear();
                    }
                }
            }
        }, timeout);
    }
}
