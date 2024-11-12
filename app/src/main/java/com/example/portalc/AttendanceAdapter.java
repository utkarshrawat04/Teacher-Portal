package com.example.portalc;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

public class AttendanceAdapter extends BaseAdapter {

    private Context context;
    private List<StudentAttendance> studentAttendances;

    public AttendanceAdapter(Context context, List<StudentAttendance> studentAttendances) {
        this.context = context;
        this.studentAttendances = studentAttendances;
    }

    @Override
    public int getCount() {
        return studentAttendances.size();
    }

    @Override
    public Object getItem(int position) {
        return studentAttendances.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.attendance_item, parent, false);
        }

        StudentAttendance attendance = studentAttendances.get(position);

        TextView studentName = convertView.findViewById(R.id.studentName);
        TextView subject = convertView.findViewById(R.id.subjectName);
        CheckBox present = convertView.findViewById(R.id.present);
        CheckBox absent = convertView.findViewById(R.id.absent);
        CheckBox late = convertView.findViewById(R.id.late);

        studentName.setText(attendance.getStudentName());
        subject.setText(attendance.getSubject());

        present.setChecked(attendance.getStatus() == StudentAttendance.Status.PRESENT);
        absent.setChecked(attendance.getStatus() == StudentAttendance.Status.ABSENT);
        late.setChecked(attendance.getStatus() == StudentAttendance.Status.LATE);

        present.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                attendance.setStatus(StudentAttendance.Status.PRESENT);
            }
        });
        absent.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                attendance.setStatus(StudentAttendance.Status.ABSENT);
            }
        });
        late.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                attendance.setStatus(StudentAttendance.Status.LATE);
            }
        });

        return convertView;
    }
}
