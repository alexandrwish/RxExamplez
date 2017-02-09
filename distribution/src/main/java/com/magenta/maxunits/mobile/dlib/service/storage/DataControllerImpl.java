package com.magenta.maxunits.mobile.dlib.service.storage;

import android.util.Pair;

import com.magenta.maxunits.mobile.dlib.DistributionApplication;
import com.magenta.maxunits.mobile.dlib.db.dao.CommonsDAO;
import com.magenta.maxunits.mobile.dlib.mc.MxSettings;
import com.magenta.maxunits.mobile.dlib.service.DataController;
import com.magenta.maxunits.mobile.dlib.service.ServicesRegistry;
import com.magenta.maxunits.mobile.dlib.service.events.EventType;
import com.magenta.maxunits.mobile.dlib.service.events.JobEvent;
import com.magenta.maxunits.mobile.dlib.service.listeners.BroadcastEvent;
import com.magenta.maxunits.mobile.dlib.service.storage.entity.FullJobHistory;
import com.magenta.maxunits.mobile.dlib.service.storage.entity.Job;
import com.magenta.maxunits.mobile.dlib.service.storage.entity.JobStatus;
import com.magenta.maxunits.mobile.dlib.service.storage.entity.Stop;
import com.magenta.maxunits.mobile.dlib.utils.DateUtils;
import com.magenta.maxunits.mobile.entity.JobType;
import com.magenta.maxunits.mobile.entity.TaskState;
import com.magenta.mc.client.MobileApp;
import com.magenta.mc.client.client.resend.Resender;
import com.magenta.mc.client.exception.UnknownJobStatusException;
import com.magenta.mc.client.log.MCLogger;
import com.magenta.mc.client.log.MCLoggerFactory;
import com.magenta.mc.client.settings.Settings;
import com.magenta.mc.client.setup.Setup;
import com.magenta.mc.client.util.Resources;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class DataControllerImpl implements DataController<Job, JobStatus, Stop> {

    private static final MCLogger LOG = MCLoggerFactory.getLogger(DataController.class);

    private Map<String, Job> refToJob = new HashMap<>();
    private Map<String, FullJobHistory> fullJobHistory = new HashMap<>();
    private boolean clear = true;

    public void clear() {
        refToJob.clear();
        fullJobHistory.clear();
        clear = true;
    }

    public void init() {
        if (clear) {
            loadJobCache();
            checkCancelledAndCompletedJobs();
            loadJobHistory();
            clear = false;
        }
    }

    private void loadJobCache() {
        if (Settings.get().isOfflineVersion()) {
            return;
        }
        List jobs = Setup.get().getStorage().load(Job.STORABLE_METADATA);
        refToJob = listJobToMap(jobs);
    }

    private void loadFullHistory() {
        if (Settings.get().isOfflineVersion()) {
            return;
        }
        List jobs = Setup.get().getStorage().load(FullJobHistory.STORABLE_METADATA);
        fullJobHistory = listFullJobHistoryToMap(jobs);
        deleteUnusedHistory();
    }

    private void deleteUnusedHistory() {
        List<String> historyForRemove = new ArrayList<>();
        for (Map.Entry<String, FullJobHistory> entry : fullJobHistory.entrySet()) {
            if (isOldDate(entry.getValue().getDate(), "deleteUnusedHistory jobId:" + entry.getValue().getId())) {
                historyForRemove.add(entry.getKey());
            }
        }
        for (String jobID : historyForRemove) {
            fullJobHistory.remove(jobID);
            Setup.get().getStorage().delete(FullJobHistory.STORABLE_METADATA, jobID);
        }
    }

    private HashMap<String, Job> listJobToMap(List list) {
        if (list == null || list.isEmpty()) {
            return new HashMap<>(0);
        }
        HashMap<String, Job> result = new HashMap<>(list.size());
        for (Object aList : list) {
            if (aList instanceof Job) {
                Job job = (Job) aList;
                result.put(job.getId(), job);
            }
        }
        return result;
    }

    private HashMap<String, FullJobHistory> listFullJobHistoryToMap(List list) {
        if (list == null || list.isEmpty()) {
            return new HashMap<>(0);
        }
        HashMap<String, FullJobHistory> result = new HashMap<>(list.size());
        for (Object aList : list) {
            if (aList instanceof FullJobHistory) {
                FullJobHistory fullJobHistory = (FullJobHistory) aList;
                result.put(fullJobHistory.getId(), fullJobHistory);
            }
        }
        return result;
    }

    public void checkCancelledAndCompletedJobs() {
        try {
            // clonning keyset not to produce concurrent modification of 'refToJob' map
            Set referenceSet = refToJob.keySet();
            for (String jobRef : (String[]) referenceSet.toArray(new String[referenceSet.size()])) {
                Job nextJob = refToJob.get(jobRef);
                if (nextJob == null) {
                    LOG.error("Job [" + jobRef + "] is null");
                } else if (nextJob.isCompleted() || nextJob.isCancelled() || nextJob.getState() == TaskState.RUN_REJECTED) {
                    moveToHistory(nextJob);
                }
            }
        } catch (Exception e) {
            LOG.error("checkCancelledAndCompletedJobs exception: " + e.getMessage());
        }
    }

    public Job findJob(String refId) {
        return refToJob.get(refId);
    }

    public Job findFromHistoryJob(String refId) {
        if (fullJobHistory.get(refId) == null) {
            return null;
        }
        return new Job(fullJobHistory.get(refId));
    }

    public Pair<Job, Stop> find(final String jobId, final String stopId) {
        final Job job = refToJob.get(jobId);
        return job != null ? new Pair<>(job, (Stop) job.getStop(stopId)) : null;
    }

    public void addJob(final Job job) {
        final String jobRef = job.getReferenceId();
        if (jobRef == null) {
            throw new IllegalStateException("incorrect job was loaded");
        }
        //removeJobHistory(jobRef);
        final Job oldJob = findJob(jobRef);
        if (oldJob == null || (oldJob.getState() == TaskState.RUN_CANCELLED || ((MxSettings) Setup.get().getSettings()).isIgnoreNewRunDuplicates())) {
            job.updated(); // mark for acknowledgement
            final List jobStatusContainer = new ArrayList();
            if (job.getState() == TaskState.RUN_ASSIGNED || job.getState() == TaskState.RUN_SENT) {
                jobStatusContainer.add(job.processSetState(TaskState.RUN_RECEIVED, false));
            }
            for (Stop stop : (List<Stop>) job.getStops()) {
                stop.setUpdateType(job.isCancelled() || job.isCompleted() ? Stop.CANCEL_STOP : Stop.NOT_CHANGED_STOP);
            }
            refToJob.put(jobRef, job);
            // continue work asynchronously and return from remote RPC call
            MobileApp.runTask(new Runnable() {
                public void run() {
                    if (jobStatusContainer.size() > 0) {
                        JobStatus jobStatus = (JobStatus) jobStatusContainer.get(0);
                        Resender.getInstance().sendSavedResendable(jobStatus);
                    }
                    ServicesRegistry.getCoreService().notifyListeners(new JobEvent(EventType.NEW_JOB, job.getReferenceId(), true));
                }
            });
        } else {
            throw new IllegalStateException("duplicate job: " + jobRef);
        }
    }

    public void updateJob(final Job job, final boolean silent) {
        final Job currentJob = refToJob.get(job.getReferenceId());
        if (currentJob == null) {
            String jobStatus = Job.getStateString(job.getState());
            if (!"Completed".equalsIgnoreCase(jobStatus)
                    && !"Cancelled".equalsIgnoreCase(jobStatus)
                    && !"COA".equalsIgnoreCase(jobStatus)
                    && !"PreCOA".equalsIgnoreCase(jobStatus)
                    && !"PreCancelled".equalsIgnoreCase(jobStatus)
                    && !"HardCancelled".equalsIgnoreCase(jobStatus)
                    ) {
                LOG.debug("Warning: update job #" + job.getReferenceId() + " was not found, creating new");
                addJob(job);
            } else {
                autoreplyPreCancelled(job, jobStatus);
                LOG.debug("Warning: update job #" + job.getReferenceId() + " was not found, job is already " + jobStatus + ", ignored.");
            }
        } else {
            final List jobStatus = new ArrayList();
            final int currentState = currentJob.getState();
            final boolean currentDone = currentJob.isCompleted() || currentJob.stopsDone();
            final boolean jobDone = job.isCompleted() || job.stopsDone();
            final boolean showAlert = /*!Job.isJobsStatusEquals(currentJob, job) &&*/ !currentDone && !jobDone && currentJob.isAccepted();
            currentJob.update(job);
            currentJob.updated(); // mark for acknowledgement
            if ((job.getState() == TaskState.RUN_ASSIGNED || job.getState() == TaskState.RUN_SENT) && currentState != job.getState()) {
                // not to show stop state for job, annihilating stop memories
                currentJob.setCurrentStop(null);
                currentJob.setLastStop(null);
                jobStatus.add(currentJob.processSetState(TaskState.RUN_RECEIVED, false));
            } else {
                save(currentJob);
            }
            LOG.debug("running update job notification");
            // continue work asynchronously and return from remote RPC call
            MobileApp.runTask(new Runnable() {
                public void run() {
                    LOG.debug("update job notification started");
                    if (jobStatus.size() > 0) {
                        ((JobStatus) jobStatus.get(0)).send();
                    }
                    LOG.debug("update status sending requested, scheduling cumulative action");
                    ServicesRegistry.getCoreService().notifyListeners(new JobEvent(EventType.JOB_UPDATED, job.getReferenceId(), showAlert));
                }
            });
        }
        if (currentJob != null && currentJob.isCompleted()) {
            new CommonsDAO(DistributionApplication.getContext()).removeAllJobData(currentJob.getReferenceId());
        }
    }

    public void updateJob(Job job) {
        updateJob(job, false);
    }

    private void autoreplyPreCancelled(Job job, String jobStatus) {
        String newStatus = null;
        if ("PreCOA".equalsIgnoreCase(jobStatus)) {
            newStatus = "COA";
        } else if ("PreCancelled".equalsIgnoreCase(jobStatus)) {
            newStatus = "Cancelled";
        }
        if (newStatus != null) {
            final JobStatus jobStatusVO = createJobStatusVO(job.getReferenceId(), newStatus, null);
            MobileApp.runTask(new Runnable() {
                public void run() {
                    //do not resend this resendable, only try to send
                    Resender.getInstance().sendSavedResendable(jobStatusVO);
                }
            });
        }
    }

    public void cancelJob(final String referenceId) {
        final Job jobForCancel = refToJob.get(referenceId);
        if (jobForCancel == null) {
            LOG.debug("canceled job with refId " + referenceId + "was not found");
            return;
        }
        jobForCancel.updated(); // mark for acknowledgement
        jobForCancel.setState(TaskState.RUN_CANCELLED);
        save(jobForCancel); // this moves the job to history
        final long runID = Long.parseLong(jobForCancel.getReferenceId(), Character.MAX_RADIX);
        final long dayStart = (int) Math.floor(Long.parseLong(jobForCancel.getReferenceId(), Character.MAX_RADIX) / 1000) * 1000L;
        final long runNum = runID - dayStart;
        Date runDate = new Date(dayStart);
        Date nextDate = new Date(dayStart + 24 * 60 * 60 * 1000);
        CommonsDAO dao = new CommonsDAO(DistributionApplication.getContext());
        dao.removeAllJobData(jobForCancel.getReferenceId());
        Map<String, String> refToNewRef = new TreeMap<>(new Comparator<String>() {
            public int compare(String o1, String o2) {
                long l1 = Long.valueOf(o1, Character.MAX_RADIX);
                long l2 = Long.valueOf(o2, Character.MAX_RADIX);
                return l1 > l2 ? 1 : (l1 < l2 ? -1 : 0);
            }
        });
        for (Job job : refToJob.values()) {
            if (job.getDate().after(runDate) && job.getDate().before(nextDate)) {
                String oldRef = job.getReferenceId();
                long num = Long.valueOf(oldRef, Character.MAX_RADIX) - dayStart;
                if (runNum < num) {
                    String newRef = Long.toString(dayStart + num - 1, Character.MAX_RADIX).toUpperCase();
                    job.setReferenceId(newRef);
                    job.setParameter("number", num - 1 + "");
                    dao.updateJobReferens(oldRef, newRef);
                    refToNewRef.put(oldRef, newRef);
                }
            }
        }
        for (Map.Entry<String, String> entry : refToNewRef.entrySet()) {
            refToJob.put(entry.getValue(), refToJob.remove(entry.getKey()));
        }
        ServicesRegistry.getCoreService().notifyListeners(new JobEvent(EventType.JOB_CANCELLED, referenceId, true));
    }

    public JobStatus saveJobStatus(final Job job, final Map parameters) {
        final JobStatus jobStatus = saveStatus(job, null, parameters);
        if (job.isCompleted() && job.isAcknowledged()) {
            moveToHistory(job);
        }
        return jobStatus;
    }

    private void moveToHistory(Job job) {
        refToJob.remove(job.getReferenceId());
        Setup.get().getStorage().delete(job);
        final FullJobHistory fullHistory = new FullJobHistory(job);
        fullJobHistory.put(job.getReferenceId(), fullHistory);
        Setup.get().getStorage().save(fullHistory);
    }

    private void removeFromHistory(Job job) {
        if (fullJobHistory.containsKey(job.getReferenceId())) {
            FullJobHistory fullHistory = new FullJobHistory(job);
            fullJobHistory.remove(job.getReferenceId());
            Setup.get().getStorage().delete(fullHistory);
        }
    }

    public JobStatus saveStatus(final Job job, final Stop stop, final Map parameters) {
        String jobStatusString = Job.getStateString(job.getState());
        if (stop != null) {
            jobStatusString = stop.getStateString();
        }
        final HashMap map = new HashMap();
        if (parameters != null) {
            map.putAll(parameters);
        }
        if (stop != null) {
            stop.fillStatusMap(map); //todo stop.getParams()
        }
        try {
            map.put("type", JobType.stringValue(job.getType()));
        } catch (UnknownJobStatusException e) {
            // ignore
        }
        if (JobType.BREAK == job.getType()) {
            map.put("other-ref", job.getParameter("break-real-id"));
        }
        final JobStatus jobStatus = createJobStatusVO(job.getReferenceId(), jobStatusString, map);
        Resender.getInstance().saveResendable(jobStatus);
        Setup.get().getStorage().save(job);
        return jobStatus;
    }

    private JobStatus createJobStatusVO(String jobReferenceId, String jobStatusString, Map additionalParams) {
        final JobStatus jobStatus = new JobStatus();
        jobStatus.setId("" + System.currentTimeMillis());
        jobStatus.setJobReferenceId(jobReferenceId);
        jobStatus.setJobStatus(jobStatusString);
        final Map<String, String> params = new HashMap<>();
        params.put("date", Resources.UTC_DATE_FORMAT.format(Setup.get().getSettings().getCurrentDate()));
        if (additionalParams != null) {
            params.putAll(additionalParams);
        }
        jobStatus.setValues(params);
        return jobStatus;
    }

    public void save(Job job) {
        if (job.isCompleted() && job.isAcknowledged()) {
            moveToHistory(job);
        } else {
            Setup.get().getStorage().save(job);
            removeFromHistory(job);
        }
    }

    public List<Job> loadCurrentJobs() {
        final List<Job> active = new ArrayList<>();
        for (final Job job : refToJob.values()) {
            if (!job.isCompleted() && job.isAllStopsCompleted() && job.isAcknowledged()) {
                moveToHistory(job);
            } else {
                active.add(job);
            }
        }
        final ArrayList<Job> jobsArray = new ArrayList<>(active);
        Collections.sort(jobsArray, new Comparator<Job>() {
            public int compare(Job job1, Job job2) {
                return job1.getDate() != null && job2.getDate() != null
                        ? job1.getDate().compareTo(job2.getDate())
                        : 0;
            }
        });
        return jobsArray;
    }

    private void loadJobHistory() {
        loadFullHistory();
    }

    public List<Job> getJobsFromHistory() {
        List<Job> result = new ArrayList<>();
        if (fullJobHistory.isEmpty()) {
            loadFullHistory();
        }
        for (FullJobHistory history : fullJobHistory.values()) {
            result.add(new Job(history));
        }
        return result;
    }

    /*
        clear all jobs and unsent statuses and reload jobs
     */
    public void reloadJobs(List jobs) {
        LOG.debug("Reload jobs has been called. Number of jobs: " + jobs.size());
        clearCurrentJobsStorageAndCache();
        for (int i = 0; i < jobs.size(); i++) {
            Job job = (Job) jobs.get(i);
            if (isOldDate(job.getDate(), "reloadJobs skip job Id:" + job.getId())) {
                refToJob.remove(job.getReferenceId());
                continue;
            }
            updateStopFlags(job);
            final String jobRef = job.getReferenceId();
            if (jobRef == null) {
                LOG.debug("Warning: a job with no Ref received");
                continue;
            }
            if (job.getState() == TaskState.PRE_COA || job.getState() == TaskState.PRE_CANCELLED) {
                autoreplyPreCancelled(job, Job.getStateString(job.getState()));
            } else {
                if (refToJob.get(jobRef) == null) {
                    refToJob.put(jobRef, job);
                    if (job.getState() == TaskState.RUN_ASSIGNED || job.getState() == TaskState.RUN_SENT) {
                        final JobStatus jobStatus = (JobStatus) job.processSetState(TaskState.RUN_RECEIVED, false);
                        // continue work asynchronously and return from remote RPC call
                        MobileApp.runTask(new Runnable() {
                            public void run() {
                                Resender.getInstance().sendSavedResendable(jobStatus);
                            }
                        });
                    } else if (!job.isCompleted() && job.isAllStopsCompleted()) {
                        final JobStatus jobStatus = (JobStatus) job.processSetState(TaskState.RUN_COMPLETED, false);
                        MobileApp.runTask(new Runnable() {
                            public void run() {
                                Resender.getInstance().sendSavedResendable(jobStatus);
                            }
                        });
                        moveToHistory(job);
                    } else {
                        save(job);
                    }
                } else {
                    LOG.debug("Warning: duplicate job received: " + jobRef);
                }
            }
        }
        ServicesRegistry.getCoreService().notifyListeners(new BroadcastEvent<String>(EventType.RELOAD_SCHEDULE));
    }

    private void updateStopFlags(Job job) {
        boolean upd;
        Job j = refToJob.get(job.getReferenceId());
        if (j != null) {
            for (Stop stop : (List<Stop>) job.getStops()) {
                upd = false;
                for (Stop s : (List<Stop>) j.getStops()) {
                    if (s.getReferenceId().equalsIgnoreCase(stop.getReferenceId())) {
                        stop.setUpdateType(s.getUpdateType());
                        upd = true;
                        break;
                    }
                }
                if (!upd) {
                    stop.setUpdateType(Stop.UPDATE_STOP);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void addMockJob(final Job job) {
        refToJob.put(job.getReferenceId(), job);
    }

    private void clearCurrentJobsStorageAndCache() {
        for (final String reference : refToJob.keySet()) {
            Setup.get().getStorage().delete(Job.STORABLE_METADATA, reference);
        }
        refToJob.clear();
        Resender.getInstance().clearCache(JobStatus.RESENDABLE_METADATA);
    }

    private boolean isOldDate(Date date, String logprefix) {
        Date deleteJobsOlder = DateUtils.getDateFromCurrent(MxSettings.getInstance().getDeleteJobsOlder() * -1);
        boolean isOld = DateUtils.getStartOfDay(date).before(deleteJobsOlder);
        if (isOld) {
            MCLoggerFactory.getLogger(this.getClass()).info(logprefix + ", " + date);
        }
        return isOld;
    }
}