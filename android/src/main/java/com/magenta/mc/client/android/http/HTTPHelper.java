package com.magenta.mc.client.android.http;

import com.magenta.mc.client.android.mc.log.MCLoggerFactory;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class HTTPHelper {

    public String postJson(String url, Map requestBody, Map<String, String> headers) {
        String jsonString = new JSONObject(requestBody).toString();
        return postJson(url, jsonString, headers);
    }

    private String postJson(String url, String json, Map<String, String> headers) {
        try {
            URL urlObject = new URL(url);
            HttpURLConnection urlConnection = (HttpURLConnection) urlObject.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            for (String header_key : headers.keySet()) {
                urlConnection.setRequestProperty(header_key, headers.get(header_key));
            }
            OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
            try {
                out.write(json);
                out.flush();
            } finally {
                try {
                    out.close();
                } catch (Exception e) {
                    MCLoggerFactory.getLogger(getClass()).error(e.getMessage(), e);
                }
            }
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                return readResponse(urlConnection);
            } else {
                String errMsg = String.format("POST request is failed status = %s url = %s ", urlConnection.getResponseCode(), urlConnection.getURL());
                MCLoggerFactory.getLogger(getClass()).error(errMsg);
                throw new IOException(errMsg);
            }
        } catch (IOException e) {
            MCLoggerFactory.getLogger(getClass()).error(e.getMessage(), e);
            return null;
        }
    }

    private String readResponse(HttpURLConnection urlConnection) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
        try {
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
            }
            return builder.toString();
        } finally {
            try {
                reader.close();
            } catch (Exception e) {
                MCLoggerFactory.getLogger(getClass()).error(e.getMessage(), e);
            }
        }
    }
}