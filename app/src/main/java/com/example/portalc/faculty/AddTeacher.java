package com.example.portalc.faculty;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class AddTeacher extends AppCompatActivity {

    private ImageView addTeacherImage;
    private EditText addTeacherName, addTeacherEmail, addTeacherPost;
    private Spinner addTeacherCategory;
    private final int REQ = 1;
    private Button addTeacherB;
    private Bitmap bitmap;
    private String category;
    private ProgressDialog pd;

    private StorageReference storageReference;
    private DatabaseReference reference;

    private String name, email, post, downloadUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_teacher);

        addTeacherImage = findViewById(R.id.addTeacherImage);
        addTeacherName = findViewById(R.id.addTeacherName);
        addTeacherEmail = findViewById(R.id.addTeacherEmail);
        addTeacherPost = findViewById(R.id.addTeacherPost);
        addTeacherCategory = findViewById(R.id.addTeacherCatagory);
        addTeacherB = findViewById(R.id.addTeacherB);

        reference = FirebaseDatabase.getInstance().getReference().child("Teacher");
        storageReference = FirebaseStorage.getInstance().getReference();

        pd = new ProgressDialog(this);

        String[] items = new String[]{"Select Category", "Computer Science", "AI", "Cyber Security", "Mechanical"};
        addTeacherCategory.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items));

        addTeacherCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category = addTeacherCategory.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing or set a default value
            }
        });

        addTeacherImage.setOnClickListener(view -> openGallery());

        addTeacherB.setOnClickListener(view -> checkValidation());
    }

    private void checkValidation() {
        name = addTeacherName.getText().toString();
        email = addTeacherEmail.getText().toString();
        post = addTeacherPost.getText().toString();

        if (name.isEmpty()) {
            addTeacherName.setError("Empty");
            addTeacherName.requestFocus();
        } else if (email.isEmpty()) {
            addTeacherEmail.setError("Empty");
            addTeacherEmail.requestFocus();
        } else if (post.isEmpty()) {
            addTeacherPost.setError("Empty");
            addTeacherPost.requestFocus();
        } else if (category.equals("Select Category")) {
            Toast.makeText(this, "Please provide category", Toast.LENGTH_SHORT).show();
        } else if (bitmap == null) {
            insertData();
        } else {
            insertImage();
        }
    }

    private void insertImage() {
        pd.setMessage("Uploading...");
        pd.show();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] finalImg = baos.toByteArray();
        final StorageReference filePath = storageReference.child("Teachers").child(System.currentTimeMillis() + ".jpg"); // Use a unique name
        final UploadTask uploadTask = filePath.putBytes(finalImg);

        uploadTask.addOnCompleteListener(AddTeacher.this, task -> {
            if (task.isSuccessful()) {
                filePath.getDownloadUrl().addOnSuccessListener(uri -> {
                    downloadUrl = uri.toString();
                    insertData();
                }).addOnFailureListener(e -> {
                    pd.dismiss();
                    Toast.makeText(AddTeacher.this, "Failed to get download URL", Toast.LENGTH_LONG).show();
                });
            } else {
                pd.dismiss();
                Toast.makeText(AddTeacher.this, "Something Went Wrong", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void insertData() {
        DatabaseReference dbRef = reference.child(category);
        String uniqueKey = dbRef.push().getKey();

        Teacherdata teacherdata = new Teacherdata(name, email, post, downloadUrl, uniqueKey);

        dbRef.child(uniqueKey).setValue(teacherdata).addOnSuccessListener(aVoid -> {
            pd.dismiss();
            Toast.makeText(AddTeacher.this, "Teacher Added", Toast.LENGTH_SHORT).show();
            finish(); // Optional: Close activity after adding teacher
        }).addOnFailureListener(e -> {
            pd.dismiss();
            Toast.makeText(AddTeacher.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
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
                addTeacherImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
