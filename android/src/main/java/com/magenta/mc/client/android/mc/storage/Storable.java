package com.magenta.mc.client.android.mc.storage;

import com.magenta.mc.client.android.mc.log.MCLoggerFactory;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.Map;

public abstract class Storable implements Externalizable {

    private static final long serialVersionUID = -1920107221860824214L;

    public abstract StorableMetadata getMetadata();

    public abstract String getId();

    public abstract FieldSetter[] getSetters();

    public abstract FieldGetter[] getGetters();

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        final int fieldCount = in.readInt();
        Map<String, Object> map = new HashMap<>();
        for (int i = 0; i < fieldCount; i++) {
            final String name = in.readObject().toString();
            try {
                final Object o = in.readObject();
                map.put(name, o);
            } catch (Exception e) {
                MCLoggerFactory.getLogger(getClass()).error(e.getMessage(), e);
            }
        }

        setFieldsByMap(map);
    }

    private void setFieldsByMap(Map map) {
        final FieldSetter[] setters = getSetters();
        for (FieldSetter setter : setters) {
            setter.setValue(map.get(setter.getName()));
        }
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        FieldGetter[] getters = getGetters();
        out.writeInt(getters.length);
        for (FieldGetter getter : getters) {
            out.writeObject(getter.getName());
            out.writeObject(getter.getValue());
        }
    }
}