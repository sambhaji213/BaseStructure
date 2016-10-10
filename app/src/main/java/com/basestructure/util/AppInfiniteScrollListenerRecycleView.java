package com.basestructure.util;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public abstract class AppInfiniteScrollListenerRecycleView extends
        RecyclerView.OnScrollListener {
    public static String TAG = AppInfiniteScrollListenerRecycleView.class
            .getSimpleName();

    private int previousTotal = 0;
    private boolean loading = true;
    private int mVisibleThreshold = 5;
    private int currentPage = 0;
    private boolean isLoading = true;
    int firstVisibleItem, visibleItemCount, totalItemCount;

    private int current_page = 1;

    private LinearLayoutManager mLinearLayoutManager;
    private GridLayoutManager mGridLayoutManager;

    public AppInfiniteScrollListenerRecycleView(int visibleThreshold) {
        this.mVisibleThreshold=visibleThreshold;
    }

    public void setLinearLayoutManager( LinearLayoutManager linearLayoutManager ){
        this.mLinearLayoutManager = linearLayoutManager;
    }

    public void setGridLayoutManager( GridLayoutManager gridLayoutManager ){
        this.mGridLayoutManager = gridLayoutManager;
    }
    public void initialize( ){
        this.previousTotal = 0;
        this.loading = false;
        this.current_page = 0;
    }

    public void initialize( int previousTotal, boolean loading, int current_page){
        this.previousTotal = previousTotal;
        this.loading = loading;
        this.current_page = current_page;
    }

    public void resetPageCounter() {
        currentPage=0;
        isLoading=false;
    }
    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        if(mLinearLayoutManager ==null)
            return;

        visibleItemCount = recyclerView.getChildCount();
        totalItemCount = mLinearLayoutManager.getItemCount();
        firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();

        if (loading) {
            if (totalItemCount > previousTotal) {
                loading = false;
                previousTotal = totalItemCount;
            }
        }
        if (!loading
                && (totalItemCount - visibleItemCount) <= (firstVisibleItem + mVisibleThreshold)) {
            // End has been reached

            // Do something
            current_page++;

            onLoadMore(current_page);

            loading = true;
        }
    }

    public abstract void onLoadMore(int current_page);

    public void resetPageCount(int currentPage){
        this.current_page = currentPage;
    }
}
