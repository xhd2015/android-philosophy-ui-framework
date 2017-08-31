package fulton.shaw.android.tellmelibrary.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;

import fulton.util.android.utils.SqlUtil;

/**
 * Created by 13774 on 7/17/2017.
 */

/**
 * Different context needs different initialization
 *
 *
 * This is associated with one application context.
 */
public class SqliteHelper extends SQLiteOpenHelper {
    public static final int DBVERSION=1;

    public static final String DBNAME="calendarapp";

    public static final String TYPE_BY_DAY="BY_DAY";
    public static final String TYPE_BY_MONTH="BY_MONTH";
    public static final String TYPE_BY_YEAR="BY_YEAR";
    public static final String TYPE_ONCE="ONCE";
    public static final String[] ALLOWED_TYPES={TYPE_ONCE,TYPE_BY_DAY,TYPE_BY_MONTH,TYPE_BY_YEAR};

    public static final String WEATHER_RAINY="rainy";
    public static final String WEATHER_SUNNY="sunny";
    public static final String WEATHER_THUNDER="thunder";
    public static final String WEATHER_SNOW="snow";
    public static final String[] ALLOWED_WEATHER_STATES={WEATHER_RAINY,WEATHER_SUNNY,WEATHER_THUNDER,WEATHER_SNOW};


    public static final String TABLE_ISSUE_LIST="IssueList";
    public static final String TABLE_NOTE="TableNote";
    public static final String TABLE_SOLAR_CALENDAR_FESTIVALS="TableSolarCalendarFestivals";
    public static final String TABLE_WEATHER="TableWeather";
    public static final String TABLE_PROVINCE="TableProvince";
    public static final String TABLE_CITY="TableCity";
    public static final String TABLE_TRAVEL_PLAN="TableTravelPlan";
    public static final String TABLE_TEST="TableTest";

    public static final String[] TABLES={TABLE_ISSUE_LIST,TABLE_NOTE,TABLE_SOLAR_CALENDAR_FESTIVALS,
            TABLE_WEATHER,TABLE_PROVINCE,TABLE_CITY,TABLE_TRAVEL_PLAN,
            TABLE_TEST
    };


    public static final String[] CREATE_TABLES={
            "Create Table "+TABLE_ISSUE_LIST+"("+ SqlUtil.COL_ID+" integer primary key autoincrement," + //0
                    SqlUtil.COL_YEAR+" integer," +
                     SqlUtil.COL_MONTH+" integer," +
                      SqlUtil.COL_DATE+" integer," +
                       SqlUtil.COL_HOUR+" integer,"+
                        SqlUtil.COL_MINUTE+" integer,"+
                         SqlUtil.COL_ISSUE+" issue varchar(256) not null,"+
                          SqlUtil.COL_TYPE+" character(10) check("+ SqlUtil.COL_TYPE+" in (\""+TYPE_ONCE+"\",\""+TYPE_BY_DAY+"\",\""+TYPE_BY_MONTH+"\",\""+TYPE_BY_YEAR+"\"))"+
                            ");",
            "Create Table "+TABLE_NOTE+"("+ SqlUtil.COL_YEAR+" integer not null,"+ //1
                    SqlUtil.COL_MONTH+" integer not null,"+
                    SqlUtil.COL_DATE + " integer not null,"+
                    SqlUtil.COL_HOUR + " integer not null,"+
                    SqlUtil.COL_NOTE+" varchar(512) not null"+
                    ");",
            "Create Table "+TABLE_SOLAR_CALENDAR_FESTIVALS+"("+  //2
                    SqlUtil.COL_ID+" integer primary key,"+
                    SqlUtil.COL_MONTH+" integer not null,"+
                    SqlUtil.COL_DATE + " integer not null,"+
                    SqlUtil.COL_FESTNAME+" varchar(64) not null"+
                    ");" , //The first one will be select as master festival to show
            "Create Table "+TABLE_WEATHER+"("+  //3
                    SqlUtil.COL_PROVINCE_ID+" integer,"+
                    SqlUtil.COL_CITY_ID+" integer,"+
                    SqlUtil.COL_YEAR+" integer,"+
                    SqlUtil.COL_MONTH+" integer,"+
                    SqlUtil.COL_DATE+" integer,"+
                    SqlUtil.COL_WEATHER_STATE + " character(10) check("+ SqlUtil.COL_WEATHER_STATE+" in (\""+WEATHER_RAINY+"\",\""+
                                WEATHER_SUNNY+"\",\""+WEATHER_THUNDER+"\",\""+WEATHER_SNOW+"\")),"+
                    SqlUtil.COL_CUR_TEMPERATURE +" integer,"+
                    SqlUtil.COL_MAX_TEMPERATURE+" integer,"+
                    SqlUtil.COL_MIN_TEMPERATURE+" integer,"+
                    SqlUtil.COL_LAST_UPDATE_HOUR + " integer,"+
                    SqlUtil.COL_LAST_UPDATE_MINUTE + " integer,"+
                    SqlUtil.COL_WEATHER_TEXT +" character(10),"+
                    SqlUtil.COL_WIND_SCALE+" integer,"+
                    SqlUtil.COL_WIND_DIRECTION+" character(10),"+
                    SqlUtil.COL_SUGGESTION_CAR_WASHING+" character(32),"+
                    SqlUtil.COL_SUGGESTION_DRESSING+" character(32),"+
                    SqlUtil.COL_SUGGESTION_FLU+" character(32),"+
                    SqlUtil.COL_SUGGESTION_SPORT+" character(32),"+
                    SqlUtil.COL_SUGGESTION_TRAVEL+" character(32),"+
                    SqlUtil.COL_SUGGESTION_UV+" character(32),"+
                    "PRIMARY KEY("+ SqlUtil.COL_PROVINCE_ID+","+ SqlUtil.COL_CITY_ID+","+ SqlUtil.COL_YEAR+","+ SqlUtil.COL_MONTH+","+ SqlUtil.COL_DATE+")"+
                    ");",
            "Create Table "+TABLE_PROVINCE+"("+  //4
                    SqlUtil.COL_ID + " integer primary key,"+
                    SqlUtil.COL_NAME+ " character(32) not null unique"+
                    ");",
            "Create Table "+TABLE_CITY+"("+  //5
                    SqlUtil.COL_ID + " integer primary key,"+
                    SqlUtil.COL_NAME+ " character(32) not null,"+
                    SqlUtil.COL_PROVINCE_ID + " integer not null,"+
                    SqlUtil.COL_QUERY_ID+" character(16)"+ //if we cannot getByGetter the information,means no QUERY_ID or QUERY_ID=null
                    ");",
            "Create Table "+TABLE_TRAVEL_PLAN+"("+//6
                    SqlUtil.COL_ID+" integer primary key autoincrement,"+
                    SqlUtil.COL_YEAR+" integer not null,"+
                    SqlUtil.COL_MONTH+" integer not null,"+
                    SqlUtil.COL_DATE+" integer not null,"+
                    SqlUtil.COL_SRC_PROV_ID+" integer,"+
                    SqlUtil.COL_SRC_CITY_ID+" integer,"+
                    SqlUtil.COL_DST_PROV_ID+" integer not null,"+
                    SqlUtil.COL_DST_CITY_ID+" integer not null"+
                    ");",
            "Create Table "+TABLE_TEST+"("+//7
                SqlUtil.COL_ID+ SqlUtil.SQL_INTEGER_PRIMARY_KEY_AUTOINCREMENT+","+
                    SqlUtil.COL_CONTENT+" character(10)"+
                    ");"
    };

