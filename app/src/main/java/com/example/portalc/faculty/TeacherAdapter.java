package com.example.portalc.faculty;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.portalc.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class TeacherAdapter extends RecyclerView.Adapter<TeacherAdapter.TeacherViewHolder> {
    private List<Teacherdata> list;
    private Context context;
    private String category;

    public TeacherAdapter(List<Teacherdata> list, Context context,String category) {
        this.list = list;
        this.context = context;
        this.category = category;
    }

    @NonNull
    @Override
    public TeacherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.faculty_item_layout, parent, false);
        return new TeacherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TeacherViewHolder holder, int position) {
        Teacherdata item = list.get(position);

        // Set text for name, email, and post
        holder.name.setText(item.getName());
        holder.email.setText(item.getEmail());
        holder.post.setText(item.getPost());

        // Get the image URL
        String imageUrl = item.getImage();

        // Log the URL for debugging
        Log.d("TeacherAdapter", "Image URL: " + imageUrl);

        if (imageUrl != null && !imageUrl.isEmpty()) {
            // Load image with Picasso
            Picasso.get()
                    .load(imageUrl)
                    .placeholder(R.drawable.circle_red) // Optional: placeholder image
                    .error(R.drawable.circle_red) // Optional: error image
                    .into(holder.imageView);
        } else {
            // Set a default image if URL is invalid or empty
            holder.imageView.setImageResource(R.drawable.circle_red);
        }

        // Set click listener for the update button
        holder.update.setOnClickListener(v -> {
            // Handle update action
            Intent intent = new Intent(context, UpdateTeacherActivity.class);
            intent.putExtra( "name", item.getName());
            intent.putExtra( "email",item.getEmail());
            intent.putExtra( "post", item.getPost());
            intent.putExtra( "image",item.getImage());
            intent.putExtra("key",item.getKey());
            intent.putExtra("category",category);
            context.startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class TeacherViewHolder extends RecyclerView.ViewHolder {
        private TextView name, email, post;
        private Button update;
        private ImageView imageView;

        public TeacherViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.teacherName);
            email = itemView.findViewById(R.id.teacherEmail);
            post = itemView.findViewById(R.id.teacherPost);
            imageView = itemView.findViewById(R.id.teacherImage);
            update = itemView.findViewById(R.id.teacherUpdate);
        }
    }
}

