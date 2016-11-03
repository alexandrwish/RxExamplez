package com.magenta.maxunits.mobile.dlib.service.storage.entity;

import com.magenta.maxunits.mobile.entity.AbstractJob;
import com.magenta.maxunits.mobile.entity.AbstractJobStatus;
import com.magenta.maxunits.mobile.entity.JobEntity;
import com.magenta.maxunits.mobile.service.ServicesRegistry;
import com.magenta.mc.client.storage.StorableMetadata;

import java.util.Map;

public class FullJobHistory extends AbstractJob implements JobEntity {

    public static final StorableMetadata STORABLE_METADATA = new StorableMetadata("full_job");
    private static final long serialVersionUID = -1092181227060822414L;

    public FullJobHistory(Job job) {
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

    public FullJobHistory() {

    }

    public AbstractJobStatus processSetState(int state, boolean send, Map parameters) {
        return null;
    }

    public void save() {
        ServicesRegistry.getDataController().save(this);
    }

    public StorableMetadata getMetadata() {
        return STORABLE_METADATA;
    }
}