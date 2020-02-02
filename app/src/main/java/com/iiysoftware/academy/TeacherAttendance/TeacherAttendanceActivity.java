package com.iiysoftware.academy.TeacherAttendance;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.type.Color;
import com.iiysoftware.academy.R;
import com.iiysoftware.academy.TeachersLeaves.TeachersLeavesActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TeacherAttendanceActivity extends AppCompatActivity {

    private TextView mtxtSelectDate;
    private RecyclerView mTeacherAttendanceRecyclerView;
    private Button mBtnSave;
    private TeacherAttendanceAdapter adapter;
    private FirebaseFirestore db;
    private Toolbar mToolbar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_attendance);

        init();

        mToolbar.setTitle("Teacher's Attendance");
        mToolbar.setNavigationIcon(R.drawable.baseline_arrow_back_white_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Date date = new Date();
        SimpleDateFormat dt = new SimpleDateFormat("dd-MMM-yyyy");
        mtxtSelectDate.setText(dt.format(date));
        fetchAllAttendance();
        mtxtSelectDate.setOnClickListener(new DateSelectListener());
    }

    private class DateSelectListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            DatePickerDialog datePickerDialog = new DatePickerDialog(TeacherAttendanceActivity.this);
            datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                    String date_s = i + "-" + (i1+1) + "-" + i2;
                    SimpleDateFormat dt = new SimpleDateFormat("yyyy-M-d");
                    Date date;
                    try {
                        date = dt.parse(date_s);
                        SimpleDateFormat dt1 = new SimpleDateFormat("dd-MMM-yyyy"); //Replace your own date format here
                        mtxtSelectDate.setText(dt1.format(date));
                        fetchAllAttendance();
                    } catch (ParseException e) {
                        Log.e("error", e+"");
                    }

                }
            });
            datePickerDialog.show();
        }
    }

    private ArrayList<TeacherAttendanceModel> allAttendance = new ArrayList<>();
    private void fetchAllAttendance(){

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Loading...");
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        dialog.show();
                    }
                });
            }
        };
        final Thread thread = new Thread(runnable);
        thread.start();
        allAttendance.clear();
        db.collection("Teachers").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            ArrayList<String> teachernames = new ArrayList();
                            ArrayList<String> teacherids = new ArrayList();
                            for (QueryDocumentSnapshot snapshot: task.getResult()){
                                teachernames.add(snapshot.getString("name"));
                                teacherids.add("T" + snapshot.getString("id"));
                            }

                            for (int i = 0; i < teacherids.size(); i++) {
                                final String teacherid = teacherids.get(i);
                                final String teachername = teachernames.get(i);
                                db.collection("Attendance")
                                        .document(mtxtSelectDate.getText().toString())
                                        .collection("List")
                                        .whereEqualTo("teacherID", teacherids.get(i))
                                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()){
                                            ArrayList<String> attval = new ArrayList();
                                            for(QueryDocumentSnapshot snapshot:task.getResult()){
                                                String attendancevalue = snapshot.getString("attendanceValue");
                                                attval.add(attendancevalue);
                                            }
                                            if (attval.size()==0){
                                                TeacherAttendanceModel model = new TeacherAttendanceModel(teachername, teacherid, "P");
                                                allAttendance.add(model);
                                            }
                                            else{
                                                TeacherAttendanceModel model = new TeacherAttendanceModel(teachername, teacherid, attval.get(0));
                                                allAttendance.add(model);
                                            }
                                            adapter = new TeacherAttendanceAdapter(TeacherAttendanceActivity.this, mtxtSelectDate.getText().toString(), allAttendance);
                                            mTeacherAttendanceRecyclerView.setAdapter(adapter);
                                            mTeacherAttendanceRecyclerView.setLayoutManager(new LinearLayoutManager(TeacherAttendanceActivity.this));

                                            dialog.dismiss();
                                            thread.interrupt();

                                            mBtnSave.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    adapter.saveData();
                                                }
                                            });
                                        }
                                    }
                                });
                            }
                        }
                    }
                });
    }

    private void init() {
        mtxtSelectDate = findViewById(R.id.select_date);
        mTeacherAttendanceRecyclerView = findViewById(R.id.teacher_attendance_recyclerview);
        mBtnSave = findViewById(R.id.btnSave);
        mToolbar = findViewById(R.id.activityTeacherAttendanceToolBar);
        db = FirebaseFirestore.getInstance();
    }
}
