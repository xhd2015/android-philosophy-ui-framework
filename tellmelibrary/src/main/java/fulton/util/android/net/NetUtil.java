package fulton.util.android.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;


/**
 * Created by 13774 on 7/20/2017.
 */

public class NetUtil {
    public static String getUrlContent(String urlString)throws Exception
    {
        URL url = new URL(urlString);
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();



        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

        StringBuilder sb = new StringBuilder();
        try{
            String input;
            while ((input = br.readLine()) != null)
                sb.append(input);
        }finally {
            br.close();
        }
        return sb.toString();
    }

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
