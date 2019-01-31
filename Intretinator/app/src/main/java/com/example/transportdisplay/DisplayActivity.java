package com.example.transportdisplay;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class DisplayActivity extends FragmentActivity implements OnMapReadyCallback {

    private HashMap<String, Marker> mMarkers = new HashMap<>();
    private GoogleMap mMap;
    private float[] distance = new float[2];
    public double test = 0.0;
    private int numar = 500;
    private EditText currentEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Toast.makeText(getBaseContext(), "Place circle on map.", Toast.LENGTH_LONG).show();
        currentEditText = findViewById(R.id.radius);


        //Open new activity when the button is pressed
        TextView pictures = findViewById(R.id.pictures);
        pictures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent picturesIntent = new Intent(DisplayActivity.this, PicturesActivity.class);
                startActivity(picturesIntent);
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMaxZoomPreference(16);
        subscribeToUpdates();
    }



    //Get values from database and look for new values added
    private void subscribeToUpdates() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Locatie");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                setMarker(dataSnapshot);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    private void setMarker(final DataSnapshot dataSnapshot) {
        String key = dataSnapshot.getKey();
        final HashMap<Double,Marker> hashMapMarker = new HashMap<>();
        hashMapMarker.clear();
        final List<Circle> myList = new ArrayList<>();
        myList.clear();

        //get person coordinates from database
        final double lat = Double.parseDouble(dataSnapshot.child("Latitudine").getValue().toString());
        final double lng = Double.parseDouble(dataSnapshot.child("Longitudine").getValue().toString());

        //Add marker for the current position of the person
        LatLng location = new LatLng(lat, lng);
        if (!mMarkers.containsKey(key)) {
            mMarkers.put(key, mMap.addMarker(new MarkerOptions().title(location.toString()).position(location)));
        } else {
            mMarkers.get(key).setPosition(location);
        }
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : mMarkers.values()) {
            builder.include(marker.getPosition());
        }
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 300));


        //Action to perform when the map is clicked
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {
                //delete previous marker

                if (!hashMapMarker.isEmpty()) {
                    Marker marker = hashMapMarker.get(test);
                    marker.remove();
                    hashMapMarker.remove(test);
                }
                hashMapMarker.clear();
                //delete previous circle
                for (Circle myCircle : myList) {
                    myCircle.remove();
                }
                myList.clear();

                // Creating a marker
                MarkerOptions markerOptions = new MarkerOptions();

                // Setting the position for the marker
                markerOptions.position(latLng);

                // Setting the title and the color for the marker
                markerOptions.title(latLng.latitude + " : " + latLng.longitude);
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                test = latLng.latitude;

                //Check if user set radius value and read it
                if(currentEditText.getText().toString().trim().length() != 0) {
                    numar = Integer.parseInt(currentEditText.getText().toString());
                }
                Log.d("raza", Integer.toString(numar));

                //Creating intent and sending center coordinates to NotificationService
                Intent intent = new Intent(getApplicationContext(), NotificationService.class);
                intent.putExtra("lat",String.valueOf(latLng.latitude));
                intent.putExtra("lng", String.valueOf(latLng.longitude));
                intent.putExtra("raza", String.valueOf(numar));
                startService(intent);



                //Creating circle
                Circle circle = mMap.addCircle(new CircleOptions().center(new LatLng(latLng.latitude,latLng.longitude)).radius(numar).strokeColor(Color.BLUE));
                circle.setVisible(true);
                myList.add(circle);

                //Determine if the person in inside the defined perimeter(circle)
                Location.distanceBetween( lat, lng, circle.getCenter().latitude, circle.getCenter().longitude, distance);
                if( distance[0] > circle.getRadius()  ){
                    Toast.makeText(getBaseContext(), "Outside", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getBaseContext(), "Inside", Toast.LENGTH_SHORT).show();
                }


                // Animating to the touched position
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                // Placing a marker on the touched position
                Marker marker2 = mMap.addMarker(markerOptions);
                hashMapMarker.put(test,marker2);
            }
        });


    }

}
