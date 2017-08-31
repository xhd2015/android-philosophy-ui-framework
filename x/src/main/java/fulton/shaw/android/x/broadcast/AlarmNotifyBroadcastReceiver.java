package fulton.shaw.android.x.broadcast;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.NotificationCompat;
import android.widget.Toast;

import fulton.shaw.android.x.R;
import fulton.shaw.android.x.activities.ViewSubitemDetailActivity;
import fulton.shaw.android.x.public_interfaces.SqliteHelper;
import fulton.util.android.utils.SqlUtil;
import fulton.util.android.utils.Util;

public class AlarmNotifyBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        int type=intent.getIntExtra("TYPE",-1);
        if(type==-1)return;
        if(type==0)
        {
            String table=intent.getStringExtra("TABLE");
            int     innerId=intent.getIntExtra("INNERID",-1);
            String causing=intent.getStringExtra("CAUSING");

            SqliteHelper sqliteHelper=new SqliteHelper(context);
            Cursor cursor=sqliteHelper.rawQuery("Select * From "+table+" Where _id=?",new String[]{String.valueOf(innerId)});
            if(cursor.getCount()!=1)return;
            cursor.moveToFirst();

            String contentText=null;
            if(table.equals(SqliteHelper.TABLE_GENERAL_RECORDS))
            {
                contentText=cursor.getString(cursor.getColumnIndex(SqlUtil.COL_DETAIL));
            }

            //send a notification
            Util.logi("a new alarm notification");
            Toast.makeText(context,"你有一个提醒",Toast.LENGTH_LONG).show();
            NotificationCompat.Builder builder=new NotificationCompat.Builder(context);
            builder.setSmallIcon(R.drawable.ic_search_black_24dp);
            builder.setContentTitle(causing);
            builder.setContentText(contentText);
            builder.setAutoCancel(true);

            NotificationManager notificationManager=(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0,builder.build());
            cursor.close();
        }else if(type==1){
            //cancel
            Util.logi("cancel an alarm");
            int requestCode=intent.getIntExtra("REQUEST_CODE",-1);
            if(requestCode==-1)return;
            Intent alarmIntent= ViewSubitemDetailActivity.getAlarmBroadcastIntent(context,0);
            PendingIntent pendingIntent= PendingIntent.getBroadcast(context,requestCode,alarmIntent,PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager= (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);
        }
    }
}
