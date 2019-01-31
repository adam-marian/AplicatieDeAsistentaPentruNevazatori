package com.example.transportdisplay;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import java.util.Objects;

import static com.example.transportdisplay.App.CHANNEL_1_ID;

public class NotificationService extends Service {

    private float[] distance = new float[2];
    private NotificationManagerCompat notificationManager;

    @Override
    public IBinder onBind(Intent intent) {
        return  null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        //Get values passed with the intent
        final double latitudine = Double.parseDouble(intent.getStringExtra("lat"));
        final double longitudine = Double.parseDouble(intent.getStringExtra("lng"));
        final double radius = Double.parseDouble(intent.getStringExtra("raza"));

        //Create reference to database
        notificationManager = NotificationManagerCompat.from(this);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Locatie");

        ref.addValueEventListener(new ValueEventListener() {
            @Override

            //look for new values added
            public void onDataChange(DataSnapshot dataSnapshot) {

                //get person coordinates from database
                double lat = Double.parseDouble(dataSnapshot.child("Latitudine").getValue().toString());
                double lng = Double.parseDouble(dataSnapshot.child("Longitudine").getValue().toString());

                //Check if the person pressed the SOS button
                String str = dataSnapshot.child("SOS").getValue().toString();


                //Determine if the person in inside the defined perimeter(circle)
                Location.distanceBetween( lat, lng, latitudine, longitudine, distance);

                //Set the intent for notification tap
                Intent activityIntent = new Intent(getApplicationContext(), DisplayActivity.class);
                PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(),
                        0, activityIntent, 0);

                Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.large_icon);

                //If the person is outside the circle,send notification
                if( distance[0] > radius  ){
                    String title = "WARNING";
                    String message = "Perimeter breach!";
                    Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_1_ID)
                            .setSmallIcon(R.drawable.ic_error)
                            .setContentTitle(title)
                            .setContentText(message)
                            .setLargeIcon(largeIcon)
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                            .setColor(Color.RED)
                            .setContentIntent(contentIntent)
                            .setAutoCancel(true)
                            .build();
                    notificationManager.notify(1, notification);
                }

                //If the person pressed the SOS button,send notification
                if(Objects.equals(str,"True")){
                    String title = "WARNING";
                    String message = "SOS Button pressed!";

                    Notification notification2 = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_1_ID)
                            .setSmallIcon(R.drawable.ic_error)
                            .setContentTitle(title)
                            .setContentText(message)
                            .setLargeIcon(largeIcon)
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                            .setColor(Color.RED)
                            .setContentIntent(contentIntent)
                            .setAutoCancel(true)
                            .build();
                    notificationManager.notify(2, notification2);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        return START_STICKY;
    }

}






