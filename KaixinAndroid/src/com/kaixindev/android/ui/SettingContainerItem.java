package com.kaixindev.android.ui;

import com.kaixindev.android.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class SettingContainerItem extends FrameLayout {
    
    private SettingItemImpl mImpl = new SettingItemImpl();

    /**
     * Constructor
     * @param context
     * @param attrs
     * @throws SettingItemException 
     */
    public SettingContainerItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        TypedArray a = context.obtainStyledAttributes(
        		attrs, R.styleable.com_gaya3d_android_ui_SettingItem);     
        mImpl.init(context, a, this, R.layout.setting_container_item);
        Drawable indicator = a.getDrawable(
        		R.styleable.com_gaya3d_android_ui_SettingItem_container_indicator);
        if (indicator != null) {
            getIndicator().setImageDrawable(indicator);
        }
        a.recycle();
    }

    public ImageView getIndicator() {
        return (ImageView) findViewById(R.id.setting_container_indicator);
    }
    
    public TextView getTitleView() {
        return mImpl.getTitleView(this);
    }
    
    public TextView getDescriptionView() {
        return mImpl.getDescriptionView(this);
    }
    
    public ImageView getIconView() {
        return mImpl.getIconView(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        super.onTouchEvent(ev);
        mImpl.onTouchEvent(this,ev);
        return true;
    }
    
    @Override
    public void dispatchDraw(Canvas canvas) {
        if (!mImpl.mHighlightOnTop) {
            mImpl.drawHighlight(canvas);
        }
        super.dispatchDraw(canvas);
        if (mImpl.mHighlightOnTop) {
            mImpl.drawHighlight(canvas);
        }        
    }
}
