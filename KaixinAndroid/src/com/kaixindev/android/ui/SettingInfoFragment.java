package com.kaixindev.android.ui;

import com.kaixindev.android.R;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;

public class SettingInfoFragment extends FrameLayout {
	
    public SettingInfoFragment(Context context, AttributeSet attrs) {
        super(context, attrs);
        ((Activity)getContext()).getLayoutInflater().inflate(R.layout.setting_info_fragment, this);
    }
    
    protected TextView getTitleView() {
        return (TextView)findViewById(R.id.setting_item_title);
    }
    
    protected TextView getDescriptionView() {
        return (TextView) findViewById(R.id.setting_item_desc);
    }
}
