package fulton.shaw.android.x.services;

import android.app.IntentService;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;

import fulton.shaw.android.x.broadcast.StartAlarmServiceBroadcast;
import fulton.util.android.utils.Util;

public class SetAlarmService extends IntentService {
    public static final String SERVICE_NAME="setAlarmService";
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */
    public SetAlarmService() {
        super(SERVICE_NAME);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        //must be from a broadcast
        Util.logi("in service");
    }

    @Override
    public void onDestroy() {
        Intent intent=new Intent(getApplicationContext(), StartAlarmServiceBroadcast.class);
        sendBroadcast(intent); //they getByGetter not stopped.
        super.onDestroy();
    }
}
