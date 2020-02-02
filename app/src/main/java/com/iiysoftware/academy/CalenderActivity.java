package com.iiysoftware.academy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.iiysoftware.academy.TeachersLeaves.TeachersLeavesActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class CalenderActivity extends AppCompatActivity {

    private EditText reason;
    private TextView date;
    private Button updateBtn;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);

        Toolbar toolbar = findViewById(R.id.calendar_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Holiday Calendar");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        reason = findViewById(R.id.reason_holiday);
        date = findViewById(R.id.holiday_date);
        updateBtn = findViewById(R.id.holiday_update_btn);

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDate(date);
            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String reason_holiday = reason.getText().toString();
                String holiday_date = date.getText().toString();

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("reason", reason_holiday);
                hashMap.put("date", holiday_date);


                if (!TextUtils.isEmpty(reason_holiday) || !TextUtils.isEmpty(holiday_date) ){
                    db.collection("HolidayEvents").document(holiday_date)
                            .set(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(CalenderActivity.this, "Successfully updated", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(CalenderActivity.this, DashActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        }
                    });
                }else {
                    Toast.makeText(CalenderActivity.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void selectDate(final TextView view){
        DatePickerDialog datePickerDialog = new DatePickerDialog(CalenderActivity.this);
        datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                String date_s = i + "-" + (i1+1) + "-" + i2;
                SimpleDateFormat dt = new SimpleDateFormat("yyyy-M-d");
                Date date;
                try {
                    date = dt.parse(date_s);
                    SimpleDateFormat dt1 = new SimpleDateFormat("dd-MMM-yyyy"); //Replace your own date format here
                    view.setText(dt1.format(date));
                } catch (ParseException e) {
                    Log.e("error", e+"");
                }

            }
        });
        datePickerDialog.show();
    }
}
