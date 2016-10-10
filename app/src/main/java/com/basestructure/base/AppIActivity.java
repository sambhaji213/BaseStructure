package com.basestructure.base;

import android.support.v4.app.Fragment;

public interface AppIActivity {

    void replaceFragment(Fragment fragment, String backStackTag);
    void replaceFragment(Fragment fragment);

    void setActionBarTitle(String actionbarTitle);
    String getActionBarTitle();
    void setNavigationView(int resId);
    boolean removeFromBackStack(Fragment fragment, String backStackTag);
    boolean removeFromBackStack(Fragment fragment);

    void replaceWithFragmentLandingPage();
}
