package fulton.shaw.android.tellme;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

public class DetailPageFragment extends Fragment {
	
	private final static String TITLE="x_title",WEB="x_web";
	public static final String TAG="DetailedPage";
	WebView mWeber;
	TextView mTitle;
	Bundle mSavedState=new Bundle();
	Button	mBack,mForward;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view=inflater.inflate(R.layout.webdetail_fragment, container,false);
		initFields(view);
		return view;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		super.onDetach();
	}

	void initFields(View v)
	{
		
		mTitle=(TextView)v.findViewById(R.id.textView_detailed_1);
		
		mWeber=(WebView) v.findViewById(R.id.webView_detailed_1);
		WebSettings settings=mWeber.getSettings();
		settings.setSupportZoom(true);
		settings.setBuiltInZoomControls(true);
		settings.setJavaScriptEnabled(true);
		settings.setJavaScriptCanOpenWindowsAutomatically(true);
		settings.setSaveFormData(true);
		settings.setAllowFileAccess(true);
		mWeber.setWebViewClient(new WebViewClient(){
		  @Override
		  public boolean shouldOverrideUrlLoading(WebView view, String url) {
           view.loadUrl(url);
           return true;
	     }
		});//can load multiple pages
		
		mBack=(Button)v.findViewById(R.id.button_back);
		mForward=(Button)v.findViewById(R.id.button_forward);
		mBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mWeber.goBack();
			}
		});
		mForward.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mWeber.goForward();
			}
		});
	}
	

	/*!not ui safe!*/
	public void openURL(String title,String url)
	{
		// TODO Auto-generated method stub
		//url="http://m.sm.cn";
		mTitle.setText(title);
		mWeber.loadUrl(url);
	}
	
}
