package com.view.custom.dosometest.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import com.view.custom.dosometest.R;

/**
 * 描述当前版本功能
 *
 * @Project: DoSomeTest
 * @author: cjx
 * @date: 2019-12-08 19:57  星期日
 */
public class MatrixView extends View {

    private Paint mPaint;
    private Bitmap mBitmap;
    private Path mPath;
    private Path mPath1;
    private int mWidth;
    private int mHeight;
    private Matrix mMatrix;
    private float mScX;
    private float mScY;

    public MatrixView(Context context) {
        super(context);
        init(context);
    }


    public MatrixView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);

    }

    private void init(Context context) {

        setLayerType(LAYER_TYPE_SOFTWARE, null);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img1);

        mPath = new Path();
        mPath1 = new Path();

        mWidth = mBitmap.getWidth();
        mHeight = mBitmap.getHeight();

        mScX = 500f / mWidth;
        mScY = 500f / mHeight;
        mWidth = 500;
        mHeight = 500;

        mMatrix = new Matrix();
        mPath.addCircle(mWidth / 2, mHeight / 2, mWidth / 2, Path.Direction.CCW);
        mPath1.addCircle(mWidth / 2, mHeight / 2, mWidth / 2 - 10, Path.Direction.CCW);


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        setMeasuredDimension(mWidth,mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.BLUE);

        canvas.save();
        canvas.clipPath(mPath);
        canvas.drawColor(Color.RED);
        canvas.clipPath(mPath1);
//        canvas.drawBitmap(mBitmap, 0, 0, mPaint);
        mMatrix.postScale(mScX, mScY, 0, 0);

        canvas.drawBitmap(mBitmap, mMatrix, mPaint);
        mPaint.setColor(Color.YELLOW);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(30);



    }
}
