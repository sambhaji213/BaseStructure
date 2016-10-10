package com.basestructure.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.basestructure.R;
import com.basestructure.base.BaseActivity;
import com.basestructure.fragment.FragmentFive;
import com.basestructure.fragment.FragmentFour;
import com.basestructure.fragment.FragmentOne;
import com.basestructure.fragment.FragmentThree;
import com.basestructure.fragment.FragmentTwo;
import com.basestructure.util.AppAndroidUtils;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        setToolBar();
        setNavigationDrawer();

        replaceWithFragmentLandingPage();
    }

    private void setToolBar() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public void setNavigationDrawer() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                mDrawerLayout.closeDrawers();
                Fragment fragment = null;
                String mTitle = "";

                if (menuItem.getItemId() == R.id.nav_one){
                    if (fragmentOne == null) {
                        fragmentOne = new FragmentOne();
                    }
                    mTitle = getResources().getString(R.string.menu_one);
                    fragment = fragmentOne;

                } else if (menuItem.getItemId() == R.id.nav_two) {
                    if (fragmentTwo == null) {
                        fragmentTwo = new FragmentTwo();
                    }
                    mTitle = getResources().getString(R.string.menu_two);
                    fragment = fragmentTwo;

                } else if (menuItem.getItemId() == R.id.nav_three) {
                    if (fragmentThree == null) {
                        fragmentThree = new FragmentThree();
                    }
                    mTitle = getResources().getString(R.string.menu_three);
                    fragment = fragmentThree;

                } else if (menuItem.getItemId() == R.id.nav_four) {
                    if (fragmentFour == null) {
                        fragmentFour = new FragmentFour();
                    }
                    mTitle = getResources().getString(R.string.menu_four);
                    fragment = fragmentFour;

                } else if (menuItem.getItemId() == R.id.nav_five) {
                    if (fragmentFive == null) {
                        fragmentFive = new FragmentFive();
                    }
                    mTitle = getResources().getString(R.string.menu_five);
                    fragment = fragmentFive;
                }

                if (fragment != null) {
                    replaceFragment(fragment, fragment.getClass().toString());
                    mDrawerLayout.closeDrawers();
                    ActionBar actionBar = getSupportActionBar();
                    if (actionBar != null) {
                        actionBar.setTitle(mTitle);
                    }
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        switch(itemId) {
            case android.R.id.home: {
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            }
            case R.id.action_settings:
                return true;

            case R.id.action_test:
                Intent intent = new Intent(getApplicationContext(), TestActivity.class);
                startActivity(intent);
                AppAndroidUtils.startFwdAnimation(MainActivity.this);
                return true;
        }
        return true;
    }

    @Override
    public void onBackPressed() {

        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawers();
        } else {
            super.onBackPressed();
        }
    }
}
