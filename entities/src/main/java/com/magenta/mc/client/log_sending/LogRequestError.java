package com.magenta.mc.client.log_sending;

import com.magenta.mc.client.client.resend.Resendable;
import com.magenta.mc.client.client.resend.ResendableMetadata;
import com.magenta.mc.client.storage.FieldGetter;
import com.magenta.mc.client.storage.FieldSetter;
import com.magenta.mc.client.storage.StorableMetadata;
import com.magenta.mc.client.xmpp.extensions.rpc.DefaultRpcResponseHandler;

/**
 * Created by IntelliJ IDEA.
 * User: const
 * Date: 13.06.12
 * Time: 21:38
 * To change this template use File | Settings | File Templates.
 */
public class LogRequestError extends Resendable {

    public static final ResendableMetadata METADATA = new ResendableMetadata("log", true, true);
    private static final long serialVersionUID = -7863628456663848886L;
    private static final String FIELD_ID = "FIELD_ID";
    private static final String FIELD_REQUEST_ID = "FIELD_REQUEST_ID";
    private static final String FIELD_MESSAGE = "FIELD_MESSAGE";
    private String id;
    private long requestId;
    private String message;

    public LogRequestError(long requestId, String message) {
        this.requestId = requestId;
        this.message = message;
    }

    public boolean send() {
        DefaultRpcResponseHandler.logRequestError(this);
        return true;
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

    public FieldSetter[] getSetters() {
        return new FieldSetter[]{
                new FieldSetter(FIELD_ID) {
                    public void setValue(Object value) {
                        id = (String) value;
                    }
                },
                new FieldSetter(FIELD_REQUEST_ID) {
                    public void setValue(Object value) {
                        requestId = ((Long) value).longValue();
                    }
                },
                new FieldSetter(FIELD_MESSAGE) {
                    public void setValue(Object value) {
                        message = (String) value;
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
                new FieldGetter(FIELD_REQUEST_ID) {
                    public Object getValue() {
                        return new Long(requestId);
                    }
                },
                new FieldGetter(FIELD_MESSAGE) {
                    public Object getValue() {
                        return message;
                    }
                }

        };
    }

    public long getRequestId() {
        return requestId;
    }

    public String getMessage() {
        return message;
    }
}
