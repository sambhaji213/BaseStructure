package com.basestructure.util;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

public class AppRecyclerView extends RecyclerView {
    Context context;

    public void setVelocityX(double velocityX) {
        this.velocityX = velocityX;
    }

    public void setVelocityY(double velocityY) {
        this.velocityY = velocityY;
    }

    double  velocityX=0.8;
    double  velocityY=0.8;


    public AppRecyclerView(Context context) {
        super(context);
        this.context = context;
    }

    public AppRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AppRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean fling(int velocityX, int velocityY) {

        velocityX *= this.velocityX;
        velocityY *= this.velocityY;

        return super.fling(velocityX, velocityY);
    }

}
