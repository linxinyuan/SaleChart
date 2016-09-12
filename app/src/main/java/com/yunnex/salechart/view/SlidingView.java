package com.yunnex.salechart.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

/**
 * Author name venco
 * Created on 2016/9/12.
 */
public class SlidingView extends ScrollView {

    private int mHideViewHeight;
    private int mHalfHideViewHeight;
    private int mMsgContainerHeight;

    private int mWidgetHeight = 160;
    private int mWidgetMargin = 10;
    private int mMsgWidgetHeight = 90;

    private boolean once;

    public SlidingView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (!once) {
            LinearLayout wrapper = (LinearLayout) getChildAt(0);

            ViewGroup hideView = (ViewGroup) wrapper.getChildAt(0);
            ViewGroup msgContent = (ViewGroup) wrapper.getChildAt(1);

            mHideViewHeight = getAttrInDip(mWidgetHeight * 2 + mWidgetMargin);
            mHalfHideViewHeight = mHideViewHeight / 2;
            mMsgContainerHeight = getAttrInDip((mWidgetHeight + mWidgetMargin) * 2 + mMsgWidgetHeight);

            hideView.getLayoutParams().height = mHideViewHeight;
            msgContent.getLayoutParams().height = mMsgContainerHeight;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed) {
            this.scrollTo(0, mHideViewHeight);
            once = true;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_UP:
                int scrollY = getScrollY();
                if (scrollY > mHalfHideViewHeight)
                    this.smoothScrollTo(0, mHideViewHeight);
                else
                    this.smoothScrollTo(0, 0);
                return true;
        }
        return super.onTouchEvent(ev);
    }

    protected int getAttrInDip(int def) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                def, getResources().getDisplayMetrics());
    }
}