    public static final String SELECTION_MONTH_DATE= SqlUtil.COL_MONTH+"=? and "+ SqlUtil.COL_DATE+"=?";
    public static final String SELECTION_YEAR_MONTH_DATE= SqlUtil.COL_YEAR+"=? and "+SELECTION_MONTH_DATE;




    public SqliteHelper(Context context) {
        super(context, DBNAME, null, DBVERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        for(String sql:CREATE_TABLES)
            db.execSQL(sql);
        initFestivals(db);
    }

    public static void recreate(SQLiteDatabase db,String table)
    {
        int i=0;
        for(;i<TABLES.length;i++)
            if(TABLES[i].equals(table))break;
        if(i<TABLES.length)
        {
            db.execSQL(SqlUtil.SQL_DROP_TABLE_IF_EXISTS +table);
            db.execSQL(CREATE_TABLES[i]);
        }
    }

    public static void initFestivals(SQLiteDatabase db)
    {
        SqliteHelper.recreate(db,TABLE_SOLAR_CALENDAR_FESTIVALS);
        ArrayList<Object[]> initFestivals=new ArrayList<>();
        initFestivals.add(new Object[]{4-1,1,"Fool's Day"});
        initFestivals.add(new Object[]{5-1,1,"Labour's Day"});
        initFestivals.add(new Object[]{6-1,1,"Children's Day"});
        initFestivals.add(new Object[]{7-1,18,"Test Day"});
        initFestivals.add(new Object[]{10-1,1,"National Festival"});
        initFestivals.add(new Object[]{12-1,25,"Christmas Day"});

        for(Object[] o:initFestivals)
        {
            ContentValues values=new ContentValues();
            values.put(SqlUtil.COL_MONTH,(int)o[0]);
            values.put(SqlUtil.COL_DATE,(int)o[1]);
            values.put(SqlUtil.COL_FESTNAME,(String)o[2]);
            db.insert(TABLE_SOLAR_CALENDAR_FESTIVALS,null,values);
        }
    }
    public static void initProvinces(SQLiteDatabase db)
    {
        SqliteHelper.recreate(db,TABLE_PROVINCE);
        ArrayList<Object[]> initFestivals=new ArrayList<>();
        initFestivals.add(new Object[]{11,"北京市"});
        initFestivals.add(new Object[]{12,"天津市"});
        initFestivals.add(new Object[]{13,"河北市"});
        initFestivals.add(new Object[]{14,"山西省"});
        initFestivals.add(new Object[]{15,"内蒙古自治区"});
        initFestivals.add(new Object[]{21,"辽宁省"});
        initFestivals.add(new Object[]{22,"吉林省"});
        initFestivals.add(new Object[]{23,"黑龙江省"});
        initFestivals.add(new Object[]{31,"上海市"});
        initFestivals.add(new Object[]{32,"江苏省"});
        initFestivals.add(new Object[]{33,"浙江省"});
        initFestivals.add(new Object[]{34,"安徽省"});
        initFestivals.add(new Object[]{35,"福建省"});
        initFestivals.add(new Object[]{36,"江西省"});
        initFestivals.add(new Object[]{37,"山东省"});
        initFestivals.add(new Object[]{43,"湖南省"});
        initFestivals.add(new Object[]{44,"广东省"});
        initFestivals.add(new Object[]{45,"广西自治区"});
        initFestivals.add(new Object[]{46,"海南省"});
        initFestivals.add(new Object[]{50,"重庆市"});
        initFestivals.add(new Object[]{51,"四川省"});
        initFestivals.add(new Object[]{52,"贵州省"});
        initFestivals.add(new Object[]{53,"云南省"});
        initFestivals.add(new Object[]{54,"西藏自治区"});
        initFestivals.add(new Object[]{61,"陕西省"});
        initFestivals.add(new Object[]{62,"甘肃省"});
        initFestivals.add(new Object[]{63,"青海省"});
        initFestivals.add(new Object[]{64,"宁夏自治区"});
        initFestivals.add(new Object[]{65,"新疆自治区"});


        for(Object[] o:initFestivals)
        {
            ContentValues values=new ContentValues();
            values.put(SqlUtil.COL_ID,(int)o[0]);
            values.put(SqlUtil.COL_NAME,(String)o[1]);
            db.insert(TABLE_PROVINCE,null,values);
        }
    }
    public static void initCities(SQLiteDatabase db)
    {
        recreate(db,TABLE_CITY);
        ArrayList<Object[]> initFestivals=new ArrayList<>();

        initFestivals.add(new Object[]{11,"东城区",110101,"CHBJ000000"});
        initFestivals.add(new Object[]{11,"西城区",110102,null});
        initFestivals.add(new Object[]{11,"朝阳区",110105,"CHBJ000200"});
        initFestivals.add(new Object[]{11,"丰台区",110106,"CHBJ000800"});
        initFestivals.add(new Object[]{11,"石景山区",110107,"CHBJ000900"});
        initFestivals.add(new Object[]{11,"海淀区",110108,"CHBJ000100"});
        initFestivals.add(new Object[]{11,"门头沟区",110109,"CHBJ001300"});
        initFestivals.add(new Object[]{11,"房山区",110111,"CHBJ001100"});
        initFestivals.add(new Object[]{11,"通州区",110112,"CHBJ000500"});
        initFestivals.add(new Object[]{11,"顺义区",110113,"CHBJ000300"});
        initFestivals.add(new Object[]{11,"昌平区",110114,"CHBJ000600"});
        initFestivals.add(new Object[]{11,"大兴区",110115,"CHBJ001000"});
        initFestivals.add(new Object[]{11,"怀柔区",110116,"CHBJ000400"});
        initFestivals.add(new Object[]{11,"平谷区",110117,"CHBJ001400"});
        initFestivals.add(new Object[]{11,"密云县",110228,"CHBJ001200"});
        initFestivals.add(new Object[]{11,"延庆县",110229,"CHBJ000700"});
        initFestivals.add(new Object[]{12,"和平区",120101,null});
        initFestivals.add(new Object[]{12,"河东区",120102,null});
        initFestivals.add(new Object[]{12,"河西区",120103,null});
        initFestivals.add(new Object[]{12,"南开区",120104,null});
        initFestivals.add(new Object[]{12,"河北区",120105,null});
        initFestivals.add(new Object[]{12,"红桥区",120106,null});
        initFestivals.add(new Object[]{12,"东丽区",120110,"CHTJ000300"});
        initFestivals.add(new Object[]{12,"西青区",120111,"CHTJ000400"});
        initFestivals.add(new Object[]{12,"津南区",120112,"CHTJ000900"});
        initFestivals.add(new Object[]{12,"北辰区",120113,"CHTJ000500"});
        initFestivals.add(new Object[]{12,"武清区",120114,"CHTJ000100"});
        initFestivals.add(new Object[]{12,"宝坻区",120115,"CHTJ000200"});
        initFestivals.add(new Object[]{12,"滨海新区",120116,null});
        initFestivals.add(new Object[]{12,"宁河县",120221,"CHTJ000600"});
        initFestivals.add(new Object[]{12,"静海县",120223,"CHTJ000800"});
        initFestivals.add(new Object[]{12,"蓟县",120225,"CHTJ001200"});
        initFestivals.add(new Object[]{13,"石家庄市",130100,"CHHE000000"});
        initFestivals.add(new Object[]{13,"唐山市",130200,"CHHE040000"});
        initFestivals.add(new Object[]{13,"秦皇岛市",130300});
        initFestivals.add(new Object[]{13,"邯郸市",130400});
        initFestivals.add(new Object[]{13,"邢台市",130500});
        initFestivals.add(new Object[]{13,"保定市",130600});
        initFestivals.add(new Object[]{13,"张家口市",130700});
        initFestivals.add(new Object[]{13,"承德市",130800});
        initFestivals.add(new Object[]{13,"沧州市",130900});
        initFestivals.add(new Object[]{13,"廊坊市",131000});
        initFestivals.add(new Object[]{13,"衡水市",131100});
        initFestivals.add(new Object[]{14,"太原市",140100});
        initFestivals.add(new Object[]{14,"大同市",140200});
        initFestivals.add(new Object[]{14,"阳泉市",140300});
        initFestivals.add(new Object[]{14,"长治市",140400});
        initFestivals.add(new Object[]{14,"晋城市",140500});
        initFestivals.add(new Object[]{14,"朔州市",140600});
        initFestivals.add(new Object[]{14,"晋中市",140700});
        initFestivals.add(new Object[]{14,"运城市",140800});
        initFestivals.add(new Object[]{14,"忻州市",140900});
        initFestivals.add(new Object[]{14,"临汾市",141000});
        initFestivals.add(new Object[]{14,"吕梁市",141100});
        initFestivals.add(new Object[]{15,"呼和浩特市",150100});
        initFestivals.add(new Object[]{15,"包头市",150200});
        initFestivals.add(new Object[]{15,"乌海市",150300});
        initFestivals.add(new Object[]{15,"赤峰市",150400});
        initFestivals.add(new Object[]{15,"通辽市",150500});
        initFestivals.add(new Object[]{15,"鄂尔多斯市",150600});
        initFestivals.add(new Object[]{15,"呼伦贝尔市",150700});
        initFestivals.add(new Object[]{15,"巴彦淖尔市",150800});
        initFestivals.add(new Object[]{15,"乌兰察布市",150900});
        initFestivals.add(new Object[]{15,"兴安盟",152200});
        initFestivals.add(new Object[]{15,"锡林郭勒盟",152500});
        initFestivals.add(new Object[]{15,"阿拉善盟",152900});
        initFestivals.add(new Object[]{21,"沈阳市",210100,"CHLN000000"});
        initFestivals.add(new Object[]{21,"大连市",210200,"CHLN010000"});
        initFestivals.add(new Object[]{21,"鞍山市",210300});
        initFestivals.add(new Object[]{21,"抚顺市",210400});
        initFestivals.add(new Object[]{21,"本溪市",210500});
        initFestivals.add(new Object[]{21,"丹东市",210600});
        initFestivals.add(new Object[]{21,"锦州市",210700});
        initFestivals.add(new Object[]{21,"营口市",210800});
        initFestivals.add(new Object[]{21,"阜新市",210900});
        initFestivals.add(new Object[]{21,"辽阳市",211000});
        initFestivals.add(new Object[]{21,"盘锦市",211100});
        initFestivals.add(new Object[]{21,"铁岭市",211200});
        initFestivals.add(new Object[]{21,"朝阳市",211300});
        initFestivals.add(new Object[]{21,"葫芦岛市",211400});
        initFestivals.add(new Object[]{22,"长春市",220100});
        initFestivals.add(new Object[]{22,"吉林市",220200});
        initFestivals.add(new Object[]{22,"四平市",220300});
        initFestivals.add(new Object[]{22,"辽源市",220400});
        initFestivals.add(new Object[]{22,"通化市",220500});
        initFestivals.add(new Object[]{22,"白山市",220600});
        initFestivals.add(new Object[]{22,"松原市",220700});
        initFestivals.add(new Object[]{22,"白城市",220800});
        initFestivals.add(new Object[]{22,"延边朝鲜族自治",222400});
        initFestivals.add(new Object[]{23,"哈尔滨市",230100,"CHHL000000"});
        initFestivals.add(new Object[]{23,"齐齐哈尔市",230200,"CHHL010000"});
        initFestivals.add(new Object[]{23,"鸡西市",230300,"CHHL100000"});
        initFestivals.add(new Object[]{23,"鹤岗市",230400});
        initFestivals.add(new Object[]{23,"双鸭山市",230500});
        initFestivals.add(new Object[]{23,"大庆市",230600});
        initFestivals.add(new Object[]{23,"伊春市",230700});
        initFestivals.add(new Object[]{23,"佳木斯市",230800});
        initFestivals.add(new Object[]{23,"七台河市",230900});
        initFestivals.add(new Object[]{23,"牡丹江市",231000});
        initFestivals.add(new Object[]{23,"黑河市",231100});
        initFestivals.add(new Object[]{23,"绥化市",231200});
        initFestivals.add(new Object[]{23,"大兴安岭地区",232700});
        initFestivals.add(new Object[]{31,"黄浦区",310101});
        initFestivals.add(new Object[]{31,"徐汇区",310104});
        initFestivals.add(new Object[]{31,"长宁区",310105});
        initFestivals.add(new Object[]{31,"静安区",310106});
        initFestivals.add(new Object[]{31,"普陀区",310107});
        initFestivals.add(new Object[]{31,"闸北区",310108});
        initFestivals.add(new Object[]{31,"虹口区",310109});
        initFestivals.add(new Object[]{31,"杨浦区",310110});
        initFestivals.add(new Object[]{31,"闵行区",310112});
        initFestivals.add(new Object[]{31,"宝山区",310113});
        initFestivals.add(new Object[]{31,"嘉定区",310114});
        initFestivals.add(new Object[]{31,"浦东新区",310115});
        initFestivals.add(new Object[]{31,"金山区",310116});
        initFestivals.add(new Object[]{31,"松江区",310117});
        initFestivals.add(new Object[]{31,"青浦区",310118});
        initFestivals.add(new Object[]{31,"奉贤区",310120});
        initFestivals.add(new Object[]{31,"崇明县",310230});
        initFestivals.add(new Object[]{32,"南京市",320100});
        initFestivals.add(new Object[]{32,"无锡市",320200});
        initFestivals.add(new Object[]{32,"徐州市",320300});
        initFestivals.add(new Object[]{32,"常州市",320400});
        initFestivals.add(new Object[]{32,"苏州市",320500});
        initFestivals.add(new Object[]{32,"南通市",320600});
        initFestivals.add(new Object[]{32,"连云港市",320700});
        initFestivals.add(new Object[]{32,"淮安市",320800});
        initFestivals.add(new Object[]{32,"盐城市",320900});
        initFestivals.add(new Object[]{32,"扬州市",321000});
        initFestivals.add(new Object[]{32,"镇江市",321100});
        initFestivals.add(new Object[]{32,"泰州市",321200});
        initFestivals.add(new Object[]{32,"宿迁市",321300});
        initFestivals.add(new Object[]{33,"杭州市",330100});
        initFestivals.add(new Object[]{33,"宁波市",330200});
        initFestivals.add(new Object[]{33,"温州市",330300});
        initFestivals.add(new Object[]{33,"嘉兴市",330400});
        initFestivals.add(new Object[]{33,"湖州市",330500});
        initFestivals.add(new Object[]{33,"绍兴市",330600});
        initFestivals.add(new Object[]{33,"金华市",330700});
        initFestivals.add(new Object[]{33,"衢州市",330800});
        initFestivals.add(new Object[]{33,"舟山市",330900});
        initFestivals.add(new Object[]{33,"台州市",331000});
        initFestivals.add(new Object[]{33,"丽水市",331100});
        initFestivals.add(new Object[]{34,"合肥市",340100});
        initFestivals.add(new Object[]{34,"芜湖市",340200});
        initFestivals.add(new Object[]{34,"蚌埠市",340300});
        initFestivals.add(new Object[]{34,"淮南市",340400});
        initFestivals.add(new Object[]{34,"马鞍山市",340500});
        initFestivals.add(new Object[]{34,"淮北市",340600});
        initFestivals.add(new Object[]{34,"铜陵市",340700});
        initFestivals.add(new Object[]{34,"安庆市",340800});
        initFestivals.add(new Object[]{34,"黄山市",341000});
        initFestivals.add(new Object[]{34,"滁州市",341100});
        initFestivals.add(new Object[]{34,"阜阳市",341200});
        initFestivals.add(new Object[]{34,"宿州市",341300});
        initFestivals.add(new Object[]{34,"六安市",341500});
        initFestivals.add(new Object[]{34,"亳州市",341600});
        initFestivals.add(new Object[]{34,"池州市",341700});
        initFestivals.add(new Object[]{34,"宣城市",341800});
        initFestivals.add(new Object[]{35,"福州市",350100});
        initFestivals.add(new Object[]{35,"厦门市",350200});
        initFestivals.add(new Object[]{35,"莆田市",350300});
        initFestivals.add(new Object[]{35,"三明市",350400});
        initFestivals.add(new Object[]{35,"泉州市",350500});
        initFestivals.add(new Object[]{35,"漳州市",350600});
        initFestivals.add(new Object[]{35,"南平市",350700});
        initFestivals.add(new Object[]{35,"龙岩市",350800});
        initFestivals.add(new Object[]{35,"宁德市",350900});
        initFestivals.add(new Object[]{36,"南昌市",360100});
        initFestivals.add(new Object[]{36,"景德镇市",360200});
        initFestivals.add(new Object[]{36,"萍乡市",360300});
        initFestivals.add(new Object[]{36,"九江市",360400});
        initFestivals.add(new Object[]{36,"新余市",360500});
        initFestivals.add(new Object[]{36,"鹰潭市",360600});
        initFestivals.add(new Object[]{36,"赣州市",360700});
        initFestivals.add(new Object[]{36,"吉安市",360800});
        initFestivals.add(new Object[]{36,"宜春市",360900});
        initFestivals.add(new Object[]{36,"抚州市",361000});
        initFestivals.add(new Object[]{36,"上饶市",361100});
        initFestivals.add(new Object[]{37,"济南市",370100});
        initFestivals.add(new Object[]{37,"青岛市",370200});
        initFestivals.add(new Object[]{37,"淄博市",370300});
        initFestivals.add(new Object[]{37,"枣庄市",370400});
        initFestivals.add(new Object[]{37,"东营市",370500});
        initFestivals.add(new Object[]{37,"烟台市",370600});
        initFestivals.add(new Object[]{37,"潍坊市",370700});
        initFestivals.add(new Object[]{37,"济宁市",370800});
        initFestivals.add(new Object[]{37,"泰安市",370900});
        initFestivals.add(new Object[]{37,"威海市",371000});
        initFestivals.add(new Object[]{37,"日照市",371100});
        initFestivals.add(new Object[]{37,"莱芜市",371200});
        initFestivals.add(new Object[]{37,"临沂市",371300});
        initFestivals.add(new Object[]{37,"德州市",371400});
        initFestivals.add(new Object[]{37,"聊城市",371500});
        initFestivals.add(new Object[]{37,"滨州市",371600});
        initFestivals.add(new Object[]{37,"菏泽市",371700});
        initFestivals.add(new Object[]{41,"郑州市",410100});
        initFestivals.add(new Object[]{41,"开封市",410200});
        initFestivals.add(new Object[]{41,"洛阳市",410300});
        initFestivals.add(new Object[]{41,"平顶山市",410400});
        initFestivals.add(new Object[]{41,"安阳市",410500});
        initFestivals.add(new Object[]{41,"鹤壁市",410600});
        initFestivals.add(new Object[]{41,"新乡市",410700});
        initFestivals.add(new Object[]{41,"焦作市",410800});
        initFestivals.add(new Object[]{41,"濮阳市",410900});
        initFestivals.add(new Object[]{41,"许昌市",411000});
        initFestivals.add(new Object[]{41,"漯河市",411100});
        initFestivals.add(new Object[]{41,"三门峡市",411200});
        initFestivals.add(new Object[]{41,"南阳市",411300});
        initFestivals.add(new Object[]{41,"商丘市",411400});
        initFestivals.add(new Object[]{41,"信阳市",411500});
        initFestivals.add(new Object[]{41,"周口市",411600});
        initFestivals.add(new Object[]{41,"驻马店市",411700});
        initFestivals.add(new Object[]{41,"省直辖县级行政",419000});
        initFestivals.add(new Object[]{42,"武汉市",420100});
        initFestivals.add(new Object[]{42,"黄石市",420200});
        initFestivals.add(new Object[]{42,"十堰市",420300});
        initFestivals.add(new Object[]{42,"宜昌市",420500});
        initFestivals.add(new Object[]{42,"襄阳市",420600});
        initFestivals.add(new Object[]{42,"鄂州市",420700});
        initFestivals.add(new Object[]{42,"荆门市",420800});
        initFestivals.add(new Object[]{42,"孝感市",420900});
        initFestivals.add(new Object[]{42,"荆州市",421000});
        initFestivals.add(new Object[]{42,"黄冈市",421100});
        initFestivals.add(new Object[]{42,"咸宁市",421200});
        initFestivals.add(new Object[]{42,"随州市",421300});
        initFestivals.add(new Object[]{42,"恩施土家族苗族",422800});
        initFestivals.add(new Object[]{43,"长沙市",430100});
        initFestivals.add(new Object[]{43,"株洲市",430200});
        initFestivals.add(new Object[]{43,"湘潭市",430300});
        initFestivals.add(new Object[]{43,"衡阳市",430400});
        initFestivals.add(new Object[]{43,"邵阳市",430500});
        initFestivals.add(new Object[]{43,"岳阳市",430600});
        initFestivals.add(new Object[]{43,"常德市",430700});
        initFestivals.add(new Object[]{43,"张家界市",430800});
        initFestivals.add(new Object[]{43,"益阳市",430900});
        initFestivals.add(new Object[]{43,"郴州市",431000});
        initFestivals.add(new Object[]{43,"永州市",431100});
        initFestivals.add(new Object[]{43,"怀化市",431200});
        initFestivals.add(new Object[]{43,"娄底市",431300});
        initFestivals.add(new Object[]{43,"湘西土家族苗族",433100});
        initFestivals.add(new Object[]{44,"广州市",440100});
        initFestivals.add(new Object[]{44,"韶关市",440200});
        initFestivals.add(new Object[]{44,"深圳市",440300});
        initFestivals.add(new Object[]{44,"珠海市",440400});
        initFestivals.add(new Object[]{44,"汕头市",440500});
        initFestivals.add(new Object[]{44,"佛山市",440600});
        initFestivals.add(new Object[]{44,"江门市",440700});
        initFestivals.add(new Object[]{44,"湛江市",440800});
        initFestivals.add(new Object[]{44,"茂名市",440900});
        initFestivals.add(new Object[]{44,"肇庆市",441200});
        initFestivals.add(new Object[]{44,"惠州市",441300});
        initFestivals.add(new Object[]{44,"梅州市",441400});
        initFestivals.add(new Object[]{44,"汕尾市",441500});
        initFestivals.add(new Object[]{44,"河源市",441600});
        initFestivals.add(new Object[]{44,"阳江市",441700});
        initFestivals.add(new Object[]{44,"清远市",441800});
        initFestivals.add(new Object[]{44,"东莞市",441900});
        initFestivals.add(new Object[]{44,"中山市",442000});
        initFestivals.add(new Object[]{44,"潮州市",445100});
        initFestivals.add(new Object[]{44,"揭阳市",445200});
        initFestivals.add(new Object[]{44,"云浮市",445300});
        initFestivals.add(new Object[]{45,"南宁市",450100});
        initFestivals.add(new Object[]{45,"柳州市",450200});
        initFestivals.add(new Object[]{45,"桂林市",450300});
        initFestivals.add(new Object[]{45,"梧州市",450400});
        initFestivals.add(new Object[]{45,"北海市",450500});
        initFestivals.add(new Object[]{45,"防城港市",450600});
        initFestivals.add(new Object[]{45,"钦州市",450700});
        initFestivals.add(new Object[]{45,"贵港市",450800});
        initFestivals.add(new Object[]{45,"玉林市",450900});
        initFestivals.add(new Object[]{45,"百色市",451000});
        initFestivals.add(new Object[]{45,"贺州市",451100});
        initFestivals.add(new Object[]{45,"河池市",451200});
        initFestivals.add(new Object[]{45,"来宾市",451300});
        initFestivals.add(new Object[]{45,"崇左市",451400});
        initFestivals.add(new Object[]{46,"海口市",460100});
        initFestivals.add(new Object[]{46,"三亚市",460200});
        initFestivals.add(new Object[]{46,"三沙市",460300});
        initFestivals.add(new Object[]{50,"万州区",500101});
        initFestivals.add(new Object[]{50,"涪陵区",500102});
        initFestivals.add(new Object[]{50,"渝中区",500103});
        initFestivals.add(new Object[]{50,"大渡口区",500104});
        initFestivals.add(new Object[]{50,"江北区",500105});
        initFestivals.add(new Object[]{50,"沙坪坝区",500106});
        initFestivals.add(new Object[]{50,"九龙坡区",500107});
        initFestivals.add(new Object[]{50,"南岸区",500108});
        initFestivals.add(new Object[]{50,"北碚区",500109});
        initFestivals.add(new Object[]{50,"綦江区",500110});
        initFestivals.add(new Object[]{50,"大足区",500111});
        initFestivals.add(new Object[]{50,"渝北区",500112});
        initFestivals.add(new Object[]{50,"巴南区",500113});
        initFestivals.add(new Object[]{50,"黔江区",500114});
        initFestivals.add(new Object[]{50,"长寿区",500115});
        initFestivals.add(new Object[]{50,"江津区",500116});
        initFestivals.add(new Object[]{50,"合川区",500117});
        initFestivals.add(new Object[]{50,"永川区",500118});
        initFestivals.add(new Object[]{50,"南川区",500119});
        initFestivals.add(new Object[]{50,"潼南县",500223});
        initFestivals.add(new Object[]{50,"铜梁县",500224});
        initFestivals.add(new Object[]{50,"荣昌县",500226});
        initFestivals.add(new Object[]{50,"璧山县",500227});
        initFestivals.add(new Object[]{50,"梁平县",500228});
        initFestivals.add(new Object[]{50,"城口县",500229});
        initFestivals.add(new Object[]{50,"丰都县",500230});
        initFestivals.add(new Object[]{50,"垫江县",500231});
        initFestivals.add(new Object[]{50,"武隆县",500232});
        initFestivals.add(new Object[]{50,"忠县",500233});
        initFestivals.add(new Object[]{50,"开县",500234});
        initFestivals.add(new Object[]{50,"云阳县",500235});
        initFestivals.add(new Object[]{50,"奉节县",500236});
        initFestivals.add(new Object[]{50,"巫山县",500237});
        initFestivals.add(new Object[]{50,"巫溪县",500238});
        initFestivals.add(new Object[]{50,"石柱土家族自治",500240});
        initFestivals.add(new Object[]{50,"秀山土家族苗族",500241});
        initFestivals.add(new Object[]{50,"酉阳土家族苗族",500242});
        initFestivals.add(new Object[]{50,"彭水苗族土家族",500243});
        initFestivals.add(new Object[]{51,"成都市",510100,"CHSC200000"});
        initFestivals.add(new Object[]{51,"自贡市",510300});
        initFestivals.add(new Object[]{51,"攀枝花市",510400});
        initFestivals.add(new Object[]{51,"泸州市",510500});
        initFestivals.add(new Object[]{51,"德阳市",510600});
        initFestivals.add(new Object[]{51,"绵阳市",510700});
        initFestivals.add(new Object[]{51,"广元市",510800,"CHSC200000"});
        initFestivals.add(new Object[]{51,"遂宁市",510900});
        initFestivals.add(new Object[]{51,"内江市",511000});
        initFestivals.add(new Object[]{51,"乐山市",511100});
        initFestivals.add(new Object[]{51,"南充市",511300});
        initFestivals.add(new Object[]{51,"眉山市",511400});
        initFestivals.add(new Object[]{51,"宜宾市",511500});
        initFestivals.add(new Object[]{51,"广安市",511600});
        initFestivals.add(new Object[]{51,"达州市",511700});
        initFestivals.add(new Object[]{51,"雅安市",511800});
        initFestivals.add(new Object[]{51,"巴中市",511900});
        initFestivals.add(new Object[]{51,"资阳市",512000});
        initFestivals.add(new Object[]{51,"阿坝藏族羌族自",513200});
        initFestivals.add(new Object[]{51,"甘孜藏族自治州",513300});
        initFestivals.add(new Object[]{51,"凉山彝族自治州",513400});
        initFestivals.add(new Object[]{52,"贵阳市",520100,"CHGZ000000"});
        initFestivals.add(new Object[]{52,"六盘水市",520200,"CHGZ070000"});
        initFestivals.add(new Object[]{52,"遵义市",520300,"CHGZ010000"});
        initFestivals.add(new Object[]{52,"安顺市",520400,"CHGZ020000"});
        initFestivals.add(new Object[]{52,"毕节市",520500,"CHGZ060000"});
        initFestivals.add(new Object[]{52,"铜仁市",520600,"CHGZ050000"});
        initFestivals.add(new Object[]{52,"黔西南布依族苗",522300,"CHGZ080000"});//兴义
        initFestivals.add(new Object[]{52,"黔东南苗族侗族",522600,"CHGZ040000"});//凯里
        initFestivals.add(new Object[]{52,"黔南布依族苗族",522700,"CHGZ030000"});//都匀
        initFestivals.add(new Object[]{53,"昆明市",530100,"CHYN000000"});
        initFestivals.add(new Object[]{53,"曲靖市",530300});
        initFestivals.add(new Object[]{53,"玉溪市",530400});
        initFestivals.add(new Object[]{53,"保山市",530500});
        initFestivals.add(new Object[]{53,"昭通市",530600});
        initFestivals.add(new Object[]{53,"丽江市",530700});
        initFestivals.add(new Object[]{53,"普洱市",530800});
        initFestivals.add(new Object[]{53,"临沧市",530900});
        initFestivals.add(new Object[]{53,"楚雄彝族自治州",532300});
        initFestivals.add(new Object[]{53,"红河哈尼族彝族",532500});
        initFestivals.add(new Object[]{53,"文山壮族苗族自",532600});
        initFestivals.add(new Object[]{53,"西双版纳傣族自",532800});
        initFestivals.add(new Object[]{53,"大理白族自治州",532900});
        initFestivals.add(new Object[]{53,"德宏傣族景颇族",533100});
        initFestivals.add(new Object[]{53,"怒江傈僳族自治",533300});
        initFestivals.add(new Object[]{53,"迪庆藏族自治州",533400});
        initFestivals.add(new Object[]{54,"拉萨市",540100});
        initFestivals.add(new Object[]{54,"昌都地区",542100});
        initFestivals.add(new Object[]{54,"山南地区",542200});
        initFestivals.add(new Object[]{54,"日喀则地区",542300});
        initFestivals.add(new Object[]{54,"那曲地区",542400});
        initFestivals.add(new Object[]{54,"阿里地区",542500});
        initFestivals.add(new Object[]{54,"林芝地区",542600});
        initFestivals.add(new Object[]{61,"西安市",610100});
        initFestivals.add(new Object[]{61,"铜川市",610200});
        initFestivals.add(new Object[]{61,"宝鸡市",610300});
        initFestivals.add(new Object[]{61,"咸阳市",610400});
        initFestivals.add(new Object[]{61,"渭南市",610500});
        initFestivals.add(new Object[]{61,"延安市",610600});
        initFestivals.add(new Object[]{61,"汉中市",610700});
        initFestivals.add(new Object[]{61,"榆林市",610800});
        initFestivals.add(new Object[]{61,"安康市",610900});
        initFestivals.add(new Object[]{61,"商洛市",611000});
        initFestivals.add(new Object[]{62,"兰州市",620100});
        initFestivals.add(new Object[]{62,"嘉峪关市",620200});
        initFestivals.add(new Object[]{62,"金昌市",620300});
        initFestivals.add(new Object[]{62,"白银市",620400});
        initFestivals.add(new Object[]{62,"天水市",620500});
        initFestivals.add(new Object[]{62,"武威市",620600});
        initFestivals.add(new Object[]{62,"张掖市",620700});
        initFestivals.add(new Object[]{62,"平凉市",620800});
        initFestivals.add(new Object[]{62,"酒泉市",620900});
        initFestivals.add(new Object[]{62,"庆阳市",621000});
        initFestivals.add(new Object[]{62,"定西市",621100});
        initFestivals.add(new Object[]{62,"陇南市",621200});
        initFestivals.add(new Object[]{62,"临夏回族自治州",622900});
        initFestivals.add(new Object[]{62,"甘南藏族自治州",623000});
        initFestivals.add(new Object[]{63,"西宁市",630100});
        initFestivals.add(new Object[]{63,"海东市",630200});
        initFestivals.add(new Object[]{63,"海北藏族自治州",632200});
        initFestivals.add(new Object[]{63,"黄南藏族自治州",632300});
        initFestivals.add(new Object[]{63,"海南藏族自治州",632500});
        initFestivals.add(new Object[]{63,"果洛藏族自治州",632600});
        initFestivals.add(new Object[]{63,"玉树藏族自治州",632700});
        initFestivals.add(new Object[]{63,"海西蒙古族藏族",632800});
        initFestivals.add(new Object[]{64,"银川市",640100});
        initFestivals.add(new Object[]{64,"石嘴山市",640200});
        initFestivals.add(new Object[]{64,"吴忠市",640300});
        initFestivals.add(new Object[]{64,"固原市",640400});
        initFestivals.add(new Object[]{64,"中卫市",640500});
        initFestivals.add(new Object[]{65,"乌鲁木齐市",650100});
        initFestivals.add(new Object[]{65,"克拉玛依市",650200});
        initFestivals.add(new Object[]{65,"吐鲁番地区",652100});
        initFestivals.add(new Object[]{65,"哈密地区",652200});
        initFestivals.add(new Object[]{65,"昌吉回族自治州",652300});
        initFestivals.add(new Object[]{65,"博尔塔拉蒙古自",652700});
        initFestivals.add(new Object[]{65,"巴音郭楞蒙古自",652800});
        initFestivals.add(new Object[]{65,"阿克苏地区",652900});
        initFestivals.add(new Object[]{65,"克孜勒苏柯尔克",653000});
        initFestivals.add(new Object[]{65,"喀什地区",653100});
        initFestivals.add(new Object[]{65,"和田地区",653200});
        initFestivals.add(new Object[]{65,"伊犁哈萨克自治",654000});
        initFestivals.add(new Object[]{65,"塔城地区",654200});
        initFestivals.add(new Object[]{65,"阿勒泰地区",654300});
        initFestivals.add(new Object[]{65,"自治区直辖县级",659000});

        for(Object[] o:initFestivals)
        {
            ContentValues values=new ContentValues();
            values.put(SqlUtil.COL_PROVINCE_ID,(int)o[0]);
            values.put(SqlUtil.COL_NAME,(String)o[1]);
            values.put(SqlUtil.COL_ID,(int)o[2]);
            if(o.length==4 && o[3]!=null)
                values.put(SqlUtil.COL_QUERY_ID,(String)o[3]);
            db.insert(TABLE_CITY,null,values);
        }
    }

    protected static HashMap<Integer,String> DATA_PROV_ID_NAME;
    public static HashMap<Integer,String> getProvinceIDToNameAsHashMap(SQLiteDatabase db)
    {
        if(DATA_PROV_ID_NAME==null)
        {
            DATA_PROV_ID_NAME=new HashMap<>();
            Cursor c=db.query(TABLE_PROVINCE,new String[]{SqlUtil.COL_ID, SqlUtil.COL_NAME},null,null,null,null,null);
            while(c.moveToNext())
                DATA_PROV_ID_NAME.put(c.getInt(c.getColumnIndex(SqlUtil.COL_ID)),c.getString(c.getColumnIndex(SqlUtil.COL_NAME)));
            c.close();
        }
        return DATA_PROV_ID_NAME;
    }

    protected static HashMap<Integer,String> DATA_CITY_ID_NAME;
    public static HashMap<Integer,String> getCityIDToNameAsHashMap(SQLiteDatabase db)
    {
        if(DATA_CITY_ID_NAME==null)
        {
            DATA_CITY_ID_NAME=new HashMap<>();
            Cursor c=db.query(TABLE_CITY,new String[]{SqlUtil.COL_ID, SqlUtil.COL_NAME},null,null,null,null,null);
            while(c.moveToNext())
                DATA_CITY_ID_NAME.put(c.getInt(c.getColumnIndex(SqlUtil.COL_ID)),c.getString(c.getColumnIndex(SqlUtil.COL_NAME)));
            c.close();
        }
        return DATA_CITY_ID_NAME;
    }
    protected static ArrayList<Object[]> DATA_PROVINCE;//{[_id,prov_name]}
    protected static HashMap<Integer,ArrayList<Object[]>> DATA_CITY;//<prov_id,{ [city_id,city_name,query_id]}>
    public static ArrayList<Object[]> getProvincesAsArrayList(SQLiteDatabase db) {
        if(DATA_PROVINCE==null)
        {
            DATA_PROVINCE = new ArrayList<>();
            Cursor c=db.query(TABLE_PROVINCE,new String[]{SqlUtil.COL_ID, SqlUtil.COL_NAME},null,null,null,null,null);
            while (c.moveToNext())
            {
                DATA_PROVINCE.add(new Object[]{c.getInt(c.getColumnIndex(SqlUtil.COL_ID)), c.getString(c.getColumnIndex(SqlUtil.COL_NAME))});
            }
        }
        return DATA_PROVINCE;
    }
    public static HashMap<Integer,ArrayList<Object[]>> getCitiesAsHashMap(SQLiteDatabase db) {
        if(DATA_CITY==null)
        {
            DATA_CITY = new HashMap<>();
            Cursor c=db.query(TABLE_CITY,new String[]{SqlUtil.COL_ID, SqlUtil.COL_NAME, SqlUtil.COL_PROVINCE_ID, SqlUtil.COL_QUERY_ID},null,null,null,null,null);
            while (c.moveToNext())
            {
                int provId=c.getInt(c.getColumnIndex(SqlUtil.COL_PROVINCE_ID));
                ArrayList<Object[]> thislist=DATA_CITY.get(provId);
                if(thislist==null)
                {
                    thislist=new ArrayList<>();
                    DATA_CITY.put(provId,thislist);
                }
                thislist.add(new Object[]{c.getInt(c.getColumnIndex(SqlUtil.COL_ID)),
                        c.getString(c.getColumnIndex(SqlUtil.COL_NAME)),
                        c.getString(c.getColumnIndex(SqlUtil.COL_QUERY_ID))}
                );
            }
        }
        return DATA_CITY;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for(String table:TABLES)
            db.execSQL("Drop Table If Exists "+table);
        onCreate(db);
    }

    /**
     *
     * @param db
     * @param cityId
     * @return
     */
    public static @Nullable String getCityQueryId(SQLiteDatabase db, int cityId)
    {
        Cursor c=db.query(SqliteHelper.TABLE_CITY,new String[]{SqlUtil.COL_QUERY_ID}, SqlUtil.COL_ID+"=?",new String[]{String.valueOf(cityId)},null,null,null);
        if(c.moveToNext())
            return c.getString(c.getColumnIndex(SqlUtil.COL_QUERY_ID));
        return null;
    }
}
