package fulton.shaw.android.tellme.experiment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * TODO: document your custom view class.
 */
public class MyView extends View {
    private Paint mPaint;
    private Path mPath;
    public static final int CONDITION_RESET=-1,CONDITION_DOWN=0,CONDITION_MOVE=1,CONDITION_UP=2;

    private ArrayList<DrawnRatioChangedListener> listeners=new ArrayList<>();

    public static interface DrawnRatioChangedListener
    {
        public void onDrawnRatioChanged(View v,int condition);
    }
    public MyView(Context context)
    {
        super(context);
        init();
    }

    public MyView(Context context,AttributeSet attrSet)
    {
        super(context,attrSet);
        init();
    }

    protected void init()
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

        this.setDrawingCacheEnabled(true);
        this.setDrawingCacheQuality(DRAWING_CACHE_QUALITY_HIGH);
    }

    /**
     * Exposing for external call
     */
    public void clearDrawings()
    {
        mPath.reset();
        postInvalidate();
        updateDrawnPercentage(CONDITION_RESET);
    }

    protected float calculateDrawnPercentage()
    {

        this.destroyDrawingCache();//destroy + getByGetter = updatee
        Bitmap bm=this.getDrawingCache();
        //Bitmap bm= oldbm.copy(oldbm.getConfig(),false);

        int w=bm.getWidth(),h=bm.getHeight();
        int totalTransparent=0;
        for(int i=0;i<w;i++)
        {
            for(int j=0;j<h;j++)
            {
                try {
                    if(bm.getPixel(i,j)==Color.TRANSPARENT)
                    {
                        totalTransparent++;
                    }
                }catch (Exception e)
                {
                    break;
                }


            }
        }
        return 1.0f - (float)totalTransparent/w/h;
    }
    public void setDrawnRatioChangedListener(DrawnRatioChangedListener listener)
    {
        listeners.add(listener);
    }

    /**
     *
     * @param condition
     */
    protected void updateDrawnPercentage(int condition)
    {
        for(DrawnRatioChangedListener listener:listeners)
            listener.onDrawnRatioChanged(this,condition);
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
                break;
            case MotionEvent.ACTION_MOVE:
//                touch_move(x, y);
                mPath.lineTo(x,y);
                invalidate();
                updateDrawnPercentage(CONDITION_MOVE);
                break;
            case MotionEvent.ACTION_UP:
//                touch_up();
                mPath.lineTo(x,y);
                invalidate();
                updateDrawnPercentage(CONDITION_UP);
                break;
        }
        return true;
    }

}
