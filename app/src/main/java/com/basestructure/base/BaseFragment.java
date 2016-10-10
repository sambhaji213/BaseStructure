package com.basestructure.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ViewFlipper;

/**
 * The Class BaseFragment.
 */
public class BaseFragment extends Fragment {

    public ViewFlipper mViewFlipper;
    public static final int SWIPE_MIN_DISTANCE = 20;
    public static final int SWIPE_THRESHOLD_VELOCITY = 20;
    public final GestureDetector detector = new GestureDetector(new SwipeGestureDetector());
    public AppIActivity mAppIActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void replaceFragment(Fragment fragment, String backStackTag) {
    }

    public void setActionBarTitle(String actionbarTitle) {
    }

    public void showActionBar(boolean bShow) {
        ActionBar actionBar= ((AppCompatActivity)getActivity()).getSupportActionBar();
        if(actionBar!=null && bShow)
            actionBar.show();
        else if(actionBar !=null)
            actionBar.hide();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public void updateAvailability(int availability, int fromUser){

    }

    public boolean isAttachedToActivity(){
        Activity activity = getActivity();
        return activity != null && isAdded();
    }

    public boolean isNotAttachedToActivity(){
        Activity activity = getActivity();
        return !(activity != null && isAdded());
    }

    public CharSequence resourceToString(int resource){
        if(isAttachedToActivity())
            return getResources().getString(resource);
        else
            return "";
    }

    class SwipeGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    if(mViewFlipper.getDisplayedChild()<mViewFlipper.getChildCount()-1) {
                        mViewFlipper.showNext();
                        mViewFlipper.stopFlipping();
                    }
                    return true;
                } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    if(mViewFlipper.getDisplayedChild() > 0) {
                        mViewFlipper.showPrevious();
                        mViewFlipper.stopFlipping();
                    }
                    return true;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return false;
        }
    }
}
