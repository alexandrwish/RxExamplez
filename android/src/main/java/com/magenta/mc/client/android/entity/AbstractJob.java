package com.magenta.mc.client.android.entity;

import com.magenta.mc.client.android.storage.FieldGetter;
import com.magenta.mc.client.android.storage.FieldSetter;
import com.magenta.mc.client.android.storage.Storable;
import com.magenta.mc.client.android.storage.StorableMetadata;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

public abstract class AbstractJob extends Storable {

    public static final StorableMetadata STORABLE_METADATA = new StorableMetadata("job");

    protected static final long serialVersionUID = 1;

    private static final String FIELD_REFERENCEID = "referenceId";
    private static final String FIELD_NOTES = "notes";
    private static final String FIELD_DATE = "date";
    private static final String FIELD_CONTACTNAME = "contactName";
    private static final String FIELD_CONTACTPHONE = "contactPhone";
    private static final String FIELD_ADDRESS = "address";
    private static final String FIELD_END_ADDRESS = "endAddress";
    private static final String FIELD_START_ADDRESS = "startAddress";
    private static final String FIELD_STOPS = "stops";
    private static final String FIELD_STATE = "state";
    private static final String FIELD_LASTSTOP = "lastStop";
    private static final String FIELD_CURRENT_STOP = "currentStop";
    private static final String FIELD_LAST_VALID_STATE = "lastValidState";
    private static final String FIELD_PARAMETERS = "parameters";
    private static final String FIELD_ATTRIBUTES = "attributes";

    protected String referenceId;
    protected String notes;
    protected Date date;
    protected String contactName;
    protected String contactPhone;
    protected Address address;
    protected Address endAddress;
    protected Address startAddress;
    protected List<AbstractStop> stops;
    protected int state = -1;
    protected AbstractStop lastStop;
    protected AbstractStop currentStop;
    protected String lastValidState;
    protected boolean acknowledged = true;
    protected Map<String, String> parameters;
    protected Set attributes;

    public static String getStateString(int state) {
        return TaskState.stringValue(state);
    }

    public StorableMetadata getMetadata() {
        return STORABLE_METADATA;
    }

    public String getId() {
        return getReferenceId();
    }

    public void processSetState(int state) {
        processSetState(state, true);
    }

    public AbstractJobStatus processSetState(int state, boolean send) {
        return processSetState(state, send, null);
    }

    public abstract AbstractJobStatus processSetState(int state, boolean send, Map parameters);

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Address getEndAddress() {
        return endAddress != null ? endAddress : address;
    }

    public void setEndAddress(Address endAddress) {
        this.endAddress = endAddress;
    }

    public Address getStartAddress() {
        return startAddress != null ? startAddress : address;
    }

    public void setStartAddress(Address startAddress) {
        this.startAddress = startAddress;
    }

    public String getAddressAsString() {
        return address != null ? address.getFullAddress() : "";
    }

    public List<AbstractStop> getStops() {
        return stops;
    }

    public void setStops(final List<AbstractStop> stops) {
        for (int i = 0; i < stops.size(); i++) {
            stops.get(i).setParentJob(this);
        }
        this.stops = stops;
    }

    public AbstractStop getStop(String stopRef) {
        for (int i = 0; i < stops.size(); i++) {
            AbstractStop stop = stops.get(i);
            if (stopRef.equalsIgnoreCase(stop.getReferenceId())) {
                return stop;
            }
        }
        return null;
    }

    public boolean stopsDone() {
        for (int i = 0; i < stops.size(); i++) {
            AbstractStop stop = stops.get(i);
            if (!stop.isCompleted()) {
                return false;
            }
        }
        return true;
    }

    public boolean stopsInProgress() {
        for (int i = 0; i < stops.size(); i++) {
            AbstractStop stop = stops.get(i);
            if (stop.isProcessing() || stop.isCompleted()) {
                return true;
            }
        }
        return false;
    }

    public boolean isCompleted() {
        return state == TaskState.COA
                || state == TaskState.RUN_CANCELLED
                || state == TaskState.RUN_COMPLETED
                || state == TaskState.RUN_UNASSIGNED
                || state == TaskState.RUN_FINISHED;
    }

    public boolean isCancelled() {
        return state == TaskState.COA
                || state == TaskState.PRE_COA
                || state == TaskState.RUN_CANCELLED
                || state == TaskState.PRE_CANCELLED
                || state == TaskState.RUN_ABORTED;
    }

    public boolean isLate() {
        return state == TaskState.RUN_LATE15
                || state == TaskState.RUN_LATE30
                || state == TaskState.RUN_LATE60;
    }

    public AbstractStop getCurrentStop() {
        return currentStop;
    }

    public void setCurrentStop(AbstractStop currentStop) {
        boolean changed = (this.currentStop != currentStop);
        this.currentStop = currentStop;
        if (changed) {
            save();
        }
    }

