package com.gmail.tantraamsha.in.findmehere;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
 
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

 
public class FindMeHereWidgetProvider extends AppWidgetProvider implements
GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener,
com.google.android.gms.location.LocationListener{
	DateFormat df = new SimpleDateFormat("hh:mm:ss");
  public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

      
      final int N = appWidgetIds.length;
      
      Log.i("ExampleWidget",  "Updating widgets " + Arrays.asList(appWidgetIds));
   
      // Perform this loop procedure for each App Widget that belongs to this
      // provider
      for (int i = 0; i < N; i++) {
        int appWidgetId = appWidgetIds[i];
   
        // Create an Intent to launch ExampleActivity
        Intent intent = new Intent(context, FindMeHere.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
   
        // Get the layout for the App Widget and attach an on-click listener
        // to the button
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.activity_find_me_here_appwidget);
        views.setOnClickPendingIntent(R.id.button1, pendingIntent);
        views.setOnClickPendingIntent(R.id.button, pendingIntent);
        // To update a label
        views.setTextViewText(R.id.widget1label, df.format(new Date()));
   
        // Tell the AppWidgetManager to perform an update on the current app
        // widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
      }
  }
@Override
public void onLocationChanged(Location arg0) {
	// TODO Auto-generated method stub
	
}
@Override
public void onConnectionFailed(ConnectionResult arg0) {
	// TODO Auto-generated method stub
	
}
@Override
public void onConnected(Bundle arg0) {
	// TODO Auto-generated method stub
	
}
@Override
public void onDisconnected() {
	// TODO Auto-generated method stub
	
}
}
