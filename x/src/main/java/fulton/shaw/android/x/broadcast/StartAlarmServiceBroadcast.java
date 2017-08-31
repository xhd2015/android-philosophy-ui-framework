package fulton.shaw.android.x.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import fulton.shaw.android.x.services.SetAlarmService;
import fulton.util.android.utils.Util;

public class StartAlarmServiceBroadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Util.logi("set alarm broadcast");
        Intent startIntent=new Intent(context, SetAlarmService.class);
        context.startService(startIntent);
    }
}
