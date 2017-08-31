package fulton.shaw.android.x.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import fulton.shaw.android.x.R;

/**
 * TODO: document your custom view class.
 */
public class CircleView extends View {
    protected Paint mCurvePaint;
    protected Paint mInnerPaint;
    protected Paint mOutsidePaint;
    protected float mRadius;
    protected int mCurveColor;

    public CircleView(Context context) {
        super(context);
        init(null, 0);
    }

    public CircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public CircleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.CircleView, defStyle, 0);

        mRadius=a.getDimensionPixelSize(R.styleable.CircleView_radius,-1);
        if(mRadius==-1)
            throw new UnsupportedOperationException("radius must be set.");
        mCurveColor=a.getColor(R.styleable.CircleView_curveColor,getResources().getColor(R.color.black));

        a.recycle();

        // Set up a default TextPaint object
        mCurvePaint=new Paint();
        mInnerPaint=new Paint();
        mOutsidePaint=new Paint();


        // Update TextPaint and text measurements from attributes
        invalidateCurvePaintAndMeasurements();
    }

    protected void invalidateCurvePaintAndMeasurements()
    {
        mCurvePaint.setColor(mCurveColor);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // TODO: consider storing these as member variables to reduce
        // allocations per draw cycle.
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        getLayoutParams().height= (int) mRadius+paddingLeft+paddingRight;
        getLayoutParams().width= (int) mRadius+paddingBottom+paddingTop;

        int contentWidth = (int) (mRadius - paddingLeft - paddingRight);
        int contentHeight = (int) (mRadius - paddingTop - paddingBottom);

        float x=getX();
        float y=getY();

        float centerx=x+mRadius/2;
        float centery=y+mRadius/2;


        canvas.drawCircle(centerx,centery,mRadius,mCurvePaint);

    }


}
