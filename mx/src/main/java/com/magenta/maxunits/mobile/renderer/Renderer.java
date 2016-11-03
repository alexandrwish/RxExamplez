package com.magenta.maxunits.mobile.renderer;

import com.magenta.maxunits.mobile.entity.JobEntity;
import com.magenta.mc.client.xml.XMLDataBlock;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created 16.07.2010
 *
 * @author Konstantin Pestrikov
 */
public class Renderer {
    private static Class<? extends ObjectRenderer> singleJobRenderer;
    private static Class<? extends ObjectRenderer> jobHistoryRenderer;

    public static void registerRenderers(final Class<? extends ObjectRenderer> singleJobRendererClass,
                                         final Class<? extends ObjectRenderer> jobHistoryRendererClass) {
        singleJobRenderer = singleJobRendererClass;
        jobHistoryRenderer = jobHistoryRendererClass;
    }

    private static ObjectRenderer newSingleJobRenderer() {
        try {
            return singleJobRenderer != null ? singleJobRenderer.newInstance() : null;
        } catch (Exception e) {
            return null;
        }
    }

    private static ObjectRenderer newJobHistoryRenderer() {
        try {
            return jobHistoryRenderer != null ? jobHistoryRenderer.newInstance() : null;
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

    static List parseJobsHistory(XMLDataBlock jobsBlock) {
        return parseObjects(jobsBlock, newJobHistoryRenderer());
    }

    @SuppressWarnings("unchecked")
    public static List parseObjects(XMLDataBlock objectsBlock, ObjectRenderer objectRenderer) {
        final List objects = new ArrayList();
        if (objectsBlock != null && objectsBlock.getChildBlocks() != null) {
            for (Iterator iterator = objectsBlock.getChildBlocks().iterator(); iterator.hasNext(); ) {
                XMLDataBlock objectBlock = (XMLDataBlock) iterator.next();
                objects.add(objectRenderer.renderFromBlock(objectBlock));
            }
        }
        return objects;
    }
}
