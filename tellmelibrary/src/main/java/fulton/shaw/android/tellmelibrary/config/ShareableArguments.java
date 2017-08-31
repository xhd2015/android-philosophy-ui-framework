package fulton.shaw.android.tellmelibrary.config;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import fulton.shaw.android.tellmelibrary.sql.SqliteHelper;

import fulton.shaw.android.tellmelibrary.sql.SqliteHelper;

/**
 * Created by 13774 on 7/25/2017.
 */

public class ShareableArguments {
        //Used in Bundle,Intent, fragment.Arguments, ContentValues
        public static final String ARG_ACTIVITY="activity";
        public static final String ARG_YEAR="year";
        public static final String ARG_MONTH="month";
        public static final String ARG_DATE="date";
        public static final String ARG_HOUR="hour";
        public static final String ARG_MINUTE="minute";
        public static final String ARG_LASTEST_UPDATE_TIME="lastest_update_time";
        public static final String ARG_MAX_TEMPERATURE="maxTmperature";
        public static final String ARG_MIN_TEMPERATURE="minTemperature";
        public static final String ARG_TEMPERATURE="temperature";
        public static final String ARG_WEATHER_TEXT="weatherText";
        public static final String ARG_WEATHER_CODE="weather_code";
        public static final String ARG_WIND_SCALE="windScale";
        public static final String ARG_WIND_DIRECTION="windDirection";
        public static final String ARG_INDEX="index";
        public static final String ARG_CITY="city";
        public static final String ARG_SRC_CITY_NAME="srcCityName";
        public static final String ARG_SRC_PROV_NAME="srcProvName";
        public static final String ARG_DST_PROV_NAME="dstProvName";
        public static final String ARG_DST_CITY_NAME="dstCityName";
        public static final String ARG_PROV_INDEX="provIndex";
        public static final String ARG_CITY_INDEX="cityIndex";
        public static final String ARG_CITY_ID="cityID";
        public static final String ARG_PROV_ID="provID";
        public static final String ARG_LISTENER="listener";
        public static final String ARG_EXCEPTION="exception";
        public static final String ARG_SUGGESTION_CAR_WASHING ="car_washing";
        public static final String ARG_SUGGESTION_DRESSING ="dressing";
        public static final String ARG_SUGGESTION_FLU ="flu";
        public static final String ARG_SUGGESTION_SPORT ="sport";
        public static final String ARG_SUGGESTION_TRAVEL ="travel";
        public static final String ARG_SUGGESTION_UV ="uv";
        public static final String ARG_PAGE_NUM="page_num";
        public static final String ARG_DATE_NUM="date_num";
        public static final String ARG_TITLE="title";
        public static final String ARG_NUM ="num";
        public static final String ARG_INIT_DATABASES="initDatabases";
        public static final String ARG_DEFAULT_DIALOG_CHECKED="defaultDialogChecked";

        public static final String ARG_TYPE="type";

        public static final String ARG_BUNDLE_RES_TYPE="bundle_res_type";
        public static final int RES_TYPE_NOW=0;
        public static final int RES_TYPE_GIVEN_DATE=1;
        public static final int RES_TYPE_FAILED_WITH_EXCEPTION=-1;


        public static final String ARG_LOCATION="location";

        //used in url connection,json
        public static final String DATEFORMAT_LASTUPDATE="yyyy-MM-dd'T'HH:mm:ssXXX";//X is bad for xiaomi,but good for simulator
        public static final String DATEFORMAT_LASTUPDATE_2="yyyy-MM-dd'T'HH:mm:ssZ";//works fine for xiaomi
        public static final String JSON_KEY_RESULTS="results";
        public static final String JSON_KEY_LASTUPDATE="last_update";
        public static final String JSON_KEY_NOW="now";
        public static final String JSON_KEY_TEXT="text";//多云
        public static final String JSON_KEY_CODE="code";//4
        public static final String JSON_KEY_TEMPERATURE="temperature";//23
        public static final String JSON_KEY_SUGGESTION="suggestion";
        public static final String JSON_KEY_BRIEF="brief";
        public static final String JSON_KEY_DAILY="daily";
        public static final String JSON_KEY_HIGH="high";
        public static final String JSON_KEY_LOW="low";
        public static final String JSON_KEY_CODE_DAY="code_day";
        public static final String JSON_KEY_TEXT_DAY="text_day";
        //wind_direction wind_scale
        public static final String JSON_KEY_WIND_SCALE="wind_scale";
        public static final String JSON_KEY_WIND_DIRECTION="wind_direction";


        public static final String CONFIG_SHARED_PREF_FILE="conifgSharedPrefFile";


        public static boolean isNetworkConnected(Context context)
        {
            ConnectivityManager cm =
                    (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();

            return  isConnected;
        }

        public static boolean isNetworkWifi(Context context)
        {
            ConnectivityManager cm =
                    (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();
            boolean isWiFi = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
            return isConnected && isWiFi;
        }

        public static <E> E nonNullOrDefault(E e,E defaultValue)
        {
            return e!=null?e:defaultValue;
        }

}
