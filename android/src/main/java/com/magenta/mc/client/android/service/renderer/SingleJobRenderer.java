package com.magenta.mc.client.android.service.renderer;

import com.google.gson.Gson;
import com.magenta.hdmate.mx.model.AttributeRecord;
import com.magenta.hdmate.mx.model.LocationRecord;
import com.magenta.hdmate.mx.model.Order;
import com.magenta.hdmate.mx.model.OrderItem;
import com.magenta.hdmate.mx.model.TimeWindow;
import com.magenta.mc.client.android.db.dao.DistributionDAO;
import com.magenta.mc.client.android.entity.AbstractStop;
import com.magenta.mc.client.android.entity.Address;
import com.magenta.mc.client.android.entity.DynamicAttributeEntity;
import com.magenta.mc.client.android.entity.DynamicAttributeType;
import com.magenta.mc.client.android.entity.JobEntity;
import com.magenta.mc.client.android.entity.JobType;
import com.magenta.mc.client.android.entity.LocalizeStringEntity;
import com.magenta.mc.client.android.entity.OrderItemEntity;
import com.magenta.mc.client.android.entity.OrderItemStatus;
import com.magenta.mc.client.android.entity.TaskState;
import com.magenta.mc.client.android.mc.log.MCLoggerFactory;
import com.magenta.mc.client.android.mc.util.Resources;
import com.magenta.mc.client.android.mc.xml.XMLDataBlock;
import com.magenta.mc.client.android.record.DynamicAttributeRecord;
import com.magenta.mc.client.android.record.OrderItemRecord;
import com.magenta.mc.client.android.renderer.ObjectRenderer;
import com.magenta.mc.client.android.service.storage.entity.Job;
import com.magenta.mc.client.android.service.storage.entity.Stop;
import com.magenta.mc.client.android.util.ParametersParser;
import com.magenta.mc.client.android.util.RpcParser;
import com.magenta.mc.client.android.util.StringUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class SingleJobRenderer implements ObjectRenderer {

    private static List createStops(XMLDataBlock stopsBlock, Job job) {
        final List stops = new ArrayList();
        boolean stopOrderDetected = false;
        if (stopsBlock != null && stopsBlock.getChildBlocks() != null) {
            final Vector<XMLDataBlock> stopBlocks = stopsBlock.getChildBlocks();
            for (XMLDataBlock stopBlock : stopBlocks) {
                final String orderValue = stopBlock.getChildBlockText("index");
                int index;
                try {
                    index = Integer.parseInt(orderValue);
                    if (!stopOrderDetected)
                        stopOrderDetected = true;
                } catch (Exception e) {
                    index = -1;
                }
                final Stop stop = new Stop(
                        stopBlock.getChildBlockText("name"),
                        stopBlock.getChildBlockText("reference"),
                        stopBlock.getChildBlockText("type"),
                        createAddress(stopBlock.getChildBlock("address")),
                        stopBlock.getChildBlockText("notes"),
                        index,
                        stopBlock.getChildBlockText("state")
                );
                final String date = stopBlock.getChildBlockText("date");
                if (date != null && date.trim().length() > 0) {
                    try {
                        stop.setDate(Resources.UTC_DATE_FORMAT.parse(date));
                    } catch (ParseException e) {
                        // incorrect date format
                        e.printStackTrace();
                    }
                }
                stop.setGroupId(stopBlock.getChildBlockText("group"));
                stop.setCompleted(stop.getState() == TaskState.STOP_COMPLETED);
                stop.setParentJob(job);
                stop.setParameters(ParametersParser.fromString(stopBlock.getChildBlockText("parameters")));
                stop.setAttributes(RpcParser.parseAttributes(stopBlock.getChildBlock("attributes")));
                String oirStr = (String) stop.getParameters().remove("orderItems");
                String darStr = (String) stop.getParameters().remove("attributes");
                OrderItemRecord[] orderItemRecords = new Gson().fromJson(StringUtils.decodeURI(oirStr), OrderItemRecord[].class);
                DynamicAttributeRecord[] dynamicAttributeRecords = new Gson().fromJson(StringUtils.decodeURI(darStr), DynamicAttributeRecord[].class);
                try {
                    DistributionDAO dao = DistributionDAO.getInstance();
                    dao.clearOrderItems(stop.getReferenceId());
                    dao.clearDynamicAttribute(stop.getReferenceId());
                    if (orderItemRecords != null && orderItemRecords.length > 0) {
                        List<OrderItemEntity> orderItemEntities = new ArrayList<>(orderItemRecords.length);
                        for (OrderItemRecord record : orderItemRecords) {
                            OrderItemEntity entity = record.toEntity();
                            entity.setJob(job.getReferenceId());
                            entity.setStop(stop.getReferenceId());
                            orderItemEntities.add(entity);
                        }
                        dao.createOrderItems(orderItemEntities);
                    }
                    if (dynamicAttributeRecords != null && dynamicAttributeRecords.length > 0) {
                        List<DynamicAttributeEntity> dynamicAttributeEntities = new ArrayList<>(dynamicAttributeRecords.length);
                        for (DynamicAttributeRecord record : dynamicAttributeRecords) {
                            DynamicAttributeEntity entity = record.toEntity();
                            entity.setJob(job.getReferenceId());
                            entity.setStop(stop.getReferenceId());
                            dynamicAttributeEntities.add(entity);
                        }
                        dao.createDynamicAttributes(dynamicAttributeEntities);
                    }
                } catch (Exception ignore) {
                }
                stops.add(stop);
            }
        }
        if (stopOrderDetected) {
            // if any order values specified
            // then sort stops by their 'order' values
            Collections.sort(stops, new Comparator() {
                public int compare(Object o, Object o1) {
                    Stop stop1 = (Stop) o;
                    Stop stop2 = (Stop) o1;
                    final Integer order1 = stop1.getIndex();
                    final Integer order2 = stop2.getIndex();
                    if (order1 > -1 && order2 > -1) {
                        // both orders defined
                        return order1.compareTo(order2);
                    } else if (order1 < 0 && order2 < 0) {
                        // both undefined
                        return 0;
                    } else if (order1 < 0) {
                        // first undefined
                        return -1;
                    } else {
                        // second undefined
                        return 1;
                    }
                }
            });
        }
        return stops;
    }

    private static Address createAddress(final XMLDataBlock data) {
        if (data == null) {
            return null;
        }
        final String latitude = data.getChildBlockText("latitude");
        final String longitude = data.getChildBlockText("longitude");
        final Address address = new Address(data.getChildBlockText("full"), data.getChildBlockText("postcode"));
        if (latitude != null && latitude.trim().length() > 0) {
            address.setLatitude(Double.parseDouble(latitude));
        }
        if (longitude != null && longitude.trim().length() > 0) {
            address.setLongitude(Double.parseDouble(longitude));
        }
        return address;
    }

    public static JobEntity renderJob(com.magenta.hdmate.mx.model.Job job) {
        Job result = new Job();
        result.setAddress(createAddress(job.getDepotLocation()));
        result.setStartAddress(createAddress(job.getStartLocation()));
        result.setEndAddress(createAddress(job.getEndLocation()));
        result.setReferenceId(job.getReference());
        result.setDate(new Date(job.getDate()));
        result.setParameter(Job.ATTR_NUMBER, String.valueOf(job.getNumber()));
        result.setParameter(Job.ATTR_LOADING_END_TIME, String.valueOf(job.getLoadingEndTime()));
        result.setParameter(Job.ATTR_LOADING_DURATION, String.valueOf(job.getLoadingDuration()));
        result.setParameter(Job.ATTR_UNLOADING_DURATION, String.valueOf(job.getUnloadingDuration()));
        result.setParameter(Job.ATTR_UNLOADING_END_TIME, String.valueOf(job.getUnloadingEndTime()));
        result.setParameter(Job.ATTR_DRIVING_TIME, String.valueOf(job.getDrivingTime()));
        result.setParameter(Job.ATTR_TOTAL_DISTANCE, String.valueOf(job.getDistance()));
        result.setParameter(Job.ATTR_TOTAL_VOLUME, String.valueOf(job.getVolume()));
        result.setParameter(Job.ATTR_TOTAL_LOAD, String.valueOf(job.getLoad()));
        List<AbstractStop> stops = new LinkedList<>();
        for (Order order : job.getStops()) {
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
                "pu",
                createAddress(order.getLocation()),
                order.getDescription(),
                -1,
                "STOP_RUN_ACCEPTED");
        stop.setParentJob(job);
        stop.setAddress(createAddress(order.getLocation()));
//        stop.setState(order.getStatus()); // TODO: 3/11/17 impl
        stop.setState(1);
        stop.setDate(new Date(order.getDate()));
        stop.setArriveDate(new Date(order.getExpectedArrival()));
        stop.setParameter(Stop.ATTR_PRIORITY, String.valueOf(1));
//        stop.setParameter(Stop.ATTR_PRIORITY, order.getPriority()); // TODO: 3/11/17 impl
        stop.setParameter(Stop.ATTR_CUSTOMER, order.getCustomer());
        stop.setParameter(Stop.ATTR_DURATION, String.valueOf(order.getDuration()));
        stop.setParameter(Stop.ATTR_CONTACT_PERSON, order.getContactName());
        stop.setParameter(Stop.ATTR_CONTACT_NUMBER, order.getContactPhone());
        stop.setParameter(Stop.ATTR_CUSTOMER_LOCATION_VERIFIED, order.getCustomerLocationIsVerified());
        if (order.getTimeWindows() != null && order.getTimeWindows().size() > 0) {
            TimeWindow w = order.getTimeWindows().get(0);
            stop.setParameter(Stop.ATTR_WINDOW_START_TIME, String.valueOf(w.getStart().getTime()));
            stop.setParameter(Stop.ATTR_WINDOW_END_TIME, String.valueOf(w.getFinish().getTime()));
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
                    entity.setStatus(OrderItemStatus.NOT_CHECKED); // TODO: 3/11/17 impl
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
                    entity.setTitle(new LocalizeStringEntity());
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

    private static Address createAddress(LocationRecord record) {
        if (record != null) {
            Address a = new Address();
            a.setFullAddress(record.getAddress());
            a.setLatitude(record.getLat());
            a.setLongitude(record.getLon());
            return a;
        }
        return null;
    }

    public Object renderFromBlock(XMLDataBlock jobBlock) {
        Job result = new Job();
        result.setReferenceId(jobBlock.getChildBlockText("reference"));
        result.setNotes(jobBlock.getChildBlockText("notes"));
        final String date = jobBlock.getChildBlockText("date");
        if (date != null && date.trim().length() > 0) {
            try {
                result.setDate(Resources.UTC_DATE_FORMAT.parse(date));
            } catch (ParseException ignore) {
            }
        }
        result.setContactName(jobBlock.getChildBlockText("contactName"));
        result.setContactPhone(jobBlock.getChildBlockText("contactPhone"));
        result.setStops(createStops(jobBlock.getChildBlock("stops"), result));
        result.setParameters(ParametersParser.fromString(jobBlock.getChildBlockText("parameters")));
        result.setEndAddress(getAddressFromString(result.getParameters().remove("end-location")));
        result.setStartAddress(getAddressFromString(result.getParameters().remove("start-location")));
        String status = jobBlock.getChildBlockText("status");
        if (status != null && (status = status.trim()).length() > 0) {
            result.setState(TaskState.intValue(status));
            if (result.getState() == TaskState.UNKNOWN) {
                result.setLastValidState(status);
            }
        }
        String type = jobBlock.getChildBlockText("type");
        if (type != null && (type = type.trim()).length() > 0) {
            result.setType(JobType.fromString(type));
        }
        result.setAddress(createAddress(jobBlock.getChildBlock("address")));
        return result;
    }

    private Address getAddressFromString(Object o) {
        if (o != null && o instanceof String) {
            try {
                Map addressMap = new Gson().fromJson(StringUtils.decodeURI((String) o), Map.class);
                if (addressMap.size() != 3) {
                    return null;
                }
                Address address = new Address();
                address.setFullAddress(addressMap.get("address").toString());
                address.setLatitude(Double.valueOf(addressMap.get("lat").toString()));
                address.setLongitude(Double.valueOf(addressMap.get("lon").toString()));
                return address;
            } catch (Exception ignore) {
            }
        }
        return null;
    }
}