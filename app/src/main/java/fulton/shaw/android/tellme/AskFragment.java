package fulton.shaw.android.tellme;


import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Browser;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import fulton.shaw.android.tellme.newdesign.ViewPagerActivity;

public class AskFragment extends Fragment{
	View mRootView;
	Button mAskButton;
	Spinner	mType;
	Map<String,String> mMapper;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mRootView=inflater.inflate(R.layout.ask_fragment_choose_type, container,false);
		initFileds(mRootView);
		return mRootView;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
	}
	/*
	 * 
	 * https://answers.yahoo.com/#ask=y&r=1543
	 */
	void initFileds(View v)
	{
		mAskButton=(Button)v.findViewById(R.id.button1);
		mType=(Spinner)v.findViewById(R.id.spinner1);
		mMapper=new HashMap<String, String>();
		mMapper.put("Stackoverflow", "https://stackoverflow.com/questions/ask");
		mMapper.put("Yahoo Answer", "http://answers.yahoo.com/#ask=y&r=1543");
		mMapper.put("Baidu Answer", "https://zhidao.baidu.com/new?word=");
		mAskButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				/*
				String packageName = "com.android.browser"; 
				String className = "com.android.browser.BrowserActivity"; 
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.addCategory(Intent.CATEGORY_LAUNCHER); 
				intent.setClassName(packageName, className); 
				
				intent.setData(Uri.parse(mMapper.get(mType.getSelectedItem().toString())));
				startActivity(Intent.createChooser(intent, "Open..."));
				*/ //old story
				final String type=mType.getSelectedItem().toString();
				((ViewPagerActivity)getActivity()).changeToDetail(
						"Ask: "+type,
						mMapper.get(type)
						);
			}
		});
	}

}
