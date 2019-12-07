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
public class RefreshView extends LinearLayout {


    private ScrollView mScrollView;
    private View mHeader;
    private int mHeaderHeight;
    private MarginLayoutParams mLp;

    public RefreshView(Context context) {
        super(context);
        init(context);
    }

    public RefreshView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RefreshView(Context context, AttributeSet attrs, int defStyleAttr) {
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

    int curActiveId = 0;// 当前操作滑动的手指的id
    int lastActiveId = 0;//上次操作滑动的手指的id
    int curActiveIndex = 0;//当前操作滑动的手指的index

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int count = event.getPointerCount();
        // 避免索引越界，应该不会越界，判断一下稳妥
        curActiveIndex = (curActiveIndex >= count) ? count - 1 : curActiveIndex;
        curActiveIndex = (curActiveIndex < 0) ? 0 : curActiveIndex;
        Log.e("qqq", "curActiveIndex:" + curActiveIndex);
        float y = event.getY(curActiveIndex);//得到操控手指的坐标（只是关心操控手指）
        curActiveId = event.getPointerId(curActiveIndex);
        //下面判断手指是不是同一个，必须用id，因为index随时会变的
        if (curActiveId != lastActiveId) {//判断当前操控手指id和上次操控手指id是不是一样
            mLastY = y;//★★★如果不一样，马上把此刻的y坐标赋值给上次的y坐标，这是避免位移突变的关键点
        }

        switch (event.getActionMasked()) {//一定要用getActionMasked
            case MotionEvent.ACTION_DOWN:

                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                //新手指按下，让它成为控制手指，更新下当前的控制手指的index
                curActiveIndex = event.getActionIndex();
                break;

            case MotionEvent.ACTION_POINTER_UP:

                int upIndex = event.getActionIndex();
                Log.e("qqq", "upIndex:" + upIndex + " curActiveIndex:" + curActiveIndex);
                if (curActiveIndex > upIndex) {
                    // 如果当前控制手指的index>抬起的手指index，需要减去一（很关键，博客分析过）
                    curActiveIndex = curActiveIndex - 1;
                } else if (curActiveIndex == upIndex) {
                    // 如果相等，说明你抬起来的就是操控手指，那么变更操控手指为第一根手指
                    curActiveIndex = 0;
                }

                break;


            case MotionEvent.ACTION_MOVE:
                float deltaY = y - mLastY;

                // 防止刷新头被无限制下拉，限定个高度

                if (mLp.topMargin + deltaY > mHeaderHeight) {
                    deltaY = mHeaderHeight - mLp.topMargin;
                }

                else if (mLp.topMargin + deltaY<=-mHeaderHeight){
                    deltaY=-mHeaderHeight-mLp.topMargin;
                }
                // 动态改变刷新头的topMargin
                mLp.topMargin += (int) deltaY;
                Log.e("ccc", "y:" + y + "mLastY：" + mLastY + "deltaY：" + deltaY + "mLp.topMargin：" + mLp.topMargin);
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
                //松手后，看位置，如果过半，刷新头全部显示，没过半，刷新头全部隐藏
                if (mLp.topMargin > -mHeaderHeight / 2) {
                    smoothChangeTopMargin(mLp.topMargin, 0);
                } else {
                    smoothChangeTopMargin(mLp.topMargin, -mHeaderHeight);
                }

                break;
        }

        mLastY = y;
        lastActiveId = curActiveId;//别忘了，更新上次的操控手指id

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
