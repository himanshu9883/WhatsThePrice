package com.techrums.whatstheprice.ui.activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;

public class NavUtils {
    public static Fragment getCurrentFragment(Activity activity) {
        FragmentManager fragmentManager = activity.getFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            String lastFragmentName = fragmentManager.getBackStackEntryAt(
                    fragmentManager.getBackStackEntryCount() - 1).getName();
            return fragmentManager.findFragmentByTag(lastFragmentName);
        }
        return null;
    }
}
