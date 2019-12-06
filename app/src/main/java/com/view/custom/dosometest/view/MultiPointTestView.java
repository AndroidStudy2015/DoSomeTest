package com.view.custom.dosometest.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

/**
 * 描述当前版本功能
 *
 * @Project: DoSomeTest
 * @author: cjx
 * @date: 2019-12-01 10:06  星期日
 */
public class MultiPointTestView extends LinearLayout {


    private ScrollView mScrollView;
    private View mHeader;
    private int mHeaderHeight;
    private MarginLayoutParams mLp;
    private int mLastPointerCount;

    public MultiPointTestView(Context context) {
        super(context);
        init(context);
    }

    public MultiPointTestView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MultiPointTestView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private void init(Context context) {
        setBackgroundColor(Color.GRAY);

        post(new Runnable() {
            @Override
            public void run() {
                initView();// 因为涉及到获取控件宽高的问题，所以写到post里
            }
        });

    }

    private void initView() {

        if (getChildCount() > 2) {

            // 给刷新头设置负高度的margin，让他隐藏
            mHeader = getChildAt(0);
            mHeaderHeight = mHeader.getMeasuredHeight();
            mLp = (MarginLayoutParams) mHeader.getLayoutParams();
            mLp.topMargin = -mHeaderHeight;
            mHeader.setLayoutParams(mLp);

            // 得到第二个view，scrollView
            View child1 = getChildAt(1);
            if (child1 instanceof ScrollView) {
                mScrollView = (ScrollView) child1;
            }

        }
    }


    float mLastY;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        boolean intercept = false;
        int x = (int) ev.getX();
        int y = (int) ev.getY();

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                intercept = false;
                break;

            case MotionEvent.ACTION_MOVE:
                int deltaY = (int) (y - mLastY);
                if (needIntercept(deltaY)) {//外部拦截的模板代码，只要重写needIntercept方法逻辑就行
                    //注意当前ViewGroup一旦拦截，一次事件序列中就再也不会调用onInterceptTouchEvent了，
                    // 所以子View再也不会得到事件处理的机会了
                    // 为了解决这个问题，就引出了《嵌套滑动》这个新的事物，见下文
                    intercept = true;
                } else {
                    intercept = false;
                }

                break;

            case MotionEvent.ACTION_UP:

                intercept = false;

                break;
            default:
                break;
        }

        mLastY = y;
//        intercept = true;
        return intercept;
    }

    private boolean needIntercept(int deltaInteceptY) {
        // mScrollView已经下拉到最顶部&&你还在下来，那么父容器拦截
        if (!mScrollView.canScrollVertically(-1) && deltaInteceptY > 0) {
            Log.e("ccc", "不能再往下拉了&&你还在往下拉，父布局拦截，开始拉出刷新头");
            return true;
        }
        if (mLp.topMargin > -mHeaderHeight) {
            Log.e("ccc", "只要顶部刷新头，显示着，就让父布局拦截");
            return true;
        }

        return false;
    }

    @Override
    public void requestDisallowInterceptTouchEvent(boolean b) {
        // 去掉默认行为，使得每个事件都会经过这个Layout
    }

    int curActiveId = 0;
    int lastActiveId = 0;
    int curActiveIndex = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int count = event.getPointerCount();
        curActiveIndex = (curActiveIndex >= count) ? count - 1 : curActiveIndex;
        Log.e("qqq", "curActiveIndex:" + curActiveIndex);
        float y = event.getY(curActiveIndex);

        curActiveId = event.getPointerId(curActiveIndex);
        if (curActiveId != lastActiveId) {
            mLastY = y;
        }


        // 获取index，在move时候，此方法无效，只能在ACTION_DOWN，ACTION_POINTER_DOWN，ACTION_POINTER_UP，ACTION_UP里得到的index才是有效的
        int action_index = event.getActionIndex();
        // 通过index得到该手指的id
        int action_id = event.getPointerId(action_index);
        // 得到事件类型
        int action = event.getActionMasked();

        String s = " action: " + action + "   action_index: " + action_index + " action_id: " + action_id;
        Log.e("qwe", s);

//        String s = "";


        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                Log.e("cjx", "ACTION_DOWN-----------" + s);

                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                Log.e("cjx", "ACTION_POINTER_DOWN---" + s);
                curActiveIndex = event.getActionIndex();
                break;

            case MotionEvent.ACTION_POINTER_UP:
                Log.e("cjx", "ACTION_POINTER_UP-----" + s);
//                int curUpId = event.getPointerId(event.getActionIndex());
//                if (curUpId == curActiveId) {// 多指操作的情况下，如果抬起的手指，是当前正在激活的手指，那么当前索引变为0
//                    curActiveIndex = 0;
//                }
                int upIndex = event.getActionIndex();
                Log.e("qqq", "upIndex:" + upIndex + " curActiveIndex:" + curActiveIndex);
                if (curActiveIndex > upIndex) {
                    curActiveIndex = curActiveIndex - 1;
                } else if (curActiveIndex == upIndex) {
                    curActiveIndex = 0;
                }

                break;


            case MotionEvent.ACTION_MOVE:
//                Log.e("cjx", "ACTION_MOVE"+ s);
                Log.e("woaini", "ACTION_MOVE-----------" + event.getActionIndex() + "  ");

                float deltaY = y - mLastY;

                // 防止刷新头被无限制下拉，限定个高度

                if (mLp.topMargin + deltaY > mHeaderHeight * 2) {
                    deltaY = mHeaderHeight * 2 - mLp.topMargin;
                }
                // 动态改变刷新头的topMargin
                mLp.topMargin += (int) deltaY;
                mHeader.setLayoutParams(mLp);

                if (mLp.topMargin <= -mHeaderHeight && deltaY < 0) {
                    // 重新dispatch一次down事件，使得列表可以继续滚动
                    int oldAction = event.getAction();
                    event.setAction(MotionEvent.ACTION_DOWN);
                    dispatchTouchEvent(event);
                    event.setAction(oldAction);
                }
                break;


            case MotionEvent.ACTION_UP:
                Log.e("cjx", "ACTION_UP-------------" + s);

                //松手后，看位置，如果过半，刷新头全部显示，没过半，刷新头全部隐藏
                if (mLp.topMargin > -mHeaderHeight / 2) {
                    smoothChangeTopMargin(mLp.topMargin, 0);
                } else {
                    smoothChangeTopMargin(mLp.topMargin, -mHeaderHeight);
                }

                break;
        }

        mLastY = y;
        lastActiveId = curActiveId;

        return true;
    }

    /**
     * 使用属性动画平滑地过度topMargin
     *
     * @param start
     * @param end
     */
    private void smoothChangeTopMargin(int start, int end) {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(start, end);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mLp.topMargin = (int) animation.getAnimatedValue();
                mHeader.setLayoutParams(mLp);

            }
        });
        valueAnimator.setDuration(300);
        valueAnimator.start();

    }
}
