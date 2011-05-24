package net.airbana.android.map;

import java.util.List;

import net.airbana.android.tabletmap.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TimingLogger;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class Map extends MapActivity
{
	AirsoftLocationOverlays itemizedoverlay,itemizedoverlay1,itemizedoverlay2,itemizedoverlay3,itemizedoverlay4,itemizedoverlay5,itemizedoverlay6,itemizedoverlay7,itemizedoverlay8 = null;
	private LocationManager locationManager;
    List<Overlay> mapOverlays = null;
    Drawable drawable = null;
    AirbanaAPI APIFuncs = new AirbanaAPI();
	JSONObject SiteDetail = null;
	JSONArray SiteDetails = null;
    int Latitude = 0;
    int Longitude = 0;
    String SiteID = "0";
    String SiteName = "Unknown";
    String Other = "Unknown";
    int userLatitude = 0;
	int userLongitude = 0;
	String SiteType = "0";
	MapView MapViewer;
	GeoPoint p;
	SharedPreferences settings;
	Location LastLocation = null;
	Criteria criteria = new Criteria();
	TimingLogger timings = null;
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.locationsmap);
        setTitle("Airbana Airsoft Map - Tablet Edition");
        settings = getSharedPreferences("Airbana", 0);
        MapViewer = (MapView) findViewById(R.id.AirbanaSiteMap);
        MapViewer.setSatellite(false);
        MapViewer.setBuiltInZoomControls(true);
        
        locationManager = (LocationManager) getSystemService(Map.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new GeoUpdateHandler());

        //Prepare the map objects
        //Debug Key: 	0-H7P1kp9dWMgzSLu13icqIGytHyJZPEnnN36Dw
		//Other Debug:	0XtGNN3QfxSuwSKFFJODY1ZCt2tti92XWAGESPw
        //Airbana Key: 	0XtGNN3QfxStuoNVCqF5ZHsQOhrD6e9qRL0ituQ
        mapOverlays = MapViewer.getOverlays();
        drawable = this.getResources().getDrawable(R.drawable.pin_map);
        itemizedoverlay = new AirsoftLocationOverlays(drawable, this);
        itemizedoverlay1 = new AirsoftLocationOverlays(drawable, this);
        itemizedoverlay2 = new AirsoftLocationOverlays(drawable, this);
        itemizedoverlay3 = new AirsoftLocationOverlays(drawable, this);
        itemizedoverlay4 = new AirsoftLocationOverlays(drawable, this);
        itemizedoverlay5 = new AirsoftLocationOverlays(drawable, this);
        itemizedoverlay6 = new AirsoftLocationOverlays(drawable, this);
        itemizedoverlay7 = new AirsoftLocationOverlays(drawable, this);
        itemizedoverlay8 = new AirsoftLocationOverlays(drawable, this);
        
        final Handler mHandler = new Handler();
        
        final Runnable SitesData = new Runnable() 
        {
        	public void run() 
        	{ 
            	//Get current location and move there
        		if(locationManager.getBestProvider(criteria, true) != null)
        		{
	        		LastLocation = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, true));
	        		
	        		if(LastLocation != null)
	        		{
		        		userLatitude = (int) (LastLocation.getLatitude() * 1E6);
		        		userLongitude = (int) (LastLocation.getLongitude() * 1E6);
		        		p = new GeoPoint(userLatitude,userLongitude);
		        		MapController mc = MapViewer.getController();
		        		
		        		//Draw where the user is
						MyLocationOverlay myLocationOverlay = new MyLocationOverlay();
						List<Overlay> list = MapViewer.getOverlays();
						list.add(myLocationOverlay);
						
						//Animate moving to where we are
		        		mc.animateTo(p);
	        		}
        		}
        		
        		if(settings.getBoolean("useGPS", false) == true && locationManager.getBestProvider(criteria, true) != null)
        			Toast.makeText(Map.this, "Your results are currently filtered to your nearby area.\nFor full results change your settings.", Toast.LENGTH_LONG).show();
        	}
        };
        
        final ProgressDialog dialog = ProgressDialog.show(Map.this, "Contacting Airbana", "Loading Data...\nPlease note this may take 30secs" + '\n' + "This is a CPU intensive operation", true);
        
        new Thread(new Runnable()
        {
        	public void run()
        	{
        		//This is a quick test of the location nearness
        		if(locationManager.getBestProvider(criteria, true) != null)
        		{
        			LastLocation = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, true));
        			if(LastLocation != null)
        			{
        				userLatitude = (int) (LastLocation.getLatitude() * 1E6);
        				userLongitude = (int) (LastLocation.getLongitude() * 1E6);
        			}
        		}
        		
        		try
        		{
        			int region = settings.getInt("region", 0);
        			SiteDetails = APIFuncs.getSiteDetails(region);
        			
        			if(LastLocation != null && settings.getBoolean("useGPS", false) == true)
        			{
        				for (int i = 0; i < SiteDetails.length(); i++) 
                        {
                        	try 
                        	{
                				SiteDetail = SiteDetails.getJSONObject(i);
                	        	Latitude = (int) (SiteDetail.getDouble("Latitude") * 1000000);
                	        	Longitude = (int) (SiteDetail.getDouble("Longitude") * 1000000);
                	        	SiteName = SiteDetail.getString("SiteName");
                	        	SiteID = SiteDetail.getString("SiteID");
                	        	//SiteType = SiteDetail.getInt("Type");
                        	} 
                        	catch (JSONException e) 
                        	{
                				// TODO Auto-generated catch block
                				e.printStackTrace();
                			}
                        	if(Latitude > userLatitude - 500000 && 
    	                    	Latitude < userLatitude + 500000 &&
    	                    	Longitude > userLongitude - 900000 && 
    	                    	Longitude < userLongitude + 900000)
	                    	{
	                    		GeoPoint point = new GeoPoint(Latitude,Longitude);
	                    		OverlayItem overlayitem = new OverlayItem(point, SiteID ,SiteName);
	                    		itemizedoverlay.addOverlay(overlayitem);
	                    	}
                        }
        			}
        			else
        			{
        				//Add all of the items to the overlay
            			for (int i = 0; i < SiteDetails.length(); i++) 
                        {
                        	try 
                        	{
                				SiteDetail = SiteDetails.getJSONObject(i);
                	        	Latitude = (int) (SiteDetail.getDouble("Latitude") * 1000000);
                	        	Longitude = (int) (SiteDetail.getDouble("Longitude") * 1000000);
                	        	SiteName = SiteDetail.getString("SiteName");
                	        	SiteID = SiteDetail.getString("SiteID");
                	        	SiteType = SiteDetail.getString("Type");
                        	} 
                        	catch (JSONException e) 
                        	{
                				//e.printStackTrace();
                				Log.e("JSON","Error Getting something");
                			}

                    		GeoPoint point = new GeoPoint(Latitude,Longitude);
                    		OverlayItem overlayitem = new OverlayItem(point, SiteID ,SiteName);
                    		
                    		int which = i % 8;
                    		//Log.i("which",Integer.toString(which));
                    		if (which == 0)
                    		{
                    			itemizedoverlay.addOverlay(overlayitem);
                    		}
                    		else if (which == 1)
                    		{
                    			itemizedoverlay1.addOverlay(overlayitem);
                    		}
                    		else if (which == 2)
                    		{
                    			itemizedoverlay2.addOverlay(overlayitem);
                    		}
                    		else if (which == 3)
                    		{
                    			itemizedoverlay3.addOverlay(overlayitem);
                    		}
                    		else if (which == 4)
                    		{
                    			itemizedoverlay4.addOverlay(overlayitem);
                    		}
                    		else if (which == 5)
                    		{
                    			itemizedoverlay5.addOverlay(overlayitem);
                    		}
                    		else if (which == 6)
                    		{
                    			itemizedoverlay6.addOverlay(overlayitem);
                    		}
                    		else if (which == 7)
                    		{
                    			itemizedoverlay7.addOverlay(overlayitem);
                    		}
                    		else
                    		{
                    			itemizedoverlay8.addOverlay(overlayitem);
                    		}
                        }
        			}
      
            		//Add the overlays to the map
        			if(itemizedoverlay != null && itemizedoverlay.size() != 0)
        				mapOverlays.add(itemizedoverlay);
        			
        			if(itemizedoverlay1 != null && itemizedoverlay1.size() != 0)
                		mapOverlays.add(itemizedoverlay1);
        			
                	if(itemizedoverlay2 != null && itemizedoverlay2.size() != 0)
                		mapOverlays.add(itemizedoverlay2);
                	
                	if(itemizedoverlay3 != null && itemizedoverlay3.size() != 0)
                		mapOverlays.add(itemizedoverlay3);
                	
                	if(itemizedoverlay4 != null && itemizedoverlay4.size() != 0)
                		mapOverlays.add(itemizedoverlay4);
                	
                	if(itemizedoverlay5 != null && itemizedoverlay5.size() != 0)
                		mapOverlays.add(itemizedoverlay5);
                	
                	if(itemizedoverlay6 != null && itemizedoverlay6.size() != 0)
                		mapOverlays.add(itemizedoverlay6);
                	
                	if(itemizedoverlay7 != null && itemizedoverlay7.size() != 0)
                		mapOverlays.add(itemizedoverlay7);
                	
                	if(itemizedoverlay8 != null && itemizedoverlay8.size() != 0)
                		mapOverlays.add(itemizedoverlay8);
                	
        			//Post back to the handler
        			mHandler.post(SitesData);   			
        			dialog.dismiss();
        		}
        		catch(Exception e)
        		{
        			Log.e("APIFuncs",e.getMessage());
        			dialog.dismiss();
        		}
        	}
        	}).start();
       
    }

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	
	 /*@Override
	 public boolean onSearchRequested() 
	 {
		 Intent searchLandingIntent = new Intent(Map.this, SearchLanding.class);
		 Map.this.startActivity(searchLandingIntent);
	     return false;  // don't go ahead and show the search box
	 }*/
	 
	//Menu Creation
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.settings_menu, menu);
	    return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) 
    {
        switch (item.getItemId()) 
        {
	        case R.id.settings:
	        {
	        	Toast.makeText(Map.this, "Settings", Toast.LENGTH_LONG).show();
	        	Intent settingsLandingIntent = new Intent(this, settingsLanding.class);
	        	this.startActivity(settingsLandingIntent);
	            return true;
	        }
        }
        return false;
    }
	
	/*@Override
    public boolean onContextItemSelected(MenuItem item) 
	{
		switch (item.getItemId()) 
        {
	        case R.id.settings:
	        {
	        	Toast.makeText(Map.this, "Settings", Toast.LENGTH_LONG).show();
	        	Intent settingsLandingIntent = new Intent(this, settingsLanding.class);
	        	this.startActivity(settingsLandingIntent);
	            return true;
	        }
	        default:
	        {
	            return super.onOptionsItemSelected(item);
	        }

        }
	}*/

	public class GeoUpdateHandler implements LocationListener 
    {

		public void onLocationChanged(Location location) 
		{
			if (location != null) 
			{
				userLatitude = (int) (location.getLatitude() * 1E6);
				userLongitude = (int) (location.getLongitude() * 1E6);
				p = new GeoPoint(userLatitude,userLongitude);
				MapController mc = MapViewer.getController();
				
				//Draw where the user is
				MyLocationOverlay myLocationOverlay = new MyLocationOverlay();
				List<Overlay> list = MapViewer.getOverlays();
				list.add(myLocationOverlay);

				//Animate moving to the current location
				mc.animateTo(p);
			}

			
		}

		@Override
		public void onProviderDisabled(String provider) 
		{
		}

		@Override
		public void onProviderEnabled(String provider) 
		{
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) 
		{
			
		}
	}
	
	class MyLocationOverlay extends com.google.android.maps.Overlay 
	{
		@Override
		public boolean draw(Canvas canvas, MapView mapView, boolean shadow, long when) 
		{
			super.draw(canvas, mapView, shadow);
			Paint paint = new Paint();
			// Converts lat/lng-Point to OUR coordinates on the screen.
			Point myScreenCoords = new Point();
			mapView.getProjection().toPixels(p, myScreenCoords);
			//paint.setStrokeWidth(2);
			//paint.setARGB(255, 255, 0, 0);
			//paint.setStyle(Paint.Style.STROKE);
			Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.pin_user);
			canvas.drawBitmap(bmp, myScreenCoords.x, myScreenCoords.y, paint);
			//canvas.drawText("Your location...", myScreenCoords.x, myScreenCoords.y, paint);
			return true;
		} 
	} 
}