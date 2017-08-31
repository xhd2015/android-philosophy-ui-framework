package fulton.shaw.android.tellme.experiment.viewhelpers;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by 13774 on 7/17/2017.
 */

public class WhetherView extends View {
    private int mType;
    public static final int TYPE_RAINY=0;
    public static final int TYPE_CLOUDY=1;
    public static final int TYPE_SUNNY=2;
    public static final int TYPE_VARY=3;


    public WhetherView(Context context) {
        super(context);
    }

    public WhetherView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public static Path getSunnyPath()
    {
        Path path=new Path();

//        path.addCircle();



        return path;
    }
}
