package com.magenta.mc.client.android.mc.bin_chunks;

import com.magenta.mc.client.android.mc.settings.Settings;
import com.magenta.mc.client.android.mc.storage.FieldGetter;
import com.magenta.mc.client.android.mc.storage.FieldSetter;
import com.magenta.mc.client.android.mc.storage.Storable;
import com.magenta.mc.client.android.mc.storage.StorableMetadata;
import com.magenta.mc.client.android.mc.util.StrUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: const
 * Date: 08.06.12
 * Time: 18:46
 * To change this template use File | Settings | File Templates.
 */
public class BinaryTransmissionTask extends Storable {
    public static final StorableMetadata STORABLE_METADATA = new StorableMetadata("bin_transmission", true);
    public static final int STATE_RECEIVED = 0;
    public static final int STATE_DONE = 1;
    public static final int STATE_SIGNALLED = 2;
    private static final long serialVersionUID = 106546548L;
    private static final String FIELD_CREATION_DATE = "FIELD_CREATION_DATE";
    private static final String FIELD_URI = "FIELD_URI";
    private static final String FIELD_LOCAL_URI = "FIELD_LOCAL_URI";
    private static final String FIELD_STATE = "FIELD_STATE";
    private static final String FIELD_COUNT = "FIELD_COUNT";
    private static final String FIELD_DELETE_BLOB = "FIELD_DELETE_BLOB";
    private static final String FIELD_CHUNKS = "FIELD_CHUNKS";
    private Date creationDate;
    private String uri;
    private String localUri;
    private int count = -1;
    private int state = STATE_RECEIVED;
    private List chunks = new ArrayList(); // List of Strings
    private boolean deleteBlob; // whether we need to delete blob upon transmission

    public BinaryTransmissionTask() {
    }

    public BinaryTransmissionTask(String uri, String localUri, boolean deleteBlob) {
        this.creationDate = Settings.get().getCurrentDate();
        this.uri = uri;
        this.localUri = localUri;
        this.deleteBlob = deleteBlob;
    }

    public BinaryTransmissionTask(String uri, String localUri) {
        this(uri, localUri, false);
    }

    /**
     * Replace '/' by '_separator_' to store all tasks in same dir
     */
    public static String escapeUri(String uri) {
        return StrUtil.replace(uri, "/", "_spr_");
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getLocalUri() {
        return localUri;
    }

    public void setLocalUri(String localUri) {
        this.localUri = localUri;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public List getChunks() {
        return chunks;
    }

    public boolean isDeleteBlob() {
        return deleteBlob;
    }

    public StorableMetadata getMetadata() {
        return STORABLE_METADATA;
    }

    public String getId() {
        return escapeUri(uri);
    }

    public FieldSetter[] getSetters() {
        return new FieldSetter[]{
                new FieldSetter(FIELD_CREATION_DATE) {
                    public void setValue(Object value) {
                        creationDate = (Date) value;
                    }
                },
                new FieldSetter(FIELD_URI) {
                    public void setValue(Object value) {
                        uri = (String) value;
                    }
                },
                new FieldSetter(FIELD_LOCAL_URI) {
                    public void setValue(Object value) {
                        localUri = (String) value;
                    }
                },
                new FieldSetter(FIELD_COUNT) {
                    public void setValue(Object value) {
                        count = ((Integer) value).intValue();
                    }
                },
                new FieldSetter(FIELD_STATE) {
                    public void setValue(Object value) {
                        state = ((Integer) value).intValue();
                    }
                },
                new FieldSetter(FIELD_DELETE_BLOB) {
                    public void setValue(Object value) {
                        deleteBlob = ((Boolean) value).booleanValue();
                    }
                },
                new FieldSetter(FIELD_CHUNKS) {
                    public void setValue(Object value) {
                        chunks = (List) value;
                    }
                }
        };
    }

    public FieldGetter[] getGetters() {
        return new FieldGetter[]{
                new FieldGetter(FIELD_CREATION_DATE) {
                    public Object getValue() {
                        return creationDate;
                    }
                },
                new FieldGetter(FIELD_URI) {
                    public Object getValue() {
                        return uri;
                    }
                },
                new FieldGetter(FIELD_LOCAL_URI) {
                    public Object getValue() {
                        return localUri;
                    }
                },
                new FieldGetter(FIELD_COUNT) {
                    public Object getValue() {
                        return new Integer(count);
                    }
                },
                new FieldGetter(FIELD_STATE) {
                    public Object getValue() {
                        return new Integer(state);
                    }
                },
                new FieldGetter(FIELD_DELETE_BLOB) {
                    public Object getValue() {
                        return Boolean.valueOf(deleteBlob);
                    }
                },
                new FieldGetter(FIELD_CHUNKS) {
                    public Object getValue() {
                        return chunks;
                    }
                }
        };
    }
}
