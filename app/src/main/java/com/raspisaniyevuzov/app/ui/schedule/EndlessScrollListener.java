package com.raspisaniyevuzov.app.ui.schedule;

import android.widget.AbsListView;

/**
 * Created by SAPOZHKOV on 25.09.2015.
 */
public abstract class EndlessScrollListener implements AbsListView.OnScrollListener {

    public final static int SCROLL_DIRECTION_UP = 0;
    public final static int SCROLL_DIRECTION_DOWN = 1;

    // The minimum amount of items to have below your current scroll position
    // before loading more.
    private int visibleThreshold = 5;
    // The current offset index of data you have loaded
    private int currentPage = 0;
    // The total number of items in the dataset after the last load
    private int previousTotalItemCount = 0;

    private int previousFirstVisibleItem = 0;

    // True if we are still waiting for the last set of data to load.
    private boolean loading = true;
    // Sets the starting page index
    private int startingPageIndex = 0;

    private int scrollDirection = SCROLL_DIRECTION_DOWN;

    public EndlessScrollListener() {
    }

    public EndlessScrollListener(int visibleThreshold) {
        this.visibleThreshold = visibleThreshold;
    }

    public EndlessScrollListener(int visibleThreshold, int startPage) {
        this.visibleThreshold = visibleThreshold;
        this.startingPageIndex = startPage;
        this.currentPage = startPage;
    }

    // This happens many times a second during a scroll, so be wary of the code you place here.
    // We are given a few useful parameters to help us work out if we need to load some more data,
    // but first we check if we are waiting for the previous load to finish.
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        // If the total item count is zero and the previous isn't, assume the
        // list is invalidated and should be reset back to initial state
        if (totalItemCount < previousTotalItemCount) {
            this.currentPage = this.startingPageIndex;
            this.previousTotalItemCount = totalItemCount;
            if (totalItemCount == 0)
                this.loading = true;
        }

        setScrollDirection(firstVisibleItem < previousFirstVisibleItem && previousFirstVisibleItem != 0 ? SCROLL_DIRECTION_UP : SCROLL_DIRECTION_DOWN);

        previousFirstVisibleItem = firstVisibleItem;

        // If it’s still loading, we check to see if the dataset count has
        // changed, if so we conclude it has finished loading and update the current page
        // number and total item count.
        if (loading && (totalItemCount > previousTotalItemCount)) {
            loading = false;
            previousTotalItemCount = totalItemCount;
            currentPage++;
        }

        // If it isn’t currently loading, we check to see if we have breached
        // the visibleThreshold and need to reload more data.
        // If we do need to reload some more data, we execute onLoadMore to fetch the data.
        if (!loading) {
            if (scrollDirection == SCROLL_DIRECTION_DOWN && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                onLoadMore(currentPage + 1, totalItemCount);
                loading = true;
            } else if (scrollDirection == SCROLL_DIRECTION_UP && firstVisibleItem <= visibleThreshold) {
                onLoadMore(currentPage + 1, totalItemCount);
                loading = true;
            }
        }
    }

    // Defines the process for actually loading more data based on page
    public abstract void onLoadMore(int page, int totalItemsCount);

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        // Don't take any action on changed
    }

    public int getScrollDirection() {
        return scrollDirection;
    }

    public void setScrollDirection(int scrollDirection) {
        if (scrollDirection == SCROLL_DIRECTION_DOWN || scrollDirection == SCROLL_DIRECTION_UP) {
            this.scrollDirection = scrollDirection;
        }
    }

}