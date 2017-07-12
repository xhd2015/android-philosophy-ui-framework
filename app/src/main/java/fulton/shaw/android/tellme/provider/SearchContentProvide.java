package fulton.shaw.android.tellme.provider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketImplFactory;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import android.util.Log;

public class SearchContentProvide {
	
	public static final String TAG="fulton.shaw.android.tellme.provider.SearchContentProvide";
	
	static Map<String,String> catagoryMapping=new HashMap<String, String>();
	static int timeout=1000*10;
	static String userAgent="Mozzila/5.0 (jsoup)";
	
	
	
	public static String getURL(String url)
	{
		try{
			URL u=new URL(url);
			URLConnection conn=u.openConnection();
			conn.connect();
			BufferedReader reader=new BufferedReader(
					new InputStreamReader(conn.getInputStream()));
			StringBuilder s=new StringBuilder();
			String temps=null;
			while((temps=reader.readLine())!=null)
			{
				s.append(temps);
				s.append("\n");
			}
			
			reader.close();
			
			return s.toString();
		}catch(Exception e){
			return "<Exception>"+e.toString();
		}
		

	}
	
}
