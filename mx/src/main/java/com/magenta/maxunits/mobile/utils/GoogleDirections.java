package com.magenta.maxunits.mobile.utils;

import com.google.android.maps.GeoPoint;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

/**
 * @author Sergey Grachev
 */
public final class GoogleDirections {

    private static final DocumentBuilderFactory DBF = DocumentBuilderFactory.newInstance();
    private static final XPath XPATH = XPathFactory.newInstance().newXPath();
    private static XPathExpression XPATH_STEPS;
    private static XPathExpression XPATH_POLYLINE_POINTS;
    private static XPathExpression XPATH_START_LOCATION;
    private static XPathExpression XPATH_END_LOCATION;

    static {
        try {
            XPATH_STEPS = XPATH.compile("/DirectionsResponse/route/leg/step");
            XPATH_POLYLINE_POINTS = XPATH.compile("polyline/points");
            XPATH_START_LOCATION = XPATH.compile("start_location");
            XPATH_END_LOCATION = XPATH.compile("end_location");
        } catch (final Exception ignore) {
        }
    }

    public static List<Point> decodePolyline(final String encoded) {
        final List<Point> points = new ArrayList<Point>();
        int index = 0;
        final int len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            final int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            final int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            final Point p = new Point((int) (((double) lat / 1E5) * 1E6), (int) (((double) lng / 1E5) * 1E6));
            points.add(p);
        }

        return points;
    }

    private static String makeUrl(final int originLat, final int originLon,
                                  final int destinationLat, final int destinationLon) {
        return "http://maps.googleapis.com/maps/api/directions/xml"
                + "?origin=" + Double.toString((double) originLat / 1.0E6) + "," + Double.toString((double) originLon / 1.0E6)
                + "&destination=" + Double.toString((double) destinationLat / 1.0E6) + "," + Double.toString((double) destinationLon / 1.0E6)
                + "&sensor=false&units=metric";
    }

    public static GeoPoint makeGeoPoint(final double latitude, final double longitude) {
        return new GeoPoint((int) (latitude * 1E6), (int) (longitude * 1E6));
    }

    public List<Point> getRoute(final int originLat, final int originLon,
                                final int destinationLat, final int destinationLon) throws IOException, ParserConfigurationException, SAXException, XPathExpressionException {

        final String apiUrl = makeUrl(originLat, originLon, destinationLat, destinationLon);

        final URL url = new URL(apiUrl);
        HttpURLConnection connection = null;
        Document doc;
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.connect();
            doc = DBF.newDocumentBuilder().parse(connection.getInputStream());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        final NodeList steps = (NodeList) XPATH_STEPS.evaluate(doc, XPathConstants.NODESET)/* doc.getElementsByTagName("step")*/;
        if (steps.getLength() == 0) {
            return Collections.emptyList();
        }

        final List<Point> result = new ArrayList<Point>();
        for (int i = 0, nodesLength = steps.getLength(); i < nodesLength; i++) {
            final Node step = steps.item(i);
            final String polylinePoints = (String) XPATH_POLYLINE_POINTS.evaluate(step, XPathConstants.STRING);
            if (polylinePoints != null) {
                result.addAll(decodePolyline(polylinePoints));
            } else {
                final Node startLocation = (Node) XPATH_START_LOCATION.evaluate(step, XPathConstants.NODE);
                final Node endLocation = (Node) XPATH_END_LOCATION.evaluate(step, XPathConstants.NODE);
                if (startLocation != null && endLocation != null) {
                    NodeList list = startLocation.getChildNodes();
                    result.add(new Point(
                            (int) (Double.parseDouble(list.item(1).getTextContent()) * 1E6),
                            (int) (Double.parseDouble(list.item(3).getTextContent()) * 1E6)
                    ));
                    list = endLocation.getChildNodes();
                    result.add(new Point(
                            (int) (Double.parseDouble(list.item(1).getTextContent()) * 1E6),
                            (int) (Double.parseDouble(list.item(3).getTextContent()) * 1E6)
                    ));
                }
            }
        }

        return result;
    }

    public static final class Point {
        public int latitude;
        public int longitude;

        public Point(final int latitude, final int longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        @Override
        public String toString() {
            return "Point{" +
                    "latitude=" + latitude +
                    ", longitude=" + longitude +
                    '}';
        }
    }
}
