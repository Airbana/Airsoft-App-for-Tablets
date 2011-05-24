package net.airbana.android.map;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import net.airbana.android.tabletmap.R;

public class settingsLanding extends Activity
{
	private SharedPreferences settings = null;
	private RadioButton regionAllFilter, regionUKFilter, regionUSFilter, regionEUFilter, regionAsiaFilter = null;
	CheckBox useGPS, downloadTeamImages = null;
	@Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settingslanding);
        settings = getSharedPreferences("Airbana", 0);
        
        regionAllFilter = (RadioButton) findViewById(R.id.regionAll);
        regionUKFilter = (RadioButton) findViewById(R.id.regionUK);
        regionUSFilter = (RadioButton) findViewById(R.id.regionUS);
        regionEUFilter = (RadioButton) findViewById(R.id.regionEU);
        regionAsiaFilter = (RadioButton) findViewById(R.id.regionAsia);
        useGPS = (CheckBox) findViewById(R.id.useGPSCheckBox);
        
        if(settings.getBoolean("useGPS", false) == true)
        	useGPS.setChecked(true);
        
        switch(settings.getInt("region", 0))
        {
	        case 0:
	        {
	        	regionAllFilter.setChecked(true);
	        }
	        break;
	        
	        case 1:
	        {
	        	regionUKFilter.setChecked(true);
	        }
	        break;
	        
	        case 2:
	        {
	        	regionEUFilter.setChecked(true);
	        }
	        break;
	        
	        case 3:
	        {
	        	regionUSFilter.setChecked(true);
	        }
	        break;
	        
	        case 4:
	        {
	        	regionAsiaFilter.setChecked(true);
	        }
	        break;
        }
        
        Button LoginButton = (Button) findViewById(R.id.saveSettingsButton);
        LoginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) 
            {
            	SharedPreferences.Editor editor = settings.edit();
            	
            	/*if(downloadTeamImages.isChecked() == true)
            	{
            		editor.putBoolean("downloadTeamImages", true);
            	}
            	else
            	{
            		editor.putBoolean("downloadTeamImages", false);
            	}*/
            	
            	if(useGPS.isChecked() == true)
            	{
            		editor.putBoolean("useGPS", true);
            	}
            	else
            	{
            		editor.putBoolean("useGPS", false);
            	}
            	
            	if(regionAllFilter.isChecked() == true)
            	{
            		editor.putInt("region", 0);
            	}
            	else if(regionUKFilter.isChecked() == true)
            	{
            		editor.putInt("region", 1);
            	}
            	else if(regionEUFilter.isChecked() == true)
            	{
            		editor.putInt("region", 2);
            	}
            	else if(regionUSFilter.isChecked() == true)
            	{
            		editor.putInt("region", 3);
            	}
            	else if(regionAsiaFilter.isChecked() == true)
            	{
            		editor.putInt("region", 4);
            	}
            	else
            	{
            		editor.putInt("region", 0);
            	}
            	
            	
                editor.commit();
                finish();
            }
        });
        	
        
        /*EditText AcctCodeEditText = (EditText) findViewById(R.id.AcctCodeInput);
        EditText EmailEditText = (EditText) findViewById(R.id.EmailInput);
        EditText PasswordEditText = (EditText) findViewById(R.id.PasswordInput);
        CheckBox Remember = (CheckBox) findViewById(R.id.RememberDetailsCheckBox);
        
        AcctCodeEditText.setText(settings.getString("AcctCode", ""));
        EmailEditText.setText(settings.getString("Email", ""));
        PasswordEditText.setText(settings.getString("Password", ""));
        Remember.setChecked(settings.getBoolean("Remember", false));*/
    }
}
