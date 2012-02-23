package com.kaixindev.android.ui;

import com.kaixindev.android.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RadioButton;

public class SettingRadioItem extends RadioItem {
	
	private SettingItemImpl mImpl = new SettingItemImpl();
	
	/**
	 * Constructor
	 * @param context
	 * @param attrs
	 */
	public SettingRadioItem(Context context, AttributeSet attrs) {
		super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(
        		attrs, R.styleable.com_gaya3d_android_ui_SettingItem); 
        setValue(a.getString(R.styleable.com_gaya3d_android_ui_SettingItem_value));
        mImpl.init(context, a, this, R.layout.setting_radio_item);
        setChecked(a.getBoolean(R.styleable.com_gaya3d_android_ui_SettingItem_checked, false));        
        a.recycle();
	}

	/**
	 * Set item checked
	 * @param checked
	 */
	public void setChecked(boolean checked) {
		getRadioButton().setChecked(checked);
	}
	
	/**
	 * Is checked.
	 */
	public boolean isChecked() {
		return getRadioButton().isChecked();
	}
	
	////////////////////////////////////////////////////////////
	
    protected RadioButton getRadioButton() {
        return (RadioButton) findViewById(R.id.setting_radio_item_radio);
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
