package com.magenta.mc.client.android.mc.storage;

/**
 * User: stukov
 * Date: 17.05.2010
 * Time: 20:25:35
 */
public abstract class FieldGetter {

    private final String name;

    public FieldGetter(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract Object getValue();

}

