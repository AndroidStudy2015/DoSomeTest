package com.view.custom.dosometest.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
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
public class CanvasView extends View {


    private Paint mPaint;

    int width;
    int height;
    private Bitmap mSrcBitmap;
    private Bitmap mDstBitmap;

    public CanvasView(Context context) {
        super(context);
        init(context);
    }


    public CanvasView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);

    }

    private void init(Context context) {

        setLayerType(LAYER_TYPE_SOFTWARE, null);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);


        post(new Runnable() {
            @Override
            public void run() {
                width = 400;
                height = 400;
                mDstBitmap = makeSrc(width, height);
                mSrcBitmap = makeImg(width, height);

            }
        });


    }

    private Bitmap makeDst(int w, int h) {

        Bitmap bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bm);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.RED);
        c.drawRect(0, 0, w, h, paint);
        return bm;
    }

    private Bitmap makeSrc(int w, int h) {
        Bitmap bm = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bm);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLUE);
        c.drawOval(new RectF(0, 0, w, h), paint);
        return bm;
    }

    private Bitmap makeImg(int w, int h) {

        Bitmap bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bm);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.RED);
        Bitmap img = BitmapFactory.decodeResource(getResources(), R.drawable.img1);
        c.drawBitmap(img,0,0,paint);
        return bm;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.YELLOW);
canvas.save();

//        int layId = canvas.saveLayer(0, 0, width , height , mPaint, Canvas.ALL_SAVE_FLAG);
canvas.clipRect(0, 0, width , height );
        canvas.drawColor(Color.RED);

        canvas.drawBitmap(mDstBitmap, 0, 0, mPaint);

        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
//        canvas.drawBitmap(mSrcBitmap, width / 2, height / 2, mPaint);
//        canvas.drawBitmap(mSrcBitmap, 0,0, mPaint);
        mPaint.setXfermode(null);
//        canvas.restoreToCount(layId);

    }
}
