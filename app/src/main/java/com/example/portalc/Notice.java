package com.example.portalc;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.portalc.notice.NoticeData;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Notice extends AppCompatActivity {

    private CardView addImage;  // Ensure that this is indeed a CardView in XML
    private ImageView noticeImageView;
    private final int REQ = 1;
    private Bitmap bitmap;
    private EditText noticeTitle;
    private Button uploadNoticebutton;
    private DatabaseReference reference,dbRef;
    private StorageReference storageReference;
    String downloadUrl = "";  // Corrected typo: downloadeurl to downloadUrl
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notice);

        pd = new ProgressDialog(this);

        reference = FirebaseDatabase.getInstance().getReference("Notice");
        storageReference = FirebaseStorage.getInstance().getReference("Notice");

        addImage = findViewById(R.id.addImage);  // Make sure this is a CardView in the layout
        noticeImageView = findViewById(R.id.noticeImageView);
        noticeTitle = findViewById(R.id.noticeTitle);
        uploadNoticebutton = findViewById(R.id.uploadNoticebutton);

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        uploadNoticebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (noticeTitle.getText().toString().isEmpty()) {
                    noticeTitle.setError("IT IS EMPTY");
                    noticeTitle.requestFocus();
                } else if (bitmap == null) {
                    uploadData();
                } else {
                    uploadImage();
                }
            }
        });
    }

    private void uploadData() {
        dbRef = reference.child("Notice");
        String title = noticeTitle.getText().toString();

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MM-yy");
        String date = currentDate.format(calForDate.getTime());

        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        String time = currentTime.format(calForTime.getTime());

        String uniqueKey = dbRef.push().getKey();

        NoticeData noticeData = new NoticeData(title, downloadUrl, date, time, uniqueKey);

        dbRef.child(uniqueKey).setValue(noticeData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                pd.dismiss();
                Toast.makeText(Notice.this, "Notice Uploaded", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(Notice.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadImage() {
        pd.setMessage("Uploading...");
        pd.show();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] finalImg = baos.toByteArray();
        final StorageReference filePath = storageReference.child(finalImg + ".jpg");
        final UploadTask uploadTask = filePath.putBytes(finalImg);

        uploadTask.addOnCompleteListener(Notice.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            downloadUrl = uri.toString(); // Set image URL
                            uploadData(); // Upload notice data
                        }
                    });
                } else {
                    pd.dismiss();
                    Toast.makeText(Notice.this, "Something Went Wrong", Toast.LENGTH_LONG).show();
                }
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
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            } catch (IOException e) {
                e.printStackTrace();  // Handle exceptions properly
            }
            noticeImageView.setImageBitmap(bitmap);
        }
    }
}
