package fulton.util.android.searcher.processors;

import java.util.ArrayList;
import java.util.HashMap;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import fulton.util.android.searcher.ContentProcessor;

public class StackOverflowProcessor implements ContentProcessor {

	@Override
	public ArrayList<HashMap<String, String>> process(Document doc) {
		// TODO Auto-generated method stub
		ArrayList<HashMap<String,String>> res=new ArrayList<HashMap<String,String>>();
		HashMap<String,String> one=null;
		
		Elements base=doc.select(".search-results");
		Elements title=base.select(".result-link").select("a");
		Elements href=base.select(".result-link").select("a");
		Elements brief=base.select(".excerpt");
		String temp;
		for(int i=0;i!=title.size();i++)
		{
			one=new HashMap<String,String>();
			one.put("title",title.get(i).text());
			temp=href.get(i).attr("href");
			if(!temp.startsWith("http"))
			{
				temp=getDomain()+temp;
			}
			one.put("url",temp);
			one.put("brief",brief.get(i).text());
			res.add(one);
		}
		
		return res;
	}

	@Override
	public String getBaseUrl() {
		// TODO Auto-generated method stub
		return "http://stackoverflow.com/search";
	}
	
	public String getDomain(){
		return "http://stackoverflow.com";
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "stackoverflow";
	}


	@Override
	public String getParameterFormater() {
		// TODO Auto-generated method stub
		return "q=%s";
	}
}
