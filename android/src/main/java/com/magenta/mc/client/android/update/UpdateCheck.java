package com.magenta.mc.client.android.update;

/**
 * @autor Petr Popov
 * Created 11.03.12 15:34
 */
public interface UpdateCheck {

    void check();

    void updateReported(String platform, String application);

    void complete(final Boolean available);

    boolean checkDownloadedUpdate();

    void installDownloadedUpdate();
}
