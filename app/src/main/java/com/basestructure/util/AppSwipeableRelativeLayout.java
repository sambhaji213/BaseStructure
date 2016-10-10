package com.basestructure.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

public class AppSwipeableRelativeLayout extends RelativeLayout {

    boolean  mProcessingSwipe=false, mCancelChildren=false;
    float    mTouchStartPositionX, mTouchStartPositionY;
    Context mContext;
    private static final int SWIPE_DISTANCE_THRESHOLD = 200;

    public AppSwipeableRelativeLayout(Context context) {
        super(context);
        this.mContext=context;
    }

    public AppSwipeableRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext=context;
    }

    public AppSwipeableRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext=context;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AppSwipeableRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mContext=context;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // Preview the touch event to detect a swipe:
        switch (ev.getAction())
        {
            case  MotionEvent.ACTION_DOWN:
                mProcessingSwipe = false;
                mTouchStartPositionX = ev.getX();
                mTouchStartPositionY = ev.getY();
                break;

            case MotionEvent.ACTION_MOVE:
                if (!mProcessingSwipe)
                {
                    float distanceX = ev.getX() - mTouchStartPositionX;
                    float distanceY = ev.getY() - mTouchStartPositionY;
                    if (Math.abs(distanceX) > Math.abs(distanceY) && Math.abs(distanceX) > SWIPE_DISTANCE_THRESHOLD && distanceX>0) {
                        mProcessingSwipe = true;
                        mCancelChildren = true;
                        onSwipeRight();
                    }

                }
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    public void onSwipeRight(){
        ((Activity)mContext).onBackPressed();
    }

    @Override
    public boolean onTouchEvent(MotionEvent e)
    {
        // To make sure to receive touch events, tell parent we are handling them:
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // Cancel all children when processing a swipe:
        if (mCancelChildren)
        {
            // Reset cancel flag here, as OnInterceptTouchEvent won't be called until the next MotionEventActions.Down:
            mCancelChildren = false;
            return true;
        }
        return false;
    }
}
