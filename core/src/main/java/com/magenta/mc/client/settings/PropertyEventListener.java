package com.magenta.mc.client.settings;

/**
 * Created 18.05.2010
 *
 * @author Konstantin Pestrikov
 */
public interface PropertyEventListener {
    void propertyChanged(String property, String oldValue, String newValue);
}
