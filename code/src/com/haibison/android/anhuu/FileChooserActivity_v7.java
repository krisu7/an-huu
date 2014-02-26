/*
 *    Copyright (c) 2012 Hai Bison
 *
 *    See the file LICENSE at the root directory of this project for copying
 *    permission.
 */

package com.haibison.android.anhuu;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import com.haibison.android.anhuu.utils.ui.Dlg;
import com.haibison.android.anhuu.utils.ui.UI;

/**
 * Helper class for {@link FileChooserActivity} in API 7+.
 * <p>
 * See {@link FileChooserActivity} for usage.
 * </p>
 * 
 * @author Hai Bison
 * @since v5.4 beta
 */
public class FileChooserActivity_v7 extends ActionBarActivity {

    /**
     * The full name of this class. Generally used for debugging.
     */
    private static final String CLASSNAME = FileChooserActivity_v7.class
            .getName();

    /*
     * CONTROLS
     */

    FragmentFiles mFragmentFiles;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        /*
         * EXTRA_THEME
         */

        if (getIntent().hasExtra(FileChooserActivity.EXTRA_THEME))
            setTheme(getIntent().getIntExtra(FileChooserActivity.EXTRA_THEME,
                    R.style.AnHuu_Theme_Dark));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.anhuu_f5be488d_activity_filechooser);
        UI.adjustDialogSizeForLargeScreens(getWindow());

        /*
         * Make sure RESULT_CANCELED is default.
         */
        setResult(RESULT_CANCELED);

        mFragmentFiles = FragmentFiles.newInstance(getIntent());
        mFragmentFiles.getArguments().putString(FragmentFiles.EXTRA_ACTION,
                getIntent().getAction());
        getSupportFragmentManager().beginTransaction()
                .add(R.id.anhuu_fragment_files, mFragmentFiles).commit();
    }// onCreate()

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        UI.adjustDialogSizeForLargeScreens(getWindow());
    }// onConfigurationChanged()

    @Override
    public void onBackPressed() {
        if (BuildConfig.DEBUG)
            Log.d(CLASSNAME, "onBackPressed()");

        if (mFragmentFiles.isLoading()) {
            if (BuildConfig.DEBUG)
                Log.d(CLASSNAME,
                        "onBackPressed() >> cancelling previous query...");
            mFragmentFiles.cancelPreviousLoader();
            Dlg.toast(this, R.string.anhuu_msg_cancelled, Dlg.LENGTH_SHORT);
            return;
        }

        super.onBackPressed();
    }// onBackPressed()

}
