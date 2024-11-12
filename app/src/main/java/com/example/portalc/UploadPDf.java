package com.example.portalc;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

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

import java.io.File;
import java.util.HashMap;

public class UploadPDf extends AppCompatActivity {
    private CardView addPdf;
    private TextView pdfTextView;
    private EditText pdfTitle;
    private Button uploadPdfButton;
    private Uri pdfData;
    private String pdfName, title;
    private ProgressDialog pd;

    private DatabaseReference reference, dbRef;
    private StorageReference storageReference;
    private final int REQ = 1;
    private String downloadUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.uploadpdf);

        pd = new ProgressDialog(this);

        reference = FirebaseDatabase.getInstance().getReference("PDFs");
        storageReference = FirebaseStorage.getInstance().getReference("PDFs");

        addPdf = findViewById(R.id.addMaterial);
        pdfTextView = findViewById(R.id.pdfTextView);
        pdfTitle = findViewById(R.id.pdfTitle);
        uploadPdfButton = findViewById(R.id.uploadMaterialbutton);

        addPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        uploadPdfButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title = pdfTitle.getText().toString();

                if (title.isEmpty()) {
                    pdfTitle.setError("Title is empty");
                    pdfTitle.requestFocus();
                } else if (pdfData == null) {
                    Toast.makeText(UploadPDf.this, "Please select a PDF", Toast.LENGTH_SHORT).show();
                } else {
                    uploadPdf();
                }
            }
        });
    }

    private void uploadPdf() {
        pd.setMessage("Uploading...");
        pd.show();

        StorageReference filePath = storageReference.child("pdfs/" + pdfName + "-" + System.currentTimeMillis() + ".pdf");
        filePath.putFile(pdfData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        downloadUrl = uri.toString(); // Set the download URL
                        uploadData(downloadUrl);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(UploadPDf.this, "Upload Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadData(String downloadUrl) {
        dbRef = reference.child("pdfs");
        String uniqueKey = dbRef.push().getKey();

        HashMap<String, String> data = new HashMap<>();
        data.put("pdfTitle", title);
        data.put("pdfUri", downloadUrl);
        data.put("pdfName", pdfName);  // Store the name of the PDF

        dbRef.child(uniqueKey).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                pd.dismiss();
                Toast.makeText(UploadPDf.this, "PDF Uploaded Successfully", Toast.LENGTH_SHORT).show();
                pdfTitle.setText("");
                pdfTextView.setText("");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(UploadPDf.this, "Failed to Upload PDF", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select PDF File"), REQ);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ && resultCode == RESULT_OK && data != null) {
            pdfData = data.getData();

            // Check the API level to ensure backward compatibility
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // For API level 26 and above
                if (pdfData.toString().startsWith("content://")) {
                    Cursor cursor = null;
                    try {
                        cursor = UploadPDf.this.getContentResolver().query(pdfData, null, null, null);
                        if (cursor != null && cursor.moveToFirst()) {
                            int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                            if (nameIndex != -1) {
                                pdfName = cursor.getString(nameIndex);
                            } else {
                                Toast.makeText(this, "Unable to retrieve file name", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (cursor != null) {
                            cursor.close();
                        }
                    }
                }
            } else {
                // For API levels below 26
                if (pdfData.toString().startsWith("file://")) {
                    pdfName = new File(pdfData.toString()).getName();
                }
            }

            pdfTextView.setText(pdfName);
        }
    }
}

