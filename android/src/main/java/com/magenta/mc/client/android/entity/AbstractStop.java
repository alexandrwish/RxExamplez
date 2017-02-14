package com.magenta.mc.client.android.entity;

import android.app.Activity;
import android.content.Context;

import com.magenta.mc.client.android.mc.storage.FieldGetter;
import com.magenta.mc.client.android.mc.storage.FieldSetter;
import com.magenta.mc.client.android.mc.storage.StorableField;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class AbstractStop extends StorableField {

    protected static final long serialVersionUID = 6;

    protected static final String FIELD_REFERENCEID = "referenceId";
    protected static final String FIELD_GROUPID = "groupId";
    protected static final String FIELD_TYPE = "type";
    protected static final String FIELD_ADDRESS = "address";
    protected static final String FIELD_NOTES = "notes";
    protected static final String FIELD_INDEX = "index";
    protected static final String FIELD_STATE = "state";
    protected static final String FIELD_PARENTJOB = "parentJob";
    protected static final String FIELD_COMPLETED = "completed";
    protected static final String FIELD_ARRIVEDATE = "arriveDate";
    protected static final String FIELD_PASSENGERS = "passengers";
    protected static final String FIELD_PARCELS = "pasrcels";
    protected static final String FIELD_STOPVALUES = "stopValues";
    protected static final String FIELD_DATE = "date";
    protected static final String FIELD_PARAMETERS = "parameters";
    protected static final String FIELD_ATTRIBUTES = "attributes";
    protected static final String FIELD_ARRIVE_RADIUS = "arriveRadius";

    protected String referenceId;
    protected String groupId;
    protected String type;
    protected Address address;
    protected String notes;
    protected int index;
    protected int state;
    protected AbstractJob parentJob;
    protected boolean completed;
    protected Date arriveDate;
    protected Map<String, String> stopValues;
    protected Date date;
    protected Map parameters;
    protected Set attributes;
    protected Integer arriveRadius;
    protected List passengers;
    protected List parcels;

    public AbstractStop() {
    }

    public AbstractStop(String referenceId,
                        String type,
                        Address address,
                        String notes,
                        int index,
                        String state) {
        this.referenceId = referenceId;
        this.type = format(type);
        this.address = address;
        this.notes = notes;
        this.index = index;
        this.state = TaskState.intValue(state);
        stopValues = new HashMap<>();
        stopValues.put("stop-ref", referenceId);
        stopValues.put("stop-order", "" + index);
    }

    public Date getArriveDate() {
        return arriveDate;
    }

    public void setArriveDate(Date arriveDate) {
        this.arriveDate = arriveDate;
    }

    public Map<String, String> getStopValues() {
        return stopValues;
    }

    public AbstractJob getParentJob() {
        return parentJob;
    }

    public void setParentJob(AbstractJob job) {
        this.parentJob = job;
    }

    public void setPassengers(List passengers) {
        this.passengers = passengers;
    }

    public List getParcels() {
        return parcels;
    }

    public void setParcels(List parcels) {
        this.parcels = parcels;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AbstractStop stop = (AbstractStop) o;
        return referenceId != null && referenceId.equalsIgnoreCase(stop.referenceId);
    }

    public int hashCode() {
        return referenceId != null ? referenceId.toLowerCase().hashCode() : 0;
    }

    public Passenger getContact() {
        if (passengers == null || passengers.size() == 0) return null;
        return (Passenger) passengers.get(0);
    }

    public void setOrderItems(String barcodes) {
        this.setValue("orderItems", barcodes);
    }

    public void setDynamicAttributes(String dynamicAttributes) {
        this.setValue("dynamicAttributes", dynamicAttributes);
    }

    public String getFactCost() {
        return (String) getStopValues().get("factCost");
    }

    public void setFactCost(String factCost) {
        this.setValue("factCost", factCost);
    }

    private String format(String type) {
        return type.toUpperCase().substring(0, 1) + type.toLowerCase().substring(1);
    }

    public boolean isPickup() {
        return "pickup".equalsIgnoreCase(getType());
    }

    public boolean isDrop() {
        return "drop".equalsIgnoreCase(getType());
    }

    public String getReferenceId() {
        return referenceId;
    }

    public String getType() {
        return type;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getAddressAsString() {
        return address != null ? address.getFullAddress() : "";
    }

    public String getNotes() {
        return notes;
    }

    public int getIndex() {
        return index;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getStateString() {
        return TaskState.stringValue(getState());
    }

    public AbstractJobStatus processSetState(int state) {
        return processSetState(state, true);
    }

    public abstract AbstractJobStatus processSetState(int state, boolean send);

    public boolean isProcessing() {
        return state == TaskState.STOP_ON_ROUTE
                || state == TaskState.STOP_DROP10
                || state == TaskState.STOP_DROP5
                || state == TaskState.STOP_ARRIVE10
                || state == TaskState.STOP_ARRIVE5
                || state == TaskState.STOP_ARRIVED
                || state == TaskState.STOP_LATE15
                || state == TaskState.STOP_LATE30
                || state == TaskState.STOP_LATE60
                || state == TaskState.STOP_LATE_A_LOT
                || state == TaskState.STOP_RUN_STARTED
                || state == TaskState.STOP_COMPLETED
                || (state == TaskState.UNKNOWN && parentJob.isProcessing());
    }

    public boolean isCompleted() {
        return completed
                || state == TaskState.STOP_COMPLETED
                || state == TaskState.STOP_ABORTED
                || state == TaskState.STOP_FAIL
                || state == TaskState.STOP_RUN_FINISHED;
    }

    public boolean complete(boolean send) {
        boolean changed = !isCompleted();
        setCompleted(true);
        final int state = TaskState.STOP_COMPLETED;
        if (send) {
            processSetState(state);
        } else {
            setState(state);
            save();
        }
        return changed;
    }

    public void save() {
        if (parentJob != null) {
            parentJob.save();
        }
    }

    public void fillStatusMap(HashMap<String, String> map) {
        map.putAll(stopValues);
    }

    public void setValue(String name, String value) {
        stopValues.put(name, value);
    }

    public String getCompleted() {
        if (completed || state == TaskState.STOP_COMPLETED) {
            return "Yes";
        }
        return "No";
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public void update(AbstractStop update) {
        this.type = update.type;
        this.address = update.address;
        this.notes = update.notes;
        this.index = update.index;
        this.parcels = update.parcels;
        this.passengers = update.passengers;
        this.state = update.state;
        this.parameters = update.parameters;
        this.attributes = update.attributes;
        this.arriveRadius = update.arriveRadius;
    }

    public boolean isFirst() {
        for (int i = 0; i < parentJob.getStops().size(); i++) {
            AbstractStop _stop = parentJob.getStops().get(i);
            if (_stop == this) continue;
            if (_stop.isCompleted()) return false;
        }
        return true;
    }

    public boolean isLast() {
        for (int i = 0; i < parentJob.getStops().size(); i++) {
            AbstractStop _stop = parentJob.getStops().get(i);
            if (_stop == this) continue;
            if (!_stop.isCompleted()) return false;
        }
        return true;
    }

    public FieldSetter[] getSetters() {
        return new FieldSetter[]{
                new FieldSetter(FIELD_REFERENCEID) {
                    public void setValue(Object value) {
                        referenceId = (String) value;
                    }
                },
                new FieldSetter(FIELD_GROUPID) {
                    public void setValue(Object value) {
                        groupId = (String) value;
                    }
                },
                new FieldSetter(FIELD_TYPE) {
                    public void setValue(Object value) {
                        type = (String) value;
                    }
                },
                new FieldSetter(FIELD_ADDRESS) {
                    public void setValue(Object value) {
                        address = (Address) value;
                    }
                },
                new FieldSetter(FIELD_NOTES) {
                    public void setValue(Object value) {
                        notes = (String) value;
                    }
                },
                new FieldSetter(FIELD_INDEX) {
                    public void setValue(Object value) {
                        index = (Integer) value;
                    }
                },
                new FieldSetter(FIELD_STATE) {
                    public void setValue(Object value) {
                        state = (Integer) value;
                    }
                },
                new FieldSetter(FIELD_PARENTJOB) {
                    public void setValue(Object value) {
                        parentJob = (AbstractJob) value;
                    }
                },
                new FieldSetter(FIELD_COMPLETED) {
                    public void setValue(Object value) {
                        completed = (Boolean) value;
                    }
                },
                new FieldSetter(FIELD_ARRIVEDATE) {
                    public void setValue(Object value) {
                        arriveDate = (Date) value;
                    }
                },
                new FieldSetter(FIELD_PASSENGERS) {
                    public void setValue(Object value) {
                        passengers = (List) value;
                    }
                },
                new FieldSetter(FIELD_PARCELS) {
                    public void setValue(Object value) {
                        parcels = (List) value;
                    }
                },
                new FieldSetter(FIELD_DATE) {
                    public void setValue(Object value) {
                        date = (Date) value;
                    }
                },
                new FieldSetter(FIELD_STOPVALUES) {
                    public void setValue(Object value) {
                        stopValues = (Map<String, String>) value;
                    }
                },
                new FieldSetter(FIELD_PARAMETERS) {
                    public void setValue(Object value) {
                        parameters = (Map) value;
                    }
                },
                new FieldSetter(FIELD_ARRIVE_RADIUS) {
                    public void setValue(Object value) {
                        if (value != null) {
                            arriveRadius = (Integer) value;
                        }
                    }
                },
                new FieldSetter(FIELD_ATTRIBUTES) {
                    public void setValue(Object value) {
                        attributes = (Set) value;
                    }
                }
        };
    }

    public FieldGetter[] getGetters() {
        return new FieldGetter[]{
                new FieldGetter(FIELD_REFERENCEID) {
                    public Object getValue() {
                        return referenceId;
                    }
                },
                new FieldGetter(FIELD_GROUPID) {
                    public Object getValue() {
                        return groupId;
                    }
                },
                new FieldGetter(FIELD_TYPE) {
                    public Object getValue() {
                        return type;
                    }
                },
                new FieldGetter(FIELD_ADDRESS) {
                    public Object getValue() {
                        return address;
                    }
                },
                new FieldGetter(FIELD_NOTES) {
                    public Object getValue() {
                        return notes;
                    }
                },
                new FieldGetter(FIELD_INDEX) {
                    public Object getValue() {
                        return index;
                    }
                },
                new FieldGetter(FIELD_STATE) {
                    public Object getValue() {
                        return state;
                    }
                },
                new FieldGetter(FIELD_PARENTJOB) {
                    public Object getValue() {
                        return parentJob;
                    }
                },
                new FieldGetter(FIELD_COMPLETED) {
                    public Object getValue() {
                        return completed;
                    }
                },
                new FieldGetter(FIELD_ARRIVEDATE) {
                    public Object getValue() {
                        return arriveDate;
                    }
                },
                new FieldGetter(FIELD_PASSENGERS) {
                    public Object getValue() {
                        return passengers;
                    }
                },
                new FieldGetter(FIELD_PARCELS) {
                    public Object getValue() {
                        return parcels;
                    }
                },
                new FieldGetter(FIELD_DATE) {
                    public Object getValue() {
                        return date;
                    }
                },
                new FieldGetter(FIELD_STOPVALUES) {
                    public Object getValue() {
                        return stopValues;
                    }
                },
                new FieldGetter(FIELD_PARAMETERS) {
                    public Object getValue() {
                        return parameters;
                    }
                },
                new FieldGetter(FIELD_ARRIVE_RADIUS) {
                    public Object getValue() {
                        return arriveRadius;
                    }
                },
                new FieldGetter(FIELD_ATTRIBUTES) {
                    public Object getValue() {
                        return attributes;
                    }
                }
        };
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public Map getParameters() {
        return parameters;
    }

    public void setParameters(Map parameters) {
        this.parameters = parameters;
    }

    public String getParameter(String name) {
        return parameters != null ? (String) parameters.get(name) : null;
    }

    public long getParameterAsLong(final String name, final long defaultValue) {
        try {
            return Long.parseLong(getParameter(name));
        } catch (final Exception ignore) {
            return defaultValue;
        }
    }

    public Set getAttributes() {
        return attributes;
    }

    public void setAttributes(final Set attributes) {
        this.attributes = attributes;
    }

    public void setArriveRadius(Integer arriveRadius) {
        this.arriveRadius = arriveRadius;
    }

    public abstract int getUpdateType();

    public abstract void setUpdateType(int stop);

    public abstract String getStopName();

    public abstract String getTimeWindowAsString();

    public abstract String getCustomerInfo();

    public abstract String getContactPerson();

    public abstract String getContactPhone();

    public abstract int getPriority();

    public abstract String getTimeAsString();

    public abstract String getStatusString(Context context);

    public abstract boolean isCancelled();

    public abstract String getCustomer();

    public abstract String getLocation();
}