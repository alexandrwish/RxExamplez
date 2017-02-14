package com.magenta.mc.client.android.service.renderer;

import com.magenta.mc.client.android.mc.util.Resources;
import com.magenta.mc.client.android.mc.xml.XMLDataBlock;
import com.magenta.mc.client.android.renderer.ObjectRenderer;
import com.magenta.mc.client.android.service.storage.entity.JobHistory;
import com.magenta.mc.client.android.entity.TaskState;

import java.text.ParseException;
import java.util.Date;

public class JobHistoryRenderer implements ObjectRenderer {

    private static Date parseDate(String dateString) {
        Date date = null;
        if (dateString != null && dateString.trim().length() > 0) {
            try {
                date = Resources.UTC_DATE_FORMAT.parse(dateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return date;
    }

    public Object renderFromBlock(XMLDataBlock jobBlock) {
        String reference = jobBlock.getChildBlockText("reference");
        String shortDescription = jobBlock.getChildBlockText("shortDescription");
        String service = jobBlock.getChildBlockText("service");
        String waitReturn = jobBlock.getChildBlockText("wait_return");
        Date date = parseDate(jobBlock.getChildBlockText("date"));
        String status = jobBlock.getChildBlockText("status");
        int state = TaskState.intValue(status);
        return new JobHistory(reference, date, service, shortDescription, waitReturn, state);
    }
}