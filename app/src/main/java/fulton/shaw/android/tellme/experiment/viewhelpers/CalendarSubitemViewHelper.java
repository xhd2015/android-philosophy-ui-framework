package fulton.shaw.android.tellme.experiment.viewhelpers;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import fulton.shaw.android.tellme.R;

/**
 * Created by 13774 on 7/17/2017.
 */

public final class CalendarSubitemViewHelper extends ViewHelper{
    private TextView mDateNumber;
    private TextView mIssueNumber;
    private ImageView mWeatherIndicater;
    private TextView mDateFestival;

    private int mCurWeather;

    public static final int TYPE_ROOTVIEW_CLICKED =0;
    public static final int TYPE_DATENOTE_CLICKED =1;
    public static final int EVENT_TYPE_COUNT=3;

    public static final int WEATHER_SUNNY=0;
    public static final int WEATHER_RAINY=1;
    public static final int WEATHER_THUNDER=2;
    public static final int WEATHER_SNOW=3;



//    public static final

    public CalendarSubitemViewHelper(Activity activity, View rootView) {
        super(activity, rootView, EVENT_TYPE_COUNT);

        mDateNumber= (TextView) rootView.findViewById(R.id.date_number);
        mIssueNumber= (TextView) rootView.findViewById(R.id.importantIssuesNumber);
        mWeatherIndicater= (ImageView) rootView.findViewById(R.id.weatherIndicator);
        mDateFestival= (TextView) rootView.findViewById(R.id.mainFestivalName);

        mTargetRootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalendarSubitemViewHelper.this.notifyListeners(TYPE_ROOTVIEW_CLICKED,v);
            }
        });
        mDateFestival.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalendarSubitemViewHelper.this.notifyListeners(TYPE_DATENOTE_CLICKED,v);
            }
        });
    }

    public void setDateNumber(int newNumber)
    {
        mDateNumber.setText(""+newNumber);
    }

    public void setWeatherIndicater(int whichWeather)
    {
        mCurWeather=whichWeather;
    }

    public void setDateNote(final String s)
    {

    }





}
