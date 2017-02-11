package com.magenta.mc.client.android.acra;

import org.acra.ReportField;
import org.acra.collector.CrashReportData;
import org.acra.sender.ReportSender;
import org.acra.sender.ReportSenderException;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

final class AcraSentrySender implements ReportSender {

    public void send(CrashReportData report) throws ReportSenderException {
        String stackTrace = report.get(ReportField.STACK_TRACE);
        String[] stackTraceLines = stackTrace.split("\n");
        String message = stackTraceLines.length > 0 ? stackTraceLines[0] : stackTrace;
        String culprit = stackTraceLines.length > 1 ? stackTraceLines[1] : stackTrace;
        Map<String, String> extra = new HashMap<>();
        for (Map.Entry<ReportField, String> reportField : report.entrySet()) {
            extra.put(reportField.getKey().name(), reportField.getValue());
        }
        SentryHelper sentryHelper = new SentryHelper();
        sentryHelper.captureEvent(new SentryHelper.SentryEventBuilder().
                setLevel(SentryHelper.SentryEventBuilder.SentryEventLevel.ERROR).
                setMessage(message).
                setCulprit(culprit).
                setExtra(extra).
                setTimestamp(new Date().getTime()));
    }
}