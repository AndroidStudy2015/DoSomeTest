package com.view.custom.dosometest.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * 描述当前版本功能
 *
 * @Project: DoSomeTest
 * @author: cjx
 * @date: 2019-12-01 10:06  星期日
 */
public class SwitchView extends LinearLayout {


    private ImageView mImageView;
    private LayoutParams mLayoutParams;
    private int mSliderHeight;
    private int mSliderWidth;
    private int mWidth;
    private int mHeight;

    public SwitchView(Context context) {
        super(context);
        init(context);
    }

    public SwitchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SwitchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private void init(Context context) {
        setBackgroundColor(Color.GRAY);

        post(new Runnable() {
            @Override
            public void run() {
                addSlider();
            }
        });

    }

    private void addSlider() {
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();

        mSliderWidth = mWidth / 2;
        mSliderHeight = mHeight;

        mLayoutParams = new LayoutParams(mSliderWidth, mSliderHeight);
        mImageView = new ImageView(getContext());
        mImageView.setBackgroundColor(Color.CYAN);
        mImageView.setLayoutParams(mLayoutParams);
        addView(mImageView);
    }


    float mLastX;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float x = event.getX();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                mLastX = x;
                break;

            case MotionEvent.ACTION_MOVE:
                float deltaX = x - mLastX;

                // 防止滑块滑出边界，矫正deltaX
                if (mLayoutParams.leftMargin + mSliderWidth + deltaX > mWidth) {
                    deltaX = mWidth - mLayoutParams.leftMargin - mSliderWidth;
                } else if (mLayoutParams.leftMargin + deltaX < 0) {
                    deltaX = -mLayoutParams.leftMargin;
                }

                mLayoutParams.leftMargin += (int) deltaX;
                mImageView.setLayoutParams(mLayoutParams);
                mLastX = x;
                break;


            case MotionEvent.ACTION_UP:

                if (mLayoutParams.leftMargin > mSliderWidth / 2) {
                    smoothChangeLeftMargin(mLayoutParams.leftMargin, mSliderWidth);

                } else {
                    smoothChangeLeftMargin(mLayoutParams.leftMargin, 0);

                }

                break;
        }
        return true;
    }

    /**
     * 使用属性动画平滑地过度leftMargin
     * @param start
     * @param end
     */
    private void smoothChangeLeftMargin(int start, int end) {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(start, end);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mLayoutParams.leftMargin = (int) animation.getAnimatedValue();
                mImageView.setLayoutParams(mLayoutParams);

            }
        });
        valueAnimator.setDuration(300);
        valueAnimator.start();

    }
}
