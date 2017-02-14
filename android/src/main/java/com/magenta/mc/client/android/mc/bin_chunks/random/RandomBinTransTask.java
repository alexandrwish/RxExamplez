package com.magenta.mc.client.android.mc.bin_chunks.random;

import com.magenta.mc.client.android.mc.client.resend.ManagedResendableMetadata;
import com.magenta.mc.client.android.mc.client.resend.Resendable;
import com.magenta.mc.client.android.mc.client.resend.ResendableMetadata;
import com.magenta.mc.client.android.mc.settings.Settings;
import com.magenta.mc.client.android.mc.storage.FieldGetter;
import com.magenta.mc.client.android.mc.storage.FieldSetter;
import com.magenta.mc.client.android.mc.storage.StorableMetadata;
import com.magenta.mc.client.android.mc.util.StrUtil;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: const
 * Date: 24.08.12
 * Time: 18:08
 * To change this template use File | Settings | File Templates.
 */
public class RandomBinTransTask extends Resendable {
    public static final ResendableMetadata RESENDABLE_METADATA = new ManagedResendableMetadata("rnd_bin_transmission", true, true, RandomBinaryTransmitter.BIN_TRANS_MGMT);
    public static final int STATE_RECEIVED = 0;
    public static final int STATE_SIGNALLED = 2;
    private static final long serialVersionUID = 106546548L;
    private static final String FIELD_CREATION_DATE = "FIELD_CREATION_DATE";
    private static final String FIELD_URI = "FIELD_URI";
    private static final String FIELD_LOCAL_URI = "FIELD_LOCAL_URI";
    private static final String FIELD_STATE = "FIELD_STATE";
    private static final String FIELD_COUNT = "FIELD_COUNT";
    private static final String FIELD_DELETE_BLOB = "FIELD_DELETE_BLOB";
    private static final String FIELD_POSITION = "FIELD_POSITION";
    private static final String FIELD_READ_WHILE_LAST_PROCESS = "FIELD_READ_WHILE_LAST_PROCESS";
    private static final String FIELD_CHUNK_LENGTH = "FIELD_CHUNK_LENGTH";
    private static final String FIELD_CURRENT_CHUNK = "FIELD_CURRENT_CHUNK";
    private static final String FIELD_CURRENT_CHUNK_ID = "FIELD_CURRENT_CHUNK_ID";
    private Date creationDate;
    private String uri;
    private String localUri;
    private int count = 0;
    private int state = STATE_RECEIVED;
    private int position = 0;
    private int readWhileLastProcess = 0;
    private int chunkLength = 0;
    private int currentChunk = 0;
    private String currentChunkId;
    private boolean deleteBlob; // whether we need to delete blob upon transmission

    public RandomBinTransTask() {
    }

    public RandomBinTransTask(String uri, String localUri, boolean deleteBlob) {
        this.creationDate = Settings.get().getCurrentDate();
        this.uri = uri;
        this.localUri = localUri;
        this.deleteBlob = deleteBlob;
    }

    public RandomBinTransTask(String uri, String localUri) {
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

    public boolean isDeleteBlob() {
        return deleteBlob;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getReadWhileLastProcess() {
        return readWhileLastProcess;
    }

    public void setReadWhileLastProcess(int readWhileLastProcess) {
        this.readWhileLastProcess = readWhileLastProcess;
    }

    public int getChunkLength() {
        return chunkLength;
    }

    public void setChunkLength(int chunkLength) {
        this.chunkLength = chunkLength;
    }

    public int getCurrentChunk() {
        return currentChunk;
    }

    public void setCurrentChunk(int currentChunk) {
        this.currentChunk = currentChunk;
    }

    public String getCurrentChunkId() {
        return currentChunkId;
    }

    public void setCurrentChunkId(String currentChunkId) {
        this.currentChunkId = currentChunkId;
    }

    public StorableMetadata getMetadata() {
        return RESENDABLE_METADATA;
    }

    public String getId() {
        return escapeUri(uri);
    }

    public void setId(String id) {
        //To change body of implemented methods use File | Settings | File Templates.
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
                new FieldSetter(FIELD_POSITION) {
                    public void setValue(Object value) {
                        position = ((Integer) value).intValue();
                    }
                },
                new FieldSetter(FIELD_READ_WHILE_LAST_PROCESS) {
                    public void setValue(Object value) {
                        readWhileLastProcess = ((Integer) value).intValue();
                    }
                },
                new FieldSetter(FIELD_CHUNK_LENGTH) {
                    public void setValue(Object value) {
                        chunkLength = ((Integer) value).intValue();
                    }
                },
                new FieldSetter(FIELD_CURRENT_CHUNK) {
                    public void setValue(Object value) {
                        currentChunk = ((Integer) value).intValue();
                    }
                },
                new FieldSetter(FIELD_CURRENT_CHUNK_ID) {
                    public void setValue(Object value) {
                        currentChunkId = (String) value;
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
                new FieldGetter(FIELD_POSITION) {
                    public Object getValue() {
                        return new Integer(position);
                    }
                },
                new FieldGetter(FIELD_READ_WHILE_LAST_PROCESS) {
                    public Object getValue() {
                        return new Integer(readWhileLastProcess);
                    }
                },
                new FieldGetter(FIELD_CHUNK_LENGTH) {
                    public Object getValue() {
                        return new Integer(chunkLength);
                    }
                },
                new FieldGetter(FIELD_CURRENT_CHUNK) {
                    public Object getValue() {
                        return new Integer(currentChunk);
                    }
                },
                new FieldGetter(FIELD_CURRENT_CHUNK_ID) {
                    public Object getValue() {
                        return currentChunkId;
                    }
                }
        };
    }

    public boolean send() {
        return false;
    }
}
