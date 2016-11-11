package com.magenta.mc.client.components.dialogs.manager;

import com.magenta.mc.client.components.dialogs.DialogCallback;
import com.magenta.mc.client.util.FutureRunnable;

/**
 * @author: Petr Popov
 * Created: 14.10.11 14:08
 */
public interface IDialogManager {
    // runs the given task on consecutive thread pool (consequently with other async dialog tasks)
    // synchronizing on dialogSync mutex
    void runAsyncDialogTask(Runnable task);

    // runs the given task synchronizing on dialogSync mutex
    void runDialogTask(Runnable task);

    // runs the given task synchronizing on dialogSync mutex
    void runDialogTask(FutureRunnable taskWithFuture);

    /**
     * shows confirmation dialog synchronously WITHOUT synchronization on common mutex
     * <p>
     * be careful - using this method you have to manually synchronize your execution with common mutex
     * so no other dialogs or frame switchings could happen while the dialog is open
     *
     * @param title   - dialog title
     * @param message - dialog message
     * @return true if user confirms
     */
    boolean confirmUnsafe(String title, String message);

    /**
     * shows confirmation dialog synchronously WITHOUT synchronization on common mutex
     * <p>
     * be careful - using this method you have to manually synchronize your execution with common mutex
     * so no other dialogs or frame switchings could happen while the dialog is open
     *
     * @param title    - dialog title
     * @param message  - dialog message
     * @param callback - will be called once dialog is closed, asynchronously (outside the critical section)
     * @return true if user confirms
     */
    boolean confirmUnsafe(String title, String message, DialogCallback callback);

    /**
     * shows confirmation dialog synchronously WITH synchronization on common mutex
     * <p>
     * be careful - any logic requiring this mutex (e.g. switching frames) will wait untill dialog closes
     *
     * @param title   - dialog title
     * @param message - dialog message
     * @return true if user confirms
     */
    boolean confirmSafe(String title, String message);

    /**
     * shows confirmation dialog synchronously WITH synchronization on common mutex
     * <p>
     * be careful - any logic requiring this mutex (e.g. switching frames) will wait untill dialog closes
     *
     * @param title    - dialog title
     * @param message  - dialog message
     * @param callback - will be called once dialog is closed, asynchronously (outside the critical section)
     * @return true if user confirms
     */
    boolean confirmSafe(String title, String message, DialogCallback callback);

    /**
     * shows confirmation dialog asynchronously WITH synchronization on common mutex
     * <p>
     * be careful - any logic requiring this mutex (e.g. switching frames) will wait untill dialog closes
     *
     * @param title   - dialog title
     * @param message - dialog message
     */
    void asyncConfirmSafe(String title, String message);

    /**
     * shows confirmation dialog asynchronously WITH synchronization on common mutex
     * <p>
     * be careful - any logic requiring this mutex (e.g. switching frames) will wait untill dialog closes
     *
     * @param title    - dialog title
     * @param message  - dialog message
     * @param callback - will be called once dialog is closed, asynchronously (outside the critical section)
     */
    void asyncConfirmSafe(String title, String message, DialogCallback callback);

    /**
     * shows message dialog synchronously WITHOUT synchronization on common mutex
     * <p>
     * be careful - using this method you have to manually synchronize your execution with common mutex
     * so no other dialogs or frame switchings could happen while the dialog is open
     *
     * @param title - dialog title
     * @param msg   - dialog message
     */
    void messageUnsafe(String title, String msg);

    /**
     * shows message dialog synchronously WITHOUT synchronization on common mutex
     * <p>
     * be careful - using this method you have to manually synchronize your execution with common mutex
     * so no other dialogs or frame switchings could happen while the dialog is open
     *
     * @param title    - dialog title
     * @param msg      - dialog message
     * @param callback - will be called once dialog is closed, asynchronously (outside the critical section)
     */
    void messageUnsafe(String title, String msg, DialogCallback callback);

    /**
     * shows message dialog synchronously WITH synchronization on common mutex
     * <p>
     * be careful - any logic requiring this mutex (e.g. switching frames) will wait untill dialog closes
     *
     * @param title - dialog title
     * @param msg   - dialog message
     */
    void messageSafe(String title, String msg);

    /**
     * shows message dialog synchronously WITH synchronization on common mutex
     * <p>
     * be careful - any logic requiring this mutex (e.g. switching frames) will wait untill dialog closes
     *
     * @param title    - dialog title
     * @param msg      - dialog message
     * @param callback - will be called once dialog is closed, asynchronously (outside the critical section)
     */
    void messageSafe(String title, String msg, DialogCallback callback);

    /**
     * shows confirmation dialog asynchronously WITH synchronization on common mutex
     * <p>
     * be careful - any logic requiring this mutex (e.g. switching frames) will wait untill dialog closes
     *
     * @param title - dialog title
     * @param msg   - dialog message
     */
    void asyncMessageSafe(String title, String msg);

    /**
     * shows confirmation dialog ssynchronously WITH synchronization on common mutex
     * <p>
     * be careful - any logic requiring this mutex (e.g. switching frames) will wait untill dialog closes
     *
     * @param title    - dialog title
     * @param msg      - dialog message
     * @param callback - will be called once dialog is closed, asynchronously (outside the critical section)
     */
    void asyncMessageSafe(String title, String msg, DialogCallback callback);

    void acquireDialogSync() throws InterruptedException;

    void releaseDialogSync();

    void ShowDialogsAgain(Object o);
}
