package com.magenta.mc.client.android.mc.xmpp.extensions.rpc;

import com.magenta.mc.client.android.mc.xml.XMLDataBlock;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class Parametrized {
    protected List params = new ArrayList();
    protected Class[] paramClasses;
    protected Class[] dataParamClasses;
    protected Object[] paramValues;
    protected Object[] paramData;

    protected static void initParams(XMLDataBlock containerBlock, Parametrized parametrized) {
        XMLDataBlock paramsBlock = containerBlock.getChildBlock("params");
        Vector paramsVector = paramsBlock.getChildBlocks();
        if (paramsVector != null && paramsVector.size() > 0) {
            for (int i = 0; i < paramsVector.size(); i++) {
                XMLDataBlock param = (XMLDataBlock) paramsVector.get(i);
                parametrized.params.add(Param.fromDataBlock(param));
            }
        }
        parametrized.initParamInfo();
    }

    protected void initParamInfo() {
        paramClasses = new Class[params.size()];
        dataParamClasses = new Class[params.size()];
        paramValues = new Object[params.size()];
        paramData = new XMLDataBlock[params.size()];
        int i = 0;
        for (Iterator iterator = params.iterator(); iterator.hasNext(); ) {
            Param param = (Param) iterator.next();
            paramClasses[i] = param.getType();
            dataParamClasses[i] = XMLDataBlock.class;
            paramValues[i] = param.getValue();
            paramData[i] = param.getValueBlock();
            i++;
        }
    }

    protected void initParamsBlock(XMLDataBlock containerBlock) {
        final XMLDataBlock paramsBlock = new XMLDataBlock("params", containerBlock, null);
        containerBlock.addChild(paramsBlock);
        for (int i = 0; i < params.size(); i++) {
            Param param = (Param) params.get(i);
            paramsBlock.addChild(param.getParamBlock());
        }
    }
}