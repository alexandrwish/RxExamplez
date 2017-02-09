package com.magenta.maxunits.mobile.dlib.service.storage.entity;

import android.content.Context;

import com.magenta.maxunits.mobile.dlib.handler.AlertHandler;
import com.magenta.maxunits.mobile.entity.AbstractJob;
import com.magenta.maxunits.mobile.entity.AbstractJobStatus;
import com.magenta.maxunits.mobile.entity.AbstractStop;
import com.magenta.maxunits.mobile.dlib.entity.JobEntity;
import com.magenta.maxunits.mobile.dlib.entity.LocationEntity;
import com.magenta.maxunits.mobile.entity.TaskState;
import com.magenta.maxunits.mobile.dlib.mc.MxAndroidUtil;
import com.magenta.maxunits.mobile.dlib.service.ServicesRegistry;
import com.magenta.maxunits.mobile.dlib.utils.StatusUtils;
import com.magenta.mc.client.client.resend.Resender;
import com.magenta.mc.client.setup.Setup;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class Job extends AbstractJob implements JobEntity {

    public static final String ATTR_NUMBER = "number";
    public static final String ATTR_LOADING_DURATION = "loading-duration";
    public static final String ATTR_LOADING_END_TIME = "loading-end-time";
    public static final String ATTR_UNLOADING_END_TIME = "unloading-end-time";
    public static final String ATTR_UNLOADING_DURATION = "unloading-duration";
    public static final String ATTR_TOTAL_LOAD = "total-load";
    public static final String ATTR_TOTAL_VOLUME = "total-volume";
    public static final String ATTR_TOTAL_TIME = "total-time";
    public static final String ATTR_TOTAL_DISTANCE = "total-distance";
    public static final String ATTR_DRIVING_TIME = "driving-time";
    public static final String ATTR_PICKUP_CENTRE_NAME = "pickup-centre-name";

    public Job() {
        super();
    }

    public Job(FullJobHistory job) {
        referenceId = job.getReferenceId();
        notes = job.getNotes();
        date = job.getDate();
        contactName = job.getContactName();
        contactPhone = job.getContactPhone();
        address = job.getAddress();
        stops = job.getStops();
        passengers = job.getPassengers();
        parcels = job.getParcels();
        state = job.getState();
        lastStop = job.getCurrentStop();
        currentStop = job.getCurrentStop();
        lastValidState = job.getLastValidState();
        type = job.getType();
        acknowledged = job.isAcknowledged();
        parameters = job.getParameters();
        attributes = job.getAttributes();
        startAddress = job.getStartAddress();
        endAddress = job.getEndAddress();
    }

    public static boolean isJobsStatusEquals(final Job j1, final Job j2) {
        if ((j1 == null && j2 == null) || j1 == null || j2 == null /*|| j1.getState() != j2.getState()*/) {
            return false;
        }
        final List stops1 = j1.getStops(), stops2 = j2.getStops();
        if ((stops1 == null && stops2 == null) || stops1 == null || stops2 == null || stops1.size() != stops2.size()) {
            return false;
        }
        final Hashtable hashtable = new Hashtable();
        for (int i = stops1.size() - 1; i >= 0; i--) {
            final Stop stop = (Stop) stops1.get(i);
            hashtable.put(stop.getReferenceId(), stop);
        }
        for (int i = stops2.size() - 1; i >= 0; i--) {
            final Stop stop2 = (Stop) stops2.get(i);
            if (stop2 == null) {
                return false;
            }
            final Stop stop1 = (Stop) hashtable.get(stop2.getReferenceId());
            if (stop1 == null || stop1.getState() != stop2.getState()) {
                return false;
            }
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    public void save() {
        ServicesRegistry.getDataController().save(this);
    }

    @SuppressWarnings("unchecked")
    public AbstractJobStatus processSetState(final int state, final boolean send, Map parameters) {
        setState(state);
        setLastValidState(getStateString(getState()));
        if (parameters == null) parameters = new HashMap();
        LocationEntity location = MxAndroidUtil.getGeoLocation();
        if (location != null) {
            parameters.put("lat", location.getLat());
            parameters.put("lon", location.getLon());
        }
        parameters.put("performer", Setup.get().getSettings().getUserId());
        final JobStatus jobStatus = (JobStatus) ServicesRegistry.getDataController().saveJobStatus(this, parameters);
        if (send) {
            Resender.getInstance().sendSavedResendable(jobStatus);
        }
        return jobStatus;
    }

    public String getStatusString(final Context context) {
        if (isCancelled() || isCompleted()) {
            return StatusUtils.translate(context, getState());
        }
        if (getState() > TaskState.UNKNOWN) {
            return stopsInProgress()
                    ? StatusUtils.translate(context, TaskState.RUN_IN_PROGRESS)
                    : StatusUtils.translate(context, getState());
        }
        return "";
    }

    protected void completeStop(AbstractStop stop) {
        ((Stop) stop).setUpdateType(Stop.CANCEL_STOP);
        super.removeCanceledStop(stop);
    }

    protected void removeCanceledStop(AbstractStop stop) {
        ((Stop) stop).setUpdateType(Stop.CANCEL_STOP);
        super.removeCanceledStop(stop);
    }

    protected void setNewStops(List stops) {
        for (Stop stop : (List<Stop>) stops) {
            stop.setUpdateType(Stop.UPDATE_STOP);
        }
        AlertHandler.getInstance().start();
        super.setNewStops(stops);
    }

    protected void addNewStop(AbstractStop stop) {
        ((Stop) stop).setUpdateType(Stop.UPDATE_STOP);
        AlertHandler.getInstance().start();
        super.addNewStop(stop);
    }
}