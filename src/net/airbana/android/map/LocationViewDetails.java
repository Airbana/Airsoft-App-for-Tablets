package net.airbana.android.map;

import net.airbana.android.tabletmap.R;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class LocationViewDetails extends Activity
{
	String SiteID = "0";
	String PostCodeStr = "";
	JSONObject Site = null;
	AirbanaAPI APIFuncs = new AirbanaAPI();
	Drawable image = null;
	
	private SharedPreferences settings = null;
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
       
        SiteID = getIntent().getStringExtra("SiteID");
        if(SiteID == "0")
        {
        	 setContentView(R.layout.viewsitefailed);
        }
        else
        {
        	 setContentView(R.layout.viewsite);
        	 final Handler mHandler = new Handler();
             
             /**
              * When called this will create a linear layout, an image view (for the news site logos)
              * and 
              */
             final Runnable SiteResult = new Runnable() 
             {
             	public void run() 
             	{ 
             		TextView SiteName = (TextView)findViewById(R.id.viewsite_SiteName);
             		TextView Website = (TextView)findViewById(R.id.viewsite_Website);
             		TextView Phone = (TextView)findViewById(R.id.viewsite_ContactPhone);
             		TextView Email = (TextView)findViewById(R.id.viewsite_ContactEmail);
             		TextView Address1 = (TextView)findViewById(R.id.viewsite_Address1);
             		TextView Address2 = (TextView)findViewById(R.id.viewsite_Address2);
             		TextView PostCode = (TextView)findViewById(R.id.viewsite_PostCode);
             		LinearLayout SiteFeatues = (LinearLayout)findViewById(R.id.SiteFeaturesLinear);
             		ImageView SiteFeature = new ImageView(LocationViewDetails.this);
             		
             		PostCode.setOnClickListener(new View.OnClickListener() 
        	        {
        	            @SuppressWarnings("deprecation")
						public void onClick(View v) 
        	            {
        	            	ClipboardManager clipboard = (ClipboardManager) getSystemService(LocationViewDetails.CLIPBOARD_SERVICE);
        	            	clipboard.setText(PostCodeStr);
        	            	Toast.makeText(LocationViewDetails.this, "Postcode added to clipboard.\n\nTo get directions simply paste into Google Navigator!", Toast.LENGTH_SHORT).show();
        	            	//ClipData clip = ClipData.newPlainText("simple text","Hello, World!");
        	            }
        	        });
             		
             		try {
						SiteName.setText(Site.getString("SiteName"));
						Website.setText(Site.getString("Website"));
						Phone.setText(Site.getString("ContactPhone"));
						Email.setText(Site.getString("ContactEmail"));
						Address1.setText(Site.getString("Address1"));
						Address2.setText(Site.getString("Address2"));
						PostCodeStr = Site.getString("PostCode");
						PostCode.setText(PostCodeStr);
						//SiteID = Site.getString("SiteID");
					} 
             		catch (JSONException e) 
             		{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					try {
						if(Site.getInt("UKASGB") == 1)
						{
							SiteFeature = new ImageView(LocationViewDetails.this);
							SiteFeature.setImageDrawable(getResources().getDrawable(R.drawable.ukasgb));
							SiteFeature.setMinimumWidth(48);
							SiteFeature.setMinimumHeight(48);
							SiteFeatues.addView(SiteFeature);
						}
						
						if(Site.getInt("UKARA") == 1)
						{
							SiteFeature = new ImageView(LocationViewDetails.this);
							SiteFeature.setImageDrawable(getResources().getDrawable(R.drawable.ukara));
							SiteFeature.setMinimumWidth(48);
							SiteFeature.setMinimumHeight(48);
							SiteFeatues.addView(SiteFeature);
						}
						
						if(Site.getInt("ShopOnSite") == 1)
						{
							SiteFeature = new ImageView(LocationViewDetails.this);
							SiteFeature.setImageDrawable(getResources().getDrawable(R.drawable.shop));
							SiteFeature.setMinimumWidth(48);
							SiteFeature.setMinimumHeight(48);
							SiteFeatues.addView(SiteFeature);
						}
						
						if(Site.getInt("ExplosivesAllowed") == 1)
						{
							SiteFeature = new ImageView(LocationViewDetails.this);
							SiteFeature.setImageDrawable(getResources().getDrawable(R.drawable.explosives));
							SiteFeature.setMinimumWidth(48);
							SiteFeature.setMinimumHeight(48);
							SiteFeatues.addView(SiteFeature);
						}
						
						if(Site.getInt("Environment") == 1)
						{
							SiteFeature = new ImageView(LocationViewDetails.this);
							SiteFeature.setImageDrawable(getResources().getDrawable(R.drawable.environment_1));
							SiteFeature.setMinimumWidth(48);
							SiteFeature.setMinimumHeight(48);
							SiteFeatues.addView(SiteFeature);
						}
						else
						{
							SiteFeature = new ImageView(LocationViewDetails.this);
							SiteFeature.setImageDrawable(getResources().getDrawable(R.drawable.environment_2));
							SiteFeature.setMinimumWidth(48);
							SiteFeature.setMinimumHeight(48);
							SiteFeatues.addView(SiteFeature);
						}
						
						if(Site.getInt("BatteryCharging") == 1)
						{
							SiteFeature = new ImageView(LocationViewDetails.this);
							SiteFeature.setImageDrawable(getResources().getDrawable(R.drawable.battery));
							SiteFeature.setMinimumWidth(48);
							SiteFeature.setMinimumHeight(48);
							SiteFeatues.addView(SiteFeature);
						}
						
						if(Site.getInt("Repairs") == 1)
						{
							SiteFeature = new ImageView(LocationViewDetails.this);
							SiteFeature.setImageDrawable(getResources().getDrawable(R.drawable.repairs));
							SiteFeature.setMinimumWidth(48);
							SiteFeature.setMinimumHeight(48);
							SiteFeatues.addView(SiteFeature);
						}
						
						settings = getSharedPreferences("Airbana", 0);
				        if(settings.getBoolean("locationViewDetailsFirstRun", true) == true)
				        {
				        	Toast.makeText(LocationViewDetails.this, "This screen shows a sites details\n\nUse the menu for additional options.", Toast.LENGTH_LONG).show();
				        	SharedPreferences.Editor editor = settings.edit();
				        	editor.putBoolean("locationViewDetailsFirstRun", false);
				        	editor.commit();
				        }
				        
				        //Drawable image = APIFuncs.ImageOperations("http://cdn.airbana.net/images/map_cache/"+ SiteID +"_cache.png","image.jpg");
            			ImageView imgView = new ImageView(getBaseContext());
            			imgView = (ImageView)findViewById(R.id.mapThumbnail);
            			imgView.setImageDrawable(image);
            			//imgView.setM
				        
					} 
					catch (JSONException e) 
					{
						//setContentView(R.layout.viewsitefailed);
						//No site features - not the end of the world
					}
             		
             	}
             };
             
             final ProgressDialog dialog = ProgressDialog.show(LocationViewDetails.this, "Contacting Airbana.", "Loading Site Details. Please wait...", true);
     		
             new Thread(new Runnable()
             {
             	public void run()
             	{
             		try
             		{
             			Site = APIFuncs.getSiteDetail(SiteID);
             			image = APIFuncs.ImageOperations("http://cdn.airbana.net/images/map_cache/"+ SiteID +"_cache.png","image.jpg");
             			mHandler.post(SiteResult);   			
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
    }
    
    //Menu Creation
	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.location_viewdetails, menu);
	    return true;
	}
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) 
    {
        switch (item.getItemId()) 
        {
	        case R.id.add_review:
	        {
	        	Toast.makeText(LocationViewDetails.this, "This will be in the next version...\n\n(Due July 2011)", Toast.LENGTH_LONG).show();
	            return true;
	        }
	        
	        case R.id.edit_location:
	        {
	        	Toast.makeText(LocationViewDetails.this, "This will be in the next version...\n\n(Due July 2011)", Toast.LENGTH_LONG).show();
	            return true;
	        }
	        
	        case R.id.add_event:
	        {
	        	Intent addEventIntent = new Intent(LocationViewDetails.this, EventAdd.class);
	        	try 
	        	{
	        		addEventIntent.putExtra("SiteID", Site.getInt("SiteID"));
					addEventIntent.putExtra("SiteName", Site.getString("SiteName"));
				} 
	        	catch (JSONException e) 
	        	{
					//If it fails they'll just have to choose it manually
				}
	        	
	        	LocationViewDetails.this.startActivity(addEventIntent);
	            return true;
	        }
	        
	        case R.id.view_site_events:
	        {
	        	Toast.makeText(LocationViewDetails.this, "This will be in the next version...\n\n(Due July 2011)", Toast.LENGTH_LONG).show();
	            return true;
	        }
        }
        return false;
    }*/
    
    /*@Override
	 public boolean onSearchRequested() 
	 {
		 Intent searchLandingIntent = new Intent(LocationViewDetails.this, SearchLanding.class);
		 LocationViewDetails.this.startActivity(searchLandingIntent);
	     return false;  // don't go ahead and show the search box
	 }*/
}