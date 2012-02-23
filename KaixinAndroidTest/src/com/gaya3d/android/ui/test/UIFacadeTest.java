package com.gaya3d.android.ui.test;

import com.gaya3d.android.Log;
import com.gaya3d.android.test.R;
import com.gaya3d.android.ui.UIFacade;

import selena.common.facade.FacadeManager;
import android.content.res.Resources;
import android.test.AndroidTestCase;

public class UIFacadeTest extends AndroidTestCase {
    protected void setUp() {
        Log.ENABLED = true;
    }
    
    public void testGetStringResource() {
        FacadeManager fm = new FacadeManager(getContext());
        try {
            fm.registerReceiver(UIFacade.class);
            Resources r = getContext().getResources();
            assertEquals(r.getString(R.string.app_name),fm.callCommand("ui:getStringResource `app_name`"));
        }
        catch (Throwable e) {
            Log.e(e.getMessage());
            e.printStackTrace();
            assertTrue(false);
        }
    }
}
