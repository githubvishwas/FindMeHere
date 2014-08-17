package com.gmail.tantraamsha.in.findmehere;
import com.google.android.gms.ads.*;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

//import com.example.android.location.R;
import com.google.android.gms.ads.*;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.support.v4.app.FragmentActivity;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.gmail.tantraamsha.in.findmehere.R;



@SuppressLint("ResourceAsColor")
public class FindMeHere extends FragmentActivity implements
GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener,
com.google.android.gms.location.LocationListener {
	public static final int MENU_LOCATION = Menu.FIRST;
	public static final int MENU_INTERNET = Menu.FIRST + 1;
	public static final int MENU_ABOUT = Menu.FIRST + 2;
	private LocationManager mLocationManager;
	private Boolean foundLoc = false; 

	private InterstitialAd interstitial;
	private AdView adView1;
	private Location mlocation = null;
	private LocationClient mLocationClient = null;
	private LocationRequest mLocationRequest;
    // Define a request code to send to Google Play services. This code is returned in Activity.onActivityResult
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_find_me_here);

		mLocationClient = new LocationClient(this, this, this);
		mLocationRequest = new LocationRequest();
		// Use high accuracy
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
	        // Set the update interval to 5 seconds
		mLocationRequest.setInterval(5000);
	        // Set the fastest update interval to 1 second
		mLocationRequest.setFastestInterval(1000);
		// Create the interstitial.
	    interstitial = new InterstitialAd(this);
	    interstitial.setAdUnitId("ca-app-pub-9439183503098916/3226250380");
	    interstitial.setAdListener(new AdListener(){
	          public void onAdLoaded(){
	               displayInterstitial();
	          }
	});
	 // Look up the AdView as a resource and load a request.
	    adView1 = (AdView) this.findViewById(R.id.adView);
	    AdRequest adRequest = new AdRequest.Builder()
	    		.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
	            .addTestDevice("00946b999b4be935")
	            .build();
	    adView1.loadAd(adRequest);

	}
	@Override
	  public void onResume() {
	    super.onResume();
	    if (adView1 != null) {
	    	adView1.resume();
	    }
	  }

	  @Override
	  public void onPause() {
	    if (adView1 != null) {
	    	adView1.pause();
	    }
	    super.onPause();
	  }

	  /** Called before the activity is destroyed. */
	  @Override
	  public void onDestroy() {
	    // Destroy the AdView.
	    if (adView1 != null) {
	    	adView1.destroy();
	    }
	    super.onDestroy();
	  }
    @Override
    protected void onStop() {
        super.onStop();

     // Disconnecting the client invalidates it.
        mLocationClient.disconnect();
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
	// Invoke displayInterstitial() when you are ready to display an interstitial.
	  public void displayInterstitial() {
	    if (interstitial.isLoaded()) {
	      interstitial.show();
	    }
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
            	alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
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
		
		mLocationManager =
	            (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Connect the client.
        mLocationClient.connect();
	    final boolean wirelessnetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
	    final boolean gpsEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	    
	    //final boolean gpsEnabled = false ;
	    final boolean internetOn = haveNetworkConnection();
	    if (!wirelessnetworkEnabled && !internetOn && !gpsEnabled) {
	        // Build an alert dialog here that requests that the user enable
	        // the location services
	    	AlertDialog alertDialog = new AlertDialog.Builder(
	    			FindMeHere.this).create();
	    	// Setting Dialog Title
	        alertDialog.setTitle("Internet and GPS not enabled alert");
	        
	     // Setting Dialog Message
	        alertDialog.setMessage("Please enable GPS before running this App. Your internet connection is also not on, this will give you only your longitude and latitude of last known location. To get proper updated address, switch on your internet.");
	        alertDialog.setCancelable(false);
	        alertDialog.setCanceledOnTouchOutside(false);
	        // Setting OK Button
	        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int which) {
	                // Write your code here to execute after dialog closed
	                	FindMeHere.this.finish() ;
	                }
	        });
	        

	        // Showing Alert Message
	        alertDialog.show();
	    } 
	    if (!wirelessnetworkEnabled && !gpsEnabled) {
	    	AlertDialog alertDialog = new AlertDialog.Builder(
	    			FindMeHere.this).create();
	    	// Setting Dialog Title
	        alertDialog.setTitle("GPS not enabled alert");
	        
	     // Setting Dialog Message
	        alertDialog.setMessage("Please enable GPS before running this App");
	        alertDialog.setCancelable(false);
	        alertDialog.setCanceledOnTouchOutside(false);
	        // Setting OK Button
	        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int which) {
	                // Write your code here to execute after dialog closed
	                	FindMeHere.this.finish() ;
	                }
	        });
	        // Showing Alert Message
	        alertDialog.show();
	    } 
	    if (!internetOn) {
	    	AlertDialog alertDialog = new AlertDialog.Builder(
	    			FindMeHere.this).create();
	    	// Setting Dialog Title
	        alertDialog.setTitle("Internet not enabled alert");
	        
	     // Setting Dialog Message
	        alertDialog.setMessage("Your internet connection is not on, this will give you only your longitude and latitude. To get the address, switch on your internet.");
	        
	        // Setting OK Button
	        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int which) {
	                // Write your code here to execute after dialog closed
	                	//FindMeHere.this.finish() ;
	                }
	        });
	        // Showing Alert Message
	        alertDialog.show();
	    }
	    if (!gpsEnabled) {
	    	AlertDialog alertDialog = new AlertDialog.Builder(
	    			FindMeHere.this).create();
	    	// Setting Dialog Title
	        alertDialog.setTitle("GPS not enabled");
	        
	     // Setting Dialog Message
	        alertDialog.setMessage("GPS is not enabled. You will get a very rough location based on nearest mobile network tower!");
	        
	        // Setting OK Button
	        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int which) {
	                // Write your code here to execute after dialog closed
	                	//FindMeHere.this.finish() ;
	                }
	        });
	        // Showing Alert Message
	        alertDialog.show();
	    }
	    if (!wirelessnetworkEnabled) {
	    	AlertDialog alertDialog = new AlertDialog.Builder(
	    			FindMeHere.this).create();
	    	// Setting Dialog Title
	        alertDialog.setTitle("Location by mobile network not enabled");
	        
	     // Setting Dialog Message
	        alertDialog.setMessage("Location by mobile network not enabled. You may not get any location, or a very old location if proper sky view is not avilable for GPS by satellite!");
	        
	        // Setting OK Button
	        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int which) {
	                // Write your code here to execute after dialog closed
	                	//FindMeHere.this.finish() ;
	                }
	        });
	        // Showing Alert Message
	        alertDialog.show();
	    }
	}

	public void SendWhatsApp(View view) {
		try{
		    ApplicationInfo info = getPackageManager().
		            getApplicationInfo("com.whatsapp", 0 );
		} catch( PackageManager.NameNotFoundException e ){
			Toast.makeText(this, "WhatsApp not Installed", Toast.LENGTH_SHORT)
            .show();
		    return;
		}
		if (!foundLoc) {
			Toast.makeText(this, "Please click on Find Me Here button before sending message.", Toast.LENGTH_SHORT).show();
			return;
		}
		Intent waIntent = new Intent(Intent.ACTION_SEND);
	    waIntent.setType("text/plain");
	    TextView tv =  (TextView) findViewById(R.id.txtLocation);
	    String text = String.format("%s\n\nFound location using \"Find Me Here\" App from Play Store at https://play.google.com/store/apps/details?id=com.gmail.tantraamsha.in.findmehere",tv.getText());
	    waIntent.setPackage("com.whatsapp");
	    if (waIntent != null) {
	        waIntent.putExtra(Intent.EXTRA_TEXT, text);//
	        startActivity(Intent.createChooser(waIntent, "Share with"));
			AdRequest adRequest = null;
		    if (mlocation != null) {
		    	adRequest = new AdRequest.Builder()
				.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
		        .addTestDevice("00946b999b4be935")
		        .setLocation(mlocation)
		        .build();
	        } else {
		    	adRequest = new AdRequest.Builder()
				.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
		        .addTestDevice("00946b999b4be935")
		        .build();
	        }

		    // Begin loading your interstitial.
		    interstitial.loadAd(adRequest);
	    } 
	}
	public void sendMail(View view) {
		if (!foundLoc) {
			Toast.makeText(this, "Please click on Find Me Here button before sending mail.", Toast.LENGTH_SHORT).show();
			return;
		}
		Intent intent=new Intent(Intent.ACTION_SEND);
		intent.putExtra(Intent.EXTRA_SUBJECT,"My location from Find Me Here App");
		TextView tv =  (TextView) findViewById(R.id.txtLocation);
		intent.putExtra(Intent.EXTRA_TEXT,String.format("%s\n\nFound location using \"Find Me Here\" App from Play Store at https://play.google.com/store/apps/details?id=com.gmail.tantraamsha.in.findmehere",tv.getText()));
		// this will ensure that directly g mail opens and other options dont show up - vishwas
		intent.setType("message/rfc822");
		startActivity(Intent.createChooser(intent, "Send mail")); 
		AdRequest adRequest = null;
	    if (mlocation != null) {
	    	adRequest = new AdRequest.Builder()
			.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
	        .addTestDevice("00946b999b4be935")
	        .setLocation(mlocation)
	        .build();
        } else {
	    	adRequest = new AdRequest.Builder()
			.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
	        .addTestDevice("00946b999b4be935")
	        .build();
        }

	    // Begin loading your interstitial.
	    interstitial.loadAd(adRequest);
	}
	public void sendSMS(View view) {
		if (!foundLoc) {
			Toast.makeText(this, "Please click on Find Me Here button before sending sms.", Toast.LENGTH_SHORT).show();
			return;
		}
		Uri smsUri = Uri.parse("tel:123456");
		Intent intent = new Intent(Intent.ACTION_VIEW, smsUri);
		TextView tv =  (TextView) findViewById(R.id.txtLocation);
		intent.putExtra("sms_body", String.format("%s\n\nFound location using \"Find Me Here\" App from Play Store at https://play.google.com/store/apps/details?id=com.gmail.tantraamsha.in.findmehere",tv.getText()));
		intent.setType("vnd.android-dir/mms-sms"); 
		startActivity(intent);
		AdRequest adRequest = null;
	    if (mlocation != null) {
	    	adRequest = new AdRequest.Builder()
			.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
	        .addTestDevice("00946b999b4be935")
	        .setLocation(mlocation)
	        .build();
        } else {
	    	adRequest = new AdRequest.Builder()
			.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
	        .addTestDevice("00946b999b4be935")
	        .build();
        }

	    // Begin loading your interstitial.
	    interstitial.loadAd(adRequest);
	}
	public void findMeHere_cb(View view)  {
		//final boolean internetOn = haveNetworkConnection();
		foundLoc = true;
		final boolean gpsEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		final boolean wirelessnetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		TextView tv =  (TextView) findViewById(R.id.txtLocation);

		tv.setClickable(true);
		tv.setMovementMethod(LinkMovementMethod.getInstance());
		
		if (!gpsEnabled &&  !wirelessnetworkEnabled) {
			Toast.makeText(this, "No location service on!", Toast.LENGTH_SHORT).show();
			return;
		}

		GetLocation getLocation = new GetLocation();
		if (getLocation != null) {
			getLocation.mGeocoder = new Geocoder(this, Locale.ENGLISH);
			getLocation.execute("");
		} 

	}
	private class GetLocation extends AsyncTask<String, Integer, String> {
	
		private Geocoder mGeocoder;
		private String mText;
		private ProgressDialog mProgressDialog;
		
		@Override

		protected String doInBackground(String... arg0) {
			mText = "";
			publishProgress(50);
			mlocation = mLocationClient.getLastLocation();

			if (mlocation == null) {
				mText = "Could not get location, please check GPS/Internet connection !" ;
				publishProgress(100);
				return "";
			}
			double latitude = mlocation.getLatitude() ;
			double longitude = mlocation.getLongitude() ;
			String googleMapURL = String.format(Locale.ENGLISH,"http://maps.google.com/maps?q=%f,%f&z=18",latitude,longitude);
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
			StringBuilder sb = new StringBuilder();
			if (addresses == null) {
				//tv.setText(String.format("Latitude: %s \nLongitude: %s \nSorry, Could not resolve address !",Double.toString(latitude),Double.toString(longitude)));
			} else {
				Address address = addresses.get(0);
				for (int i =0; i < address.getMaxAddressLineIndex(); i++)
					sb.append(address.getAddressLine(i)).append("\n");
				sb.append(address.getCountryName()).append("\n");
				mText = String.format("Nearest Landmark:\n" + sb.toString() + "Google map location link \n" + googleMapURL );
				publishProgress(100);				//tv.setText(String.format("Nearest Google Landmark Address: %s \n%s \n%s \nFind me on google map using the following link \n%s ", address, city, country,googleMapURL ));
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
	        TextView tv =  (TextView) findViewById(R.id.txtLocation);
	        tv.setText(mText);
	        // Look up the AdView as a resource and load a request.
		    adView1 = (AdView) findViewById(R.id.adView);
		    AdRequest adRequest = new AdRequest.Builder()
		    		.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
		            .addTestDevice("00946b999b4be935")
		            .build();
		    adView1.loadAd(adRequest);
	    }
	    @Override
	    protected void onPostExecute(String str) {
	        super.onPostExecute(str);
	        mProgressDialog.dismiss();
	    }
	}
	 /*
     * Called by Location Services when the request to connect the
     * client finishes successfully. At this point, you can
     * request the current location or start periodic updates
     */
    @Override
    public void onConnected(Bundle dataBundle) {
        // Display the connection status
        Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
        //ensuring that it gets the location
        mlocation = mLocationClient.getLastLocation();
        if (mlocation == null) {
        	mLocationClient.requestLocationUpdates(mLocationRequest, this);
        }
    }
 
    /*
     * Called by Location Services if the connection to the
     * location client drops because of an error.
     */
    @Override
    public void onDisconnected() {
        // Display the connection status
        Toast.makeText(this, "Disconnected. Please re-connect.",
                Toast.LENGTH_SHORT).show();
    }

    /*
     * Called by Location Services if the attempt to
     * Location Services fails.
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
        	Toast.makeText(this,connectionResult.getErrorCode(),Toast.LENGTH_SHORT).show();
        }
    }
	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub
		mLocationClient.removeLocationUpdates(this);
	}

}
