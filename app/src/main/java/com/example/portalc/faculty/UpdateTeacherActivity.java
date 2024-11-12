package com.example.portalc.faculty;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.portalc.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class UpdateTeacherActivity extends AppCompatActivity {

    private ImageView updateTeacherImage;
    private EditText updateTeacherName, updateTeacherEmail, updateTeacherPost;
    private Button updateTeacherBtn, deleteTeacherBtn;
    private ImageView addTeacherImage;
    private final int REQ = 1;
    private StorageReference storageReference;
    private ProgressDialog pd;
    private DatabaseReference reference;

    private Bitmap bitmap = null;

    private String name, email, image, post,downloadUrl,category,uniquekey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_teacher);

        name = getIntent().getStringExtra("name");
        email = getIntent().getStringExtra("email");
        image = getIntent().getStringExtra("image");
        post = getIntent().getStringExtra("post");

        uniquekey = getIntent().getStringExtra("key");
        category = getIntent().getStringExtra("category");


        updateTeacherImage = findViewById(R.id.updateTeacherImage);
        updateTeacherName = findViewById(R.id.updateTeacherName);
        updateTeacherEmail = findViewById(R.id.updateTeacherEmail);
        updateTeacherPost = findViewById(R.id.updateTeacherPost);
        updateTeacherBtn = findViewById(R.id.updateTeacherBtn);
        deleteTeacherBtn = findViewById(R.id.deleteTeacherBtn);

        reference = FirebaseDatabase.getInstance().getReference().child("Teacher");
        storageReference = FirebaseStorage.getInstance().getReference();




        try {
            Picasso.get().load(image).into(updateTeacherImage);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        updateTeacherEmail.setText(email);
        updateTeacherName.setText(name);
        updateTeacherPost.setText(post);
        updateTeacherImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        updateTeacherBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = updateTeacherName.getText().toString();
                email = updateTeacherEmail.getText().toString();
                post = updateTeacherPost.getText().toString();

                checkValidation();
            }
        });

        deleteTeacherBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteData();
            }
        });

    }

    private void checkValidation() {
        if (name.isEmpty()) {
            updateTeacherName.setError("Empty");
            updateTeacherName.requestFocus();
        }else if (post.isEmpty()) {
            updateTeacherPost.setError("Empty");
            updateTeacherPost.requestFocus();
        }else if (email.isEmpty()) {
            updateTeacherEmail.setError("Empty");
            updateTeacherEmail.requestFocus();
        } else if (bitmap == null) {
            updateData("");
        }else{
            uploadImage();
        }
    }

    private void uploadImage() {
        pd.setMessage("Uploading...");
        pd.show();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] finalImg = baos.toByteArray();
        final StorageReference filePath = storageReference.child("Teachers").child(System.currentTimeMillis() + ".jpg"); // Use a unique name
        final UploadTask uploadTask = filePath.putBytes(finalImg);

        uploadTask.addOnCompleteListener(UpdateTeacherActivity.this, task -> {
            if (task.isSuccessful()) {
                filePath.getDownloadUrl().addOnSuccessListener(uri -> {
                    downloadUrl = uri.toString();
                    updateData(downloadUrl);
                }).addOnFailureListener(e -> {
                    pd.dismiss();
                    Toast.makeText(UpdateTeacherActivity.this, "Failed to get download URL", Toast.LENGTH_LONG).show();
                });
            } else {
                pd.dismiss();
                Toast.makeText(UpdateTeacherActivity.this, "Something Went Wrong", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateData(String imageUrl) {
        // If no new image is selected, retain the old image URL
        if (imageUrl.isEmpty()) {
            imageUrl = image;
        }

        HashMap<String, Object> hp = new HashMap<>();
        hp.put("name", name);
        hp.put("email", email);
        hp.put("post", post);
        hp.put("image", imageUrl);

        reference.child(category).child(uniquekey).updateChildren(hp)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(UpdateTeacherActivity.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(UpdateTeacherActivity.this, UpdateFaculty.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UpdateTeacherActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void deleteData() {
        reference.child(category).child(uniquekey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(UpdateTeacherActivity.this, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(UpdateTeacherActivity.this,UpdateFaculty.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UpdateTeacherActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void openGallery() {
        Intent pickImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickImage, REQ);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                updateTeacherImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }
}