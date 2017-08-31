package fulton.shaw.android.tellme.experiment.service;

import android.os.Bundle;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import fulton.util.android.event.GenericNotifier;
import fulton.shaw.android.tellme.experiment.fragments.SharedArgument;
import fulton.util.android.net.NetUtil;
import fulton.util.android.utils.Util;

/**
 * Created by 13774 on 7/20/2017.
 */

public class GetWeatherInfoHandler extends GenericNotifier{
    protected static final String API_KEY="xprsciuascqfln7b";//key=
    protected static final String API_ARG_LOCATION="location";// location=ip 根据IP获取  location=城市拼音 城市ID
    protected static final String API_ARG_LANGUAGE="language";//zh-Hans
    protected static final String API_ARG_UNIT="unit";//c or f, c for standard and desired


    protected static final String URL_CUR_WEATHER="https://api.seniverse.com/v3/weather/now.json?key=xprsciuascqfln7b&language=zh-Hans&unit=c";
    //location的设置，参考https://www.seniverse.com/weather/city/CHTW000300
//    https://api.seniverse.com/v3/weather/now.json?key=xprsciuascqfln7b&location=beijing&language=zh-Hans&unit=c  天气实况，  天气现象文字、代码和气温3项数据
//JSON:
//    {"results":
//     [
//      {"location":
//          {"id":"YB1UX38K6DY1","name":"哈尔滨","country":"CN","path":"哈尔滨,哈尔滨,黑龙江,中国","timezone":"Asia/Shanghai","timezone_offset":"+08:00"},
//       "now":
//          {"text":"多云","code":"4","temperature":"23"},
//      "last_update":
//  "2017-07-20T09:05:00+08:00"}
//      ]
//  }

    public static final int EVENT_DONE_GET_WEATHER_INFO=0;
    public static final int EVENT_COUNT=3;


    protected static final String URL_NEARST_DAYS_WEATHER="https://api.seniverse.com/v3/weather/daily.json?key=xprsciuascqfln7b&language=zh-Hans&unit=c&start=0&days=3";
    //https://api.seniverse.com/v3/weather/daily.json?key=xprsciuascqfln7b&location=beijing&language=zh-Hans&unit=c&start=0&days=5  3天内的天气预报  天气，温度，风向，风速，风力

    protected static final String URL_CUR_SUGGESTION="https://api.seniverse.com/v3/life/suggestion.json?key=xprsciuascqfln7b&language=zh-Hans";
//    https://api.seniverse.com/v3/life/suggestion.json?key=xprsciuascqfln7b&location=shanghai&language=zh-Hans  当前日期的生活建议

//    https://api.seniverse.com/v3/location/search.json?key=xprsciuascqfln7b&q=san&limit=10&offset=10  城市查询

    public GetWeatherInfoHandler()
    {
        super(EVENT_COUNT);
    }



    public Bundle handleActionGetWeatherInfo(String location) {
        Bundle result = new Bundle();
        try {


//            Util.logi(sb.toString());

            String content=NetUtil.getUrlContent(URL_CUR_WEATHER+"&"+API_ARG_LOCATION+"="+location);
            Util.logi("content="+content);
            JSONObject job = new JSONObject(content);
            //    {"results":
//     [
//      {"location":
//          {"id":"YB1UX38K6DY1","name":"哈尔滨","country":"CN","path":"哈尔滨,哈尔滨,黑龙江,中国","timezone":"Asia/Shanghai","timezone_offset":"+08:00"},
//       "now":
//          {"text":"多云","code":"4","temperature":"23"},
//      "last_update":
//  "2017-07-20T09:05:00+08:00"}
//      ]
//  }

//            int size=job.getJSONArray(SharedArgument.JSON_KEY_RESULTS).length();
//            String lastUpdate=job.getString("last_update");  //no value for last_update
            JSONObject jres = job.getJSONArray(SharedArgument.JSON_KEY_RESULTS).getJSONObject(0);
            JSONObject jresnow = jres.getJSONObject(SharedArgument.JSON_KEY_NOW);
            String lastUpdate = jres.getString(SharedArgument.JSON_KEY_LASTUPDATE);//good
            DateFormat df = new SimpleDateFormat(SharedArgument.DATEFORMAT_LASTUPDATE_2);//may be wrong
            Calendar c=Calendar.getInstance();
            c.setTime(df.parse(lastUpdate));


            result.putString(SharedArgument.ARG_CITY, location);
            result.putSerializable(SharedArgument.ARG_LASTEST_UPDATE_TIME, c);
//            Util.logi("c="+c.getByGetter(Calendar.YEAR));
            result.putInt(SharedArgument.ARG_TEMPERATURE, jresnow.getInt(SharedArgument.JSON_KEY_TEMPERATURE));
            result.putString(SharedArgument.ARG_WEATHER_TEXT, jresnow.getString(SharedArgument.JSON_KEY_TEXT));
            result.putInt(SharedArgument.ARG_WEATHER_CODE, jresnow.getInt(SharedArgument.JSON_KEY_CODE));
            result.putInt(SharedArgument.ARG_BUNDLE_RES_TYPE, SharedArgument.RES_TYPE_NOW);
        } catch (Exception e) {
//            Util.logi("exception:"+e.toString());
            result.clear();
            result.putInt(SharedArgument.ARG_BUNDLE_RES_TYPE, SharedArgument.RES_TYPE_FAILED_WITH_EXCEPTION);
            result.putSerializable(SharedArgument.ARG_EXCEPTION,e);
        }

        return  result;
    }