    public void setLastStop(AbstractStop stop) {
        lastStop = stop;
    }

    public String getLastValidState() {
        return lastValidState;
    }

    public void setLastValidState(String lastValidState) {
        this.lastValidState = lastValidState;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public void setParameter(final String name, final String value) {
        if (parameters == null) {
            parameters = new HashMap<>();
        }
        parameters.put(name, value);
    }

    public boolean isAcknowledged() {
        return acknowledged;
    }

    public void updated() {
        acknowledged = false;
    }

    public String getParameter(final String name) {
        return parameters == null ? null : parameters.get(name);
    }

    public long getParameterAsLong(final String name, final long defaultValue) {
        try {
            return Long.parseLong(getParameter(name));
        } catch (final Exception ignore) {
            return defaultValue;
        }
    }

    public double getParameterAsDouble(final String name, final double defaultValue) {
        try {
            return Double.parseDouble(getParameter(name));
        } catch (final Exception ignore) {
            return defaultValue;
        }
    }

    public int getParameterAsInt(final String name, final int defaultValue) {
        try {
            return Integer.parseInt(getParameter(name));
        } catch (final Exception ignore) {
            return defaultValue;
        }
    }

    public int findFirstProgressedStopIndex() {
        int firstIncomplete = -1;
        for (int i = 0; i < stops.size(); i++) {
            AbstractStop stop = stops.get(i);
            if (stop.isProcessing()) {
                return i;
            } else if (firstIncomplete < 0 && !stop.isCompleted()) {
                firstIncomplete = i;
            }
        }
        // no stops being processing
        return firstIncomplete < 0 // no incomplete stops found
                ? lastStop != null ? stops.indexOf(lastStop) : 0 : firstIncomplete;
    }

    public void update(AbstractJob job) {
        referenceId = job.getReferenceId();
        notes = job.getNotes();
        date = job.getDate();
        contactName = job.getContactName();
        contactPhone = job.getContactPhone();
        parameters = job.getParameters();
        endAddress = job.getEndAddress();
        startAddress = job.getStartAddress();
        setState(job.getState());
        setLastValidState(job.getLastValidState());
        if (stops != null) {
            if (state != TaskState.RUN_ASSIGNED && state != TaskState.RUN_SENT && state != TaskState.RUN_RECEIVED) {
                Set<AbstractStop> intersection = new HashSet<>();
                for (ListIterator iterator = stops.listIterator(); iterator.hasNext(); ) {
                    AbstractStop oldStop = (AbstractStop) iterator.next();
                    AbstractStop newStop = job.getStop(oldStop.getReferenceId());
                    if (newStop != null) {
                        intersection.add(oldStop);
                        oldStop.update(newStop);
                        if (!oldStop.isCompleted() && newStop.isCompleted()) {
                            completeStop(oldStop);
                        }
                    } else {
                        // the stop is deleted
                        iterator.remove();
                        removeCanceledStop(oldStop);
                    }
                }
                for (int i = 0; i < job.getStops().size(); i++) {
                    AbstractStop newStop = job.getStops().get(i);
                    if (!intersection.contains(newStop)) {
                        addNewStop(newStop);
                    }
                }
                if (currentStop == null) {
                    moveToNextStop();
                }
            } else {
                setNewStops(job.getStops());
            }
            sortStops();
        }
    }

    protected void completeStop(AbstractStop stop) {
        stop.complete(false);
        if (lastStop == null) {
            setLastStop(stop);
        }
        moveToNextStopIfCurrent(stop);
    }

    protected void removeCanceledStop(AbstractStop stop) {
        if (stop == lastStop) {
            lastStop = null;
        }
        moveToNextStopIfCurrent(stop);
    }

    protected void setNewStops(List<AbstractStop> stops) {
        setStops(stops);
    }

    protected void addNewStop(AbstractStop stop) {
        stop.setParentJob(this);
        stops.add(stop);
    }

    private void sortStops() {
        Collections.sort(stops, new Comparator<AbstractStop>() {
            public int compare(AbstractStop stop1, AbstractStop stop2) {
                return stop1.getIndex() - stop2.getIndex();
            }
        });
    }

    public void moveToNextStopIfCurrent(AbstractStop oldStop) {
        // move to next stop if this stop is being currently processed
        if (currentStop != null && currentStop.equals(oldStop)) {
            moveToNextStop();
        }
    }

    private void moveToNextStop() {
        final int nextStopIndex = findFirstProgressedStopIndex();
        if (!(nextStopIndex > stops.size() - 1)) {
            currentStop = stops.get(nextStopIndex);
        }
    }

    public boolean isAllStopsCompleted() {
        boolean allStopsCompleted = true;
        Iterator stopsIter = stops.iterator();
        while (allStopsCompleted && stopsIter.hasNext()) {
            allStopsCompleted = ((AbstractStop) stopsIter.next()).isCompleted();
        }
        return allStopsCompleted;
    }

    public abstract void save();

    public void abortStopsInGroup(String groupId) {
        for (int i = 0, count = stops.size(); i < count; i++) {
            final AbstractStop stop = stops.get(i);
            if (groupId.equals(stop.getGroupId())) {
                stop.setState(TaskState.STOP_ABORTED);
            }
        }
    }

    public boolean isProcessing() {
        return TaskState.RUN_IN_PROGRESS == state
                || TaskState.PRE_CANCELLED == state
                || TaskState.RUN_STARTED == state;
    }

    public boolean isAccepted() {
        return state >= TaskState.RUN_ACCEPTED || stopsInProgress();
    }

    public Set getAttributes() {
        return attributes;
    }

    public void setAttributes(Set attributes) {
        this.attributes = attributes;
    }

    public FieldSetter[] getSetters() {
        return new FieldSetter[]{
                new FieldSetter(FIELD_REFERENCEID) {
                    public void setValue(Object value) {
                        referenceId = (String) value;
                    }
                },
                new FieldSetter(FIELD_NOTES) {
                    public void setValue(Object value) {
                        notes = (String) value;
                    }
                },
                new FieldSetter(FIELD_DATE) {
                    public void setValue(Object value) {
                        date = (Date) value;
                    }
                },
                new FieldSetter(FIELD_CONTACTNAME) {
                    public void setValue(Object value) {
                        contactName = (String) value;
                    }
                },
                new FieldSetter(FIELD_CONTACTPHONE) {
                    public void setValue(Object value) {
                        contactPhone = (String) value;
                    }
                },
                new FieldSetter(FIELD_ADDRESS) {
                    public void setValue(Object value) {
                        address = (Address) value;
                    }
                },
                new FieldSetter(FIELD_END_ADDRESS) {
                    public void setValue(Object value) {
                        endAddress = (Address) value;
                    }
                },
                new FieldSetter(FIELD_START_ADDRESS) {
                    public void setValue(Object value) {
                        startAddress = (Address) value;
                    }
                },
                new FieldSetter(FIELD_STOPS) {
                    public void setValue(Object value) {
                        stops = (List<AbstractStop>) value;
                    }
                },
                new FieldSetter(FIELD_STATE) {
                    public void setValue(Object value) {
                        state = (Integer) value;
                    }
                },
                new FieldSetter(FIELD_LASTSTOP) {
                    public void setValue(Object value) {
                        lastStop = (AbstractStop) value;
                    }
                },
                new FieldSetter(FIELD_CURRENT_STOP) {
                    public void setValue(Object value) {
                        currentStop = (AbstractStop) value;
                    }
                },
                new FieldSetter(FIELD_LAST_VALID_STATE) {
                    public void setValue(Object value) {
                        lastValidState = (String) value;
                    }
                },
                new FieldSetter(FIELD_PARAMETERS) {
                    public void setValue(Object value) {
                        parameters = (Map<String, String>) value;
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
                new FieldGetter(FIELD_NOTES) {
                    public Object getValue() {
                        return notes;
                    }
                },
                new FieldGetter(FIELD_DATE) {
                    public Object getValue() {
                        return date;
                    }
                },
                new FieldGetter(FIELD_CONTACTNAME) {
                    public Object getValue() {
                        return contactName;
                    }
                },
                new FieldGetter(FIELD_CONTACTPHONE) {
                    public Object getValue() {
                        return contactPhone;
                    }
                },
                new FieldGetter(FIELD_ADDRESS) {
                    public Object getValue() {
                        return address;
                    }
                },
                new FieldGetter(FIELD_END_ADDRESS) {
                    public Object getValue() {
                        return endAddress;
                    }
                },
                new FieldGetter(FIELD_START_ADDRESS) {
                    public Object getValue() {
                        return startAddress;
                    }
                },
                new FieldGetter(FIELD_STOPS) {
                    public Object getValue() {
                        return stops;
                    }
                },
                new FieldGetter(FIELD_STATE) {
                    public Object getValue() {
                        return state;
                    }
                },
                new FieldGetter(FIELD_LASTSTOP) {
                    public Object getValue() {
                        return lastStop;
                    }
                },
                new FieldGetter(FIELD_CURRENT_STOP) {
                    public Object getValue() {
                        return currentStop;
                    }
                },
                new FieldGetter(FIELD_LAST_VALID_STATE) {
                    public Object getValue() {
                        return lastValidState;
                    }
                },
                new FieldGetter(FIELD_PARAMETERS) {
                    public Object getValue() {
                        return parameters;
                    }
                },
                new FieldGetter(FIELD_ATTRIBUTES) {
                    public Object getValue() {
                        return attributes;
                    }
                }
        };
    }
}