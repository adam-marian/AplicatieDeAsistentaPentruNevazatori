package com.example.transportdisplay;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import android.view.View;
import android.widget.EditText;
import java.util.ArrayList;

public class PicturesActivity extends AppCompatActivity {


    ArrayList<String> ImgUrl= new ArrayList<>();
    RecyclerView recyclerView;
    LinearLayoutManager Manager;
    Adapter adapter;
    private DatabaseReference mDatabase;
    private int numar = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pictures_display);
    }

    public void buttonAction(View view) {

        //Get number of pictures
        EditText currentEditText = findViewById(R.id.numarPoze);
        if(currentEditText.getText().toString().trim().length() != 0) {
            numar = Integer.parseInt(currentEditText.getText().toString());
        }
        //set reference to database
        ImgUrl.clear();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = mDatabase.child("downloadURL");

        //set EventListener to check for new values added; get the links and add them to ImgUrl
        ref.orderByKey().limitToLast(numar).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getValue() != null) {
                    Log.d("dataSnapshot", dataSnapshot.getValue().toString());
                    ImgUrl.add(dataSnapshot.getValue().toString());
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });


        //create adapter and send ImgUrl as input
        this.recyclerView = findViewById(R.id.recyclerView);
        Manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(Manager);
        adapter = new Adapter(ImgUrl, this);
        recyclerView.setAdapter(adapter);
    }

}
