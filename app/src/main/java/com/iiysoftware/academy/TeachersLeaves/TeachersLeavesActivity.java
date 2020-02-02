package com.iiysoftware.academy.TeachersLeaves;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.iiysoftware.academy.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TeachersLeavesActivity extends AppCompatActivity {

    private RecyclerView mAllTeachersLeavesRecyclerView;
    private TextView mTxtSelectDate;
    private FirebaseFirestore db;
    private TeacherLeaveAdapter adapter;
    private Toolbar mToolbar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teachers_leaves);

        init();

        mToolbar.setTitle("Teachers' Leaves");
        mToolbar.setNavigationIcon(R.drawable.baseline_arrow_back_white_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setOnClickListeners();

    }

    private void setOnClickListeners() {
        mTxtSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(TeachersLeavesActivity.this);
                datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        String date_s = i + "-" + (i1+1) + "-" + i2;
                        SimpleDateFormat dt = new SimpleDateFormat("yyyy-M-d");
                        Date date;
                        try {
                            date = dt.parse(date_s);
                            SimpleDateFormat dt1 = new SimpleDateFormat("dd-MMM-yyyy"); //Replace your own date format here
                            mTxtSelectDate.setText(dt1.format(date));
                            fetchAllLeaves();
                        } catch (ParseException e) {
                            Log.e("error", e+"");
                        }

                    }
                });
                datePickerDialog.show();
            }
        });
    }

    private void fetchAllLeaves() {
        db.collection("TeachersLeaves")
                .document(mTxtSelectDate.getText().toString())
                .collection("List")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    ArrayList<TeacherLeaveModel> leaveModels = new ArrayList<>();
                    for (QueryDocumentSnapshot snapshot: task.getResult()){
                        String teacherName = snapshot.getString("teacherName");
                        String teacherID = snapshot.getString("teacherID");
                        String date = snapshot.getString("date");
                        String leavereason = snapshot.getString("leaveReason");

                        TeacherLeaveModel model = new TeacherLeaveModel(teacherName, teacherID, date, leavereason);
                        leaveModels.add(model);
                    }

                    adapter = new TeacherLeaveAdapter(TeachersLeavesActivity.this, leaveModels);
                    mAllTeachersLeavesRecyclerView.setAdapter(adapter);
                    mAllTeachersLeavesRecyclerView.setLayoutManager(new LinearLayoutManager(TeachersLeavesActivity.this));
                }
            }
        });
    }

    private void init() {
        mAllTeachersLeavesRecyclerView = findViewById(R.id.teacher_leaves_recyclerView);
        mTxtSelectDate = findViewById(R.id.txtSelectDate);
        mToolbar = findViewById(R.id.activity_teacher_leaves_toolbar);
        db = FirebaseFirestore.getInstance();
    }
}
