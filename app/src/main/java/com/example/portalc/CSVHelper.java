package com.example.portalc;

import android.content.Context;
import android.os.Environment;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CSVHelper {

    private static final String FILE_NAME = "attendance.csv"; // Overall attendance file name

    // Write data to the overall attendance CSV in Downloads directory
    public static void writeToDownloadsCSV(List<StudentAttendance> studentAttendances, Context context) {
        // Access the Downloads directory
        File downloadsDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        if (!downloadsDirectory.exists()) {
            downloadsDirectory.mkdirs(); // Create the directory if it doesn't exist
        }

        // Create or overwrite the file in the Downloads directory
        File file = new File(downloadsDirectory, FILE_NAME);

        try (FileWriter writer = new FileWriter(file, false)) { // false for overwriting data
            // Write header row
            writer.append("Name,Date,Subject,Status\n");

            // Write attendance records
            for (StudentAttendance attendance : studentAttendances) {
                writer.append(attendance.getStudentName()).append(",")
                        .append(attendance.getDate()).append(",")
                        .append(attendance.getSubject()).append(",")
                        .append(attendance.getStatus().toString()).append("\n");
            }
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Write individual student attendance to CSV file
    public static void writeToIndividualStudentCSV(StudentAttendance attendance) {
        // Create individual student CSV file in external storage
        File file = new File(Environment.getExternalStorageDirectory(), attendance.getStudentName() + "_attendance.csv");
        try (FileWriter writer = new FileWriter(file, true)) { // true for appending data
            // Write individual student attendance data
            writer.append(attendance.getDate()).append(",")
                    .append(attendance.getSubject()).append(",")
                    .append(attendance.getStatus().toString()).append("\n");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Export all CSV files (overall and individual student files)
    public static void exportCSV(List<StudentAttendance> studentAttendances) {
        // Clear and write to the overall CSV in the Downloads directory
        writeToDownloadsCSV(studentAttendances, null);
        // Write individual student CSV files
        for (StudentAttendance attendance : studentAttendances) {
            writeToIndividualStudentCSV(attendance);
        }
    }

    // Delete all attendance records (clear overall and individual student CSVs)
    public static void deleteAllAttendanceRecords() {
        // Delete the overall attendance CSV file
        File downloadsDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File overallFile = new File(downloadsDirectory, FILE_NAME);
        if (overallFile.exists()) {
            overallFile.delete();
        }

        // Loop through all individual student CSV files and delete them
        File studentDirectory = Environment.getExternalStorageDirectory();
        File[] files = studentDirectory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.getName().endsWith("_attendance.csv")) {
                    file.delete();
                }
            }
        }
    }
}
