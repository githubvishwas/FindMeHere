package com.gmail.tantraamsha.in.findmehere;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

//import com.example.android.location.R;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import com.gmail.tantraamsha.in.findmehere.R;

@SuppressLint("ResourceAsColor")
public class FindMeHere extends Activity {
	public static final int MENU_LOCATION = Menu.FIRST;
	public static final int MENU_INTERNET = Menu.FIRST + 1;
	public static final int MENU_ABOUT = Menu.FIRST + 2;
	private LocationManager mLocationManager;
	private Boolean foundLoc = false; 
	private Boolean mIsGPS;

	private Geocoder mGeocoder;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_find_me_here);
		mGeocoder = new Geocoder(FindMeHere.this, Locale.ENGLISH);
	}
    @Override
    protected void onStop() {
        super.onStop();
        if ( mLocationManager != null) {
        	mLocationManager.removeUpdates(mlistener);
        }
    }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_find_me_here, menu);
		menu.clear();
		menu.add(Menu.NONE, MENU_LOCATION, Menu.NONE, "Location");
		// There is some issue in bringing up mobile network options now
		menu.add(Menu.NONE, MENU_INTERNET, Menu.NONE, "Internet");
		menu.add(Menu.NONE, MENU_ABOUT, Menu.NONE, "About");
		return true;
	}

	@Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case MENU_LOCATION:
            	startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
            return true;
            case MENU_INTERNET:
            	startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS ), 0);
            	return true;
            case MENU_ABOUT:
				PackageInfo pInfo = null;
				try {
					pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
				} catch (NameNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            	String version = pInfo.versionName;
            	AlertDialog alertDialog = new AlertDialog.Builder(
	    			FindMeHere.this).create();
            	// Setting Dialog Title
            	alertDialog.setTitle("About");
            	
            	// Setting Dialog Message
            	String aboutDetail = String.format("Find Me Here \n%s", version);
            	alertDialog.setMessage(aboutDetail);
	        
            	// Setting OK Button
            	alertDialog.setButton(1,"OK", new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int which) {
	                // Write your code here to execute after dialog closed
	                	//FindMeHere.this.finish() ;
	                }
	        });
	        alertDialog.show();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
	private boolean haveNetworkConnection() {
	    boolean haveConnectedWifi = false;
	    boolean haveConnectedMobile = false;

	    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo[] netInfo = cm.getAllNetworkInfo();
	    for (NetworkInfo ni : netInfo) {
	        if (ni.getTypeName().equalsIgnoreCase("WIFI"))
	            if (ni.isConnected())
	                haveConnectedWifi = true;
	        if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
	            if (ni.isConnected())
	                haveConnectedMobile = true;
	    }
	    return haveConnectedWifi || haveConnectedMobile;
	}

	@Override
	protected void onStart() {
		super.onStart();

	    // This verification should be done during onStart() because the system calls
	    // this method when the user returns to the activity, which ensures the desired
	    // location provider is enabled each time the activity resumes from the stopped state.
		
	    LocationManager locationManager =
	            (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	    
	    final boolean wirelessnetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

	    //final boolean gpsEnabled = false ;
	    final boolean internetOn = haveNetworkConnection();
	    if (!wirelessnetworkEnabled && !internetOn ) {
	        // Build an alert dialog here that requests that the user enable
	        // the location services, then when the user clicks the "OK" button,
	        // call enableLocationSettings()
	    	AlertDialog alertDialog = new AlertDialog.Builder(
	    			FindMeHere.this).create();
	    	// Setting Dialog Title
	        alertDialog.setTitle("Internet and GPS not enabled alert");
	        
	     // Setting Dialog Message
	        alertDialog.setMessage("Please enable GPS (at least wireless network) before running this App. Your internet connection is also not on, this will give you only your longitude and latitude of last known location. To get proper updated address, switch on your internet.");
	        
	        // Setting OK Button
	        alertDialog.setButton(1,"OK", new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int which) {
	                // Write your code here to execute after dialog closed
	                	FindMeHere.this.finish() ;
	                }
	        });
	        

	        // Showing Alert Message
	        alertDialog.show();
	    } else if (!internetOn) {
	    	AlertDialog alertDialog = new AlertDialog.Builder(
	    			FindMeHere.this).create();
	    	// Setting Dialog Title
	        alertDialog.setTitle("Internet not enabled alert");
	        
	     // Setting Dialog Message
	        alertDialog.setMessage("Your internet connection is not on, this will give you only your longitude and latitude. To get the address, switch on your internet.");
	        
	        // Setting OK Button
	        alertDialog.setButton(1,"OK", new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int which) {
	                // Write your code here to execute after dialog closed
	                	//FindMeHere.this.finish() ;
	                }
	        });
	        // Showing Alert Message
	        alertDialog.show();
	    }else if (!wirelessnetworkEnabled ) {
	    	AlertDialog alertDialog = new AlertDialog.Builder(
	    			FindMeHere.this).create();
	    	// Setting Dialog Title
	        alertDialog.setTitle("GPS not enabled alert");
	        
	     // Setting Dialog Message
	        alertDialog.setMessage("Please enable GPS (at least wireless network) before running this App");
	        
	        // Setting OK Button
	        alertDialog.setButton(1,"OK", new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int which) {
	                // Write your code here to execute after dialog closed
	                	FindMeHere.this.finish() ;
	                }
	        });
	        // Showing Alert Message
	        alertDialog.show();
	    }
	    //TextView tv =  (TextView) findViewById(R.id.txtLocation);
	    //tv.setBackgroundColor(R.color.white);
	    if (wirelessnetworkEnabled ) {
	    	mLocationManager = locationManager;
		    requestUpdatesFromProvider(LocationManager.NETWORK_PROVIDER, -1);
	    }
	    
	}
	//private void enableLocationSettings() {
	//    Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
	//    startActivity(settingsIntent);
	//}
	public void UseGPSSatellites (View view) {
		CheckBox cb =  (CheckBox) findViewById(R.id.chkUseGPSSatellite);
		mLocationManager =
	            (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		if (cb.isChecked()) {
			
		    
		    final boolean gpsEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		    if (!gpsEnabled) {
		    	AlertDialog alertDialog = new AlertDialog.Builder(
		    			FindMeHere.this).create();
		    	// Setting Dialog Title
		        alertDialog.setTitle("GPS using satellite not enabled alert");
		        
		     // Setting Dialog Message
		        alertDialog.setMessage("Please enable GPS using satellite to use this");
		        
		        // Setting OK Button
		        alertDialog.setButton(1,"OK", new DialogInterface.OnClickListener() {
		                public void onClick(DialogInterface dialog, int which) {
		                // Write your code here to execute after dialog closed
		                	CheckBox cb1 =  (CheckBox) findViewById(R.id.chkUseGPSSatellite);
		                	cb1.setChecked(false);
		                }
		        });
		        // Showing Alert Message
		        alertDialog.show();
		    } else {
		    	Toast.makeText(this, "GPS Satellite may give old or no location if proper sky view is not available.", Toast.LENGTH_LONG).show();
			    requestUpdatesFromProvider(LocationManager.GPS_PROVIDER, -1);
		    }
		} else {
			requestUpdatesFromProvider(LocationManager.NETWORK_PROVIDER, -1);
		}
		
	}
	public void sendMail(View view) {
		if (!foundLoc) {
			Toast.makeText(this, "Please click on Find Me Here button before sending mail.", Toast.LENGTH_LONG).show();
		}
		Intent intent=new Intent(Intent.ACTION_SEND);
		//String[] recipients={"xyz@gmail.com"};
		//intent.putExtra(Intent.EXTRA_EMAIL, recipients);
		intent.putExtra(Intent.EXTRA_SUBJECT,"My location from Find Me Here App");
		TextView tv =  (TextView) findViewById(R.id.txtLocation);
		intent.putExtra(Intent.EXTRA_TEXT,String.format("%s\n\nI found this by using the \"Find Me Here\" App from Play Store!!",tv.getText()));
		//intent.putExtra(Intent.EXTRA_CC,"ghi");
		//intent.setType("text/html");
		// this will ensure that directly g mail opens and other options dont show up - vishwas
		intent.setType("message/rfc822");
		startActivity(Intent.createChooser(intent, "Send mail")); 
	}
	public void sendSMS(View view) {
		if (!foundLoc) {
			Toast.makeText(this, "Please click on Find Me Here button before sending sms.", Toast.LENGTH_LONG).show();
		}
		Uri smsUri = Uri.parse("tel:123456");
		Intent intent = new Intent(Intent.ACTION_VIEW, smsUri);
		TextView tv =  (TextView) findViewById(R.id.txtLocation);
		intent.putExtra("sms_body", String.format("%s\n\nI found this using the \"Find Me Here\" App , which can be downloaded from playstore!",tv.getText()));
		intent.setType("vnd.android-dir/mms-sms"); 
		startActivity(intent);
	}
	private Location requestUpdatesFromProvider(final String provider, final int errorResId) {
        Location location = null;
        if (mLocationManager.isProviderEnabled(provider)) {
            //mLocationManager.requestLocationUpdates(provider, 10, 10, mlistener);
        	Looper looper = null;
        	mLocationManager.requestSingleUpdate(provider, mlistener, looper);
            location = mLocationManager.getLastKnownLocation(provider);
        } else {
            Toast.makeText(this, errorResId, Toast.LENGTH_LONG).show();
        }
        return location;
    }
	private final LocationListener mlistener = new LocationListener() {

	    @Override
	    public void onLocationChanged(Location location) {
	        // A new location update is received.  Do something useful with it.  In this case,
	        // we're sending the update to a handler which then updates the UI with the new
	        // location.
	    	//mLatitude = location.getLatitude();
	    	//mLongitude = location.getLongitude();
	        }
	    @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
	};
	
	public void sendMessage_bak(View view)  {
		TextView tv =  (TextView) findViewById(R.id.txtLocation);
		CheckBox cb =  (CheckBox) findViewById(R.id.chkUseGPSSatellite);
		//tv.setText("Fetching location ...");
		//tv.refreshDrawableState();
		//view.refreshDrawableState();
		tv.setClickable(true);
		tv.setMovementMethod(LinkMovementMethod.getInstance());
		LocationManager locationManager =
		        (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		LocationProvider provider = null ;
		// Retrieve a list of location providers that have fine accuracy, no monetary cost, etc
		//Criteria criteria = new Criteria();
		//criteria.setAccuracy(Criteria.ACCURACY_FINE);
		//criteria.setCostAllowed(false);

		String providerName = LocationManager.NETWORK_PROVIDER ;
		if (cb.isChecked()) {
			providerName = LocationManager.GPS_PROVIDER ;
		}
		
		provider =
        locationManager.getProvider(providerName);
		if (provider == null) {
			tv.setText("Could not get location, check GPS/Internet connection ! \n There may be a temporary loss of connection, please try again");
			return ;
		}
		
		Location loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		if (cb.isChecked()) {
			loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		}
		//double altitude = loc.getAltitude() ;
		//loc = requestUpdatesFromProvider(providerName, -1);
		if (loc == null) {
			tv.setText("Could not get location, please check GPS/Internet connection !");
			return ;
		}
		double latitude = loc.getLatitude() ;
		double longitude = loc.getLongitude() ;
		String googleMapURL = String.format(Locale.ENGLISH,"http://maps.google.com/maps?q=%f,%f&z=18",latitude,longitude);
		//googleMapURL = Html.fromHtml(googleMapURL)
		Geocoder geocoder;
		List<Address> addresses = null;
		geocoder = new Geocoder(this, Locale.ENGLISH);
		
		try {
			addresses = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
        	tv.setText(String.format("Could not resolve location. Providing last known location \nLongitude: %s\nLatitude: %s\nFind me on google map using the following link \n%s \nSorry, check internet/GPS connection ! \n There may be a temporary loss of connection, please try again",longitude,latitude,googleMapURL));
            e.printStackTrace();
        }
		
		if (addresses == null) {
			//tv.setText(String.format("Latitude: %s \nLongitude: %s \nSorry, Could not resolve address !",Double.toString(latitude),Double.toString(longitude)));
		} else {
			String address = addresses.get(0).getAddressLine(0);
			String city = addresses.get(0).getAddressLine(1);
			String country = addresses.get(0).getAddressLine(2);
			tv.setText(String.format("My Nearest Landmark: \n%s \n%s \n%s \nGoogle map location link \n%s ", address, city, country,googleMapURL ));
		}
	}	
	public void Settings_cb(View view) {
		//Toast.makeText(this, "This is a very simple app and has no complex settings, so enjoy !! \nVishwas", Toast.LENGTH_LONG).show();
	}
	public void sendMessage(View view)  {
		//final boolean internetOn = haveNetworkConnection();
		foundLoc = true;
		final boolean gpsEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		final boolean wirelessnetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		TextView tv =  (TextView) findViewById(R.id.txtLocation);
		CheckBox cb =  (CheckBox) findViewById(R.id.chkUseGPSSatellite);
		tv.setClickable(true);
		tv.setMovementMethod(LinkMovementMethod.getInstance());
		mIsGPS = false;
		if (cb.isChecked()) {
			mIsGPS = true;
		}
		if (!gpsEnabled &&  !wirelessnetworkEnabled) {
			Toast.makeText(this, "No location service on!", Toast.LENGTH_LONG).show();
			return;
		}
		if (cb.isChecked() && !gpsEnabled) {
			Toast.makeText(this, "GPS service not on!", Toast.LENGTH_LONG).show();
			return;
		}
		GetLocation getLocation = new GetLocation();
		if (getLocation != null) {
			getLocation.mIsGPS =  mIsGPS;
			getLocation.mLocationManager = mLocationManager ;
			getLocation.mGeocoder = new Geocoder(this, Locale.ENGLISH);
			getLocation.execute("");
		} 
		
	}
	private class GetLocation extends AsyncTask<String, Integer, String> {
		
		private boolean mIsGPS ;
		private LocationManager mLocationManager ;
		private Geocoder mGeocoder;
		private String mText;
		private ProgressDialog mProgressDialog;
		
		@Override
		//protected String doInBackground(String... arg0) {
		//	publishProgress(50);
		//	return "";
		//}
		
		protected String doInBackground(String... arg0) {
			mText = "";
			publishProgress(50);
			LocationManager locationManager = this.mLocationManager ;
			LocationProvider provider = null ;
			
			String providerName = LocationManager.NETWORK_PROVIDER ;
			if (this.mIsGPS) {
				providerName = LocationManager.GPS_PROVIDER ;
			}
			
			provider =
	        locationManager.getProvider(providerName);
			if (provider == null) {
				mText = "Could not get location, check GPS/Internet connection ! \n There may be a temporary loss of connection, please try again" ;
				publishProgress(100);
				return "";
			}
			
			Location loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			if (this.mIsGPS) {
				loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			}
			//double altitude = loc.getAltitude() ;
			//loc = requestUpdatesFromProvider(providerName, -1);
			if (loc == null) {
				mText = "Could not get location, please check GPS/Internet connection !" ;
				publishProgress(100);
				return "";
			}
			double latitude = loc.getLatitude() ;
			double longitude = loc.getLongitude() ;
			String googleMapURL = String.format(Locale.ENGLISH,"http://maps.google.com/maps?q=%f,%f&z=18",latitude,longitude);
			//googleMapURL = Html.fromHtml(googleMapURL)
			Geocoder geocoder;
			List<Address> addresses = null;
			geocoder = mGeocoder ;
			
			try {
				addresses = geocoder.getFromLocation(latitude, longitude, 1);
	        } catch (IOException e) {
	        	//tv.setText(String.format("Could not resolve location. Providing last known location \nLongitude: %s\nLatitude: %s\nFind me on google map using the following link \n%s \nSorry, check internet/GPS connection ! \n There may be a temporary loss of connection, please try again",longitude,latitude,googleMapURL));
	        	mText = String.format("Could not resolve location. Providing last known location \nLongitude: %s\nLatitude: %s\nGoogle map location link \n%s \nSorry, check internet/GPS connection ! \n There may be a temporary loss of connection, please try again",longitude,latitude,googleMapURL);
	        	publishProgress(100);
	            e.printStackTrace();
	        }
			
			if (addresses == null) {
				//tv.setText(String.format("Latitude: %s \nLongitude: %s \nSorry, Could not resolve address !",Double.toString(latitude),Double.toString(longitude)));
			} else {
				String address = addresses.get(0).getAddressLine(0);
				String city = addresses.get(0).getAddressLine(1);
				String country = addresses.get(0).getAddressLine(2);
				mText = String.format("Nearest Landmark:\n%s \n%s \n%s \nGoogle map location link \n%s ", address, city, country,googleMapURL );
				publishProgress(100);
				//tv.setText(String.format("Nearest Google Landmark Address: %s \n%s \n%s \nFind me on google map using the following link \n%s ", address, city, country,googleMapURL ));
			}
			return "";
		}
		@Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	        mProgressDialog = new ProgressDialog(FindMeHere.this);
			mProgressDialog.setMessage("Fetching location ..");
			mProgressDialog.setIndeterminate(true);
			mProgressDialog.setCancelable(false);
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	        mProgressDialog.show();
	        
	    }

	    @Override
	    protected void onProgressUpdate(Integer... progress) {
	        super.onProgressUpdate(progress);
	        
	        //mProgressDialog.setProgress(progress[0]);
	        TextView tv =  (TextView) findViewById(R.id.txtLocation);
	        tv.setText(mText);
	    }
	    @Override
	    protected void onPostExecute(String str) {
	        super.onPostExecute(str);
	        mProgressDialog.dismiss();
	    }
	}
}
