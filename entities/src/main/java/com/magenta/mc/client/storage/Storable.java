package com.magenta.mc.client.storage;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.Map;

/**
 * Objects of subclasses can be saved and loaded using Storage
 * <p>
 * User: stukov
 * Date: 17.05.2010
 * Time: 17:47:45
 */
public abstract class Storable implements Externalizable {

    private static final long serialVersionUID = -1920107221860824214L;

    public abstract StorableMetadata getMetadata();

    public abstract String getId();

    public abstract FieldSetter[] getSetters();

    public abstract FieldGetter[] getGetters();

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        final int fieldCount = in.readInt();
        Map map = new HashMap();
        for (int i = 0; i < fieldCount; i++) {
            final String name = in.readObject().toString();
            try {
                final Object o = in.readObject();
                map.put(name, o);
            } catch (Exception e) {
                System.out.println("unable to read field " + name + "in class " + getClass().getName());
//                MCLoggerFactory.getLogger(getClass()).error("unable to read field "+name + "in class "+getClass().getName());
                e.printStackTrace();
            }
        }

        setFieldsByMap(map);
    }

    protected void setFieldsByMap(Map map) {
        final FieldSetter[] setters = getSetters();
        for (int i = 0; i < setters.length; i++) {
            FieldSetter setter = setters[i];
            setter.setValue(map.get(setter.getName()));
        }
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        FieldGetter[] getters = getGetters();

        out.writeInt(getters.length);
        for (int i = 0; i < getters.length; i++) {
            out.writeObject(getters[i].getName());
            out.writeObject(getters[i].getValue());
        }
    }

}