    public Bundle handleActionGetSuggestion(String location) {
        Bundle result = new Bundle();
        try {
            String content=NetUtil.getUrlContent(URL_CUR_SUGGESTION+"&"+API_ARG_LOCATION+"="+location);

            JSONObject job = new JSONObject(content);
//            {"results":
//                  [
//                      {"location":{"id":"YB1UX38K6DY1","name":"哈尔滨","country":"CN","path":"哈尔滨,哈尔滨,黑龙江,中国","timezone":"Asia/Shanghai","timezone_offset":"+08:00"}
//                      ,"suggestion":
//                              {
//                              "car_washing":{"brief":"不宜","details":""},
//                              "dressing":{"brief":"热","details":""},
//                               "flu":{"brief":"少发","details":""},
//                                  "sport":{"brief":"较适宜","details":""},
//                                  "travel":{"brief":"适宜","details":""},
//                                     "uv":{"brief":"中等","details":""}},"
//                      last_update":"2017-07-20T22:40:09+08:00"
//                      }
//                  ]
//          }


//            int size=job.getJSONArray(SharedArgument.JSON_KEY_RESULTS).length();
//            String lastUpdate=job.getString("last_update");  //no value for last_update
            JSONObject jres = job.getJSONArray(SharedArgument.JSON_KEY_RESULTS).getJSONObject(0);
            JSONObject jsuggestoin = jres.getJSONObject(SharedArgument.JSON_KEY_SUGGESTION);
            String lastUpdate = jres.getString(SharedArgument.JSON_KEY_LASTUPDATE);//good
            DateFormat df = new SimpleDateFormat(SharedArgument.DATEFORMAT_LASTUPDATE_2);
            Calendar c=Calendar.getInstance();
            c.setTime(df.parse(lastUpdate));

            result.putString(SharedArgument.ARG_CITY, location);
            result.putSerializable(SharedArgument.ARG_LASTEST_UPDATE_TIME, c);
            result.putString(SharedArgument.ARG_SUGGESTION_CAR_WASHING,jsuggestoin.getJSONObject(SharedArgument.ARG_SUGGESTION_CAR_WASHING).getString(SharedArgument.JSON_KEY_BRIEF));
            result.putString(SharedArgument.ARG_SUGGESTION_DRESSING,jsuggestoin.getJSONObject(SharedArgument.ARG_SUGGESTION_DRESSING).getString(SharedArgument.JSON_KEY_BRIEF));
            result.putString(SharedArgument.ARG_SUGGESTION_FLU,jsuggestoin.getJSONObject(SharedArgument.ARG_SUGGESTION_FLU).getString(SharedArgument.JSON_KEY_BRIEF));
            result.putString(SharedArgument.ARG_SUGGESTION_SPORT,jsuggestoin.getJSONObject(SharedArgument.ARG_SUGGESTION_SPORT).getString(SharedArgument.JSON_KEY_BRIEF));
            result.putString(SharedArgument.ARG_SUGGESTION_TRAVEL,jsuggestoin.getJSONObject(SharedArgument.ARG_SUGGESTION_TRAVEL).getString(SharedArgument.JSON_KEY_BRIEF));
            result.putString(SharedArgument.ARG_SUGGESTION_UV,jsuggestoin.getJSONObject(SharedArgument.ARG_SUGGESTION_UV).getString(SharedArgument.JSON_KEY_BRIEF));
            result.putInt(SharedArgument.ARG_BUNDLE_RES_TYPE, SharedArgument.RES_TYPE_NOW);
        } catch (Exception e) {
            result.clear();
            result.putInt(SharedArgument.ARG_BUNDLE_RES_TYPE, SharedArgument.RES_TYPE_FAILED_WITH_EXCEPTION);
            result.putSerializable(SharedArgument.ARG_EXCEPTION,e);
        }
        return  result;

    }

