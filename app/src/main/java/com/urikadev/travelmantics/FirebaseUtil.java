package com.urikadev.travelmantics;

import android.app.Activity;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public class FirebaseUtil {
    public  static FirebaseDatabase mFireDataBase;
    public static DatabaseReference mDatabaseReference;
    private static FirebaseUtil firebaseUtil;
    private static FirebaseAuth firebaseAuth;
    private static FirebaseAuth.AuthStateListener mAuthListener;
    public static ArrayList<TravelDeal> mDeals;
    private static final int RC_SIGN_IN =123;
    private static ListActivity caller;
    public static  boolean isAdmin;
    public static FirebaseStorage mStorage;
    public static StorageReference mStorageRef;


    private FirebaseUtil(){};

    public static void openFbRference(String ref, final ListActivity callerActivity)
    {
        if(firebaseUtil == null){
            firebaseUtil = new FirebaseUtil();
            mFireDataBase =  FirebaseDatabase.getInstance();
            firebaseAuth = FirebaseAuth.getInstance();
            caller = callerActivity;
            mAuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    if(firebaseAuth.getCurrentUser() == null) {
                        FirebaseUtil.signIn();
                    }
                    else{
                        String userId =  firebaseAuth.getUid();
                        checkAdmin(userId);

                    }
                    Toast.makeText(callerActivity.getBaseContext(), "Welcome Back!!", Toast.LENGTH_LONG).show();



                }
            };
            connectStorage();

        }
        mDeals = new ArrayList<TravelDeal>();
        mDatabaseReference = mFireDataBase.getReference().child(ref);



    }

    private static void signIn()
    {                       // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());
// Create and launch sign-in intent
        caller.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);



    }


    private static void checkAdmin(String userID){
        FirebaseUtil.isAdmin = false;
        DatabaseReference ref = mFireDataBase.getReference().child("adminstrators").child(userID);
        ChildEventListener listener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                FirebaseUtil.isAdmin = true;
                caller.showMenu();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

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
        };
        ref.addChildEventListener(listener);

    }

    public static void attachListner(){
        firebaseAuth.addAuthStateListener(mAuthListener);
    }

    public static void detachListner(){
        firebaseAuth.removeAuthStateListener(mAuthListener);
    }

    public static void connectStorage(){

        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReference().child("deals_pictures");
    }

}
