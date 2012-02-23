package com.kaixindev.android.ui;

import com.kaixindev.android.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.FrameLayout;

public class SettingCheckboxItem extends FrameLayout {
    
    private OnCheckedChangedListener mOnCheckChangedListener = null;
    private boolean mIsChecked = false;
    private SettingItemImpl mImpl = new SettingItemImpl();
    
    public interface OnCheckedChangedListener {
        public void onCheckedChanged(SettingCheckboxItem item, boolean isChecked);
    }
    
    private class OnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            mIsChecked = !mIsChecked;
            CheckBox checkBox = getCheckBox();
            checkBox.setChecked(mIsChecked);
            if (mOnCheckChangedListener != null) {
                mOnCheckChangedListener.onCheckedChanged(SettingCheckboxItem.this, mIsChecked);
            }
        }
    }

    /**
     * Constructor
     * @param context
     * @param attrs
     * @throws SettingItemException 
     */
    public SettingCheckboxItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        TypedArray a = context.obtainStyledAttributes(
        		attrs,R.styleable.com_gaya3d_android_ui_SettingItem);
        mImpl.init(context, a, this, R.layout.setting_checkbox_item);
        mIsChecked = a.getBoolean(R.styleable.com_gaya3d_android_ui_SettingItem_checked, false);  
        a.recycle();

        this.setOnClickListener(new OnClickListener()); 
    }

    public void setOnCheckedChangedListener(OnCheckedChangedListener onCheckChangeListener) {
        mOnCheckChangedListener = onCheckChangeListener;
    }

    protected CheckBox getCheckBox() {
        return (CheckBox) findViewById(R.id.setting_checkbox_item_checkbox);
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
