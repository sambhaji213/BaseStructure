package com.basestructure.base;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.basestructure.R;
import com.basestructure.fragment.FragmentFive;
import com.basestructure.fragment.FragmentFour;
import com.basestructure.fragment.FragmentOne;
import com.basestructure.fragment.FragmentThree;
import com.basestructure.fragment.FragmentTwo;
import com.basestructure.util.AppAndroidUtils;

import org.apache.commons.lang.StringUtils;

public class BaseActivity extends AppCompatActivity implements AppIActivity {

    public FragmentOne fragmentOne = null;
    public FragmentTwo fragmentTwo = null;
    public FragmentThree fragmentThree = null;
    public FragmentFour fragmentFour = null;
    public FragmentFive fragmentFive = null;

    public Toolbar toolbar;
    public DrawerLayout mDrawerLayout;
    public NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
    }

    public void replaceWithFragmentLandingPage(){
        if(fragmentOne ==null) {
            fragmentOne = new FragmentOne();
        }
        replaceFragment(fragmentOne, fragmentOne.getClass().toString());
    }

    void replaceOrAddFragment( Fragment newFragment){
        FragmentManager manager = getSupportFragmentManager();
        for(Fragment fragment:manager.getFragments()){
            if( fragment !=null && StringUtils.equals(fragment.getTag(), fragment.getClass().getName())) {
                replaceFragment(fragment, fragment.getClass().getName());
                return;
            }
        }
    }

    @Override
    protected void onStop(){
        super.onStop();
    }

    @Override
    public void setActionBarTitle(String actionbarTitle) {
        if( getSupportActionBar() != null )
            getSupportActionBar().setTitle(actionbarTitle);
    }

    @Override
    public String getActionBarTitle() {
        if( getSupportActionBar() != null && getSupportActionBar().getTitle() != null )
            return getSupportActionBar().getTitle().toString();
        return "";
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
    }

    public boolean replaceDrawerFragment (Fragment fragment, String backStackTag){
        replaceFragment (fragment, backStackTag);
        mDrawerLayout.closeDrawers();
        return true;
    }

    public void replaceFragment (Fragment fragment){
        replaceFragment(fragment, "");
    }

    @Override
    public void replaceFragment (Fragment fragment, String backStackTag){
        AppAndroidUtils.hideKeyboard(this);

        if(StringUtils.isEmpty(backStackTag))
            backStackTag =  fragment.getClass().getName();
        FragmentManager manager = getSupportFragmentManager();
        boolean fragmentPopped = manager.popBackStackImmediate(backStackTag, 0);

        if (!fragmentPopped && manager.findFragmentByTag(backStackTag) == null){
            FragmentTransaction ft = manager.beginTransaction();
            ft.replace(R.id.frame_container, fragment, backStackTag);
            ft.addToBackStack(backStackTag);
            ft.commit();
        }
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public boolean removeFromBackStack (Fragment fragment, String backStackTag){
        if(StringUtils.isEmpty(backStackTag))
            backStackTag =  fragment.getClass().getName();
        FragmentManager manager = getSupportFragmentManager();
        return manager.popBackStackImmediate(backStackTag, FragmentManager.POP_BACK_STACK_INCLUSIVE);

    }

    public boolean removeFromBackStack (Fragment fragment ){
        return removeFromBackStack(fragment, fragment.getClass().getName());
    }

    @Override
    public void onBackPressed() {
        AppAndroidUtils.hideKeyboard(this);
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount()>1) {
            fm.popBackStackImmediate();
        } else {
            onExit();
        }
    }

    public void onExit() {
        final Context mContext = this;
        AlertDialog.Builder builder=new AlertDialog.Builder(mContext);
        builder.setTitle(getResources().getString(R.string.msg_rate_us));
        builder.setIcon(R.drawable.ic_launcher);
        builder.setMessage(getResources().getString(R.string.msg_do_you_really));
        builder.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setNeutralButton(getResources().getString(R.string.rate), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/com.basestructure")));
                }
            }
        });
        AlertDialog alert=builder.create();
        alert.show();
    }

    public void shareApp() {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = "Hi, I am using 'Base Structure' App, This is number one Base application in World. " +
                "Install from https://play.google.com/store/apps/details?id=com.basestructure";
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().
                getString(R.string.app_name));
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    @Override
    public void setNavigationView(int resId){
        navigationView.setCheckedItem(resId);
    }
}
