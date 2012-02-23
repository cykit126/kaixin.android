package com.kaixindev.android.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class RadioGroup extends LinearLayout {
	
	/**
	 * This listener is fired when a button in the group is checked.
	 * @author Wilbur
	 */
	public interface OnCheckedChangedListener {
		public void onCheckedChanged(RadioItem item);
	}
	
	/**
	 * Map of groups and items.
	 */
	private List<RadioItem> mRadioItems = new ArrayList<RadioItem>();
	
	/**
	 * Map of groups and listeners.
	 */
	private OnCheckedChangedListener mCheckedChangedListener = null;
	
	/**
	 * Constructor
	 */
	public RadioGroup(Context context, AttributeSet attrs) {
	    super(context,attrs);
	}
	
	
	/**
	 * Set checked listener.
	 * @param group
	 * @param listener
	 */
	public void setOnCheckedChangedListener(OnCheckedChangedListener listener) {
		mCheckedChangedListener = listener;
	}
	
	/**
	 * Remove checked listener.
	 */
	public void removeOnCheckedListener() {
		mCheckedChangedListener = null;
	}
	
	/**
	 * Set a item with the specified value to be checked.
	 * @param value
	 */
	public void setCheckedByValue(String value) {
	    if (value == null) {
	        return;
	    }
        for (RadioItem it : mRadioItems) {
            if (value.equals(it.getValue())) {
                setItemChecked(it);
                return;
            }
        }
	}
	
	/**
	 * Attach radio item to group.
	 * @param item
	 */
	private void attach(final RadioItem item) {
		if (!mRadioItems.contains(item)) {
		    mRadioItems.add(item);
		    
		    item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setItemChecked(item);
                    if (mCheckedChangedListener != null) {
                        mCheckedChangedListener.onCheckedChanged(item);
                    }
                }
            });
		    
		    if (item.isChecked()) {
		        setItemChecked(item);
		    }
		}
	}
	
	private void setItemChecked(RadioItem item) {
	    item.setChecked(true);
        for (RadioItem it : mRadioItems) {
            if (it != item) {
                if (it.isChecked()) {
                    it.setChecked(false);
                }
            }
        }
	}
	
	@Override
	public void addView(View child, int index, ViewGroup.LayoutParams params) {
	    if (child instanceof RadioItem) {
	        attach((RadioItem)child);
	    }
	    super.addView(child,index,params);
	}
	
}
