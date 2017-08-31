package fulton.shaw.android.tellme.experiment.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;

import java.util.Calendar;

import fulton.shaw.android.tellme.R;
import fulton.shaw.android.tellme.experiment.DateDetailActivity;
import fulton.shaw.android.tellme.experiment.fragments.SharedArgument;
import fulton.shaw.android.tellmelibrary.sql.SqliteHelper;
import fulton.util.android.utils.SqlUtil;
import fulton.util.android.utils.Util;

public class SystemDateChangedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
//        Util.logi("action="+intent.getAction());
        switch (intent.getAction())
        {
            case Intent.ACTION_DATE_CHANGED:
            case Intent.ACTION_TIME_CHANGED:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        sendNotification(context,intent);
                    }
                }).start();
                break;
        }
    }

    /**
     * select today & next day's travel plans.
     */
    public static void sendNotification(Context context, Intent intent)
    {
        //getByGetter date-->retrived this day's and next day's info count,then show the first in the notification(icon,title,content)
        //SharedPreferences sharedPreferences = context.getSharedPreferences(SharedArgument.CONFIG_SHARED_PREF_FILE, Context.MODE_PRIVATE);

        Calendar calendar = Calendar.getInstance();
        String selection = SqlUtil.COL_YEAR + "=? and " + SqlUtil.COL_MONTH + "=? and " + SqlUtil.COL_DATE + "=?";
        String[] selectionArgs = new String[3];
        String[] projection = new String[]{SqlUtil.COL_SRC_PROV_ID, SqlUtil.COL_SRC_CITY_ID, SqlUtil.COL_DST_PROV_ID, SqlUtil.COL_DST_CITY_ID};

        int notifyNum=2;

        SqliteHelper sqliteHelper=new SqliteHelper(context);
        SQLiteDatabase db=sqliteHelper.getWritableDatabase();

        int count=0;
        String firstShown=null;
        String undeterminded=context.getString(R.string.undetermined);
        for (int i = 0; i < notifyNum; i++) {

//            Util.logi("y,m,d="+calendar.getByGetter(Calendar.YEAR)+","+(calendar.getByGetter(Calendar.MONTH)+1)+","+calendar.getByGetter(Calendar.DAY_OF_MONTH));
            selectionArgs[0] = String.valueOf(calendar.get(Calendar.YEAR));
            selectionArgs[1] = String.valueOf(calendar.get(Calendar.MONTH));
            selectionArgs[2] = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
            final Cursor c = db.query(SqliteHelper.TABLE_TRAVEL_PLAN, projection, selection, selectionArgs, null, null, null);
            count+=c.getCount();
            if(firstShown==null && c.getCount()>0)
            {
//                c.moveToFirst();
//                firstShown = context.getString(R.string.from)+" "+ .nonNullOrDefault(
//                        SqliteHelper.getProvinceIDToNameAsHashMap(db).getByGetter(c.getInt(c.getColumnIndex(SqlUtil.COL_SRC_PROV_ID))), undeterminded) + " "+
//                        SharedArgument.nonNullOrDefault(SqliteHelper.getCityIDToNameAsHashMap(db).getByGetter(c.getInt(c.getColumnIndex(SqlUtil.COL_SRC_CITY_ID))), undeterminded)+" "+
//                        context.getString(R.string.to)+" "+ SqliteHelper.getProvinceIDToNameAsHashMap(db).getByGetter(c.getInt(c.getColumnIndex(SqlUtil.COL_DST_PROV_ID))) + " "+
//                        SqliteHelper.getCityIDToNameAsHashMap(db).getByGetter(c.getInt(c.getColumnIndex(SqlUtil.COL_DST_CITY_ID)));
            }
            c.close();
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        calendar.add(Calendar.DAY_OF_MONTH,-notifyNum);

        if(firstShown!=null)
        {
            //build the intent
            Intent intentOpen = new Intent(context, DateDetailActivity.class);
            intentOpen.putExtra(SharedArgument.ARG_PAGE_NUM,DateDetailActivity.PAGE_TRAVEL_PLAN);
            intentOpen.putExtra(SharedArgument.ARG_YEAR,calendar.get(Calendar.YEAR));
            intentOpen.putExtra(SharedArgument.ARG_MONTH,calendar.get(Calendar.MONTH));
            intentOpen.putExtra(SharedArgument.ARG_DATE,calendar.get(Calendar.DAY_OF_MONTH));

//            Intent intentMain = new Intent(context, WeatherMainActivity.class);
//            intentMain.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);



            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addParentStack(DateDetailActivity.class);

//            stackBuilder.addNextIntent(intentMain);
            stackBuilder.addNextIntent(intentOpen);
            PendingIntent pendingIntent=stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
//            PendingIntent pendingIntent=PendingIntent.getActivities(context,0,new Intent[]{intentMain,intentOpen},PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder=new NotificationCompat.Builder(context);
            builder.setSmallIcon(R.mipmap.icon2);
            builder.setContentTitle(String.format(SharedArgument.FORMATTER_TO_DO_TRAVEL_PLANS,count));
            builder.setContentText(firstShown+"...");
            builder.setContentIntent(pendingIntent);
            builder.setAutoCancel(true);

            NotificationManager notificationManager=(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(SharedArgument.CONFIG_NOTIFY_ID_TRAVEL_PLANS,builder.build());
        }

    }
}
