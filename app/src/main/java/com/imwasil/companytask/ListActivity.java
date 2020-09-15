package com.imwasil.companytask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {

    private ArrayList<Data> dataList;
    private RecyclerView dataRecycler;
    private ListAdapter adapter;
    private DatabaseReference mRef;
    private ProgressBar mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        ActionBar actionBar=getSupportActionBar();
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
        //ini recyclerview
        dataRecycler=findViewById(R.id.recycler_id);

        mProgress=findViewById(R.id.list_pgr_id);

        //ini firebase
        mRef= FirebaseDatabase.getInstance().getReference("Data");


        dataRecycler.setLayoutManager(new LinearLayoutManager(this));
        dataRecycler.setHasFixedSize(true);

    }

    @Override
    protected void onStart() {
        super.onStart();
        readData();
    }

    private void readData(){
        mProgress.setVisibility(View.VISIBLE);
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataList=new ArrayList<>();

                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Data data=dataSnapshot.getValue(Data.class);
                    dataList.add(data);
                }
                adapter= new ListAdapter(dataList,ListActivity.this);
                adapter.notifyDataSetChanged();
                dataRecycler.setAdapter(adapter);
                Toast.makeText(ListActivity.this, "Success", Toast.LENGTH_SHORT).show();
                mProgress.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ListActivity.this,
                        "Error Reading Data: "+error.getMessage(), Toast.LENGTH_SHORT).show();
                mProgress.setVisibility(View.INVISIBLE);

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        startActivity(new Intent(ListActivity.this,MainActivity.class));
        finish();
        return super.onSupportNavigateUp();

    }
}