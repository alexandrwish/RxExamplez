package com.magenta.maxunits.mobile.utils;

import com.google.android.maps.GeoPoint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Sergey Grachev
 */
public final class GeoUtils {

    private GeoUtils() {
    }

    /**
     * Stack-based Douglas Peucker line simplification routine
     * returned is a reduced GLatLng array
     * After code by  Dr. Gary J. Robinson,
     * Environmental Systems Science Centre,
     * University of Reading, Reading, UK
     *
     * @param coordinates input coordinates
     * @param epsilon     kink in metres, kinks above this depth kept
     *                    kink depth is the height of the triangle abc where a-b and b-c are two consecutive line segments
     */
    public static List<Coordinate> reduceUsingRamerDouglasPeucker(final List<Coordinate> coordinates, final double epsilon) {
        final Coordinate[] safeArray = coordinates.toArray(new Coordinate[coordinates.size()]);

        final int n_source;
        int n_stack;
        int n_dest;
        int start;
        int end;
        int i;
        int sig;
        double dev_sqr, max_dev_sqr, band_sqr;
        double x12, y12, d12, x13, y13, d13, x23, y23, d23;
        final double F = ((Math.PI / 180.0) * 0.5);
        final Integer[] index = new Integer[safeArray.length]; /* aray of indexes of source points to include in the reduced line */
        final Integer[] sig_start = new Integer[safeArray.length]; /* indices of start & end of working section */
        final Integer[] sig_end = new Integer[safeArray.length];

        /* check for simple cases */

        if (safeArray.length < 3)
            return coordinates;    /* one or two points */

        /* more complex case. initialize stack */

        n_source = safeArray.length;
        band_sqr = epsilon * 360.0 / (2.0 * Math.PI * 6378137.0);	/* Now in degrees */
        band_sqr *= band_sqr;
        n_dest = 0;
        sig_start[0] = 0;
        sig_end[0] = n_source - 1;
        n_stack = 1;

        /* while the stack is not empty  ... */
        while (n_stack > 0) {

            /* ... pop the top-most entries off the stacks */

            start = sig_start[n_stack - 1];
            end = sig_end[n_stack - 1];
            n_stack--;

            if ((end - start) > 1) {  /* any intermediate points ? */

                /* ... yes, so find most deviant intermediate point to
                       either side of line joining start & end points */

                x12 = (safeArray[end].longitude - safeArray[start].longitude);
                y12 = (safeArray[end].latitude - safeArray[start].latitude);
                if (Math.abs(x12) > 180.0)
                    x12 = 360.0 - Math.abs(x12);
                x12 *= Math.cos(F * (safeArray[end].latitude + safeArray[start].latitude));/* use avg lat to reduceUsingRamerDouglasPeucker lng */
                d12 = (x12 * x12) + (y12 * y12);

                for (i = start + 1, sig = start, max_dev_sqr = -1.0; i < end; i++) {

                    x13 = (safeArray[i].longitude - safeArray[start].longitude);
                    y13 = (safeArray[i].latitude - safeArray[start].latitude);
                    if (Math.abs(x13) > 180.0)
                        x13 = 360.0 - Math.abs(x13);
                    x13 *= Math.cos(F * (safeArray[i].latitude + safeArray[start].latitude));
                    d13 = (x13 * x13) + (y13 * y13);

                    x23 = (safeArray[i].longitude - safeArray[end].longitude);
                    y23 = (safeArray[i].latitude - safeArray[end].latitude);
                    if (Math.abs(x23) > 180.0)
                        x23 = 360.0 - Math.abs(x23);
                    x23 *= Math.cos(F * (safeArray[i].latitude + safeArray[end].latitude));
                    d23 = (x23 * x23) + (y23 * y23);

                    if (d13 >= (d12 + d23))
                        dev_sqr = d23;
                    else if (d23 >= (d12 + d13))
                        dev_sqr = d13;
                    else
                        dev_sqr = (x13 * y12 - y13 * x12) * (x13 * y12 - y13 * x12) / d12;// solve triangle

                    if (dev_sqr > max_dev_sqr) {
                        sig = i;
                        max_dev_sqr = dev_sqr;
                    }
                }

                if (max_dev_sqr < band_sqr) {   /* is there a sig. intermediate point ? */
                    /* ... no, so transfer current start point */
                    index[n_dest] = start;
                    n_dest++;
                } else {
                    /* ... yes, so push two sub-sections on stack for further processing */
                    n_stack++;
                    sig_start[n_stack - 1] = sig;
                    sig_end[n_stack - 1] = end;
                    n_stack++;
                    sig_start[n_stack - 1] = start;
                    sig_end[n_stack - 1] = sig;
                }
            } else {
                /* ... no intermediate points, so transfer current start point */
                index[n_dest] = start;
                n_dest++;
            }
        }

        /* transfer last point */
        index[n_dest] = n_source - 1;
        n_dest++;

        /* make return array */
        final List<Coordinate> result = new ArrayList<Coordinate>();
        for (i = 0; i < n_dest; i++)
            result.add(safeArray[index[i]]);
        return result;
    }

    public static List<Coordinate> toCoordinate(final List<GeoPoint> list) {
        if (list.size() == 0) {
            return Collections.emptyList();
        }

        final List<Coordinate> result = new ArrayList<Coordinate>(list.size());
        for (final GeoPoint item : list) {
            result.add(new Coordinate(item.getLatitudeE6() / 1E6, item.getLongitudeE6() / 1E6));
        }

        return result;
    }

    public static List<GeoPoint> toGeoPoint(final List<Coordinate> list) {
        if (list.size() == 0) {
            return Collections.emptyList();
        }

        final List<GeoPoint> result = new ArrayList<GeoPoint>(list.size());
        for (final Coordinate item : list) {
            result.add(new GeoPoint((int) (item.latitude * 1E6), (int) (item.longitude * 1E6)));
        }

        return result;
    }

    public static final class Coordinate {
        public final double latitude;
        public final double longitude;

        public Coordinate(final double latitude, final double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }
}
