package com.example.portalc;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;

import com.example.portalc.faculty.UpdateFaculty;
import com.example.portalc.notice.DeleteNoticeActivity;
import com.example.portalc.notice.Notice;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    CardView uploadNotice, addMaterial, faculty, deleteNotice, attendence, mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        uploadNotice = findViewById(R.id.addNotice);
        addMaterial = findViewById(R.id.unknown1);
        faculty = findViewById(R.id.faculty);
        deleteNotice = findViewById(R.id.unknown2);
        attendence  = findViewById(R.id.unknown3);
        mode = findViewById(R.id.unknown5);

        uploadNotice.setOnClickListener(this);
        addMaterial.setOnClickListener(this);
        faculty.setOnClickListener(this);
        deleteNotice.setOnClickListener(this);
        attendence.setOnClickListener(this);
        mode.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        // Handling click events for each CardView
        if (id == R.id.addNotice) {
            Intent intent = new Intent(MainActivity.this, Notice.class);
            startActivity(intent);
        }
        if (id == R.id.unknown1) {
            Intent intent = new Intent(MainActivity.this, UploadPDf.class);
            startActivity(intent);
        }
        if (id == R.id.faculty) {
            Intent intent = new Intent(MainActivity.this, UpdateFaculty.class);
            startActivity(intent);
        }
        if (id == R.id.unknown2) {
            Intent intent = new Intent(MainActivity.this, DeleteNoticeActivity.class);
            startActivity(intent);
        }
        if (id == R.id.unknown3) {
            Intent intent = new Intent(MainActivity.this, AttendanceActivity.class);
            startActivity(intent);
        }
        if (id == R.id.unknown5) {

            showThemeChangeDialog();
        }
    }

    // Method to show theme change dialog
    private void showThemeChangeDialog() {
        // Create an AlertDialog to allow users to choose the theme
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Theme")
                .setItems(new CharSequence[]{"Light Mode", "Dark Mode"}, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0: // Light Mode
                                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                                break;
                            case 1: // Dark Mode
                                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                                break;
                        }
                    }
                })
                .create()
                .show();
    }
}
