package com.uom.happycelebrate;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.andremion.floatingnavigationview.FloatingNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uom.happycelebrate.adapters.CustomAdapter;
import com.uom.happycelebrate.adapters.CustomVehicleAdapter;
import com.uom.happycelebrate.models.Card;
import com.uom.happycelebrate.models.DataModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CardsListPage extends AppCompatActivity {

    ArrayList<DataModel> dataModels;
    ListView listView;
    private static CustomAdapter adapter;
    private static CustomVehicleAdapter customVehicleAdapter;

    private ArrayList<Card> messageList;

    private Button btnSend;

    private FloatingNavigationView mFloatingNavigationView;

    private DatabaseReference mDatabase;
    private DatabaseReference mMessageReference;
    private ChildEventListener mMessageListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cards_list_page);

        mDatabase = FirebaseDatabase.getInstance().getReference();


        mMessageReference = FirebaseDatabase.getInstance().getReference("Cards_Repository");



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initials();
       // btnSend = findViewById(R.id.button);
        listView=(ListView)findViewById(R.id.list);


        messageList = new ArrayList<>();

//        btnSend.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                submitMessage();
//            }
//        });

        mFloatingNavigationView = (FloatingNavigationView) findViewById(R.id.floating_navigation_view);
        mFloatingNavigationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFloatingNavigationView.open();
            }
        });
        mFloatingNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {




                if(item.getTitle().equals("AR Demo")){
                    Intent intent = new Intent(CardsListPage.this, BarcordReader.class);
                    intent.putExtra("redirect_page", "AR_PAGE");
                    startActivity(intent);

                }else if(item.getTitle().equals("Create Card")){

                    Intent intent = new Intent(CardsListPage.this, BarcordReader.class);
                    intent.putExtra("redirect_page", "CREATE_CARD");
                    startActivity(intent);

                }

                mFloatingNavigationView.close();
                CardsListPage.this.finish();



                return true;
            }
        });


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("card");

        ref.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        collectAllCards((Map<String,Object>) dataSnapshot.getValue());

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });

    }

    private void collectAllCards(Map<String,Object> cards) {

        for (Map.Entry<String, Object> entry : cards.entrySet()){

            //Get user map
            Map singleCard = (Map) entry.getValue();
            //Get phone field and append to list
            Card card = new Card();
//            Toast.makeText(CardsListPage.this,entry.getKey(),Toast.LENGTH_SHORT).show();
            messageList.add( card.toCard(singleCard));
        }

        customVehicleAdapter= new CustomVehicleAdapter(messageList,getApplicationContext());

        listView.setAdapter(customVehicleAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                DataModel dataModel= dataModels.get(position);

                Snackbar.make(view, dataModel.getName()+"\n"+dataModel.getType()+" API: "+dataModel.getVersion_number(), Snackbar.LENGTH_LONG)
                        .setAction("No action", null).show();
            }
        });
    }


    private void submitMessage() {
         writeNewMessage();
        }

    private void writeNewMessage() {

        Card card  = new Card();
        card.setDescription("Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text");
        card.setDesigner_id("00001D");
        card.setId("00001A");
        card.setImage_url("https://asset.holidaycardsapp.com/assets/card/j_newad_124-8487ccf02839a10684b2bbff9b599b63.png");
        card.setVatagory("BIRTHDAY");

        Map<String, Object> messageValues = card.toMap();
        Map<String, Object> childUpdates = new HashMap<>();

        String key = mDatabase.child("card").push().getKey();

        childUpdates.put("/card/" + key, messageValues);

        mDatabase.updateChildren(childUpdates);
    }



     private void initials() {

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                // A new message has been added
                // onChildAdded() will be called for each node at the first time
                Card message = dataSnapshot.getValue(Card.class);
//                Toast.makeText(CardsListPage.this, "added child: " + message.getDescription(), Toast.LENGTH_SHORT).show();
                messageList.add(message);
                System.out.println("addrddddddddddddddddddddddddddddddddddddddddddd");
                Card latest = messageList.get(messageList.size() - 1);


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
               // Log.e(TAG, "onChildChanged:" + dataSnapshot.getKey());

                // A message has changed
                Card message = dataSnapshot.getValue(Card.class);
//                Toast.makeText(CardsListPage.this, "onChildChanged: " + message.getDescription(), Toast.LENGTH_SHORT).show();
                System.out.println("addrddddddddddddddddddddddddddddddddddddddddddd");
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//                Log.e(TAG, "onChildRemoved:" + dataSnapshot.getKey());
//
//                // A message has been removed
//                Message message = dataSnapshot.getValue(Message.class);
//                Toast.makeText(MessageActivity.this, "onChildRemoved: " + message.body, Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
//                Log.e(TAG, "onChildMoved:" + dataSnapshot.getKey());
//
//                // A message has changed position
//                Message message = dataSnapshot.getValue(Message.class);
//                Toast.makeText(MessageActivity.this, "onChildMoved: " + message.body, Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.e(TAG, "postMessages:onCancelled", databaseError.toException());
//                Toast.makeText(MessageActivity.this, "Failed to load Message.", Toast.LENGTH_SHORT).show();
//            }
        };

        mMessageReference.addChildEventListener(childEventListener);
        mMessageListener = childEventListener;
    }




    @Override
    public void onBackPressed() {
        if (mFloatingNavigationView.isOpened()) {
            mFloatingNavigationView.close();
        } else {
            super.onBackPressed();
        }
    }















}


