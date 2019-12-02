package com.view.custom.dosometest.view;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;

/**
 * 描述当前版本功能
 *
 * @Project: DoSomeTest
 * @author: cjx
 * @date: 2019-12-01 10:06  星期日
 */
public class CustomView extends View {


    public CustomView(Context context) {
        super(context);
    }

    public CustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 首先计算一下在AtMost模式下，这个自定义view的宽高，
        // 这里把计算出来的宽高封装在了一个Point里，x为宽，y为高
        // 这是模板代码，你只需要按照自己的实现caculateAtMostSize()的具体逻辑,其余的不用变
        Point point = caculateAtMostSize();

        // 根据 默认宽高、AtMost下的宽高、MeasureSpec测量规格，计算出最终这个view的宽高
        int width = measureSize(0, point.x, widthMeasureSpec);
        int height = measureSize(0, point.y, heightMeasureSpec);

        // 把上面计算出来的宽高作为参数设置给setMeasuredDimension就ok了
        setMeasuredDimension(width, height);
    }


    /**
     * 通过widthMeasureSpec计算出这个View最终的宽高
     *
     * @param defalut     这个view的默认值，仅仅是为了支持下UNSPECIFIED模式，但是这个模式其实用不到，所以传递个0就行了
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
     * 我这里只是简单的返回宽高都是 200
     *
     * @return
     */
    private Point caculateAtMostSize() {
        //一般情况，写的自定义view是不需要特别计算这个值的，我会直接给一个默认值
        //但是你是自定义ViewGroup的话，这里你必须好好写了
        int width = 200;
        int height = 200;
        return new Point(width, height);
    }

}
