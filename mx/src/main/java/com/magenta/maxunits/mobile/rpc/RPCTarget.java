package com.magenta.maxunits.mobile.rpc;

import com.magenta.maxunits.mobile.mc.MxSettings;
import com.magenta.maxunits.mobile.renderer.Renderer;
import com.magenta.maxunits.mobile.service.ServicesRegistry;
import com.magenta.maxunits.mobile.service.listeners.BroadcastEvent;
import com.magenta.mc.client.setup.Setup;
import com.magenta.mc.client.xml.XMLDataBlock;
import com.magenta.mc.client.xmpp.extensions.rpc.DefaultRPCQueryListener;

import java.util.ArrayList;
import java.util.Vector;

@SuppressWarnings("unused")
public class RPCTarget extends DefaultRPCQueryListener {

    private static RPCTarget instance;

    private RPCTarget() {
    }

    public static RPCTarget getInstance() {
        if (instance == null) {
            instance = new RPCTarget();
        }
        return instance;
    }

    @SuppressWarnings("unchecked")
    public void newJob(XMLDataBlock data) {
        ServicesRegistry.getDataController().addJob(Renderer.renderJob(data));
    }

    @SuppressWarnings("unchecked")
    public void jobChanged(XMLDataBlock data) {
        ServicesRegistry.getDataController().updateJob(Renderer.renderJob(data));
    }

    public void removeJob(String referenceId) {
        ServicesRegistry.getDataController().cancelJob(referenceId);
    }

    public void requestVehicleCheck(final String regNumber, final Integer mileage, final Boolean enforceCheck) {

    }

    public void updateAvailable(String platform, String application) {
        Setup.get().getUpdateCheck().updateReported(platform, application);
    }

    public void updateReasons(XMLDataBlock response) {
        XMLDataBlock stringBlock = response.getChildBlock("string")
                .getChildBlock("accountConfig")
                .getChildBlock("orderCancelReasons");
        Vector childBlocks = stringBlock.getChildBlocks();
        if (childBlocks != null && childBlocks.size() > 0) {
            ArrayList<String> cancelReasons = new ArrayList<String>();
            for (Object o : childBlocks) {
                XMLDataBlock dataBlock = (XMLDataBlock) o;
                if (dataBlock.getChildBlocks() != null) {
                    cancelReasons.add(dataBlock.getChildBlock("text").getText());
                }
            }
            MxSettings.getInstance().setOrderCancelReasons(cancelReasons);
            ServicesRegistry.getCoreService().notifyListeners(new BroadcastEvent<String>("CANCEL_REASONS_UPDATE"));
        }
    }
}