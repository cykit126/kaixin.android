package com.kaixindev.android.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class RadioItem extends FrameLayout {
    private boolean mIsChecked = false;
    String mValue = null;
    
    public RadioItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setChecked(boolean isChecked) {
        mIsChecked = isChecked;
    }
    
    public boolean isChecked() {
        return mIsChecked;
    }
    
    public void setValue(String value) {
        mValue = value;
    }
    
    public String getValue() {
        return mValue;
    }
}
