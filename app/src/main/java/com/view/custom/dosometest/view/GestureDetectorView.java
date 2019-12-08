package com.view.custom.dosometest.view;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;

/**
 * 描述当前版本功能
 *
 * @Project: DoSomeTest
 * @author: cjx
 * @date: 2019-12-07 18:54  星期六
 */
public class GestureDetectorView extends AppCompatImageView {


    private GestureDetector mDetector;
    private GestureDetector mDetector1;
    private ScaleGestureDetector mScaleGestureDetector;
    private boolean isScale = false;

    public GestureDetectorView(Context context) {
        super(context);
        init(context);
    }

    public GestureDetectorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);

    }
float lastX;
    private void init(Context context) {
        //第一步：new一个GestureDetector.OnGestureListener，
        //★虽然是双击事件也要这个OnGestureListener，因为GestureDetector构造方法需要他
        GestureDetector.OnGestureListener listener = new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                Log.e("GestureDetectorView", "onDown");
                //onDown不返回true，只会回调onShowPress、onLongPress，其余的都检测不到
                //因为其余事件都需要检测到Move或者Up事件，才可以，但是onDown你返回了false，会导致Move或者Up事件收不到，
                //从而onSingleTapUp,onScroll,onFling都检测不到

                //（其实如果你这里返回false，但是给当前View设置了setClickable(true)，也会接受到所有事件）
                return true;
            }

            @Override
            public void onShowPress(MotionEvent e) {
                Log.e("GestureDetectorView", "onShowPress，一般用不到");

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                Log.e("GestureDetectorView", "onSingleTapUp，单击事件");

                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                Log.e("GestureDetectorView", "onScroll，滑动事件");
                lastX=e1.getX();

                float x = e2.getX()-lastX;
//                float y = e1.getY() - e2.getY();
                Log.e("qwe","x:"+x +"   e1.getX():"+e1.getX()+"    e2.getX():"+e2.getX());
                ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) getLayoutParams();
//                lp.topMargin+=y;
                lp.leftMargin+=x;
                setLayoutParams(lp);
                lastX=e2.getX();
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                Log.e("GestureDetectorView", "onLongPress，一般用不到");


            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                Log.e("GestureDetectorView", "onFling，快速滑动");

                return false;
            }
        };
        //第二步：new一个GestureDetector，需要传进来上面的listener

        mDetector = new GestureDetector(context, listener);

        //第三步：new一个OnDoubleTapListener
        GestureDetector.OnDoubleTapListener doubleTapListener = new GestureDetector.OnDoubleTapListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                // ★如果你既需要单击事件，也需要双击事件，那么单击事件的判断标志应该是本方法而不是onSingleTapUp，
                // 因为只要手指抬起就会调用onSingleTapUp，但很可能这次手指抬起，是双击事件的第一次抬起，此时回调onSingleTapUp
                // 会误以为是单击事件出发了
                // 而onSingleTapConfirmed则不同，只有真正的单击事件才会回调onSingleTapConfirmed，
                // 如果是双击的第一次单击，不会回调onSingleTapConfirmed方法
                Log.e("GestureDetector-double", "onSingleTapConfirmed，很有用");

                return false;
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                Log.e("GestureDetector-double", "onDoubleTap，双击事件");

                return false;
            }

            @Override
            public boolean onDoubleTapEvent(MotionEvent e) {
                Log.e("GestureDetector-double", "onDoubleTapEvent，一般用不到");

                return false;
            }
        };
        // mDetector设置双击监听事件
        mDetector.setOnDoubleTapListener(doubleTapListener);


        // 第一步：new simpleOnGestureListener，实现你关心的回调
        GestureDetector.SimpleOnGestureListener simpleOnGestureListener = new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return true;//不是true，单双击事件不回调
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                Log.e("GestureDetectorView", "onSingleTapUp，单击事件");

                return super.onSingleTapUp(e);
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                Log.e("GestureDetectorView", "onSingleTapConfirmed，单击确认事件");

                return super.onSingleTapConfirmed(e);
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                Log.e("GestureDetectorView", "onDoubleTap，双击事件");

                return super.onDoubleTap(e);
            }
        };
        // 第二步：new GestureDetector
        mDetector1 = new GestureDetector(context, simpleOnGestureListener);


        // 第一步：new一个OnScaleGestureListener

        ScaleGestureDetector.OnScaleGestureListener listener1 = new ScaleGestureDetector.OnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                Log.e("Scale", "onScale，正在缩放。。。");
                float scaleFactor = detector.getScaleFactor();
                Log.e("scaleFactor","scaleFactor:"+scaleFactor);

                setScaleX(scaleFactor);
                setScaleY(scaleFactor);
                return false;
            }

            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                Log.e("Scale", "onScale，缩放开始了");
                isScale = true;
                return true;
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {
                isScale = false;
                Log.e("Scale", "onScale，缩放结束了。。。");


            }
        };
        // 第二步：new一个ScaleGestureDetector
        mScaleGestureDetector = new ScaleGestureDetector(context, listener1);
        // 第三步：在onTouch方法里调用mScaleGestureDetector.onTouchEvent(event)，把事件交给mScaleGestureDetector处理
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mScaleGestureDetector.onTouchEvent(event);
//                if (!isScale){
//                    mDetector.onTouchEvent(event);
//                }
                return true;
            }
        });


    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//
//        return mScaleGestureDetector.onTouchEvent(event);
//    }
}
