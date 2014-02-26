/*
 *    Copyright (c) 2012 Hai Bison
 *
 *    See the file LICENSE at the root directory of this project for copying
 *    permission.
 */

package com.haibison.android.anhuu.utils.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.haibison.android.anhuu.R;

/**
 * Adapter for context menu.
 * 
 * @author Hai Bison
 * @since v4.3 beta
 */
public class MenuItemAdapter extends BaseAdapter {

    private final Context mContext;
    private final Integer[] mItems;

    /**
     * Creates new instance.
     * 
     * @param context
     *            {@link Context}
     * @param itemIds
     *            array of resource IDs of titles to be used.
     */
    public MenuItemAdapter(Context context, Integer[] itemIds) {
        mContext = context;
        mItems = itemIds;
    }// MenuItemAdapter()

    @Override
    public int getCount() {
        return mItems.length;
    }// getCount()

    @Override
    public Object getItem(int position) {
        return mItems[position];
    }// getItem()

    @Override
    public long getItemId(int position) {
        return position;
    }// getItemId()

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.anhuu_f5be488d_context_menu_tiem, null);
        }

        ((TextView) convertView).setText(mItems[position]);

        return convertView;
    }// getView()

}