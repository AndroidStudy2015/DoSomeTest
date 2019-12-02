package com.view.custom.dosometest.view;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * 描述当前版本功能
 *
 * @Project: DoSomeTest
 * @author: cjx
 * @date: 2019-12-01 10:06  星期日
 */
public class MyLinearLayoutWithMargin extends ViewGroup {


    public MyLinearLayoutWithMargin(Context context) {
        super(context);
    }

    public MyLinearLayoutWithMargin(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyLinearLayoutWithMargin(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int top = 0;
        int count = getChildCount();
        for (int i = 0; i < count; i++) {

            View child = getChildAt(i);

            // 得到MarginLayoutParams，margin就在这里保存着
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();


            // 获得子控件的宽高（需要加上对应的margin,让控件的宽高包含margin）
            int childWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            int childHeight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;

            // 布局，就是设置好这个子控件的左上右下
            // 在布局的时候，考虑控件的margin，把子view摆放好
            // (这是一个竖向的LinearLayout，想想如何布置子控件)
            child.layout(lp.leftMargin,
                    top + lp.topMargin,
                    childWidth - lp.rightMargin,
                    top + childHeight - lp.bottomMargin);
            top += childHeight;

        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 首先计算一下在AtMost模式下，这个自定义view的宽高，
        // 这里把计算出来的宽高封装在了一个Point里，x为宽，y为高
        Point point = caculateAtMostSize(widthMeasureSpec, heightMeasureSpec);

        // 根据 默认宽高、AtMost下的宽高、MeasureSpec测量规格，计算出最终这个view的宽高
        int width = measureSize(0, point.x, widthMeasureSpec);
        int height = measureSize(0, point.y, heightMeasureSpec);

        // 把上面计算出来的宽高作为参数设置给setMeasuredDimension就ok了
        setMeasuredDimension(width, height);
    }


    /**
     * 通过widthMeasureSpec计算出这个View最终的宽高
     *
     * @param defalut     这个view的默认值，仅仅是为了支持下UNSPECIFIED模式，但是这个模式其实用不到
     * @param atMostSize  AT_MOST下的尺寸
     * @param measureSpec 测量规格（包含了模式+尺寸）
     * @return
     */
    private int measureSize(int defalut, int atMostSize, int measureSpec) {


        int result = defalut;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        switch (specMode) {
            case MeasureSpec.UNSPECIFIED:
                result = defalut;
                break;
            case MeasureSpec.AT_MOST:
                //在AT_MOST模式下，系统传来的specSize是一个父容器所能容纳的最大值，你这个自定义view计算的尺寸不能大于这个值
                result = Math.min(atMostSize, specSize);
                break;
            case MeasureSpec.EXACTLY:
                result = specSize;
                break;
        }


        return result;
    }


    /**
     * 计算本View在AtMost模式下的宽高
     * 其他代码都是不用动的，在这里写下你特有的逻辑就可以
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     * @return
     */
    private Point caculateAtMostSize(int widthMeasureSpec, int heightMeasureSpec) {

        int width = 0;
        int height = 0;

        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            // 测量一下子控件的宽高
            measureChild(child, widthMeasureSpec, heightMeasureSpec);

            // 得到MarginLayoutParams，margin就在这里保存着
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
            // 获得子控件的宽高（需要加上对应的margin,让控件的宽高包含margin，
            // 这样才能让自定义的viewgroup在计算自身在AtMost模式的尺寸时候考虑到这些margin）
            int childWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            int childHeight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;

            // 因为我们的自定义View模拟的是竖向的LinearLayout，所以：
            // 控件的宽度为所有子控件里，宽度最大的那个view的宽度，
            // 控件高度是所有子空间的高度之和
            width = Math.max(childWidth, width);
            height += childHeight;
        }


        return new Point(width, height);
    }


//    支持Margin的固定写法，下面照抄就行了，至于为什么，可以去看源码，但是我觉得直接记住就ok了

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new MarginLayoutParams(p);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }
}
