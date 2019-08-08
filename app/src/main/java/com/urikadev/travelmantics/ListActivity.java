package com.urikadev.travelmantics;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {
    ArrayList<TravelDeal> deals;
    private FirebaseDatabase mFirebaseDB;
    private DatabaseReference mDBRef;
    private ChildEventListener mChildEventListener;
    RecyclerView rvDeal;
    DealAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.list_activity_menu, menu);
        MenuItem insertMen = menu.findItem(R.id.insert_menu);
        if(FirebaseUtil.isAdmin == true){

            insertMen.setVisible(true);
        }
        else{

            insertMen.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case (R.id.insert_menu):
                Intent intent = new Intent(this, DealActivity.class);
                startActivity(intent);
                return true;
            case (R.id.logout_menu):
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                FirebaseUtil.attachListner();

                            }
                        });
                FirebaseUtil.detachListner();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseUtil.detachListner();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //adapter.notifyDataSetChanged();
        rvDeal = (RecyclerView) findViewById(R.id.rvDeals);
        adapter = new DealAdapter(this);
        rvDeal.setAdapter(adapter);
        LinearLayoutManager dealsLayoutManager = new LinearLayoutManager(this);
        rvDeal.setLayoutManager(dealsLayoutManager);
        FirebaseUtil.attachListner();


    }

    public  void showMenu(){
        invalidateOptionsMenu();

    }
}
