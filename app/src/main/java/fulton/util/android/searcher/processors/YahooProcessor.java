package fulton.util.android.searcher.processors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import fulton.util.android.searcher.ContentProcessor;

public class YahooProcessor implements ContentProcessor {

	@Override
	public ArrayList<HashMap<String, String>> process(Document doc) {
		ArrayList<HashMap<String,String>> res=new ArrayList<HashMap<String,String>>();
		HashMap<String,String> one=null;
		
		Elements base=doc.select("#web").select(".searchCenterMiddle");
		Elements title=base.select(".title").select("a");
		Elements href=base.select(".title").select("a");
		Elements brief=base.select(".compText").not(".fl-l");
		
		for(int i=0;i!=title.size();i++)
		{
			one=new HashMap<String,String>();
			one.put("title",title.get(i).text());
			one.put("url",href.get(i).attr("href"));
			one.put("brief",brief.get(i).text());
			res.add(one);
		}
		
		return res;
	}


	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "yahoo";
	}

	@Override
	public String getBaseUrl() {
		// TODO Auto-generated method stub
		return "http://answers.search.yahoo.com/search";
	}

	@Override
	public String getParameterFormater() {
		// TODO Auto-generated method stub
		return "p=%s&fr2=sb-top-answers.search&fr=uh3_answers_vert_gs&type=2button";
		
	}


	@Override
	public String getDomain() {
		// TODO Auto-generated method stub
		return null;
	}

}
