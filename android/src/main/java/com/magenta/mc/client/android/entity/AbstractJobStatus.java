package com.magenta.mc.client.android.entity;

import com.magenta.mc.client.android.mc.client.resend.Resendable;
import com.magenta.mc.client.android.mc.client.resend.ResendableMetadata;
import com.magenta.mc.client.android.mc.storage.FieldGetter;
import com.magenta.mc.client.android.mc.storage.FieldSetter;
import com.magenta.mc.client.android.mc.storage.StorableMetadata;

import java.util.Map;

public abstract class AbstractJobStatus extends Resendable {

    public static final ResendableMetadata RESENDABLE_METADATA = new ResendableMetadata("status");

    private static final long serialVersionUID = 8;

    private final String FIELD_ID = "id";
    private final String FIELD_JOB_REF = "jobReferenceId";
    private final String FIELD_JOB_STATUS = "jobStatus";
    private final String FIELD_VALUES = "values";

    private String id;
    private String jobReferenceId;
    private String jobStatus;
    private Map values;

    public StorableMetadata getMetadata() {
        return RESENDABLE_METADATA;
    }

    public String getId() {
        return id;
    }

    public void setId(String uuid) {
        id = uuid;
    }

    public String getJobReferenceId() {
        return jobReferenceId;
    }

    public void setJobReferenceId(String jobReferenceId) {
        this.jobReferenceId = jobReferenceId;
    }

    public String getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(String jobStatus) {
        this.jobStatus = jobStatus;
    }

    public Map getValues() {
        return values;
    }

    public void setValues(Map values) {
        this.values = values;
    }

    public abstract boolean send();

    protected String valuesToString() {
        StringBuilder result = new StringBuilder();
        if (values != null) {
            for (final Object o : values.keySet()) {
                result.append((String) o).append("=").append(values.get(o)).append(";");
            }
        }
        return result.toString();
    }

    public FieldSetter[] getSetters() {
        return new FieldSetter[]{
                new FieldSetter(FIELD_ID) {
                    public void setValue(Object value) {
                        id = (String) value;
                    }
                },
                new FieldSetter(FIELD_JOB_REF) {
                    public void setValue(Object value) {
                        jobReferenceId = (String) value;
                    }
                },
                new FieldSetter(FIELD_JOB_STATUS) {
                    public void setValue(Object value) {
                        jobStatus = (String) value;
                    }
                },
                new FieldSetter(FIELD_VALUES) {
                    public void setValue(Object value) {
                        values = (Map) value;
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
                new FieldGetter(FIELD_JOB_REF) {
                    public Object getValue() {
                        return jobReferenceId;
                    }
                },
                new FieldGetter(FIELD_JOB_STATUS) {
                    public Object getValue() {
                        return jobStatus;
                    }
                },
                new FieldGetter(FIELD_VALUES) {
                    public Object getValue() {
                        return values;
                    }
                }
        };
    }
}