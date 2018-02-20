package com.raspisaniyevuzov.app.ui.widget;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

import com.raspisaniyevuzov.app.ui.ScheduleFragment;

/**
 * Created by SAPOZHKOV on 05.10.2015.
 */
public class CustomSwipeRefreshLayout extends SwipeRefreshLayout {

    private boolean mAcceptEvents;
    private ScheduleFragment scheduleFragment;

    public CustomSwipeRefreshLayout(Context context) {
        super(context);
    }

    public void setAcceptEvents(boolean mAcceptEvents) {
        this.mAcceptEvents = mAcceptEvents;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mAcceptEvents ? super.onInterceptTouchEvent(ev) : true;
    }

    public CustomSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean canChildScrollUp() {
        ScrollView scrollView = scheduleFragment.getCurrentScrollView();
        if (scrollView != null)
            return scrollView.canScrollVertically(-1);
        return false;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mAcceptEvents = true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mAcceptEvents = false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        try {
            return super.onTouchEvent(ev);
        } catch (IllegalArgumentException e) {
            //Fix for support lib bug, happening when onDestroy is
            return true;
        }
    }

    public void setDelegate(ScheduleFragment scheduleFragment) {
        this.scheduleFragment = scheduleFragment;
    }

}