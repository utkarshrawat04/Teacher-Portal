package com.example.portalc.notice;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.portalc.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DeleteNoticeActivity extends AppCompatActivity {

    private RecyclerView deleteNoticeRecycler;
    private ProgressBar progressBar;
    private ArrayList<NoticeData> list;
    private NoticeAdapter adapter;
    private DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_notice);

        deleteNoticeRecycler = findViewById(R.id.deleteNoticeRecycler);
        progressBar = findViewById(R.id.progressBar);

        reference = FirebaseDatabase.getInstance().getReference().child("Notice").child("Notice");;
        deleteNoticeRecycler.setLayoutManager(new LinearLayoutManager(this));
        deleteNoticeRecycler.setHasFixedSize(true);

        getNotice();
    }

    private void getNotice() {
        progressBar.setVisibility(View.VISIBLE);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    NoticeData data = dataSnapshot.getValue(NoticeData.class);
                    if (data != null) {
                        data.setKey(dataSnapshot.getKey()); // Set the key
                        list.add(data);
                    }
                }
                adapter = new NoticeAdapter(DeleteNoticeActivity.this, list);
                deleteNoticeRecycler.setAdapter(adapter);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(DeleteNoticeActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
