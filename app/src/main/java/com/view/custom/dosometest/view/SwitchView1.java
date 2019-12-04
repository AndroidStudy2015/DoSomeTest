package com.view.custom.dosometest.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Scroller;

import com.view.custom.dosometest.R;

/**
 * 描述当前版本功能
 * scroller实现
 *
 * @Project: DoSomeTest
 * @author: cjx
 * @date: 2019-12-01 10:06  星期日
 */
public class SwitchView1 extends LinearLayout {


    private ImageView mImageView;
    private LayoutParams mLayoutParams;
    private int mSliderHeight;
    private int mSliderWidth;
    private int mWidth;
    private int mHeight;
    private Scroller mScroller;

    public SwitchView1(Context context) {
        super(context);
        init();
    }

    public SwitchView1(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SwitchView1(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
//        setBackgroundColor(Color.GRAY);
       setBackgroundResource(R.drawable.switch_bg);

        mScroller = new Scroller(getContext());

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
//        mImageView.setBackgroundColor(Color.CYAN);
        mImageView.setBackgroundResource(R.drawable.slider);
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
                int deltaX = (int) (x - mLastX);

                // 防止滑块滑出边界，矫正deltaX,这给地方打印出getScrollX()、deltaX的日志仔细看，就写对了，小逻辑有点绕
                if (getScrollX() - deltaX <= -mWidth / 2) {
                    deltaX = getScrollX() + mWidth / 2;
                } else if (getScrollX() - deltaX > 0) {
                    deltaX = getScrollX();
                }

                // SwitchView1是个LinearLayout，他的子控件是滑块Slider，假设我们想让滑块往右侧移动
                // 那么我们应该先找到滑块的父容器，让他去调用scrollBy方法，正好父容器就是SwitchView1，所以this.scrollBy()即可，
                // ok,既然我们是让父容器SwitchView1去scrollBy，那么父容器往左侧移动，滑块才能看起来是往右侧移动，所以要在deltaX前面加负号
                // 总结：每次调用scrollBy，让该子控件的父容器去调用scrollBy方法，然后方向取反即可
                // （但是父容器scrollBy了，他的所有子View都会动，这是副作用，当你需要所有子View一起动，那就正好ok）

                ((View) mImageView.getParent()).scrollBy(-deltaX, 0);// ★这是一个普适的公式，谁要滑动，就找谁的父亲去调用scrollBy，然后方向取反
                //scrollBy(-deltaX, 0);//这句话在本例子里和上句话是等效的，因为滑块的父亲就是this


                Log.e("qwe", getScrollX() + "scroll");
                mLastX = x;
                break;


            case MotionEvent.ACTION_UP:

                if (getScrollX() > -mSliderWidth / 2) {
                    mScroller.startScroll(
                            ((View) mImageView.getParent()).getScrollX(),
                            ((View) mImageView.getParent()).getScrollY(),
                            0 - ((View) mImageView.getParent()).getScrollX(),
                            0,
                            300);
                    // 本例子下面的语句与上面的等效，因为((View) mImageView.getParent())就是this
//                    mScroller.startScroll(getScrollX(), getScrollY(), 0 - getScrollX(), 0, 300);

                } else {
                    mScroller.startScroll(
                            ((View) mImageView.getParent()).getScrollX(),
                            ((View) mImageView.getParent()).getScrollY(),
                            -mWidth / 2 - ((View) mImageView.getParent()).getScrollX(),
                            0,
                            300);
                    // 本例子下面的语句与上面的等效，因为((View) mImageView.getParent())就是this
//                    mScroller.startScroll(getScrollX(), getScrollY(), getScrollX(), 0, 300);
                }
                invalidate();
                break;
        }
        return true;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();

        if (mScroller.computeScrollOffset()) {
            ((View) mImageView.getParent()).scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            // 本例子下面的语句与上面的等效，因为((View) mImageView.getParent())就是this
//            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            invalidate();
        }
    }
}
