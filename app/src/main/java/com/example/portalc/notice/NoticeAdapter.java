package com.example.portalc.notice;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.portalc.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.NoticeViewAdapter> {

    private Context context;
    private ArrayList<NoticeData> list;

    public NoticeAdapter(Context context, ArrayList<NoticeData> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public NoticeViewAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.newsfeed_item_layout, parent, false);
        return new NoticeViewAdapter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoticeViewAdapter holder, int position) {
        NoticeData currentItem = list.get(position);

        holder.deleteNoticeTitle.setText(currentItem.getTitle());

        if (currentItem.getImageUrl() != null && !currentItem.getImageUrl().isEmpty()) {
            Picasso.get().load(currentItem.getImageUrl()).into(holder.deleteNoticeImage, new Callback() {
                @Override
                public void onSuccess() {
                    holder.deleteNoticeImage.setVisibility(View.VISIBLE);
                }

                @Override
                public void onError(Exception e) {
                    holder.deleteNoticeImage.setVisibility(View.GONE);
                    Toast.makeText(context, "Failed to load image", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            holder.deleteNoticeImage.setVisibility(View.GONE);
        }

        holder.deleteNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private void showDeleteConfirmationDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Are you sure you want to delete this notice?");
        builder.setCancelable(false);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteNotice(position);
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void deleteNotice(final int position) {
        NoticeData noticeToDelete = list.get(position);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Notice").child("Notice");

        reference.child(noticeToDelete.getKey()).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
                        list.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, list.size());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public class NoticeViewAdapter extends RecyclerView.ViewHolder {
        private Button deleteNotice;
        private TextView deleteNoticeTitle;
        private ImageView deleteNoticeImage;

        public NoticeViewAdapter(@NonNull View itemView) {
            super(itemView);
            deleteNotice = itemView.findViewById(R.id.deleteNotice);
            deleteNoticeTitle = itemView.findViewById(R.id.deleteNoticeTitle);
            deleteNoticeImage = itemView.findViewById(R.id.deleteNoticeImage);
        }
    }
}

