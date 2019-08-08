package com.urikadev.travelmantics;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class DealActivity extends AppCompatActivity {
    private FirebaseDatabase mFirebaseDatebase;
    private DatabaseReference mDatabaseReference;
    EditText txtPrice;
    EditText txtDescription;
    EditText txtTitle;
    TravelDeal deal;
    ListActivity act;
    private static final int PICTURE_RESULT =42;
    ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal);
        FirebaseUtil.openFbRference("traveldeals",act);
        mFirebaseDatebase = FirebaseUtil.mFireDataBase;
        mDatabaseReference = FirebaseUtil.mDatabaseReference;
        txtTitle = findViewById(R.id.txtTitle);
        txtDescription = findViewById(R.id.txtDescription);
        txtPrice = findViewById(R.id.txtPrice);
        imageView = findViewById(R.id.image);
        final Intent intent = getIntent();


        TravelDeal dealSelected = (TravelDeal) intent.getSerializableExtra("Deal");
        if(dealSelected == null){

            dealSelected = new TravelDeal();
        }
        this.deal = dealSelected;
        txtTitle.setText(dealSelected.getTitle());
        txtDescription.setText(dealSelected.getDescprtion());
        txtPrice.setText(dealSelected.getPrice());
        showImage(deal.getImageUrl());
        Button btnImage = findViewById(R.id.btnImage);
        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(Intent.ACTION_GET_CONTENT);
                intent1.setType("image/jpeg");
                intent1.putExtra(Intent.EXTRA_LOCAL_ONLY,true);
                startActivityForResult(intent1.createChooser(intent1,"Insert Picture"),PICTURE_RESULT );
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.save_menu:
                saveDeal();
                Toast.makeText(this, "Deal Saved", Toast.LENGTH_LONG).show();
                clean();
                backToList();
                return true;

            case (R.id.delete_menu):
                deleteDeal();
                Toast.makeText(this, "Deal Deleted", Toast.LENGTH_LONG).show();
                backToList();
                return true;


            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void clean() {
        txtPrice.setText("");
        txtDescription.setText("");
        txtTitle.setText("");
        txtTitle.requestFocus();

    }

    private void saveDeal() {
        deal.setTitle(txtTitle.getText().toString());
        deal.setDescprtion(txtDescription.getText().toString());
        deal.setPrice(txtPrice.getText().toString());
        if(deal.getId() == null) {
            mDatabaseReference.push().setValue(deal);
        }
        else{
            mDatabaseReference.child(deal.getId()).setValue(deal);

        }
    }

    private void deleteDeal(){
        if(deal == null)
        {
            Toast.makeText(this, "Please save delete before deleting deal", Toast.LENGTH_LONG).show();
            return;

        }
        mDatabaseReference.child(deal.getId()).removeValue();
        if(deal.getImageName() != null && deal.getImageName().isEmpty() == false){

            StorageReference picRef = FirebaseUtil.mStorage.getReference().child(deal.getImageName());
            picRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.i("Debugger","Image deleted");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.i("Debugger","Image failed to deleted");

                }
            });

        }
    }

    private  void backToList(){
        Intent intent = new Intent(this, ListActivity.class);
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater =getMenuInflater();
        menuInflater.inflate(R.menu.save_menu,menu);
        if(FirebaseUtil.isAdmin){
            menu.findItem(R.id.delete_menu).setVisible(true);
            menu.findItem(R.id.save_menu).setVisible(true);
            enableEditText(true);
            findViewById(R.id.btnImage).setEnabled(true);
        }
        else {

            menu.findItem(R.id.delete_menu).setVisible(false);
            menu.findItem(R.id.save_menu).setVisible(false);
            enableEditText(false);
            findViewById(R.id.btnImage).setEnabled(false);
        }
        return true;
    }

    private void enableEditText(boolean isEnabled){
        txtPrice.setEnabled(isEnabled);
        txtDescription.setEnabled(isEnabled);
        txtTitle.setEnabled(isEnabled);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICTURE_RESULT && resultCode == RESULT_OK){
            Uri imageUri = data.getData();
            StorageReference ref1 = FirebaseUtil.mStorageRef.child(imageUri.getLastPathSegment());
            Log.i("Debugger","Image "+imageUri);
            ref1.putFile(imageUri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                    taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = uri.toString();
                            //Log.i("Debugger","URL in DB "+url);
                            String pictureName = taskSnapshot.getStorage().getPath();
                            deal.setImageUrl(url);
                            deal.setImageName(pictureName);
                            showImage(url);
                        }
                    });


//                    String url = taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();
//                    Log.i("Debugger","URL in DB "+url);
//                    deal.setImageUrl(url);
//                    showImage(url);
                    //Log.i("Debugger","URL in Deal object "+deal.getImageUrl());
                }
            });

        }
    }

    private void  showImage(String url){
        if(url != null && url.isEmpty() == false){
            int width = Resources.getSystem().getDisplayMetrics().widthPixels;
            Picasso.with(this)
                    .load(url)
                    .resize(width,width*2/3)
                    .centerCrop()
                    .into(imageView);

        }
    }
}
