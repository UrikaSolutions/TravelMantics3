package com.urikadev.travelmantics;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class DealAdapter extends RecyclerView.Adapter<DealAdapter.DealViewHolder> {
    ArrayList<TravelDeal> deals;
    private FirebaseDatabase mFirebaseDB;
    private DatabaseReference mDBRef;
    private ChildEventListener mChildEventListener;
    private static Context context2;
    private ImageView imageDeal1;



    public DealAdapter(ListActivity act){
        FirebaseUtil.openFbRference("traveldeals",act);
        mFirebaseDB = FirebaseUtil.mFireDataBase;
        mDBRef = FirebaseUtil.mDatabaseReference;
        deals = FirebaseUtil.mDeals;
        deals.clear();
        Log.i("Debugger","Constructor "+deals.size());
        mChildEventListener = new ChildEventListener(){
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                TravelDeal td = dataSnapshot.getValue(TravelDeal.class);
                td.setId(dataSnapshot.getKey());
                deals.add(td);
                notifyItemInserted(deals.size()-1);


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
        mDBRef.addChildEventListener(mChildEventListener);


    }

    @NonNull
    @Override
    public DealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        context2 = parent.getContext();
        View itemView = LayoutInflater.from(context).inflate(R.layout.row, parent,false);
        return new DealViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull DealViewHolder holder, int position) {
        final TravelDeal deal =  deals.get(position);
        //holder.bind(deal);
        //Log.i("Debugger","OnBind "+deals.size());
        holder.Pricetxt.setText(deal.getPrice());
        holder.Desctxt.setText(deal.getDescprtion());
        holder.tvTitletxt.setText(deal.getTitle());
        //holder.imageDeal1.setImageResource(R.drawable.common_google_signin_btn_icon_dark);
        if(deal.getImageUrl() != null && deal.getImageUrl().isEmpty() == false )
        {      Picasso.with((imageDeal1.getContext()))
                .load(deal.getImageUrl())
                .resize(120, 140)
                .centerCrop()
                .into(imageDeal1, new Callback() {
                            @Override
                            public void onSuccess() {
                                Log.i("Debugger","Picasso Success" +deal.getImageUrl());
                            }

                            @Override
                            public void onError() {
                                Log.i("Debugger","Picasso Failure "+deal.getImageUrl());

                            }
                        }
                );
        }
        else {
            imageDeal1.setImageResource(R.drawable.common_google_signin_btn_icon_dark);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
    @Override
    public int getItemCount() {
        return deals.size();
    }

    public class DealViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView tvTitletxt;
        TextView Desctxt;
        TextView Pricetxt;

        public DealViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitletxt =itemView.findViewById(R.id.tvTitle);
            Desctxt = itemView.findViewById(R.id.tvDescription);
            Pricetxt = itemView.findViewById(R.id.tvPrice);
            imageDeal1 = itemView.findViewById(R.id.imageDeal);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            int position = getAdapterPosition();
            Log.i("Debugger","onclick "+position);
            TravelDeal selecteddeal = deals.get(position);
            Intent intent = new Intent(v.getContext(),DealActivity.class);
            intent.putExtra("Deal",selecteddeal);
            v.getContext().startActivity(intent);

        }



    }


}
