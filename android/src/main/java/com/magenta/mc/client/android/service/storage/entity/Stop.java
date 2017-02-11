package com.magenta.mc.client.android.service.storage.entity;

import android.content.Context;

import com.magenta.maxunits.mobile.entity.AbstractJob;
import com.magenta.maxunits.mobile.entity.AbstractJobStatus;
import com.magenta.maxunits.mobile.entity.AbstractStop;
import com.magenta.maxunits.mobile.entity.Address;
import com.magenta.maxunits.mobile.entity.TaskState;
import com.magenta.mc.client.android.DistributionApplication;
import com.magenta.mc.client.android.db.dao.StopsDAO;
import com.magenta.mc.client.android.entity.LocationEntity;
import com.magenta.mc.client.android.mc.MxAndroidUtil;
import com.magenta.mc.client.android.service.ServicesRegistry;
import com.magenta.mc.client.android.service.events.EventType;
import com.magenta.mc.client.android.service.events.JobEvent;
import com.magenta.mc.client.android.service.storage.DataControllerImpl;
import com.magenta.mc.client.android.util.DateUtils;
import com.magenta.mc.client.android.util.JobWorkflowUtils;
import com.magenta.mc.client.android.util.StatusUtils;
import com.magenta.mc.client.android.util.StringUtils;
import com.magenta.mc.client.android.util.WorkflowStatusAuto;
import com.magenta.mc.client.client.resend.Resender;
import com.magenta.mc.client.log.MCLoggerFactory;
import com.magenta.mc.client.setup.Setup;
import com.magenta.mc.client.storage.FieldGetter;
import com.magenta.mc.client.storage.FieldSetter;
import com.magenta.mc.client.util.Resources;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class Stop extends AbstractStop {

    public static final String ATTR_CONTACT_PERSON = "contactPerson";
    public static final String ATTR_CONTACT_NUMBER = "contactNumber";
    public static final String ATTR_DURATION = "duration";
    public static final String ATTR_WINDOW_START_TIME = "window-start-time";
    public static final String ATTR_WINDOW_END_TIME = "window-end-time";
    public static final String ATTR_LOAD = "load";
    public static final String ATTR_VOLUME = "volume";
    public static final String ATTR_DEPART_TIME = "depart-time";
    public static final String ATTR_PRIORITY = "priority";
    public static final String ATTR_CUSTOMER = "customer";
    public static final String ATTR_COST = "cost";
    public static final String ATTR_ORDER_TYPE = "orderTypes";
    public static final String FIELD_UPDATE_TYPE = "updateType";
    public static final String ATTR_LOCATION = "locationName";

    public static final int UPDATE_STOP = 1;
    public static final int NOT_CHANGED_STOP = 0;
    public static final int CANCEL_STOP = -1;
    private static final String FIELD_STOP_NAME = "stopName";
    protected Integer updateType = 0;
    private String stopName;

    public Stop() {
    }

    public Stop(String name, String referenceId, String type, Address address, String notes, int index, String state) {
        super(referenceId, type, address, notes, index, state);
        setStopName(name);
    }

    private static boolean equalsOrBoothNull(Object o1, Object o2) {
        if (o1 == null) {
            return o2 == null;
        } else {
            if (o2 == null) {
                return false;
            } else {
                if (o1 instanceof String && o2 instanceof String) {
                    return ((String) o1).equalsIgnoreCase((String) o2);
                } else {
                    return o1.equals(o2);
                }
            }
        }
    }

    public AbstractJobStatus processSetState(final int state, final boolean send) {
        Context context = DistributionApplication.getContext();
        MCLoggerFactory.getLogger(getClass()).info("Update stop state in DB. stopName:" + stopName
                + ", referenceId:" + referenceId
                + " \"" + StatusUtils.translate(context, this.state) + "\"->\"" + StatusUtils.translate(context, state) + "\"");
        setState(state);
        if (state == TaskState.STOP_ARRIVED) {
            setArriveDate(Setup.get().getSettings().getCurrentDate());
            getStopValues().put("arriveDate", Resources.DATE_FORMAT.format(getArriveDate()));
        } else {
            getStopValues().remove("arriveDate");
        }
        LocationEntity location = MxAndroidUtil.getGeoLocation();
        if (location != null) {
            getStopValues().put("lat", location.getLat());
            getStopValues().put("lon", location.getLon());
        }
        getStopValues().put("performer", Setup.get().getSettings().getUserId());
        parentJob.setLastValidState(getStateString());
        if (isCompleted()) {
            parentJob.setLastStop(this);
            parentJob.moveToNextStopIfCurrent(this);
        }
        final AbstractJobStatus jobStatus = ((DataControllerImpl) ServicesRegistry.getDataController()).saveStatus((Job) parentJob, this, parameters);
        if (send) {
            Resender.getInstance().sendSavedResendable(jobStatus);
        }
        if (TaskState.STOP_ABORTED == state && getGroupId() != null) {
            parentJob.abortStopsInGroup(getGroupId());
        }
        final int jobState;
        if (parentJob.isAllStopsCompleted()) {
            jobState = TaskState.RUN_COMPLETED;
        } else {
            jobState = JobWorkflowUtils.nextStatus(WorkflowStatusAuto.Entity.KIND_JOB, (Job) parentJob);
        }
        if (Integer.MIN_VALUE != jobState) {
            parentJob.processSetState(jobState);
        }
        ServicesRegistry.getCoreService().notifyListeners(new JobEvent(EventType.JOB_UPDATED, parentJob.getReferenceId(), false));
        new StopsDAO(context).updateState(parentJob.getReferenceId(), getReferenceId(), state);
        return jobStatus;
    }

    public void update(final AbstractStop update) {
        super.update(update);
        this.date = update.getDate();
        this.arriveDate = update.getArriveDate();
        if (updateType == NOT_CHANGED_STOP) {
            this.updateType = equalsStops((Stop) update) ? NOT_CHANGED_STOP : UPDATE_STOP;
        }
    }

    public void setDynamicAttributes(String dynamicAttributes) {
        this.setValue("dynamicAttributes", StringUtils.encodeURI(dynamicAttributes));
    }

    public void setOrderItems(String barcodes) {
        this.setValue("orderItems", StringUtils.encodeURI(barcodes));
    }

    public boolean isCancelled() {
        return state == TaskState.STOP_ABORTED || state == TaskState.STOP_FAIL;
    }

    public String getStopName() {
        return stopName;
    }

    private void setStopName(String stopName) {
        this.stopName = stopName;
    }

    public String getStatusString(final Context context) {
        if (getParentJob().isCompleted() || getParentJob().isCancelled()) {
            return StatusUtils.translate(context, getParentJob().getState());
        }
        if (getState() > TaskState.UNKNOWN) {
            return StatusUtils.translate(context, getState());
        }
        return "";
    }

    public String getCustomer() {
        return getParameter(ATTR_CUSTOMER);
    }

    public String getLocation() {
        return getParameter(ATTR_LOCATION);
    }

    public String getTimeWindowAsString() {
        final long timeStart = Long.parseLong(getParameter(Stop.ATTR_WINDOW_START_TIME));
        final long timeEnd = Long.parseLong(getParameter(Stop.ATTR_WINDOW_END_TIME));
        return String.format("%s - %s",
                DateUtils.toStringTime(new Date(TimeUnit.SECONDS.toMillis(timeStart))),
                DateUtils.toStringTime(new Date(TimeUnit.SECONDS.toMillis(timeEnd))));
    }

    public String getTimeAsString() {
        return DateUtils.toStringTime(getDate());
    }

    public Integer getUpdateType() {
        return updateType;
    }

    public void setUpdateType(Integer updateType) {
        this.updateType = updateType;
        save();
    }

    public FieldSetter[] getSetters() {
        return new FieldSetter[]{
                new FieldSetter(FIELD_STOP_NAME) {
                    public void setValue(Object value) {
                        stopName = (String) value;
                    }
                },
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
                        stopValues = (Map) value;
                    }
                },
                new FieldSetter(FIELD_PARAMETERS) {
                    public void setValue(Object value) {
                        parameters = (Map) value;
                    }
                },
                new FieldSetter(FIELD_UPDATE_TYPE) {
                    public void setValue(Object value) {
                        updateType = value != null ? (Integer) value : NOT_CHANGED_STOP;
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
                new FieldGetter(FIELD_STOP_NAME) {
                    public Object getValue() {
                        return stopName;
                    }
                },
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
                new FieldGetter(FIELD_UPDATE_TYPE) {
                    public Object getValue() {
                        return updateType;
                    }
                },
                new FieldGetter(FIELD_ATTRIBUTES) {
                    public Object getValue() {
                        return attributes;
                    }
                }
        };
    }

    public String getContactPerson() {
        return getParameter(ATTR_CONTACT_PERSON);
    }

    public String getContactPhone() {
        return getParameter(ATTR_CONTACT_NUMBER);
    }

    public int getPriority() {
        try {
            return Integer.parseInt(getParameter(ATTR_PRIORITY));
        } catch (final Exception ignore) {
            return 0;
        }
    }

    public boolean equalsStops(Stop otherStop) {
        if (otherStop == null) {
            return false;
        } else {
            try {
                return equalsOrBoothNull(state, otherStop.getState()) &&
                        equalsOrBoothNull(stopName, otherStop.getStopName()) &&
                        equalsOrBoothNull(type, otherStop.getType()) &&
                        equalsOrBoothNull(getTimeWindowAsString(), otherStop.getTimeWindowAsString()) &&
                        equalsOrBoothNull(date, otherStop.getDate()) &&
                        equalsOrBoothNull(getParameterAsLong(ATTR_DEPART_TIME, date.getTime() / 1000),
                                otherStop.getParameterAsLong(ATTR_DEPART_TIME, otherStop.getDate().getTime() / 1000)) &&
                        equalsOrBoothNull(address.getFullAddress(), otherStop.getAddress().getFullAddress()) &&
                        equalsOrBoothNull(getParameter(ATTR_COST), otherStop.getParameter(ATTR_COST)) &&
                        equalsOrBoothNull(getParameter(ATTR_ORDER_TYPE), otherStop.getParameter(ATTR_ORDER_TYPE)) &&
                        equalsOrBoothNull(getParameter(ATTR_VOLUME), otherStop.getParameter(ATTR_VOLUME)) &&
                        equalsOrBoothNull(getParameter(ATTR_LOAD), otherStop.getParameter(ATTR_LOAD)) &&
                        equalsOrBoothNull(getParameter(ATTR_CONTACT_PERSON), otherStop.getParameter(ATTR_CONTACT_PERSON)) &&
                        equalsOrBoothNull(getParameter(ATTR_CONTACT_NUMBER), otherStop.getParameter(ATTR_CONTACT_NUMBER)) &&
                        equalsOrBoothNull(notes, otherStop.getNotes());
            } catch (Exception e) {
                return false;
            }
        }
    }

    public String getCustomerInfo() {
        String locationName = getLocation();
        String customerName = getCustomer();
        if (StringUtils.isBlank(locationName)) {
            if (!StringUtils.isBlank(customerName)) {
                return customerName;
            } else {
                return "";
            }
        } else {
            if (StringUtils.isBlank(customerName)) {
                return locationName;
            } else {
                return (String.format("%s - %s", customerName, locationName));
            }
        }
    }
}