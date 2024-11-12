package com.example.portalc;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class AttendanceActivity extends Activity {

    private List<StudentAttendance> studentAttendances;
    private AttendanceAdapter adapter;
    private Button exportCsv, deleteAllRecords, addStudentBtn;
    private EditText newStudentName, newSubject;
    private static final String FILENAME = "attendance_data.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendence);


        studentAttendances = new ArrayList<>();
        ListView attendanceList = findViewById(R.id.attendanceList);
        newStudentName = findViewById(R.id.newStudentName);
        newSubject = findViewById(R.id.newSubject);
        addStudentBtn = findViewById(R.id.addStudentBtn);
        exportCsv = findViewById(R.id.exportCsv);
        deleteAllRecords = findViewById(R.id.deleteAllRecords);


        adapter = new AttendanceAdapter(this, studentAttendances);
        attendanceList.setAdapter(adapter);

        loadDataFromFile();

        addStudentBtn.setOnClickListener(v -> {
            String studentName = newStudentName.getText().toString().trim();
            String subject = newSubject.getText().toString().trim();

            if (!studentName.isEmpty() && !subject.isEmpty()) {
                studentAttendances.add(new StudentAttendance(studentName, subject, StudentAttendance.Status.PRESENT, getCurrentDate()));
                adapter.notifyDataSetChanged();

                saveUserData(studentName, subject);

                newStudentName.setText("");
                newSubject.setText("");

                Toast.makeText(this, "Student added successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Please enter both student name and subject.", Toast.LENGTH_SHORT).show();
            }
        });

        exportCsv.setOnClickListener(v -> {
            CSVHelper.exportCSV(studentAttendances); // Clear and write new data to CSV
            Toast.makeText(this, "Attendance exported successfully.", Toast.LENGTH_SHORT).show();
        });

        deleteAllRecords.setOnClickListener(v -> {
            studentAttendances.clear();

            clearDataFromFile();

            adapter.notifyDataSetChanged();

            Toast.makeText(this, "All attendance records deleted.", Toast.LENGTH_SHORT).show();
        });
    }

    private String getCurrentDate() {
        return java.text.DateFormat.getDateInstance().format(new java.util.Date());
    }

    private void saveUserData(String studentName, String subject) {
        String csvLine = studentName + "," + subject + "\n";

        try (FileOutputStream fos = openFileOutput(FILENAME, MODE_APPEND)) {
            fos.write(csvLine.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving student data", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearDataFromFile() {
        try (FileOutputStream fos = openFileOutput(FILENAME, MODE_PRIVATE)) {
            fos.write("".getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error clearing data from file.", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadDataFromFile() {
        try (FileInputStream fis = openFileInput(FILENAME);
             InputStreamReader isr = new InputStreamReader(fis);
             BufferedReader reader = new BufferedReader(isr)) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    String studentName = parts[0];
                    String subject = parts[1];
                    studentAttendances.add(new StudentAttendance(studentName, subject, StudentAttendance.Status.PRESENT, getCurrentDate()));
                }
            }


            adapter.notifyDataSetChanged();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error loading student data", Toast.LENGTH_SHORT).show();
        }
    }
}
