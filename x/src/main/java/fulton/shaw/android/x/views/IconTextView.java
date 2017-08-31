package fulton.shaw.android.x.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import fulton.shaw.android.x.R;
import fulton.util.android.utils.Util;
import fulton.util.android.utils.ViewUtil;

/**
 * Created by 13774 on 8/14/2017.
 */

public class IconTextView extends LinearLayout {
    protected TextView mText;
    protected ImageView mIcon;

    public IconTextView(Context context) {
        super(context);
        initViews(context,null);
    }

    public IconTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initViews(context,attrs);
    }

    /**
     *  image res
     *  text
     * @param context
     * @param attrs
     */
    protected void initViews(Context context,AttributeSet attrs)
    {
        mText=new TextView(context);
        mIcon=new ImageView(context);//Image
        mText.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mIcon.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        if(getOrientation()==HORIZONTAL )
        {
            mText.getLayoutParams().height= ViewGroup.LayoutParams.MATCH_PARENT;
            mText.setGravity(Gravity.CENTER);
        }


        if(attrs!=null)
        {
            TypedArray a=context.getTheme().obtainStyledAttributes(attrs, R.styleable.IconTextView,0,0);
            try{
                if(a.getType(R.styleable.IconTextView_iconHeight)==TypedValue.TYPE_DIMENSION){//5
                    mIcon.getLayoutParams().height=a.getDimensionPixelSize(R.styleable.IconTextView_iconHeight,0);
                }else{//0x10
                    mIcon.getLayoutParams().height=a.getInt(R.styleable.IconTextView_iconHeight,ViewGroup.LayoutParams.WRAP_CONTENT);
                }
                if(a.getType(R.styleable.IconTextView_iconWidth)==TypedValue.TYPE_DIMENSION){
                    mIcon.getLayoutParams().width=a.getDimensionPixelSize(R.styleable.IconTextView_iconWidth,0);
                }else{
                    mIcon.getLayoutParams().width=a.getInt(R.styleable.IconTextView_iconWidth,ViewGroup.LayoutParams.WRAP_CONTENT);
                }

                float margin=a.getDimensionPixelSize(R.styleable.IconTextView_innerMargin,getResources().getDimensionPixelSize(R.dimen.default_gap));
                boolean textFirst=a.getInt(R.styleable.IconTextView_layoutMode,1)==0?true:false;

                String text=a.getString(R.styleable.IconTextView_title);
                mText.setText(text);

                int textStyle=a.getInt(R.styleable.IconTextView_titleStyle,0);
                mText.setTypeface(mText.getTypeface(),textStyle);

                float textSize=a.getDimensionPixelSize(R.styleable.IconTextView_titleSize,getResources().getDimensionPixelSize(R.dimen.default_text_size));
                mText.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize);

                int srcImg=a.getResourceId(R.styleable.IconTextView_src,0);
                mIcon.setImageResource(srcImg);
                int iconWeight=a.getInt(R.styleable.IconTextView_iconWeight,-1);
                if(iconWeight!=-1)
                    Util.cast(mIcon.getLayoutParams(),LayoutParams.class).weight=iconWeight;

                int titleWeight=a.getInt(R.styleable.IconTextView_titleWeight,-1);
                if(titleWeight!=-1)
                    Util.cast(mText.getLayoutParams(), LinearLayoutCompat.LayoutParams.class).weight=titleWeight;

                View firstView=null;
                if(textFirst)
                {
                    super.addView(firstView=mText);
                    super.addView(mIcon);
                }else{
                    super.addView(firstView=mIcon);
                    super.addView(mText);
                }
                LinearLayout.LayoutParams lp= (LayoutParams) firstView.getLayoutParams();
                lp.setMarginEnd((int)margin);
//                Util.cast(mIcon.getLayoutParams(),LayoutParams.class).rightMargin=margin;
            }finally {
                a.recycle();
            }
        }
    }

    public void setIconResource(@DrawableRes int resId) {
        mIcon.setImageResource(resId);
    }

    public void setIconDrawable(@Nullable Drawable drawable) {
        mIcon.setImageDrawable(drawable);
    }
    public void setTitle(CharSequence text)
    {
        this.mText.setText(text);
    }
    public String getTitle()
    {
        return mText.getText().toString();
    }

    public void throwError()
    {
        throw new UnsupportedOperationException("Add view for IconTextView not supported");
    }
}
