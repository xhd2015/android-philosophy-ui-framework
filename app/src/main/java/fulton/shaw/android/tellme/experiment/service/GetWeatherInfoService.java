package fulton.shaw.android.tellme.experiment.service;

import android.app.IntentService;
import android.content.Intent;

import fulton.util.android.event.GenericListener;
import fulton.util.android.event.GenericNotifier;
import fulton.util.android.event.GenericNotifierPrincipal;
import fulton.shaw.android.tellme.experiment.fragments.SharedArgument;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class GetWeatherInfoService extends IntentService implements GenericNotifierPrincipal{
    // TODO: Rename actions, choose action names that describe tasks that this

    public static final String ACTION_GET_WEATHER_INFO = "fulton.shaw.android.tellme.experiment.action.get_weather_info";


    public static final int EVENT_COUNT=3;

    private GenericNotifier mNotifier;
    public static final int EVENT_DONE_GET_WEATHER_INFO=0;

    private GetWeatherInfoHandler mHandler;

    public GetWeatherInfoService() {
        super("GetWeatherInfoService");
        mNotifier=new GenericNotifier(EVENT_COUNT);
        mHandler = new GetWeatherInfoHandler();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            GenericListener callback= (GenericListener) intent.getSerializableExtra(SharedArgument.ARG_LISTENER);
            if (ACTION_GET_WEATHER_INFO.equals(action)) {
                final int year= intent.getIntExtra(SharedArgument.ARG_YEAR,-1);
                final int month = intent.getIntExtra(SharedArgument.ARG_MONTH,-1);
                final int date=intent.getIntExtra(SharedArgument.ARG_DATE,-1);
                final String city=intent.getStringExtra(SharedArgument.ARG_CITY);
            }
        }
    }

    @Override
    public GenericNotifier getProxyNotifier() {
        return mNotifier;
    }
}
