package fulton.shaw.android.tellme;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;
import fulton.shaw.android.tellme.adapter.ResAdapter;
import fulton.shaw.android.tellme.provider.SearchContentProvide;
import fulton.util.android.searcher.ContentManager;

public class SearchFragment extends Fragment implements View.OnClickListener,Serializable{
	public static final String TAG="fulton.shaw.android.tellme.SearchFragment";
	private static final String WORD="word",LIST="list",TYPE="type",POSITION="position";
	
	Button mSearchBtn;
	ListView mListRes;
	TextView mPrompt;
	EditText mWord;
	Spinner mType;
	ResAdapter mAdapter;
	
	Bundle mSavedState=new Bundle();
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
//		return super.onCreateView(inflater, container, savedInstanceState);
		View v=inflater.inflate(R.layout.search_fragment, container,false);
		initFileds(v,inflater);
		mSearchBtn.setOnClickListener(this);
		return v;
	}
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		saveSate();
		Log.d(TAG, "onPause");
		super.onPause();
	}
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.d(TAG, "onResume");
		resumeState();
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
	}
	
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		Log.d("SearchFragment--save instance", "null state?"+(outState==null));
		outState.putString("word", "I have been saved");//mWord.getText().toString());
	}
	private void saveSate()
	{
		mSavedState.putString(WORD, mWord.getText().toString());
		mSavedState.putSerializable(LIST,mAdapter.getRes());
		mSavedState.putInt(POSITION, mListRes.getScrollY());
	}
	private void resumeState()
	{
		getActivity().runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				mWord.setText(mSavedState.getString(WORD));
				mListRes.setScrollY(mSavedState.getInt(POSITION));
				setSearchRes((ArrayList<HashMap<String, String>>) mSavedState.getSerializable(LIST));
			}
		});
	}
	void initFileds(View v,LayoutInflater inflater)
	{
		mSearchBtn=(Button) v.findViewById(R.id.button1);
		mListRes=(ListView) v.findViewById(R.id.listView1);
		mPrompt=(TextView) v.findViewById(R.id.textView1);
		mWord=(EditText) v.findViewById(R.id.editText1);
		mType=(Spinner)v.findViewById(R.id.spinner1);
		mAdapter=new ResAdapter(getActivity());
		
		
		mListRes.setAdapter(mAdapter);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v==mSearchBtn)
		{
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					doSearchAsideUi();
				}
			}).start();
			
		}
	}
	/**
	 * This is run aside UI
	 */
	void doSearchAsideUi()
	{
			
		Log.v(TAG, "do search");
		getActivity().runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				setPrompt("Searching");
			}
		});		
		
		HashMap<String, String> table=new HashMap<String, String>();
		table.put("Yahoo", "yahoo");
		table.put("Zhihu", "zhihu");
		table.put("Baidu Answer", "baiduzhidao");
		table.put("Guokr", "guokr");
		table.put("Stackoverflow", "stackoverflow");
		table.put("Askubuntu", "askubuntu");

		// TODO Auto-generated method stub
		String type=table.get(mType.getSelectedItem().toString());
		String word=mWord.getText().toString();
		String prompt="";
		Log.v(TAG, "Search for:"+word+":"+type);
		final ArrayList<HashMap<String, String>> res = ContentManager.searchFor(word, type);

		prompt="Search Finished";
		if(res.size()<10)
		{
			prompt=prompt+" ,"+res.size()+" Results.";
		}
		
		final String fprompt=prompt;
		getActivity().runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				setPrompt("");
				setSearchRes(res);
				setPrompt(fprompt);
			}
		});


	}
	void setPrompt(String s)
	{
		mPrompt.setText(s);
	}
	//For list view
	void setSearchRes(ArrayList<HashMap<String, String>> res)
	{
		mAdapter.setRes(res);
		mAdapter.notifyDataSetInvalidated();	
	}
	
	
}
