package com.magenta.mc.client.android.service.renderer;

import com.magenta.hdmate.mx.model.AttributeRecord;
import com.magenta.hdmate.mx.model.LocationRecord;
import com.magenta.hdmate.mx.model.Order;
import com.magenta.hdmate.mx.model.OrderItem;
import com.magenta.hdmate.mx.model.Run;
import com.magenta.hdmate.mx.model.StopKind;
import com.magenta.hdmate.mx.model.StopPriority;
import com.magenta.hdmate.mx.model.TimeWindow;
import com.magenta.mc.client.android.db.dao.DistributionDAO;
import com.magenta.mc.client.android.entity.AbstractStop;
import com.magenta.mc.client.android.entity.Address;
import com.magenta.mc.client.android.entity.DynamicAttributeEntity;
import com.magenta.mc.client.android.entity.Job;
import com.magenta.mc.client.android.entity.JobEntity;
import com.magenta.mc.client.android.entity.LocalizeStringEntity;
import com.magenta.mc.client.android.entity.OrderItemEntity;
import com.magenta.mc.client.android.entity.Stop;
import com.magenta.mc.client.android.entity.TaskState;
import com.magenta.mc.client.android.entity.type.DynamicAttributeType;
import com.magenta.mc.client.android.entity.type.OrderItemType;
import com.magenta.mc.client.android.log.MCLoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SingleJobRenderer {

    public static JobEntity renderJob(Run run) {
        Job result = new Job();
        result.setAddress(createAddress(run.getDepotLocation()));
        result.setStartAddress(createAddress(run.getStartLocation()));
        result.setEndAddress(createAddress(run.getEndLocation()));
        result.setReferenceId(run.getReference());
        result.setDate(new Date(run.getDate()));
        result.setParameter(Job.ATTR_NUMBER, String.valueOf(run.getNumber()));
        result.setParameter(Job.ATTR_TOTAL_LOAD, String.valueOf(run.getLoad()));
        result.setParameter(Job.ATTR_TOTAL_VOLUME, String.valueOf(run.getVolume()));
        result.setParameter(Job.ATTR_DRIVING_TIME, String.valueOf(run.getDrivingTime()));
        result.setParameter(Job.ATTR_TOTAL_DISTANCE, String.valueOf(run.getDistance()));
        result.setParameter(Job.ATTR_LOADING_END_TIME, String.valueOf(run.getLoadingEndTime()));
        result.setParameter(Job.ATTR_LOADING_DURATION, String.valueOf(run.getLoadingDuration()));
        result.setParameter(Job.ATTR_UNLOADING_DURATION, String.valueOf(run.getUnloadingDuration()));
        result.setParameter(Job.ATTR_UNLOADING_END_TIME, String.valueOf(run.getUnloadingEndTime()));
        List<AbstractStop> stops = new LinkedList<>();
        for (Order order : run.getStops()) {
            AbstractStop stop = createStop(order, result);
            stops.add(stop);
        }
        result.setStops(stops);
/*
        job.getDriver();
*/
        return result;
    }

    private static AbstractStop createStop(Order order, Job job) {
        Stop stop = new Stop(order.getName(),
                order.getReference(),
                StopKind.DROP.equals(order.getStopKind()) ? "drop" : "pickup",
                createAddress(order.getLocation()),
                order.getDescription(),
                -1,
                TaskState.getStatus(order.getStatus()));
        stop.setParentJob(job);
        stop.setDate(new Date(order.getDate()));
        stop.setArriveDate(new Date(order.getExpectedArrival()));
        stop.setParameter(Stop.ATTR_LOAD, String.valueOf(order.getCapacity1()));
        stop.setParameter(Stop.ATTR_VOLUME, String.valueOf(order.getCapacity2()));
        stop.setParameter(Stop.ATTR_PRIORITY, String.valueOf(getPriority(order.getPriority())));
        stop.setParameter(Stop.ATTR_CUSTOMER, order.getCustomer());
        stop.setParameter(Stop.ATTR_DURATION, String.valueOf(order.getDuration()));
        stop.setParameter(Stop.ATTR_LOCATION, order.getLocation() != null ? order.getLocation().getName() : "");
        stop.setParameter(Stop.ATTR_DEPART_TIME, String.valueOf(order.getTimeEnd()));
        stop.setParameter(Stop.ATTR_CONTACT_PERSON, order.getContactName());
        stop.setParameter(Stop.ATTR_CONTACT_NUMBER, order.getContactPhone());
        stop.setParameter(Stop.ATTR_CUSTOMER_LOCATION_VERIFIED, order.getCustomerLocationIsVerified());
        if (order.getTimeWindows() != null && order.getTimeWindows().size() > 0) {
            TimeWindow w = order.getTimeWindows().get(0);
            stop.setParameter(Stop.ATTR_WINDOW_START_TIME, String.valueOf(w.getStart()));
            stop.setParameter(Stop.ATTR_WINDOW_END_TIME, String.valueOf(w.getFinish()));
        }
        try {
            DistributionDAO dao = DistributionDAO.getInstance();
            dao.clearOrderItems(stop.getReferenceId());
            dao.clearDynamicAttribute(stop.getReferenceId());
            if (order.getOrderItems() != null) {
                List<OrderItemEntity> orderItemEntities = new ArrayList<>();
                for (OrderItem item : order.getOrderItems()) {
                    OrderItemEntity entity = new OrderItemEntity();
                    entity.setMxID("1"); // TODO: 3/11/17 impl
                    entity.setName(item.getName());
                    entity.setBarcode(item.getBarcode());
                    entity.setStatus(OrderItemType.NOT_CHECKED); // TODO: 3/11/17 impl
                    entity.setJob(job.getReferenceId());
                    entity.setStop(stop.getReferenceId());
                    orderItemEntities.add(entity);
                }
                dao.createOrderItems(orderItemEntities);
            }
            if (order.getAttributes() != null) {
                List<DynamicAttributeEntity> dynamicAttributeEntities = new ArrayList<>();
                for (AttributeRecord record : order.getAttributes()) {
                    DynamicAttributeEntity entity = new DynamicAttributeEntity();
                    entity.setMxID(String.valueOf(record.getMxId()));
                    entity.setPdaEditable(record.getPdaEditable());
                    entity.setPdaRequired(record.getPdaRequired());
                    entity.setValue(record.getValue());
                    entity.setName(record.getName());
                    entity.setUnit(record.getUnit());
                    entity.setTitle(getTitle(record.getTitle()));
                    entity.setTypeName(DynamicAttributeType.valueOf(record.getTypeName().name()));
                    entity.setJob(job.getReferenceId());
                    entity.setStop(stop.getReferenceId());
                    dynamicAttributeEntities.add(entity);
                }
                dao.createDynamicAttributes(dynamicAttributeEntities);
            }
        } catch (Exception e) {
            MCLoggerFactory.getLogger(SingleJobRenderer.class).error(e.getMessage(), e);
        }
        /*
        order.getId();
        order.getDrivingTime();
        order.getDistance();
        order.getJobTypes();
        order.getStopKind();
        */
        return stop;
    }

    private static String getPriority(StopPriority priority) {
        if (priority == null) {
            return "0";
        }
        switch (priority) {
            case NORMAL:
                return "0";
            case MIDHIGH:
                return "1";
            case HIGH:
                return "2";
            default:
                return "0";
        }
    }

    private static LocalizeStringEntity getTitle(Map<String, String> title) {
        LocalizeStringEntity entity = new LocalizeStringEntity();
        entity.setEn(title.get("en"));
        entity.setEs(title.get("es"));
        entity.setFr(title.get("fr"));
        entity.setRu(title.get("ru"));
        return entity;
    }

    private static Address createAddress(LocationRecord record) {
        if (record != null && record.getLat() != null && record.getLon() != null) {
            Address a = new Address();
            a.setFullAddress(record.getAddress());
            a.setLatitude(record.getLat());
            a.setLongitude(record.getLon());
            return a;
        }
        return null;
    }
}