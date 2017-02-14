package com.magenta.mc.client.android.service.storage;

import com.magenta.mc.client.android.mc.MxSettings;
import com.magenta.mc.client.android.mc.demo.DemoStorageInitializer;
import com.magenta.mc.client.android.mc.settings.Settings;
import com.magenta.mc.client.android.service.ServicesRegistry;
import com.magenta.mc.client.android.service.storage.entity.Job;
import com.magenta.mc.client.android.service.storage.entity.Stop;
import com.magenta.mc.client.android.entity.Address;
import com.magenta.mc.client.android.entity.TaskState;
import com.magenta.mc.client.android.util.DemoDataUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DemoStorageInitializerImpl implements DemoStorageInitializer {

    private DemoDataUtils.Country country;
    private DemoDataUtils.Town town;

    public boolean initStorage() {
        country = ((MxSettings) Settings.get()).getDemoCountry();
        town = ((MxSettings) Settings.get()).getDemoTown();
        createMockJobs();
        ((MxSettings) Settings.get()).setBarcodeEnabled(true);
        ((MxSettings) Settings.get()).setSignatureEnabled(true);
        return true;
    }

    private void createMockJobs() {
        int refId = 1;
        final Calendar calendar = Calendar.getInstance();
        calendar.roll(Calendar.DAY_OF_MONTH, -1);
        createMockJob(refId++, calendar.getTime(), TaskState.RUN_RECEIVED);
        calendar.roll(Calendar.HOUR_OF_DAY, 2);
        createMockJob(refId++, calendar.getTime(), TaskState.RUN_RECEIVED);
        calendar.roll(Calendar.HOUR_OF_DAY, 4);
        createMockJob(refId++, calendar.getTime(), TaskState.RUN_RECEIVED);
        calendar.setTime(new Date());
        calendar.roll(Calendar.HOUR_OF_DAY, 1);
        createMockJob(refId++, calendar.getTime(), TaskState.RUN_RECEIVED);
        calendar.roll(Calendar.HOUR_OF_DAY, 2);
        createMockJob(refId++, calendar.getTime(), TaskState.RUN_RECEIVED);
        calendar.roll(Calendar.HOUR_OF_DAY, 1);
        createMockJob(refId++, calendar.getTime(), TaskState.RUN_RECEIVED);
        calendar.roll(Calendar.HOUR_OF_DAY, 1);
        createMockJob(refId++, calendar.getTime(), TaskState.RUN_RECEIVED);
        calendar.setTime(new Date());
        calendar.roll(Calendar.DAY_OF_MONTH, 1);
        calendar.roll(Calendar.HOUR_OF_DAY, 1);
        createMockJob(refId++, calendar.getTime(), TaskState.RUN_RECEIVED);
        calendar.roll(Calendar.HOUR_OF_DAY, 2);
        createMockJob(refId, calendar.getTime(), TaskState.RUN_RECEIVED);
    }

    private void createMockJob(final int id, final Date date, final int state) {
        final Job job = new Job();
        final List<Stop> stops = new ArrayList<>();
        final long loadingDuration = Math.max(5 * 60, DemoDataUtils.RAND.nextInt(30 * 60)); // 30 minutes
        job.setReferenceId(String.valueOf(id));
        job.setDate(date);
        job.setState(state);
        job.setStops(stops);
        job.setAddress(DemoDataUtils.randomAddressAndLocation(country, town));
        job.setParameter(Job.ATTR_NUMBER, String.valueOf(date.getHours()));
        job.setParameter(Job.ATTR_LOADING_DURATION, String.valueOf(loadingDuration));
        job.setParameter(Job.ATTR_LOADING_END_TIME, String.valueOf(date.getTime() / 1000 + loadingDuration));
        job.setParameter(Job.ATTR_DRIVING_TIME, String.valueOf(DemoDataUtils.RAND.nextInt(60 * 60)));
        job.setParameter(Job.ATTR_TOTAL_LOAD, String.valueOf(DemoDataUtils.RAND.nextDouble() * 60));
        job.setParameter(Job.ATTR_TOTAL_VOLUME, String.valueOf(DemoDataUtils.RAND.nextDouble() * 60));
        job.setParameter(Job.ATTR_TOTAL_TIME, String.valueOf(DemoDataUtils.RAND.nextInt(2 * 60 * 60)));
        job.setParameter(Job.ATTR_TOTAL_DISTANCE, String.valueOf(DemoDataUtils.RAND.nextInt(200)));
        job.setParameter(Job.ATTR_PICKUP_CENTRE_NAME, "Pickup centre #" + id);
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        stops.add(createMockStop(job, 1, calendar.getTime(), TaskState.STOP_IDLE, "pickup"));
        calendar.roll(Calendar.HOUR_OF_DAY, DemoDataUtils.RAND.nextInt(4));
        for (int i = 0, stopsCount = Math.max(1, DemoDataUtils.RAND.nextInt(6)); i < stopsCount; i++) {
            calendar.roll(Calendar.HOUR_OF_DAY, DemoDataUtils.RAND.nextInt(4));
            stops.add(createMockStop(job, i + 2, calendar.getTime(), TaskState.STOP_IDLE, "drop"));
        }
        ((DataControllerImpl) ServicesRegistry.getDataController()).addMockJob(job);
    }

    private Stop createMockStop(final Job job, final int id, final Date date, final int state, final String type) {
        final Address address = DemoDataUtils.randomAddressAndLocation(country, town);
        final Map<String, String> parameters = new HashMap<>();
        final Stop stop = new Stop(
                String.valueOf(DemoDataUtils.RAND.nextInt(999999)),
                String.valueOf(id),
                type,
                address,
                DemoDataUtils.randomParcelDeliveryNotes(true),
                id,
                TaskState.stringValue(state));
        stop.setDate(date);
        stop.setParameters(parameters);
        stop.setParentJob(job);
        parameters.put(Stop.ATTR_CONTACT_PERSON, DemoDataUtils.randomFullName());
        parameters.put(Stop.ATTR_CONTACT_NUMBER, DemoDataUtils.randomPhone(country, town));
        parameters.put(Stop.ATTR_DURATION, String.valueOf(DemoDataUtils.RAND.nextInt(30 * 60)));
        parameters.put(Stop.ATTR_WINDOW_START_TIME, String.valueOf(date.getTime() / 1000));
        parameters.put(Stop.ATTR_WINDOW_END_TIME, String.valueOf(date.getTime() / 1000 + DemoDataUtils.RAND.nextInt(30 * 60)));
        parameters.put(Stop.ATTR_LOAD, String.valueOf(DemoDataUtils.RAND.nextDouble() * 10));
        parameters.put(Stop.ATTR_VOLUME, String.valueOf(DemoDataUtils.RAND.nextDouble() * 10));
        parameters.put(Stop.ATTR_DEPART_TIME, String.valueOf(date.getTime() / 1000 + DemoDataUtils.RAND.nextInt(30 * 60)));
        parameters.put(Stop.ATTR_PRIORITY, String.valueOf(DemoDataUtils.RAND.nextInt(3)));
        parameters.put(Stop.ATTR_CUSTOMER, "Magenta LTD");
        return stop;
    }
}