    public Bundle handleActionGetNearestDaysWeatherInfo(String location)
    {
        Bundle result = new Bundle();
        try {
            String content=NetUtil.getUrlContent(URL_NEARST_DAYS_WEATHER+"&"+API_ARG_LOCATION+"="+location);
            JSONObject job = new JSONObject(content);
//            {"results":
//              [
//                  {"location":{"id":"YB1UX38K6DY1","name":"哈尔滨","country":"CN","path":"哈尔滨,哈尔滨,黑龙江,中国","timezone":"Asia/Shanghai","timezone_offset":"+08:00"},
//                  "daily":[
//                      {"date":"2017-07-23","text_day":"多云","code_day":"4","text_night":"多云","code_night":"4","high":"31","low":"19","precip":"","wind_direction":"西","wind_direction_degree":"270","wind_speed":"15","wind_scale":"3"},
//                      {"date":"2017-07-24","text_day":"多云","code_day":"4","text_night":"多云","code_night":"4","high":"27","low":"15","precip":"","wind_direction":"北","wind_direction_degree":"0","wind_speed":"15","wind_scale":"3"},
//                      {"date":"2017-07-25","text_day":"晴","code_day":"0","text_night":"晴","code_night":"1","high":"27","low":"16","precip":"","wind_direction":"西","wind_direction_degree":"270","wind_speed":"10","wind_scale":"2"}
//                  ],
//              "last_update":"2017-07-23T08:00:00+08:00"}
//              ]
//          }

            JSONObject jres = job.getJSONArray(SharedArgument.JSON_KEY_RESULTS).getJSONObject(0);
            JSONArray jdaily= jres.getJSONArray(SharedArgument.JSON_KEY_DAILY);

            String lastUpdate = jres.getString(SharedArgument.JSON_KEY_LASTUPDATE);//good
            DateFormat df = new SimpleDateFormat(SharedArgument.DATEFORMAT_LASTUPDATE_2);
            Calendar c=Calendar.getInstance();
            c.setTime(df.parse(lastUpdate));


            result.putString(SharedArgument.ARG_CITY, location);
            result.putSerializable(SharedArgument.ARG_LASTEST_UPDATE_TIME, c);
            for(int i=0;i<jdaily.length();i++)
            {
                Bundle res=new Bundle();
                res.putString(SharedArgument.ARG_WEATHER_TEXT,jdaily.getJSONObject(i).getString(SharedArgument.JSON_KEY_TEXT_DAY));
                res.putInt(SharedArgument.ARG_WEATHER_CODE,jdaily.getJSONObject(i).getInt(SharedArgument.JSON_KEY_CODE_DAY));
                res.putInt(SharedArgument.ARG_MAX_TEMPERATURE,jdaily.getJSONObject(i).getInt(SharedArgument.JSON_KEY_HIGH));
                res.putInt(SharedArgument.ARG_MIN_TEMPERATURE,jdaily.getJSONObject(i).getInt(SharedArgument.JSON_KEY_LOW));
                res.putInt(SharedArgument.ARG_WIND_SCALE,jdaily.getJSONObject(i).getInt(SharedArgument.JSON_KEY_WIND_SCALE));
                res.putString(SharedArgument.ARG_WIND_DIRECTION,jdaily.getJSONObject(i).getString(SharedArgument.JSON_KEY_WIND_DIRECTION));
                result.putBundle(""+i,res);
            }
            result.putInt(SharedArgument.ARG_BUNDLE_RES_TYPE, SharedArgument.RES_TYPE_NOW);
        } catch (Exception e) {
            result.clear();
            result.putInt(SharedArgument.ARG_BUNDLE_RES_TYPE, SharedArgument.RES_TYPE_FAILED_WITH_EXCEPTION);
            result.putSerializable(SharedArgument.ARG_EXCEPTION,e);
        }
        return  result;
    }
}
