package net.airbana.android.map;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Random;
import net.airbana.android.tabletmap.R;
import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.graphics.drawable.Drawable;
import android.util.Log;


public class AirbanaAPI 
{
	public JSONArray getNews(String APIURL)
	{
		try {
			return this.getJSONAPI(APIURL).getJSONArray("News");
		} catch (JSONException e) {
			return null;
		}
	}
	
	public JSONArray getSiteDetails()
	{
		try {
			return this.getJSONAPI("http://api.airbana.net/2.0/sites.json?type=1&detailed=true&apikey=4d81d3d07c1a765a186e75433eb2555a").getJSONArray("Sites");
		} catch (JSONException e) {
			return null;
		}
	}
	
	public JSONArray getEvents()
	{
		try {
			return this.getJSONAPI("http://api.airbana.net/2.0/events.json?apikey=4d81d3d07c1a765a186e75433eb2555a").getJSONArray("Events");
		} catch (JSONException e) {
			return null;
		}
	}
	
	public JSONArray getSiteDetails(int Region)
	{
		try {
			return this.getJSONAPI("http://api.airbana.net/2.0/sites.json?type=1&detailed=true&apikey=4d81d3d07c1a765a186e75433eb2555a&region="+Region).getJSONArray("Sites");
		} catch (JSONException e) {
			return null;
		}
	}
	
	public JSONObject getSiteDetail(String SiteID)
	{
		try {
			return this.getJSONAPI("http://api.airbana.net/2.0/site.json?siteid="+SiteID+"&detailed=true").getJSONObject("Site");
		} catch (JSONException e) {
			return null;
		}
	}
	
	public JSONArray getTeams()
	{
		try {
			return this.getJSONAPI("http://api.airbana.net/2.0/teams.json?type=1&detailed=true&apikey=4d81d3d07c1a765a186e75433eb2555a").getJSONArray("Teams");
		} catch (JSONException e) {
			return null;
		}
	}
	
	public JSONObject getEventDetail(String EventID)
	{
		try {
			return this.getJSONAPI("http://api.airbana.net/2.0/event.json?eventid="+EventID+"&detailed=true").getJSONObject("Event");
		} catch (JSONException e) {
			return null;
		}
	}
	
	public JSONObject getLatestDetails(int Region)
	{
		JSONObject Return = new JSONObject();

		try 
		{
			Return.put("Latest", this.getJSONAPI("http://api.airbana.net/2.0/latest.json?region="+Region));
		}
		catch (JSONException e) 
		{
			Log.e("API","Latest Details Failed");
			//Return.put("Latest", null);
			//Fuck knows
		}
		catch (Exception e)
		{
			Log.e("API",e.getMessage());
		}
		
		try 
		{
			Return.put("Events", this.getJSONAPI("http://api.airbana.net/2.0/events.json"));
		} 
		catch (JSONException e) 
		{
			Log.e("API","Latest Events Failed");
			//Return.put("Events", null);
			//Fuck knows
		}
		catch (Exception e)
		{
			Log.e("API",e.getMessage());
		}
		
		try 
		{
			Random RNG = new Random();
			if(RNG.nextBoolean() == false)
			{
				Return.put("News", this.getJSONAPI("http://api.airbana.net/2.0/news.json?source=arnies"));
				Return.put("NewsSource", "arnies");
			}
			else
			{
				Return.put("News", this.getJSONAPI("http://api.airbana.net/2.0/news.json?source=popularairsoft"));
				Return.put("NewsSource", "popularairsoft");
			}
		} 
		catch (JSONException e) 
		{
			Log.e("API","Latest News Failed");
			//Return.put("Events", null);
			//Fuck knows
		}
		
		return Return;
	}
	
	
	/**
	 * Creates a GET request to the API Write Cache to add a user generated event
	 * @param (int) SiteID
	 * @param EventName
	 * @param StartDate
	 * @param EndDate
	 * @param EventSynopsis
	 * @return
	 */
	public int AddEvent (String SiteID, String EventName, String StartDate, String EndDate, String EventSynopsis)
	{
		try 
		{
			return this.getJSONAPI("http://api.airbana.net/2.0/write.json?type=event&SiteID="+SiteID+
									"&EventName=" + URLEncoder.encode(EventName)+
									"&StartDate=" + StartDate +
									"&EndDate=" + EndDate +
									"&Synopsis=" + URLEncoder.encode(EventSynopsis)+
									"&apikey=4d81d3d07c1a765a186e75433eb2555a").getInt("errorcode");
		} 
		catch (JSONException e) 
		{
			return 1;
		}
	}
	public JSONArray Search(int Region, String SiteName, String Address)
	{
		try 
		{
			return this.getJSONAPI("http://api.airbana.net/2.0/search.json?search=site&sitename="+SiteName+"&address="+Address).getJSONArray("Results");
		} 
		catch (JSONException e) 
		{
			Log.i("API",e.getMessage());
			return null;
		}
	}
	
	public JSONArray Search(int Region, String EventName, int Year, int Month, int Day)
	{
		try 
		{
			
			String Date = Integer.toString(Year) + "-" + Integer.toString(Month) + "-" + Integer.toString(Day);
			return this.getJSONAPI("http://api.airbana.net/2.0/search.json?search=event&eventname="+EventName+"&date="+Date).getJSONArray("Results");
		} 
		catch (JSONException e) 
		{
			return null;
		}
	}
	
	private JSONObject getJSONAPI(String APIURL)
	{
		URL updateURL = null;
		URLConnection conn = null;
		InputStream is = null;
		String HTMLReturnString = "";
		JSONObject json;
		//JSONArray Sites = null;
		 
		ByteArrayBuffer baf = new ByteArrayBuffer(50); 
		int current = 0; 
		
		try 
		{ 
			updateURL = new URL(APIURL);
		} 
		catch (MalformedURLException e) 
		{
			return null;
		}  
		
		try 
		{
			conn = updateURL.openConnection();
			is = conn.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is); 
			while((current = bis.read()) != -1)
			{  
				baf.append((byte)current);  
			}
			
			/* Convert the Bytes read to a String. */  
			HTMLReturnString = new String(baf.toByteArray());  
		} 
		catch (IOException e) 
		{
			return null;
		}
		
		try 
		{
			json = new JSONObject(HTMLReturnString);
			return json;
		} 
		catch (JSONException e) 
		{
			//Log.e("API", "assigning the string to a JSON object failed: " + HTMLReturnString);
			return null;
		}
	}
	
	public Drawable ImageOperations(String url, String saveFilename) 
	{
		try {
			InputStream is = (InputStream) this.fetch(url);
			Drawable d = Drawable.createFromStream(is, "src");
			return d;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
    
    private Object fetch(String address) throws MalformedURLException,IOException 
    {
		URL url = new URL(address);
		Object content = url.getContent();
		return content;
	}
}
