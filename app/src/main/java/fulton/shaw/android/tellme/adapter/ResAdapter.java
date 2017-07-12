package fulton.shaw.android.tellme.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import fulton.shaw.android.tellme.MainActivity;
import fulton.shaw.android.tellme.R;
import fulton.shaw.android.tellme.newdesign.ViewPagerActivity;

public class ResAdapter extends BaseAdapter{
	ArrayList<HashMap<String,String>> mRes;
	LayoutInflater mInflater;
	FragmentActivity mActivity;
	public ResAdapter(FragmentActivity activity) {
		// TODO Auto-generated constructor stub
		mInflater=activity.getLayoutInflater();
		mActivity=activity;
	}
	public void setRes(ArrayList<HashMap<String, String>> res)
	{
		mRes=res;
	}
	public ArrayList<HashMap<String,String>> getRes()
	{
		return mRes;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mRes==null?0:mRes.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int pos, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if(convertView==null)
		{
			convertView=mInflater.inflate(
					R.layout.res_itemlayout,
					parent,
					false
					);
			final int fpos=pos;
			final TextView title = (TextView) convertView.findViewById(R.id.textView1);
			final TextView brief=(TextView) convertView.findViewById(R.id.textView2);
			convertView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(mActivity instanceof MainActivity)
					{
					((MainActivity) mActivity).changeToDetail(
							title.getText().toString(),
							mRes.get(fpos).get("url"));
					}else if(mActivity  instanceof ViewPagerActivity) {
						((ViewPagerActivity) mActivity).changeToDetail(
							title.getText().toString(),
							mRes.get(fpos).get("url"));
						
					}
				}
			});
		}
		HashMap<String,String> singleRes=mRes.get(pos);
		setFileds(singleRes, convertView);
		return convertView;
	}
	
	void setFileds(HashMap<String, String> oneres,View view)
	{
		/*
		final String[] expectedKeys=new String[]{
				"title",
				"url",
				"brief"
		};
		*/
		TextView title=(TextView)view.findViewById(R.id.textView1);
		TextView brief=(TextView)view.findViewById(R.id.textView2);
		//Do not interfer to use run on UI method,because this time the
		//view is not present
		title.setText(oneres.get("title"));
		brief.setText(oneres.get("brief"));
	}

}
