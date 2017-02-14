package com.magenta.mc.client.android.renderer;

import com.magenta.mc.client.android.entity.JobEntity;
import com.magenta.mc.client.android.mc.xml.XMLDataBlock;

import java.util.ArrayList;
import java.util.List;

public class Renderer {

    private static Class<? extends ObjectRenderer> singleJobRenderer;

    public static void registerRenderers(Class<? extends ObjectRenderer> singleJobRendererClass) {
        singleJobRenderer = singleJobRendererClass;
    }

    private static ObjectRenderer newSingleJobRenderer() {
        try {
            return singleJobRenderer != null ? singleJobRenderer.newInstance() : null;
        } catch (Exception e) {
            return null;
        }
    }

    public static JobEntity renderJob(XMLDataBlock data) {
        final XMLDataBlock strBlock = data.getChildBlock("string");
        final XMLDataBlock jobBlock = strBlock.getChildBlock("job");
        return (JobEntity) newSingleJobRenderer().renderFromBlock(jobBlock);
    }

    public static List parseJobs(XMLDataBlock jobsBlock) {
        return parseObjects(jobsBlock, newSingleJobRenderer());
    }

    public static List parseObjects(XMLDataBlock objectsBlock, ObjectRenderer objectRenderer) {
        final List objects = new ArrayList();
        if (objectsBlock != null && objectsBlock.getChildBlocks() != null) {
            for (Object o : objectsBlock.getChildBlocks()) {
                XMLDataBlock objectBlock = (XMLDataBlock) o;
                objects.add(objectRenderer.renderFromBlock(objectBlock));
            }
        }
        return objects;
    }
}