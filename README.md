package fulton.shaw.android.tellme.experiment;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import fulton.shaw.android.tellme.R;

/**
 * TODO: document your custom view class.
 */
public class MyView extends View {
    Paint mPaint;
    Path mPath;
    public MyView(Context context)
    {
        super(context);
        initPaint();
    }

    public MyView(Context context,AttributeSet attrSet)
    {
        super(context,attrSet);
        initPaint();
    }

    protected void initPaint()
    {
        mPath = new Path();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(0x7F00FFFF);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(12);
    }

    /**
     * Exposing for external call
     */
    public void clearDrawings()
    {
        mPath.reset();
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(mPath,mPaint);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
//                touch_start(x, y);
                mPath.moveTo(x,y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
//                touch_move(x, y);
                mPath.lineTo(x,y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
//                touch_up();
                mPath.lineTo(x,y);
                invalidate();
                break;
        }
        return true;
    }

}
