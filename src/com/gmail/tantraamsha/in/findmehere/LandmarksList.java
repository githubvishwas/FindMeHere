package com.gmail.tantraamsha.in.findmehere;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

	public class LandmarksList extends Activity {
		private String jsonString;
	    /** Called when the activity is first created. */
	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.landmarkslist);
	 
	     // Get ListView object from xml
	        ListView listView = (ListView) findViewById(R.id.landmarkslist);
            
	        
            // Define a new Adapter
            // First parameter - Context
            // Second parameter - Layout for the row
            // Third parameter - ID of the TextView to which the data is written
            // Forth - the Array of data
            Intent ii = getIntent();
            // Receiving the Data
            jsonString = ii.getStringExtra("JSONString");
            
            JSONObject jsonObject;
            int maxRes=100;
	        final String[] names= new String[maxRes];
	        final String[] venuesId= new String[maxRes];
			try {
				jsonObject = new JSONObject(jsonString);
				JSONObject reponseJson= jsonObject.getJSONObject("response");
		        JSONArray arrayVenues= reponseJson.getJSONArray("venues");
		        
		        if (maxRes > arrayVenues.length() ) {
		        	maxRes = arrayVenues.length();
		        }
		        
		        for (int i=0;i< maxRes; i++)
		        {
		            JSONObject jObj= arrayVenues.getJSONObject(i);
		            JSONObject location = jObj.getJSONObject("location");
		            names[i]= jObj.getString("name") + " (Dist: " + location.getString("distance") +" m)";
		            venuesId[i]= jObj.getString("id");
		        }
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        //resize the array
			final String[] names1= new String[maxRes];
			for (int i=0;i< maxRes; i++)
			{
				names1[i] = names[i];
			}
	        
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
              android.R.layout.simple_list_item_1, names1);
    
    
            // Assign adapter to ListView
            listView.setAdapter(adapter); 
            
            OnItemClickListener itemClickListener = new OnItemClickListener() {
	            @Override
	            public void onItemClick(AdapterView<?> parent, View container, int position, long id) {
	                
	            	JSONObject jsonObject;
	                
	    	        String address;
	    	        
	    			try {
	    				jsonObject = new JSONObject(jsonString);
	    				JSONObject reponseJson= jsonObject.getJSONObject("response");
	    		        JSONArray arrayVenues= reponseJson.getJSONArray("venues");
	    		        
	    		        JSONObject jObj= arrayVenues.getJSONObject(position);
	    		       
	    		        JSONObject location = jObj.getJSONObject("location");
	    		        
	    		        double lat = location.getDouble("lat");
	    		        double lng = location.getDouble("lng");
	    		        address= jObj.getString("name") + " (Dist: " + location.getString("distance") +" m)\n";
	    		        if (location.has("address")) {
	    		        	address = address + location.optString("address","No address available!") + "\n";
	    		        }
	    		        if (location.has("crossStreet")) {
	    		        	address = address + location.optString("crossStreet","") + "\n";
	    		        }
	    		        if (location.has("city")) {
	    		        	address = address + location.optString("city","") + "\n";
	    		        }
	    		        if (location.has("postalCode")) {
	    		        	address = address + location.optString("postalCode","") + "\n";
	    		        }
	    		        if (jObj.has("contact")) {
	    		        	if (jObj.getString("contact") != "{}" || jObj.getString("contact") != "") {
	    		        		address = address + jObj.optString("contact"," ") + "\n";
	    		        	}
	    		        }
	    		        
	    		        Intent resultIntent = new Intent();
	    		        String googleMapURL = String.format(Locale.ENGLISH,"http://maps.google.com/maps?q=%f,%f&z=18",lat,lng);
	    		        String addText = String.format("Landmark address:\n" + address + "\nGoogle map location link \n" + googleMapURL );
	    		        resultIntent.putExtra("landmark", addText);
		    		     // TODO Add extras or a data URI to this intent as appropriate.
		    		     setResult(Activity.RESULT_OK, resultIntent);
		    		     finish();
	    			} catch (JSONException e) {
	    				// TODO Auto-generated catch block
	    				e.printStackTrace();
	    				Toast.makeText(parent.getContext(), "No address available!", Toast.LENGTH_SHORT).show();
	    			}
	            }


	        };
	 
	        // Setting the item click listener for the listview
	        listView.setOnItemClickListener(itemClickListener);
	    }
	
}
