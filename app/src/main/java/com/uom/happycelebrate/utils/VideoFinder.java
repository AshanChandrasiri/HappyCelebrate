package com.uom.happycelebrate.utils;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uom.happycelebrate.data.QRUniqueCode;

import java.util.Map;

public abstract class VideoFinder {

    private DatabaseReference mDatabase;
    private DatabaseReference mMessageReference;

    public VideoFinder(){

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mMessageReference = FirebaseDatabase.getInstance().getReference("qrcodes");

    }

    public void searchData(final String key){

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("qrcodes");
        ref.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot item: dataSnapshot.getChildren()) {
                            if (item.getKey().equals(QRUniqueCode.qr_code))
                            {
                               getData(item);
                               break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });




    }


    public abstract void getData(DataSnapshot dataSnapshot);








}
