package com.magenta.mc.client.android.storage;

/**
 * User: stukov
 * Date: 17.05.2010
 * Time: 20:25:35
 */
public abstract class FieldSetter {

    private final String name;

    public FieldSetter(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract void setValue(Object value);

}