/*
 *    Copyright (c) 2012 Hai Bison
 *
 *    See the file LICENSE at the root directory of this project for copying
 *    permission.
 */

package com.haibison.android.lib.anhuu.providers.history;

import java.util.Date;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;

import com.haibison.android.lib.anhuu.BuildConfig;
import com.haibison.android.lib.anhuu.R;
import com.haibison.android.lib.anhuu.providers.DbUtils;

/**
 * Utilities for History provider.
 * 
 * @author Hai Bison
 * @since v5.1 beta
 */
public class HistoryProviderUtils {

    private static final String CLASSNAME = HistoryProviderUtils.class
            .getName();

    /**
     * Checks and cleans up out-dated history items.
     * 
     * @param context
     *            {@link Context}.
     */
    public static void doCleanupOutdatedHistoryItems(Context context) {
        if (BuildConfig.DEBUG)
            Log.d(CLASSNAME, "doCleanupCache()");

        try {
            /*
             * NOTE: be careful with math, use long values instead of integer
             * ones.
             */
            final long validityInMillis = new Date().getTime()
                    - (context.getResources().getInteger(
                            R.integer.anhuu_pkey_history_validity_in_days_def) * DateUtils.DAY_IN_MILLIS);

            if (BuildConfig.DEBUG)
                Log.d(CLASSNAME, String.format(
                        "doCleanupCache() - validity = %,d (%s)",
                        validityInMillis, new Date(validityInMillis)));
            context.getContentResolver().delete(
                    HistoryContract.genContentUri(context),
                    String.format("%s < '%s'",
                            HistoryContract.COLUMN_MODIFICATION_TIME,
                            DbUtils.formatNumber(validityInMillis)), null);
        } catch (Throwable t) {
            /*
             * Currently we just ignore it.
             */
        }
    }// doCleanupOutdatedHistoryItems()

}
