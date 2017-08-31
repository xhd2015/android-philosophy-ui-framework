package fulton.shaw.android.tellme.experiment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import fulton.util.android.utils.Util;

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
       Toast.makeText(context,"Hello Receiver",Toast.LENGTH_LONG);//此时context还没有初始化完成，因此这条提示不会显示
        Util.logi("OnReceive");
    }
}
