package com.basestructure.util;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.animation.AnimationUtils;
import android.widget.ViewFlipper;

import com.basestructure.R;

public class AppViewFlipper extends ViewFlipper {

    Context mContext;
    AppCompatActivity parentActivity;

    public AppViewFlipper(Context context) {
        super(context);
        mContext=context;
    }

    public AppViewFlipper(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext=context;
    }

    @Override
    public void setDisplayedChild(int whichChild) {
        setFlipperChildPosition(whichChild);
        int currentDisplayed=getDisplayedChild();
        if(currentDisplayed<whichChild){
            setInAnimation(AnimationUtils.loadAnimation(mContext, R.anim.left_in));
            setOutAnimation(AnimationUtils.loadAnimation(mContext, R.anim.left_out));
        }
        else
        {
            setInAnimation(AnimationUtils.loadAnimation(mContext, R.anim.right_in));
            setOutAnimation(AnimationUtils.loadAnimation(mContext, R.anim.right_out));
        }
        super.setDisplayedChild(whichChild);
    }

    public void setActivity(AppCompatActivity parentActivity) {
        this.parentActivity=parentActivity;
    }

    public void setFlipperChildPosition(int pageNo){
        if(parentActivity==null)
            return;

        String o="o ";
        String dot=parentActivity.getString(R.string.dot)+" ";
        String displayString= pageNo==1?dot+o+dot+dot:
                              pageNo==2?dot+dot+o+dot:
                              pageNo==3?dot+dot+dot+o: o+dot+dot+dot;
        //((MainActivity)parentActivity).setFlipperPosition(displayString);
    }
}
