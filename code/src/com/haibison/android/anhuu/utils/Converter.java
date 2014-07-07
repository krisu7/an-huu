/*
 *    Copyright (c) 2012 Hai Bison
 *
 *    See the file LICENSE at the root directory of this project for copying
 *    permission.
 */

package com.haibison.android.anhuu.utils;

/**
 * The converter.
 * 
 * @author Hai Bison
 * 
 */
public class Converter {

    /**
     * Converts bytes to string.
     * 
     * @param bytes
     *            the size in bytes.
     * @return e.g.:
     *         <p/>
     *         <ul>
     *         <li>128 B</li>
     *         <li>1.5 KiB</li>
     *         <li>10 MiB</li>
     *         <li>...</li>
     *         </ul>
     */
    public static String bytesToStr(double bytes) {
        final short kib = 1024;
        if (Math.abs(bytes) < kib)
            return String.format("%.0f B", bytes);

        final String units = "KMGTPEZY";
        final double nearestPower = Math.floor(Math.log(Math.abs(bytes))
                / Math.log(2) / 10);
        String unit = units.charAt(Math.min((int) nearestPower - 1,
                units.length() - 1))
                + "iB";

        /*
         * Bytes might exceed max unit that we have. So ignore the overflow
         * value.
         */
        return String.format("%.2f %s",
                bytes / Math.pow(kib, Math.min(nearestPower, units.length())),
                unit);
    }// bytesToStr()

}
