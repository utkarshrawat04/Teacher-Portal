package com.example.portalc;

public class StudentAttendance {
    private String studentName;
    private String subject;
    private Status status;
    private String date;

    public StudentAttendance(String studentName, String subject, Status status, String date) {
        this.studentName = studentName;
        this.subject = subject;
        this.status = status;
        this.date = date;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getSubject() {
        return subject;
    }

    public Status getStatus() {
        return status;
    }

    public String getDate() {
        return date;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public enum Status {
        PRESENT, ABSENT, LATE
    }
}
