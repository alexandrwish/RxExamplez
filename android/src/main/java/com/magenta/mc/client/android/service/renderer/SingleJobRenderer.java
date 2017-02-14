package com.magenta.mc.client.android.service.renderer;

import com.google.gson.Gson;
import com.magenta.mc.client.android.DistributionApplication;
import com.magenta.mc.client.android.db.dao.DistributionDAO;
import com.magenta.mc.client.android.entity.Address;
import com.magenta.mc.client.android.entity.DynamicAttributeEntity;
import com.magenta.mc.client.android.entity.JobType;
import com.magenta.mc.client.android.entity.OrderItemEntity;
import com.magenta.mc.client.android.entity.Parcel;
import com.magenta.mc.client.android.entity.Passenger;
import com.magenta.mc.client.android.entity.TaskState;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class SingleJobRenderer implements ObjectRenderer {

    private static void setParcelsAndPassengersToStop(Job job) {
        if (job.getParcels() != null && job.getParcels().length > 0) {
            Map<Integer, List<Parcel>> idToParcels = new HashMap<>();
            for (Parcel parcel : job.getParcels()) {
                List<Parcel> stopParcels = idToParcels.get(parcel.getDrop());
                if (stopParcels == null) {
                    stopParcels = new ArrayList<>();
                    idToParcels.put(parcel.getDrop(), stopParcels);
                }
                stopParcels.add(parcel);
                stopParcels = idToParcels.get(parcel.getPickup());
                if (stopParcels == null) {
                    stopParcels = new ArrayList<>();
                    idToParcels.put(parcel.getPickup(), stopParcels);
                }
                stopParcels.add(parcel);
            }
            for (int i = 0; i < job.getStops().size(); i++) {
                Stop stop = (Stop) job.getStops().get(i);
                stop.setParcels(idToParcels.get(stop.getIndex()));
            }
        }
        if (job.getPassengers() != null && job.getPassengers().length > 0) {
            Map<Integer, List<Passenger>> idToPassenger = new HashMap<>();
            for (Passenger passenger : job.getPassengers()) {
                List<Passenger> passengers = idToPassenger.get(passenger.getDrop());
                if (passengers == null) {
                    passengers = new ArrayList<>();
                    idToPassenger.put(passenger.getDrop(), passengers);
                }
                passengers.add(passenger);
                passengers = idToPassenger.get(passenger.getPickup());
                if (passengers == null) {
                    passengers = new ArrayList<>();
                    idToPassenger.put(passenger.getPickup(), passengers);
                }
                passengers.add(passenger);
            }
            for (int i = 0; i < job.getStops().size(); i++) {
                Stop stop = (Stop) job.getStops().get(i);
                stop.setPassengers((List) idToPassenger.get(stop.getIndex()));
            }
        }
    }

    private static Passenger[] createPassengers(XMLDataBlock passengersBlock) {
        if (passengersBlock == null || passengersBlock.getChildBlocks() == null) {
            return null;
        }
        final Vector<XMLDataBlock> passangerBlocks = passengersBlock.getChildBlocks();
        final Passenger[] passengers = new Passenger[passangerBlocks.size()];
        int i = 0;
        for (XMLDataBlock passengerBlock : passangerBlocks) {
            final String name = passengerBlock.getChildBlockText("name");
            final String phone1 = passengerBlock.getChildBlockText("phone1");
            final String phone2 = passengerBlock.getChildBlockText("phone2");
            final String pickup = passengerBlock.getChildBlockText("pickup");
            final String drop = passengerBlock.getChildBlockText("drop");
            passengers[i] = new Passenger(
                    name,
                    phone1,
                    phone2,
                    pickup != null && pickup.length() > 0 ? Integer.valueOf(pickup) : null,
                    drop != null && drop.length() > 0 ? Integer.valueOf(drop) : null
            );
            i++;
        }
        return passengers;
    }

    private static Parcel[] createParcels(XMLDataBlock parcelssBlock) {
        if (parcelssBlock == null || parcelssBlock.getChildBlocks() == null) {
            return null;
        }
        final Vector<XMLDataBlock> parcelBlocks = parcelssBlock.getChildBlocks();
        final Parcel[] parcels = new Parcel[parcelBlocks.size()];
        int i = 0;
        for (XMLDataBlock parcelBlock : parcelBlocks) {
            final String description = parcelBlock.getChildBlockText("description");
            final String quantity = parcelBlock.getChildBlockText("quantity");
            final String pickup = parcelBlock.getChildBlockText("pickup");
            final String drop = parcelBlock.getChildBlockText("drop");
            parcels[i] = new Parcel(
                    description,
                    quantity != null && quantity.length() > 0 ? Integer.valueOf(quantity) : null,
                    pickup != null && pickup.length() > 0 ? Integer.valueOf(pickup) : null,
                    drop != null && drop.length() > 0 ? Integer.valueOf(drop) : null
            );
            i++;
        }
        return parcels;
    }

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
                        List<OrderItemEntity> orderItemEntities = new ArrayList<OrderItemEntity>(orderItemRecords.length);
                        for (OrderItemRecord record : orderItemRecords) {
                            OrderItemEntity entity = record.toEntity();
                            entity.setJob(job.getReferenceId());
                            entity.setStop(stop.getReferenceId());
                            orderItemEntities.add(entity);
                        }
                        dao.createOrderItems(orderItemEntities);
                    }
                    if (dynamicAttributeRecords != null && dynamicAttributeRecords.length > 0) {
                        List<DynamicAttributeEntity> dynamicAttributeEntities = new ArrayList<DynamicAttributeEntity>(dynamicAttributeRecords.length);
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
        result.setPassengers(createPassengers(jobBlock.getChildBlock("passengers")));
        result.setParcels(createParcels(jobBlock.getChildBlock("parcels")));
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
        setParcelsAndPassengersToStop(result);
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