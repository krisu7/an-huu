/*
 *    Copyright (c) 2012 Hai Bison
 *
 *    See the file LICENSE at the root directory of this project for copying
 *    permission.
 */

package com.haibison.android.anhuu.utils.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.haibison.android.anhuu.BuildConfig;

/**
 * UI utilities.
 * 
 * @author Hai Bison
 */
public class UI {

    private static final String CLASSNAME = UI.class.getName();

    /**
     * The screen sizes.
     * 
     * @author Hai Bison
     * 
     */
    public static enum ScreenSize {
        /**
         * Small.
         */
        SMALL(1, 1, 1, 1),
        /**
         * Normal.
         */
        NORMAL(1, 1, 1, 1),
        /**
         * Large.
         */
        LARGE(.6f, .9f, .6f, .9f),
        /**
         * X-Large.
         */
        XLARGE(.6f, .9f, .5f, .7f),
        /**
         * Undefined.
         */
        UNDEFINED(1, 1, 1, 1);

        /**
         * The desired fixed width for a dialog along the minor axis (the screen
         * is in portrait). This is a fraction.
         */
        public final float fixedWidthMinor,
        /**
         * The desired fixed width for a dialog along the major axis (the screen
         * is in landscape). This is a fraction.
         */
        fixedWidthMajor,
        /**
         * The desired fixed height for a dialog along the minor axis (the
         * screen is in landscape). This is a fraction.
         */
        fixedHeightMinor,
        /**
         * The desired fixed height for a dialog along the major axis (the
         * screen is in portrait). This is a fraction.
         */
        fixedHeightMajor;

        /**
         * Creates new instance.
         * 
         * @param fixedHeightMajor
         *            the fixed height major.
         * @param fixedHeightMinor
         *            the fixed height minor.
         * @param fixedWidthMajor
         *            the fixed width major.
         * @param fixedWidthMinor
         *            the fixed width minor.
         */
        private ScreenSize(float fixedHeightMajor, float fixedHeightMinor,
                float fixedWidthMajor, float fixedWidthMinor) {
            this.fixedHeightMajor = fixedHeightMajor;
            this.fixedHeightMinor = fixedHeightMinor;
            this.fixedWidthMajor = fixedWidthMajor;
            this.fixedWidthMinor = fixedWidthMinor;
        }// ScreenSize()

        /**
         * Gets current screen size.
         * 
         * @param context
         *            the context.
         * @return current screen size.
         */
        public static ScreenSize getCurrent(Context context) {
            switch (context.getResources().getConfiguration().screenLayout
                    & Configuration.SCREENLAYOUT_SIZE_MASK) {
            case Configuration.SCREENLAYOUT_SIZE_SMALL:
                return SMALL;
            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                return NORMAL;
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                return LARGE;
            case Configuration.SCREENLAYOUT_SIZE_XLARGE:
                return XLARGE;
            default:
                return UNDEFINED;
            }
        }// getCurrent()

    }// ScreenSize

    /**
     * Shows/ hides soft input (soft keyboard).
     * 
     * @param view
     *            {@link View}.
     * @param show
     *            {@code true} or {@code false}. If {@code true}, this method
     *            will use a {@link Runnable} to show the IMM. So you don't need
     *            to use it, and consider using
     *            {@link View#removeCallbacks(Runnable)} if you want to cancel.
     */
    public static void showSoftKeyboard(final View view, final boolean show) {
        final InputMethodManager imm = (InputMethodManager) view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm == null)
            return;

        if (show) {
            view.post(new Runnable() {

                @Override
                public void run() {
                    imm.showSoftInput(view, 0, null);
                }// run()
            });
        } else
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0, null);
    }// showSoftKeyboard()

    /**
     * Strikes out text of {@code view}.
     * 
     * @param view
     *            {@link TextView}.
     * @param strikeOut
     *            {@code true} to strike out the text.
     */
    public static void strikeOutText(TextView view, boolean strikeOut) {
        if (strikeOut)
            view.setPaintFlags(view.getPaintFlags()
                    | Paint.STRIKE_THRU_TEXT_FLAG);
        else
            view.setPaintFlags(view.getPaintFlags()
                    & ~Paint.STRIKE_THRU_TEXT_FLAG);
    }// strikeOutText()

    /**
     * Convenient method for {@link Context#getTheme()} and
     * {@link Resources.Theme#resolveAttribute(int, TypedValue, boolean)}.
     * 
     * @param context
     *            the context.
     * @param resId
     *            The resource identifier of the desired theme attribute.
     * @return the resource ID that {@link TypedValue#resourceId} points to, or
     *         {@code 0} if not found.
     */
    public static int resolveAttribute(Context context, int resId) {
        TypedValue typedValue = new TypedValue();
        if (context.getTheme().resolveAttribute(resId, typedValue, true))
            return typedValue.resourceId;
        return 0;
    }// resolveAttribute()

    /**
     * Uses a fixed size for {@code dialog} in large screens.
     * 
     * @param dialog
     *            the dialog.
     */
    public static void adjustDialogSizeForLargeScreens(Dialog dialog) {
        adjustDialogSizeForLargeScreens(dialog.getWindow());
    }// adjustDialogSizeForLargeScreens()

    /**
     * Uses a fixed size for {@code dialogWindow} in large screens.
     * 
     * @param dialogWindow
     *            the window <i>of the dialog</i>.
     */
    public static void adjustDialogSizeForLargeScreens(Window dialogWindow) {
        if (BuildConfig.DEBUG)
            Log.d(CLASSNAME, "adjustDialogSizeForLargeScreen()");

        if (!dialogWindow.isFloating())
            return;

        final ScreenSize screenSize = ScreenSize.getCurrent(dialogWindow
                .getContext());
        switch (screenSize) {
        case LARGE:
        case XLARGE:
            break;
        default:
            return;
        }

        final DisplayMetrics metrics = dialogWindow.getContext().getResources()
                .getDisplayMetrics();
        final boolean isPortrait = metrics.widthPixels < metrics.heightPixels;

        int width = metrics.widthPixels;// dialogWindow.getDecorView().getWidth();
        int height = metrics.heightPixels;// dialogWindow.getDecorView().getHeight();
        if (BuildConfig.DEBUG)
            Log.d(CLASSNAME,
                    String.format("width = %,d | height = %,d", width, height));

        width = (int) (width * (isPortrait ? screenSize.fixedWidthMinor
                : screenSize.fixedWidthMajor));
        height = (int) (height * (isPortrait ? screenSize.fixedHeightMajor
                : screenSize.fixedHeightMinor));

        if (BuildConfig.DEBUG)
            Log.d(CLASSNAME, String.format(
                    "NEW >>> width = %,d | height = %,d", width, height));
        dialogWindow.setLayout(width, height);
    }// adjustDialogSizeForLargeScreens()

}
