/*
 *    Copyright (c) 2012 Hai Bison
 *
 *    See the file LICENSE at the root directory of this project for copying
 *    permission.
 */

package com.haibison.android.lib.anhuu;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.haibison.android.lib.anhuu.FileChooserActivity.ViewType;
import com.haibison.android.lib.anhuu.providers.basefile.BaseFileContract.BaseFile;
import com.haibison.android.lib.anhuu.utils.Sys;

/**
 * Convenient class for working with shared preferences.
 * 
 * @author Hai Bison
 * @since v4.3 beta
 */
public class Settings {

    /**
     * This unique ID is used for storing preferences.
     * 
     * @since v4.9 beta
     */
    public static final String UID = "9795e88b-2ab4-4b81-a548-409091a1e0c6";

    /**
     * Generates global preference filename of this library.
     * 
     * @return the global preference filename.
     */
    public static final String genPreferenceFilename() {
        return String.format("%s_%s", Sys.LIB_CODE_NAME, UID);
    }

    /**
     * Generates global database filename.
     * 
     * @param name
     *            the database filename.
     * @return the global database filename.
     */
    public static final String genDatabaseFilename(String name) {
        return String.format("%s_%s_%s", Sys.LIB_CODE_NAME, UID, name);
    }

    /**
     * Gets new {@link SharedPreferences}
     * 
     * @param context
     *            the context.
     * @return {@link SharedPreferences}
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static SharedPreferences p(Context context) {
        /*
         * Always use application context.
         */
        return context.getApplicationContext().getSharedPreferences(
                genPreferenceFilename(), Context.MODE_MULTI_PROCESS);
    }

    /**
     * Setup {@code pm} to use global unique filename and global access mode.
     * You must use this method if you let the user change preferences via UI
     * (such as {@link PreferenceActivity}, {@link PreferenceFragment}...).
     * 
     * @param pm
     *            {@link PreferenceManager}.
     * @since v4.9 beta
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static void setupPreferenceManager(PreferenceManager pm) {
        pm.setSharedPreferencesMode(Context.MODE_MULTI_PROCESS);
        pm.setSharedPreferencesName(genPreferenceFilename());
    }// setupPreferenceManager()

    /**
     * Display settings.
     * 
     * @author Hai Bison
     * @since v4.3 beta
     */
    public static class Display extends Settings {

        /**
         * Delay time for waiting for other threads inside a thread... This is
         * in milliseconds.
         */
        public static final int DELAY_TIME_WAITING_THREADS = 10;

        /**
         * Delay time for waiting for very short animation, in milliseconds.
         */
        public static final int DELAY_TIME_FOR_VERY_SHORT_ANIMATION = 199;

        /**
         * Delay time for waiting for short animation, in milliseconds.
         */
        public static final int DELAY_TIME_FOR_SHORT_ANIMATION = 499;

        /**
         * Delay time for waiting for simple animation, in milliseconds.
         */
        public static final int DELAY_TIME_FOR_SIMPLE_ANIMATION = 999;

        /**
         * Gets view type.
         * 
         * @param c
         *            {@link Context}
         * @return {@link ViewType}
         */
        public static ViewType getViewType(Context c) {
            return ViewType.LIST.ordinal() == p(c).getInt(
                    c.getString(R.string.anhuu_pkey_display_view_type),
                    c.getResources().getInteger(
                            R.integer.anhuu_pkey_display_view_type_def)) ? ViewType.LIST
                    : ViewType.GRID;
        }

        /**
         * Sets view type.
         * 
         * @param c
         *            {@link Context}
         * @param v
         *            {@link ViewType}, if {@code null}, default value will be
         *            used.
         */
        public static void setViewType(Context c, ViewType v) {
            String key = c.getString(R.string.anhuu_pkey_display_view_type);
            if (v == null)
                p(c).edit()
                        .putInt(key,
                                c.getResources()
                                        .getInteger(
                                                R.integer.anhuu_pkey_display_view_type_def))
                        .commit();
            else
                p(c).edit().putInt(key, v.ordinal()).commit();
        }

        /**
         * Gets sort type.
         * 
         * @param c
         *            {@link Context}
         * @return one of {@link BaseFile#SORT_BY_MODIFICATION_TIME},
         *         {@link BaseFile#SORT_BY_NAME}, {@link BaseFile#SORT_BY_SIZE}.
         */
        public static int getSortType(Context c) {
            return p(c).getInt(
                    c.getString(R.string.anhuu_pkey_display_sort_type),
                    c.getResources().getInteger(
                            R.integer.anhuu_pkey_display_sort_type_def));
        }

        /**
         * Sets {@link SortType}
         * 
         * @param c
         *            {@link Context}
         * @param v
         *            one of {@link BaseFile#SORT_BY_MODIFICATION_TIME},
         *            {@link BaseFile#SORT_BY_NAME},
         *            {@link BaseFile#SORT_BY_SIZE}., if {@code null}, default
         *            value will be used.
         */
        public static void setSortType(Context c, Integer v) {
            String key = c.getString(R.string.anhuu_pkey_display_sort_type);
            if (v == null)
                p(c).edit()
                        .putInt(key,
                                c.getResources()
                                        .getInteger(
                                                R.integer.anhuu_pkey_display_sort_type_def))
                        .commit();
            else
                p(c).edit().putInt(key, v).commit();
        }

        /**
         * Gets sort ascending.
         * 
         * @param c
         *            {@link Context}
         * @return {@code true} if sort is ascending, {@code false} otherwise.
         */
        public static boolean isSortAscending(Context c) {
            return p(c).getBoolean(
                    c.getString(R.string.anhuu_pkey_display_sort_ascending),
                    c.getResources().getBoolean(
                            R.bool.anhuu_pkey_display_sort_ascending_def));
        }

        /**
         * Sets sort ascending.
         * 
         * @param c
         *            {@link Context}
         * @param v
         *            {@link Boolean}, if {@code null}, default value will be
         *            used.
         */
        public static void setSortAscending(Context c, Boolean v) {
            if (v == null)
                v = c.getResources().getBoolean(
                        R.bool.anhuu_pkey_display_sort_ascending_def);
            p(c).edit()
                    .putBoolean(
                            c.getString(R.string.anhuu_pkey_display_sort_ascending),
                            v).commit();
        }

        /**
         * Checks setting of showing time for old days in this year. Default is
         * {@code false}.
         * 
         * @param c
         *            {@link Context}.
         * @return {@code true} or {@code false}.
         * @since v4.7 beta
         */
        public static boolean isShowTimeForOldDaysThisYear(Context c) {
            return p(c)
                    .getBoolean(
                            c.getString(R.string.anhuu_pkey_display_show_time_for_old_days_this_year),
                            c.getResources()
                                    .getBoolean(
                                            R.bool.anhuu_pkey_display_show_time_for_old_days_this_year_def));
        }

        /**
         * Enables or disables showing time of old days in this year.
         * 
         * @param c
         *            {@link Context}.
         * @param v
         *            your preferred flag. If {@code null}, default will be used
         *            ( {@code false}).
         * @since v4.7 beta
         */
        public static void setShowTimeForOldDaysThisYear(Context c, Boolean v) {
            if (v == null)
                v = c.getResources()
                        .getBoolean(
                                R.bool.anhuu_pkey_display_show_time_for_old_days_this_year_def);
            p(c).edit()
                    .putBoolean(
                            c.getString(R.string.anhuu_pkey_display_show_time_for_old_days_this_year),
                            v).commit();
        }

        /**
         * Checks setting of showing time for old days in last year and older.
         * Default is {@code false}.
         * 
         * @param c
         *            {@link Context}.
         * @return {@code true} or {@code false}.
         * @since v4.7 beta
         */
        public static boolean isShowTimeForOldDays(Context c) {
            return p(c)
                    .getBoolean(
                            c.getString(R.string.anhuu_pkey_display_show_time_for_old_days),
                            c.getResources()
                                    .getBoolean(
                                            R.bool.anhuu_pkey_display_show_time_for_old_days_def));
        }

        /**
         * Enables or disables showing time of old days in last year and older.
         * 
         * @param c
         *            {@link Context}.
         * @param v
         *            your preferred flag. If {@code null}, default will be used
         *            ( {@code false}).
         * @since v4.7 beta
         */
        public static void setShowTimeForOldDays(Context c, Boolean v) {
            if (v == null)
                v = c.getResources().getBoolean(
                        R.bool.anhuu_pkey_display_show_time_for_old_days_def);
            p(c).edit()
                    .putBoolean(
                            c.getString(R.string.anhuu_pkey_display_show_time_for_old_days),
                            v).commit();
        }

        /**
         * Checks if remembering last location is enabled or not.
         * 
         * @param c
         *            {@link Context}.
         * @return {@code true} if remembering last location is enabled.
         * @since v4.7 beta
         */
        public static boolean isRememberLastLocation(Context c) {
            return p(c)
                    .getBoolean(
                            c.getString(R.string.anhuu_pkey_display_remember_last_location),
                            c.getResources()
                                    .getBoolean(
                                            R.bool.anhuu_pkey_display_remember_last_location_def));
        }

        /**
         * Enables or disables remembering last location.
         * 
         * @param c
         *            {@link Context}.
         * @param v
         *            your preferred flag. If {@code null}, default will be used
         *            ( {@code true}).
         * @since v4.7 beta
         */
        public static void setRememberLastLocation(Context c, Boolean v) {
            if (v == null)
                v = c.getResources().getBoolean(
                        R.bool.anhuu_pkey_display_remember_last_location_def);
            p(c).edit()
                    .putBoolean(
                            c.getString(R.string.anhuu_pkey_display_remember_last_location),
                            v).commit();
        }

        /**
         * Gets last location.
         * 
         * @param c
         *            {@link Context}.
         * @return the last location, or {@code null} if not available.
         * @since v4.7 beta
         */
        public static String getLastLocation(Context c) {
            return p(c).getString(
                    c.getString(R.string.anhuu_pkey_display_last_location),
                    null);
        }

        /**
         * Sets last location.
         * 
         * @param c
         *            {@link Context}.
         * @param v
         *            the last location.
         */
        public static void setLastLocation(Context c, String v) {
            p(c).edit()
                    .putString(
                            c.getString(R.string.anhuu_pkey_display_last_location),
                            v).commit();
        }

    }// Display

}
