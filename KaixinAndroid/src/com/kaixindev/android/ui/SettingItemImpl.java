package com.kaixindev.android.ui;

import com.kaixindev.android.R;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class SettingItemImpl {
    
    private Drawable mHighlightImage = null;
    private Rect mHighlightRect = new Rect();
    public boolean mHighlightOnTop = false;

    /**
     * @param item This object must extends ViewGroup
     * @param context
     * @param attrs
     */
    public void init(Context context, TypedArray attrs, ViewGroup viewGroup, int layout) {
        ((Activity)context).getLayoutInflater().inflate(layout, viewGroup);

        // Set title, description and icon.
        TextView titleView = getTitleView(viewGroup);
        if (titleView != null) {
            titleView.setText(attrs.getString(R.styleable.com_gaya3d_android_ui_SettingItem_title));
        }
        
        TextView descView = getDescriptionView(viewGroup);
        if (descView != null) {
            String desc = attrs.getString(R.styleable.com_gaya3d_android_ui_SettingItem_description);
            if (desc != null && desc.length() > 0) {
                descView.setText(desc);
            } else {
                descView.setVisibility(View.GONE);
            }
        }
        
        ImageView iconView = getIconView(viewGroup);
        if (iconView != null) {
            iconView.setImageDrawable(attrs.getDrawable(R.styleable.com_gaya3d_android_ui_SettingItem_icon));
        }
        
        mHighlightImage  = attrs.getDrawable(R.styleable.com_gaya3d_android_ui_SettingItem_highlight);
        mHighlightOnTop = attrs.getBoolean(
                R.styleable.com_gaya3d_android_ui_SettingItem_highlightOnTop, mHighlightOnTop);
    }  
    
    /**
     * 
     * @return
     */
    protected ImageView getIconView(ViewGroup viewGroup) {
        return (ImageView) viewGroup.findViewById(R.id.setting_item_icon);
    }
    
    /**
     * Override this method if you changed the layout.
     * @return Returns the TextView for title.
     */
    protected TextView getTitleView(ViewGroup viewGroup) {
        SettingInfoFragment infoFragment = 
            (SettingInfoFragment) viewGroup.findViewById(R.id.setting_info_fragment);
        if (infoFragment != null) {
            return (TextView) infoFragment.findViewById(R.id.setting_item_title);
        } else {
            return null;
        }
    }
    
    /**
     * Override this method if you changed the layout.
     * @return Returns the TextView for description.
     */
    protected TextView getDescriptionView(ViewGroup viewGroup) {
        SettingInfoFragment infoFragment = 
            (SettingInfoFragment) viewGroup.findViewById(R.id.setting_info_fragment);
        if (infoFragment != null) {
            return (TextView) infoFragment.findViewById(R.id.setting_item_desc);
        } else {
            return null;
        }
    }
    
    public boolean onTouchEvent(ViewGroup viewGroup, MotionEvent ev) {
        final int action = ev.getAction();
        switch (action & MotionEvent.ACTION_MASK) {
        case MotionEvent.ACTION_UP:
        case MotionEvent.ACTION_CANCEL:
            mHighlightRect.setEmpty();
            viewGroup.invalidate();
            break;
        case MotionEvent.ACTION_DOWN:
            viewGroup.getDrawingRect(mHighlightRect);
            viewGroup.invalidate();
            break;
        }
        
        return false;
    }
    
    void drawHighlight(Canvas canvas) {
        if (mHighlightImage != null && !mHighlightRect.isEmpty()) {
            mHighlightImage.setBounds(mHighlightRect);
            mHighlightImage.draw(canvas);
        }
    }
}
