package com.magenta.maxunits.mobile.entity;

import com.magenta.mc.client.storage.FieldGetter;
import com.magenta.mc.client.storage.FieldSetter;
import com.magenta.mc.client.storage.Storable;
import com.magenta.mc.client.storage.StorableMetadata;

import java.util.Date;

public abstract class AbstractJobHistory extends Storable {

    public static final StorableMetadata STORABLE_METADATA = new StorableMetadata("history");

    private static final long serialVersionUID = 7;
    private static final String FIELD_REFERENCE_ID = "referenceId";
    private static final String FIELD_START_DATE = "startDate";
    private static final String FIELD_SERVICE = "service";
    private static final String FIELD_SHORT_DESCRIPTION = "shortDescription";
    private static final String FIELD_WAIT_RETURN = "waitReturn";
    private static final String FIELD_STATUS = "status";
    private String referenceId;
    private Date startDate;
    private String service;
    private String shortDescription;
    private String waitReturn;
    private String status;

    public AbstractJobHistory() {
    }

    public AbstractJobHistory(String referenceId, Date startDate, String service, String shortDescription, String waitReturn, int state) {
        this.referenceId = referenceId;
        this.startDate = startDate;
        this.service = service;
        this.shortDescription = shortDescription;
        this.waitReturn = waitReturn;
        this.status = AbstractJob.getStateString(state);
    }

    public AbstractJobHistory(AbstractJob job) {
        this(job.getReferenceId(), job.getDate(), job.getParameter("service"), job.getNotes(), Boolean.valueOf(job.getParameter("waitReturn")).booleanValue() ? "YES" : "NO", job.getState());
    }

    public StorableMetadata getMetadata() {
        return STORABLE_METADATA;
    }

    public String getId() {
        return getReferenceId();
    }

    public String getReferenceId() {
        return referenceId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public String getService() {
        return service;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public String getWaitReturn() {
        return waitReturn;
    }

    public String getStatus() {
        return status;
    }

    public FieldSetter[] getSetters() {
        return new FieldSetter[]{
                new FieldSetter(FIELD_REFERENCE_ID) {
                    public void setValue(Object value) {
                        referenceId = (String) value;
                    }
                },
                new FieldSetter(FIELD_START_DATE) {
                    public void setValue(Object value) {
                        startDate = (Date) value;
                    }
                },
                new FieldSetter(FIELD_SERVICE) {
                    public void setValue(Object value) {
                        service = (String) value;
                    }
                },
                new FieldSetter(FIELD_SHORT_DESCRIPTION) {
                    public void setValue(Object value) {
                        shortDescription = (String) value;
                    }
                },
                new FieldSetter(FIELD_WAIT_RETURN) {
                    public void setValue(Object value) {
                        waitReturn = (String) value;
                    }
                },
                new FieldSetter(FIELD_STATUS) {
                    public void setValue(Object value) {
                        status = (String) value;
                    }
                }
        };
    }

    public FieldGetter[] getGetters() {
        return new FieldGetter[]{
                new FieldGetter(FIELD_REFERENCE_ID) {
                    public Object getValue() {
                        return referenceId;
                    }
                },
                new FieldGetter(FIELD_START_DATE) {
                    public Object getValue() {
                        return startDate;
                    }
                },
                new FieldGetter(FIELD_SERVICE) {
                    public Object getValue() {
                        return service;
                    }
                },
                new FieldGetter(FIELD_SHORT_DESCRIPTION) {
                    public Object getValue() {
                        return shortDescription;
                    }
                },
                new FieldGetter(FIELD_WAIT_RETURN) {
                    public Object getValue() {
                        return waitReturn;
                    }
                },
                new FieldGetter(FIELD_STATUS) {
                    public Object getValue() {
                        return status;
                    }
                }
        };
    }
}
