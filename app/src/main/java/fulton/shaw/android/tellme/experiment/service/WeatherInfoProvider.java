package fulton.shaw.android.tellme.experiment.service;

import android.app.Activity;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MergeCursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;

import java.util.Calendar;

import fulton.shaw.android.tellme.experiment.fragments.SharedArgument;
import fulton.shaw.android.tellmelibrary.sql.SqliteHelper;
import fulton.util.android.utils.SqlUtil;
import fulton.util.android.utils.Util;
import fulton.util.android.net.NetUtil;

public class WeatherInfoProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    protected static final String BASE_URI="fulton.shaw.android.tellme.weather";
    public static final String BASE_CONTENT_URI ="content://"+BASE_URI;

    public static final int ACTION_WEATHER_GENERAL =0;
    public static final int ACTION_WEATHER_TODAY =1;
    public static final int ACTION_WEATHER_NEXT_FEW_DAYS =2;
    public static final int ACTION_PREPARE_ALL=3;

    public static final String ROW_TODAY="today";
    public static final String ROW_NEXT_FEW_DAYS ="nextFewDays";
    public static final String ROW_PREPARE_FOR_ALL="prepareForAll";
    public static final String CONTENT_URI_TODAY_FORMATTER=BASE_CONTENT_URI+"/"+SqliteHelper.TABLE_WEATHER+"/"+ROW_TODAY+"?"+SharedArgument.ARG_PROV_ID+
            "=%d&"+SharedArgument.ARG_CITY_ID+"=%d";
    public static final String CONTENT_URI_NEXT_FEW_DAYS_FORMATTER=BASE_CONTENT_URI+"/"+SqliteHelper.TABLE_WEATHER+"/"+ROW_NEXT_FEW_DAYS+"?"+SharedArgument.ARG_PROV_ID+
            "=%d&"+SharedArgument.ARG_CITY_ID+"=%d&"+SharedArgument.ARG_DATE_NUM+"=%d";
    public static final String CONTENT_URI_PREPARE_ALL_FORMATTER=BASE_CONTENT_URI+"/"+SqliteHelper.TABLE_WEATHER+"/"+ROW_PREPARE_FOR_ALL+"?"+SharedArgument.ARG_PROV_ID+
            "=%d&"+SharedArgument.ARG_CITY_ID+"=%d&"+SharedArgument.ARG_DATE_NUM+"=%d";

    private SqliteHelper mSqliteHelper;
    private SQLiteDatabase mdb;

    static {

        sUriMatcher.addURI(BASE_URI, SqliteHelper.TABLE_WEATHER, ACTION_WEATHER_GENERAL);
        sUriMatcher.addURI(BASE_URI,SqliteHelper.TABLE_WEATHER+"/"+ROW_TODAY, ACTION_WEATHER_TODAY);
        sUriMatcher.addURI(BASE_URI,SqliteHelper.TABLE_WEATHER+"/"+ ROW_NEXT_FEW_DAYS, ACTION_WEATHER_NEXT_FEW_DAYS);
        sUriMatcher.addURI(BASE_URI,SqliteHelper.TABLE_WEATHER+"/"+ROW_PREPARE_FOR_ALL,ACTION_PREPARE_ALL);

    }

    public static void HowToUse()
    {
        Activity activity=null;
        activity.getContentResolver();
    }
    public WeatherInfoProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        return -1;
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        switch (sUriMatcher.match(uri))
        {
            case ACTION_WEATHER_GENERAL:
                long id=mdb.insert(SqliteHelper.TABLE_WEATHER,null,values);
                if(id==-1)return null;
                else return uri;
        }
        return uri;
    }

    @Override
    public boolean onCreate() {
        // TODO: Implement this to initialize your content provider on startup.
        mSqliteHelper = new SqliteHelper(getContext());
        mdb = mSqliteHelper.getWritableDatabase();

        return mdb!=null;
    }

    public static void requestPrepare(ContentResolver resolver,int provId,int cityId,int numDays)
    {
        String uri=String.format(WeatherInfoProvider.CONTENT_URI_PREPARE_ALL_FORMATTER,
                provId,
                cityId,
                numDays);
        final Cursor cursor=resolver.query(Uri.parse(uri),null,null,null,null);
    }

    protected void prepareOrUpdateWeatherTable(int provId, int cityId)
    {
        Util.logi("in prepare");
        if(!NetUtil.isNetworkConnected(getContext())){
            Util.logi("no network");

        }else {
            Util.logi("provId,cityId:" + provId + "," + cityId);
            GetWeatherInfoHandler handler = new GetWeatherInfoHandler();
            String queryId = SqliteHelper.getCityQueryId(mdb, cityId);
            if (queryId == null) {
                Util.logi("queryId is null");
                return;
            }

            Calendar calendarToday = Calendar.getInstance();
            String[] primaryKeyProjection = new String[]{SqlUtil.COL_CITY_ID};
            String primaryKeyWhereClause = SqlUtil.COL_PROVINCE_ID + "=? and " + SqlUtil.COL_CITY_ID + "=? and " + SqlUtil.COL_YEAR + "=? and " +
                    SqlUtil.COL_MONTH + "=? and " + SqlUtil.COL_DATE + "=?";
            String[] primaryKeyValues = new String[]{String.valueOf(provId), String.valueOf(cityId),
                    String.valueOf(calendarToday.get(Calendar.YEAR)),
                    String.valueOf(calendarToday.get(Calendar.MONTH)),
                    String.valueOf(calendarToday.get(Calendar.DAY_OF_MONTH))};

            Util.logi("today date=" + calendarToday.get(Calendar.DAY_OF_MONTH));

            //getByGetter nearst days
            Util.logi("queryId=" + queryId);
            Bundle resultNearestDays = handler.handleActionGetNearestDaysWeatherInfo(queryId);
            Util.logi("after handle");
            if (resultNearestDays.getInt(SharedArgument.ARG_BUNDLE_RES_TYPE) != SharedArgument.RES_TYPE_FAILED_WITH_EXCEPTION) {
//                    Util.logi("has key:"+result.containsKey(SharedArgument.ARG_LASTEST_UPDATE_TIME));
                ContentValues content = new ContentValues();
                content.put(SqlUtil.COL_CITY_ID, cityId);
                content.put(SqlUtil.COL_PROVINCE_ID, provId);
                Calendar lastUpdate = (Calendar) resultNearestDays.getSerializable(SharedArgument.ARG_LASTEST_UPDATE_TIME);
                content.put(SqlUtil.COL_LAST_UPDATE_HOUR, lastUpdate.get(Calendar.HOUR_OF_DAY));
                content.put(SqlUtil.COL_LAST_UPDATE_MINUTE, lastUpdate.get(Calendar.MINUTE));

                int i = 0;
                while (resultNearestDays.containsKey("" + i)) {
                    Bundle res = resultNearestDays.getBundle("" + i);
                    content.put(SqlUtil.COL_YEAR, calendarToday.get(calendarToday.YEAR));
                    content.put(SqlUtil.COL_MONTH, calendarToday.get(calendarToday.MONTH));
                    content.put(SqlUtil.COL_DATE, calendarToday.get(calendarToday.DAY_OF_MONTH));
                    content.put(SqlUtil.COL_WEATHER_STATE, SharedArgument.netWeatherTypeToLocalType(res.getInt(SharedArgument.ARG_WEATHER_CODE)));
                    content.put(SqlUtil.COL_WEATHER_TEXT, res.getString(SharedArgument.ARG_WEATHER_TEXT));
                    content.put(SqlUtil.COL_MAX_TEMPERATURE, res.getInt(SharedArgument.ARG_MAX_TEMPERATURE));
                    content.put(SqlUtil.COL_MIN_TEMPERATURE, res.getInt(SharedArgument.ARG_MIN_TEMPERATURE));
                    content.put(SqlUtil.COL_WIND_SCALE, res.getInt(SharedArgument.ARG_WIND_SCALE));
                    content.put(SqlUtil.COL_WIND_DIRECTION, res.getString(SharedArgument.ARG_WIND_DIRECTION));
                    Util.logi("insert [" + i + "], wind scale,wind direction=" + res.getInt(SharedArgument.ARG_WIND_SCALE) + "," + res.getString(SharedArgument.ARG_WIND_DIRECTION));


                    primaryKeyValues[2] = String.valueOf(calendarToday.get(Calendar.YEAR));
                    primaryKeyValues[3] = String.valueOf(calendarToday.get(Calendar.MONTH));
                    primaryKeyValues[4] = String.valueOf(calendarToday.get(Calendar.DAY_OF_MONTH));
                    Cursor c = mdb.query(SqliteHelper.TABLE_WEATHER, primaryKeyProjection, primaryKeyWhereClause, primaryKeyValues, null, null, null);
                    if (c.getCount() != 0) {
                        mdb.update(SqliteHelper.TABLE_WEATHER, content, primaryKeyWhereClause, primaryKeyValues);
                    } else {
                        long id = mdb.insert(SqliteHelper.TABLE_WEATHER, null, content); //getByGetter it updated, never mind if it is inserted or not
                        Util.logi("insert id:" + id);
                    }
                    c.close();
                    i++;
                    calendarToday.add(Calendar.DAY_OF_MONTH, 1);
                }
                calendarToday.add(Calendar.DAY_OF_MONTH, -i);
            } else {
                Util.logi("exception:nearst three days");
                ((Exception) resultNearestDays.getSerializable(SharedArgument.ARG_EXCEPTION)).printStackTrace();
            }

            Util.logi("after update,today date=" + calendarToday.get(Calendar.DAY_OF_MONTH));

            //=======getByGetter today's
            primaryKeyValues[2] = String.valueOf(calendarToday.get(Calendar.YEAR));
            primaryKeyValues[3] = String.valueOf(calendarToday.get(Calendar.MONTH));
            primaryKeyValues[4] = String.valueOf(calendarToday.get(Calendar.DAY_OF_MONTH));
            Bundle resultToday = handler.handleActionGetWeatherInfo(queryId);

            ContentValues content = new ContentValues();
            content.put(SqlUtil.COL_CITY_ID, cityId);
            content.put(SqlUtil.COL_PROVINCE_ID, provId);
            content.put(SqlUtil.COL_YEAR, calendarToday.get(calendarToday.YEAR));
            content.put(SqlUtil.COL_MONTH, calendarToday.get(calendarToday.MONTH));
            content.put(SqlUtil.COL_DATE, calendarToday.get(calendarToday.DAY_OF_MONTH));
            if (resultToday.getInt(SharedArgument.ARG_BUNDLE_RES_TYPE) != SharedArgument.RES_TYPE_FAILED_WITH_EXCEPTION) {
                content.put(SqlUtil.COL_WEATHER_STATE, SharedArgument.netWeatherTypeToLocalType(resultToday.getInt(SharedArgument.ARG_WEATHER_CODE)));
                Calendar lastUpdate = (Calendar) resultToday.getSerializable(SharedArgument.ARG_LASTEST_UPDATE_TIME);
                content.put(SqlUtil.COL_LAST_UPDATE_HOUR, lastUpdate.get(Calendar.HOUR_OF_DAY));
                content.put(SqlUtil.COL_LAST_UPDATE_MINUTE, lastUpdate.get(Calendar.MINUTE));
                content.put(SqlUtil.COL_CUR_TEMPERATURE, resultToday.getInt(SharedArgument.ARG_TEMPERATURE));
                content.put(SqlUtil.COL_WEATHER_TEXT, resultToday.getString(SharedArgument.ARG_WEATHER_TEXT));

                Bundle suggestionResult = handler.handleActionGetSuggestion(queryId);
                if (suggestionResult.getInt(SharedArgument.ARG_BUNDLE_RES_TYPE) != SharedArgument.RES_TYPE_FAILED_WITH_EXCEPTION) {
                    content.put(SqlUtil.COL_SUGGESTION_CAR_WASHING, suggestionResult.getString(SharedArgument.ARG_SUGGESTION_CAR_WASHING));
                    content.put(SqlUtil.COL_SUGGESTION_DRESSING, suggestionResult.getString(SharedArgument.ARG_SUGGESTION_DRESSING));
                    content.put(SqlUtil.COL_SUGGESTION_FLU, suggestionResult.getString(SharedArgument.ARG_SUGGESTION_FLU));
                    content.put(SqlUtil.COL_SUGGESTION_SPORT, suggestionResult.getString(SharedArgument.ARG_SUGGESTION_SPORT));
                    content.put(SqlUtil.COL_SUGGESTION_TRAVEL, suggestionResult.getString(SharedArgument.ARG_SUGGESTION_TRAVEL));
                    content.put(SqlUtil.COL_SUGGESTION_UV, suggestionResult.getString(SharedArgument.ARG_SUGGESTION_UV));

                    //before replace, query
                    Util.logi("before replace");
                    Cursor c = mdb.query(SqliteHelper.TABLE_WEATHER, primaryKeyProjection,
                            primaryKeyWhereClause, primaryKeyValues, null, null, null);
                    if (c.getCount() != 0)//update
                    {
                        mdb.update(SqliteHelper.TABLE_WEATHER, content, primaryKeyWhereClause, primaryKeyValues);
                    } else {//insert
                        //replace:it's not really update
                        long id = mdb.insert(SqliteHelper.TABLE_WEATHER, null, content); //getByGetter it updated, never mind if it is inserted or not
                        //replace, not update
                        Util.logi("insert id:" + id);
                    }
                    c.close();

                } else {
                    Util.logi("weatherInfoProvider suggestion exception:" + suggestionResult.getSerializable(SharedArgument.ARG_EXCEPTION).toString());
                }
            } else {
                Util.logi("weatherInfoProvider exception:" + resultToday.getSerializable(SharedArgument.ARG_EXCEPTION).toString());
            }
        }

        //query all
        Cursor c=mdb.query(SqliteHelper.TABLE_WEATHER,new String[]{
                SqlUtil.COL_PROVINCE_ID, SqlUtil.COL_CITY_ID, SqlUtil.COL_YEAR, SqlUtil.COL_MONTH, SqlUtil.COL_DATE, SqlUtil.COL_WIND_SCALE},
//                SqliteHelper.COL_PROVINCE_ID + "=? and " + SqliteHelper.COL_CITY_ID + "=? and " + SqliteHelper.COL_YEAR + "=? and " +
//                        SqliteHelper.COL_MONTH + "=? and " + SqliteHelper.COL_DATE + "=?",
//                new String[]{String.valueOf(provId), String.valueOf(cityId),
//                        String.valueOf(calendarToday.getByGetter(Calendar.YEAR)),
//                        String.valueOf(calendarToday.getByGetter(Calendar.MONTH)),
//                        String.valueOf(calendarToday.getByGetter(Calendar.DAY_OF_MONTH))},
                null,
                null,
                null,null,null
                );
        Util.logi("count:"+c.getCount());
        while (c.moveToNext())
        {
            Util.logi("data:");
            for(int i=0;i<c.getColumnCount();i++)
            {
                Util.logi("\t\t"+(c.isNull(i)?"null":c.getInt(i)));
            }
        }
        c.close();

    }
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // TODO: Implement this to handle query requests from clients.
        int action=sUriMatcher.match(uri);
        String cityId=null, provId=null;
        int iCityId=-1,iProvId=-1,inumDays=-1;
        Calendar calendarToday=null;
        Util.logi("action="+action);
        if(action==ACTION_WEATHER_TODAY||action==ACTION_WEATHER_NEXT_FEW_DAYS||action==ACTION_PREPARE_ALL)
        {
            if(action==ACTION_WEATHER_TODAY||action==ACTION_WEATHER_NEXT_FEW_DAYS) {
                calendarToday = Calendar.getInstance();
            }
            cityId = uri.getQueryParameter(SharedArgument.ARG_CITY_ID);
            provId = uri.getQueryParameter(SharedArgument.ARG_PROV_ID);
            iCityId = Integer.valueOf(cityId);
            iProvId = Integer.valueOf(provId);
            if(action==ACTION_WEATHER_NEXT_FEW_DAYS)
            {
                String numDays=uri.getQueryParameter(SharedArgument.ARG_DATE_NUM);
                inumDays= Integer.valueOf(numDays);
            }
        }
        switch (action) {
            case ACTION_WEATHER_GENERAL://fetch,return no caring that the result is null or not.
                break;
            case ACTION_WEATHER_TODAY://check network,if connected,update, arguments: cityId,provId
            {
                Cursor cursor = mdb.query(SqliteHelper.TABLE_WEATHER,
                        new String[]{SqlUtil.COL_LAST_UPDATE_HOUR, SqlUtil.COL_LAST_UPDATE_MINUTE,
                                SqlUtil.COL_WEATHER_STATE, SqlUtil.COL_CUR_TEMPERATURE,
                                SqlUtil.COL_WEATHER_TEXT, SqlUtil.COL_WIND_SCALE, SqlUtil.COL_WIND_DIRECTION,
                                SqlUtil.COL_SUGGESTION_CAR_WASHING, SqlUtil.COL_SUGGESTION_DRESSING,
                                SqlUtil.COL_SUGGESTION_FLU, SqlUtil.COL_SUGGESTION_SPORT,
                                SqlUtil.COL_SUGGESTION_TRAVEL, SqlUtil.COL_SUGGESTION_UV
                        },
                        SqlUtil.COL_PROVINCE_ID + "=? and " + SqlUtil.COL_CITY_ID + "=? and " + SqlUtil.COL_YEAR + "=? and " +
                                SqlUtil.COL_MONTH + "=? and " + SqlUtil.COL_DATE + "=?",
                        new String[]{provId, cityId,
                                String.valueOf(calendarToday.get(Calendar.YEAR)),
                                String.valueOf(calendarToday.get(Calendar.MONTH)),
                                String.valueOf(calendarToday.get(Calendar.DAY_OF_MONTH))},
                        null,
                        null,
                        null
                );
                return cursor;
        }
            case ACTION_WEATHER_NEXT_FEW_DAYS://check network,if connected,update
            {
                String[] thisprojection= new String[]{SqlUtil.COL_YEAR, SqlUtil.COL_MONTH, SqlUtil.COL_DATE,
                        SqlUtil.COL_WEATHER_STATE, SqlUtil.COL_MAX_TEMPERATURE, SqlUtil.COL_MIN_TEMPERATURE};
                String thisselection= SqlUtil.COL_PROVINCE_ID + "=? and " + SqlUtil.COL_CITY_ID + "=? and " + SqlUtil.COL_YEAR + "=? and " +
                        SqlUtil.COL_MONTH + "=? and " + SqlUtil.COL_DATE + "=?";
                String[] thisselectionArgs=new String[]{provId, cityId,null,null,null};
                Cursor[] cursors=new Cursor[inumDays];
                for(int i=0;i<inumDays;i++)
                {
                    calendarToday.add(Calendar.DAY_OF_MONTH,1);
                    thisselectionArgs[2]=String.valueOf(calendarToday.get(Calendar.YEAR));
                    thisselectionArgs[3]= String.valueOf(calendarToday.get(Calendar.MONTH));
                    thisselectionArgs[4]= String.valueOf(calendarToday.get(Calendar.DAY_OF_MONTH));
                    cursors[i]=mdb.query(SqliteHelper.TABLE_WEATHER,thisprojection,thisselection,thisselectionArgs,
                            null,
                            null,
                            null
                    );
                    Util.logi(""+i+" day count="+cursors[i].getCount());
                }
                MergeCursor mergeCursor=new MergeCursor(cursors);
                return mergeCursor;
            }
            case ACTION_PREPARE_ALL:
                prepareOrUpdateWeatherTable(iProvId,iCityId);
                return null;//succeed or not

        }
        return null;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
