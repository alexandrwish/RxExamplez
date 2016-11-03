package com.magenta.maxunits.mobile.dlib.acra;

import com.magenta.mc.client.log.MCLoggerFactory;

import org.acra.ReportField;
import org.acra.collector.CrashReportData;
import org.acra.sender.ReportSender;
import org.acra.sender.ReportSenderException;

import java.util.Map;

public class AcraLoggerSender implements ReportSender {

    public void send(CrashReportData report) throws ReportSenderException {
        StringBuilder sb = new StringBuilder();
        sb.append("\n--- ACRA Report begin ---\n");
        for (Map.Entry<ReportField, String> reportField : report.entrySet()) {
            sb.append(reportField.getKey().name()).append("=").append(reportField.getValue());
        }
        MCLoggerFactory.getLogger(getClass()).error(sb.toString());
    }
}