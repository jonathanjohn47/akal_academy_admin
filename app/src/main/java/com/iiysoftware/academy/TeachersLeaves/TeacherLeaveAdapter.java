package com.iiysoftware.academy.TeachersLeaves;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iiysoftware.academy.R;

import java.util.ArrayList;

public class TeacherLeaveAdapter extends RecyclerView.Adapter<TeacherLeaveAdapter.ViewHolder> {
    private Context mCtx;
    private ArrayList<TeacherLeaveModel> mAllTeachersLeaves;

    public TeacherLeaveAdapter(Context mCtx, ArrayList<TeacherLeaveModel> mAllTeachersLeaves) {
        this.mCtx = mCtx;
        this.mAllTeachersLeaves = mAllTeachersLeaves;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_teacher_leave_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txtTeacherName.setText(mAllTeachersLeaves.get(position).getTeacherName());
        holder.txtTeacherID.setText(mAllTeachersLeaves.get(position).getTeacherID());
        holder.txtLeaveDate.setText(mAllTeachersLeaves.get(position).getDate());
        holder.txtLeaveReason.setText(mAllTeachersLeaves.get(position).getLeaveReason());
    }

    @Override
    public int getItemCount() {
        return mAllTeachersLeaves.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView txtTeacherName, txtTeacherID, txtLeaveDate, txtLeaveReason;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtTeacherName = itemView.findViewById(R.id.txtTeacherName);
            txtTeacherID = itemView.findViewById(R.id.txtTeacherID);
            txtLeaveDate = itemView.findViewById(R.id.txtLeaveDate);
            txtLeaveReason = itemView.findViewById(R.id.txtLeaveReason);
        }
    }
}
