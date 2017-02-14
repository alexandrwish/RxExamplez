package jaba.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: const
 * Date: 12.12.11
 * Time: 14:04
 * To change this template use File | Settings | File Templates.
 */
public class PropertyResourceBundleUtf8 extends ResourceBundle {
    private Map lookup;

    /**
     * Creates a property resource bundle.
     *
     * @param stream property file to read from.
     */
    public PropertyResourceBundleUtf8(InputStream stream) throws IOException {
        Properties properties = new PropertiesUtf8();
        properties.load(stream);
        lookup = new HashMap(properties);
    }

    // Implements java.jaba.util.ResourceBundle.handleGetObject; inherits javadoc specification.
    public Object handleGetObject(String key) {
        if (key == null) {
            throw new NullPointerException();
        }
        return lookup.get(key);
    }

    // ==================privates====================

    /**
     * Implementation of ResourceBundle.getKeys.
     */
    public Enumeration getKeys() {
        ResourceBundle parent = this.parent;
        Set keys = new HashSet(lookup.keySet());
        if (parent != null && parent.getKeys().hasMoreElements()) {
            Enumeration parentKeys = parent.getKeys();
            while (parentKeys.hasMoreElements()) {
                keys.add(parentKeys.nextElement());
            }
        }
        final Iterator iterator = keys.iterator();
        return new Enumeration() {
            public boolean hasMoreElements() {
                return iterator.hasNext();
            }

            public Object nextElement() {
                return iterator.next();
            }
        };
    }
}
