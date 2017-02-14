package com.magenta.mc.client.android.mc.bin_chunks;

import com.magenta.mc.client.android.mc.client.XMPPClient;
import com.magenta.mc.client.android.mc.client.resend.Resendable;
import com.magenta.mc.client.android.mc.client.resend.ResendableMetadata;
import com.magenta.mc.client.android.mc.log.MCLoggerFactory;
import com.magenta.mc.client.android.mc.setup.Setup;
import com.magenta.mc.client.android.mc.storage.FieldGetter;
import com.magenta.mc.client.android.mc.storage.FieldSetter;
import com.magenta.mc.client.android.mc.storage.StorableMetadata;
import com.magenta.mc.client.android.mc.xmpp.extensions.rpc.DefaultRpcResponseHandler;

/**
 * Created by IntelliJ IDEA.
 * User: const
 * Date: 08.06.12
 * Time: 18:46
 * To change this template use File | Settings | File Templates.
 */
public class BinaryChunkResendable extends Resendable {
    public static final ResendableMetadata METADATA = new ResendableMetadata("bin_chunks", true, true);
    private static final long serialVersionUID = 10564645L;
    private static final String FIELD_ID = "FIELD_ID";
    private static final String FIELD_URL = "FIELD_URL";
    private static final String FIELD_ORDER_NUMBER = "FIELD_ORDER_NUMBER";
    private static final String FIELD_ERROR_OCCURED = "FIELD_ERROR_OCCURED";
    private static final String FIELD_ERROR_CODE = "FIELD_ERROR_CODE";
    private static final String FIELD_DATA = "FIELD_DATA";

    private String id;
    private String uri;
    private int orderNumber;
    private boolean errorOccured;
    private int errorCode = -1;
    private byte[] data;

    public boolean send() {
        if (XMPPClient.getInstance().isLoggedIn()) {
            BinaryTransmissionTask task = (BinaryTransmissionTask) Setup.get().getStorage().load(BinaryTransmissionTask.STORABLE_METADATA, BinaryTransmissionTask.escapeUri(uri));
            if (task == null) {
                MCLoggerFactory.getLogger(BinaryTransmitter.class).warn("Binary task not found, removing bad chunk: " + uri + ", " + orderNumber);
                Setup.get().getStorage().delete(METADATA, id);
                return false;
            }
            if (BinaryTransmissionTask.STATE_SIGNALLED == task.getState()) {
                DefaultRpcResponseHandler.binChunk(this);
                return true;
            } else {
                MCLoggerFactory.getLogger(BinaryTransmitter.class).debug("Skip bin chunk (trans. not signalled)" + uri + ", " + orderNumber);
                return false;
            }
        } else {
            return false;
        }
    }

    public StorableMetadata getMetadata() {
        return METADATA;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    public boolean isErrorOccured() {
        return errorOccured;
    }

    public void setErrorOccured(boolean errorOccured) {
        this.errorOccured = errorOccured;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public FieldSetter[] getSetters() {
        return new FieldSetter[]{
                new FieldSetter(FIELD_ID) {
                    public void setValue(Object value) {
                        id = (String) value;
                    }
                },
                new FieldSetter(FIELD_URL) {
                    public void setValue(Object value) {
                        uri = (String) value;
                    }
                },
                new FieldSetter(FIELD_ORDER_NUMBER) {
                    public void setValue(Object value) {
                        orderNumber = ((Integer) value).intValue();
                    }
                },
                new FieldSetter(FIELD_ERROR_OCCURED) {
                    public void setValue(Object value) {
                        errorOccured = ((Boolean) value).booleanValue();
                    }
                },
                new FieldSetter(FIELD_ERROR_CODE) {
                    public void setValue(Object value) {
                        errorCode = ((Integer) value).intValue();
                    }
                },
                new FieldSetter(FIELD_DATA) {
                    public void setValue(Object value) {
                        setData((byte[]) value);
                    }
                }
        };
    }

    public FieldGetter[] getGetters() {
        return new FieldGetter[]{
                new FieldGetter(FIELD_ID) {
                    public Object getValue() {
                        return id;
                    }
                },
                new FieldGetter(FIELD_URL) {
                    public Object getValue() {
                        return uri;
                    }
                },
                new FieldGetter(FIELD_ORDER_NUMBER) {
                    public Object getValue() {
                        return new Integer(orderNumber);
                    }
                },
                new FieldGetter(FIELD_ERROR_OCCURED) {
                    public Object getValue() {
                        return (errorOccured) ? Boolean.TRUE : Boolean.FALSE;
                    }
                },
                new FieldGetter(FIELD_ERROR_CODE) {
                    public Object getValue() {
                        return new Integer(errorCode);
                    }
                },
                new FieldGetter(FIELD_DATA) {
                    public Object getValue() {
                        return getData();
                    }
                }
        };
    }


}
