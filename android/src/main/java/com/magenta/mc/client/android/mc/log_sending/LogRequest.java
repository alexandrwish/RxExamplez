package com.magenta.mc.client.android.mc.log_sending;

import com.magenta.mc.client.android.mc.setup.Setup;
import com.magenta.mc.client.android.mc.storage.FieldGetter;
import com.magenta.mc.client.android.mc.storage.FieldSetter;
import com.magenta.mc.client.android.mc.storage.Storable;
import com.magenta.mc.client.android.mc.storage.StorableMetadata;
import com.magenta.mc.client.android.mc.util.DateFormatter;
import com.magenta.mc.client.android.mc.xml.XMLDataBlock;

import java.text.ParseException;
import java.util.Date;

public class LogRequest extends Storable {

    public static final StorableMetadata STORABLE_METADATA = new StorableMetadata("log_request", true);

    public static final int STATE_RECEIVED = 0;
    public static final int STATE_DONE = 1;
    public static final int STATE_SIGNALLED = 2;

    private static final long serialVersionUID = -9188692752895743643L;

    private static final String FIELD_START_DATE = "FIELD_START_DATE";
    private static final String FIELD_END_DATE = "FIELD_END_DATE";
    private static final String FIELD_DATE_RECEIVED = "FIELD_DATE_RECEIVED";
    private static final String FIELD_DATE_CHANGED = "FIELD_DATE_CHANGED";
    private static final String FIELD_REQUEST_ID = "FIELD_REQUEST_ID";
    private static final String FIELD_LOG_TYPE = "FIELD_LOG_TYPE";
    private static final String FIELD_STATE = "FIELD_STATE";
    private static final String FIELD_COUNT = "FIELD_COUNT";

    private Date startDate;
    private Date endDate;
    private Date dateReceived;
    private Date dateChanged;
    private long requestId;
    private LogType type;
    private int count = -1;
    private int state = STATE_RECEIVED;

    public LogRequest() {
    }

    public LogRequest(long requestId, Date startDate, Date endDate, LogType type) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.requestId = requestId;
        this.type = type;
        Date currentDate = Setup.get().getSettings().getCurrentDate();
        this.dateReceived = currentDate;
        this.dateChanged = currentDate;
    }

    public static LogRequest parse(XMLDataBlock block) throws ParseException {
        XMLDataBlock strBlock = block.getChildBlock("string");
        XMLDataBlock requestBlock = strBlock.getChildBlock("log_request");
        LogRequest result = new LogRequest();
        result.startDate = DateFormatter.parseFromUTC(requestBlock.getChildBlockText(LogRequest.FIELD_START_DATE));
        result.endDate = DateFormatter.parseFromUTC(requestBlock.getChildBlockText(LogRequest.FIELD_END_DATE));
        result.requestId = Long.parseLong(requestBlock.getChildBlockText(LogRequest.FIELD_REQUEST_ID));
        result.type = LogType.valueOf(requestBlock.getChildBlockText(LogRequest.FIELD_LOG_TYPE));

        return result;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public long getRequestId() {
        return requestId;
    }

    public LogType getType() {
        return type;
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

    public Date getDateReceived() {
        return dateReceived;
    }

    public void setDateReceived(Date dateReceived) {
        this.dateReceived = dateReceived;
    }

    public Date getDateChanged() {
        return dateChanged;
    }

    public void setDateChanged(Date dateChanged) {
        this.dateChanged = dateChanged;
    }

    public StorableMetadata getMetadata() {
        return STORABLE_METADATA;
    }

    public String getId() {
        return Long.toString(requestId);
    }

    public FieldSetter[] getSetters() {
        return new FieldSetter[]{
                new FieldSetter(FIELD_START_DATE) {
                    public void setValue(Object value) {
                        startDate = (Date) value;
                    }
                },
                new FieldSetter(FIELD_END_DATE) {
                    public void setValue(Object value) {
                        endDate = (Date) value;
                    }
                },
                new FieldSetter(FIELD_DATE_RECEIVED) {
                    public void setValue(Object value) {
                        dateReceived = (Date) value;
                    }
                },
                new FieldSetter(FIELD_DATE_CHANGED) {
                    public void setValue(Object value) {
                        dateChanged = (Date) value;
                    }
                },
                new FieldSetter(FIELD_REQUEST_ID) {
                    public void setValue(Object value) {
                        requestId = (Long) value;
                    }
                },
                new FieldSetter(FIELD_LOG_TYPE) {
                    public void setValue(Object value) {
                        type = LogType.valueOf((String) value);
                    }
                },
                new FieldSetter(FIELD_LOG_TYPE) {
                    public void setValue(Object value) {
                        type = LogType.valueOf((String) value);
                    }
                },
                new FieldSetter(FIELD_COUNT) {
                    public void setValue(Object value) {
                        count = (Integer) value;
                    }
                },
                new FieldSetter(FIELD_STATE) {
                    public void setValue(Object value) {
                        state = (Integer) value;
                    }
                }
        };
    }

    public FieldGetter[] getGetters() {
        return new FieldGetter[]{
                new FieldGetter(FIELD_START_DATE) {
                    public Object getValue() {
                        return startDate;
                    }
                },
                new FieldGetter(FIELD_END_DATE) {
                    public Object getValue() {
                        return endDate;
                    }
                },
                new FieldGetter(FIELD_DATE_RECEIVED) {
                    public Object getValue() {
                        return dateReceived;
                    }
                },
                new FieldGetter(FIELD_DATE_CHANGED) {
                    public Object getValue() {
                        return dateChanged;
                    }
                },
                new FieldGetter(FIELD_REQUEST_ID) {
                    public Object getValue() {
                        return requestId;
                    }
                },
                new FieldGetter(FIELD_LOG_TYPE) {
                    public Object getValue() {
                        return type.toString();
                    }
                },
                new FieldGetter(FIELD_COUNT) {
                    public Object getValue() {
                        return count;
                    }
                },
                new FieldGetter(FIELD_STATE) {
                    public Object getValue() {
                        return state;
                    }
                }
        };
    }
}
