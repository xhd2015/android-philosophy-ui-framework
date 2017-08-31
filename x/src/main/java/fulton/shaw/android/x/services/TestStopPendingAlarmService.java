package fulton.shaw.android.x.services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;

import fulton.shaw.android.x.activities.SeePriceActivity;
import fulton.util.android.utils.Util;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class TestStopPendingAlarmService extends IntentService {

    public TestStopPendingAlarmService() {
        super("TestStopPendingAlarmService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Util.logi("in service");
        int pendingId=0;
        Intent newIntent=new Intent(getApplicationContext(), SeePriceActivity.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(getApplicationContext(),pendingId,newIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager= (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }


}
