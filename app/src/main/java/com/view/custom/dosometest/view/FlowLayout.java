package com.view.custom.dosometest.view;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

/**
 * 描述当前版本功能
 *
 * @Project: DoSomeTest
 * @author: cjx
 * @date: 2019-12-01 10:06  星期日
 */
public class FlowLayout extends ViewGroup {


    public FlowLayout(Context context) {
        super(context);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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


//    ↓↓↓↓↓↓↓↓支持Margin的固定写法，下面照抄就行了，至于为什么，可以去看源码，但是我觉得直接记住就ok了↓↓↓↓↓↓↓↓

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
//    ↑↑↑↑↑↑↑↑支持Margin的固定写法，下面照抄就行了，至于为什么，可以去看源码，但是我觉得直接记住就ok了↑↑↑↑↑↑↑↑

    /**
     * 计算本View在AtMost模式下的宽高
     * 其他代码都是不用动的，在这里写下你特有的逻辑就可以
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     * @return
     */
    private Point caculateAtMostSize(int widthMeasureSpec, int heightMeasureSpec) {

        int lineWidth = 0;//当前行的宽度
        int lineHeight = 0;//当前行的高度
        int totalWidth = 0;//自定义ViewGroup的总宽度
        int totalHeight = 0;//自定义ViewGroup的总高度


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

            // 前面说过，如果是wrap_content或match_parent，getMeasuredWidth()得到的是父容器的最大值
            if (lineWidth + childWidth > getMeasuredWidth() - getPaddingLeft() - getPaddingRight()) {
                // 换行,总宽度更新，总高度更新
                totalWidth = Math.max(lineWidth, totalWidth);
                totalHeight += lineHeight;
                //新的一行，初始化lineWidth，lineHeight
                lineWidth = childWidth - lp.leftMargin;

                lineHeight = childHeight;
            } else {
                // 在这行接着放子控件
                lineWidth += childWidth;//行宽度增加
                lineHeight = Math.max(lineHeight, childHeight);//本行高度为本行最高的那个
            }

            // 上面的代码里只有在换行时候，才计算总宽高，
            // 如果没有换行的话，我们只是单纯地累加计算了本行的高度和宽度
            // 这样会导致，没有换行的的哪一行的宽高是没计算到总宽高里的，
            // 那么这样的行其实只有一个，就是最后一行（如果只有一行的话，没经过换行，这唯一的一行也相当于最后一行）
            if (i == count - 1) {
                totalWidth = Math.max(lineWidth, totalWidth);
                totalHeight += lineHeight;
            }


        }

        totalWidth += (getPaddingLeft() + getPaddingRight());
        totalHeight += (getPaddingTop() + getPaddingBottom());
        Log.e("ccc", totalWidth + "totalWidth");
        Log.e("ccc", totalHeight + "totalHeight");
        return new Point(totalWidth, totalHeight);
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int lineWidth = 0;//当前行的宽度
        int lineHeight = 0;//当前行的高度
        int top = getPaddingTop();
        int left = getPaddingLeft();

        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            boolean isChangeLine = false;
            View child = getChildAt(i);

            // 得到MarginLayoutParams，margin就在这里保存着
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();


            // 获得子控件的宽高（需要加上对应的margin,让控件的宽高包含margin）
            int childWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            int childHeight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;

            // 前面说过，如果是wrap_content或match_parent，getMeasuredWidth()得到的是父容器的最大值
            if (lineWidth + childWidth > getMeasuredWidth() - getPaddingLeft() - getPaddingRight()) {
                isChangeLine = true;
                top += lineHeight;
                left = getPaddingLeft();

                //新的一行，初始化lineWidth，lineHeight
                lineWidth = childWidth - lp.leftMargin;
                lineHeight = childHeight;
            } else {
                isChangeLine = false;
                lineHeight = Math.max(lineHeight, childHeight);
                lineWidth += childWidth;

            }


            //如果换行了，那么这个子控件的左边距不生效（为了保证左对齐，因为使用的时候把所有的子控件都设置了左margin，第一个view别设置左margin）
            int childLeft = isChangeLine ? left : left + lp.leftMargin;
            int childTop = top + lp.topMargin;
            int childRight = childLeft + child.getMeasuredWidth();
            int childBottom = childTop + child.getMeasuredHeight();

            child.layout(childLeft, childTop, childRight, childBottom);
            // 布局了一个子控件后，left往后移动一个子控件的宽度
            left += childWidth;

        }

    }
}
