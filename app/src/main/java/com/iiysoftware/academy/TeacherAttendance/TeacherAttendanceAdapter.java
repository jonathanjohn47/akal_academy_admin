package com.iiysoftware.academy.TeacherAttendance;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.iiysoftware.academy.R;

import java.util.ArrayList;
import java.util.List;

public class TeacherAttendanceAdapter extends RecyclerView.Adapter<TeacherAttendanceAdapter.ViewHolder> {

    private Context mCtx;
    private String date;
    private ArrayList<TeacherAttendanceModel> mAllTeacherAttendance;

    public TeacherAttendanceAdapter(Context mCtx, String date, ArrayList<TeacherAttendanceModel> mAllTeacherAttendance) {
        this.mCtx = mCtx;
        this.date = date;
        this.mAllTeacherAttendance = mAllTeacherAttendance;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_teacher_attendance_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.txtTeacherName.setText(mAllTeacherAttendance.get(position).getTeacherName());
        holder.txtTeacherId.setText(mAllTeacherAttendance.get(position).getTeacherID());
        final List<String> possiblevalues = new ArrayList<>();
        possiblevalues.add("P");
        possiblevalues.add("L");
        possiblevalues.add("A");
        ArrayAdapter<String> attendancevaluesadapter = new ArrayAdapter<String>(mCtx, android.R.layout.simple_list_item_1, possiblevalues);
        attendancevaluesadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.teacherattendancevalue.setAdapter(attendancevaluesadapter);

        holder.teacherattendancevalue.setSelection(setSelectionPosition(mAllTeacherAttendance.get(position).getAttendanceValue()));

        holder.teacherattendancevalue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long id) {
                holder.teacherattendancevalue.setSelection(i);
                changeAttendanceValue(position, possiblevalues.get(i)); //To interfere with the arraylist we receive in the constructor
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return mAllTeacherAttendance.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView txtTeacherName, txtTeacherId;
        private Spinner teacherattendancevalue;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtTeacherName = itemView.findViewById(R.id.txtTeacherName);
            txtTeacherId = itemView.findViewById(R.id.txtTeacherID);
            teacherattendancevalue = itemView.findViewById(R.id.attendanceValue);
        }
    }

    private int setSelectionPosition(String attendancevalue){
        if (attendancevalue.equals("P")){
            return 0;
        }
        else if (attendancevalue.equals("L")){
            return 1;
        }
        else if (attendancevalue.equals("A")){
            return 2;
        }
        else if (attendancevalue == null){
            return 0;
        }
        else{
            return 0;
        }
    }

    private void changeAttendanceValue(int position, String value){
        String teachername = mAllTeacherAttendance.get(position).getTeacherName();
        String teacherid = mAllTeacherAttendance.get(position).getTeacherID();
        String attendancevalue = value;
        TeacherAttendanceModel attendanceModel = new TeacherAttendanceModel(teachername, teacherid, attendancevalue);

        mAllTeacherAttendance.set(position, attendanceModel);
    }

    public void saveData(){
        for (int i = 0; i <mAllTeacherAttendance.size() ; i++) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("Attendance")
                    .document(date)
                    .collection("List")
                    .document(mAllTeacherAttendance.get(i).getTeacherID())
                    .set(mAllTeacherAttendance.get(i))
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("Error", e+"");
                        }
                    });

        }
        Toast.makeText(mCtx, "All Data Saved", Toast.LENGTH_SHORT).show();
    }
}
