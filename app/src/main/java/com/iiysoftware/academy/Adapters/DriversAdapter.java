package com.iiysoftware.academy.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.iiysoftware.academy.MapsActivity;
import com.iiysoftware.academy.Models.Drivers;
import com.iiysoftware.academy.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;


public class DriversAdapter extends FirestoreRecyclerAdapter<Drivers, DriversAdapter.ViewHolder> {

    private Context mContext;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;

    OnItemClick onItemClick;
    OnShowclick onShowclick;

    public DriversAdapter(Context mContext, FirestoreRecyclerOptions<Drivers> options) {
        super(options);
        this.mContext = mContext;
        this.notifyDataSetChanged();
    }

    public void setOnItemClick(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    public interface OnItemClick {

        void getPosition(String userId);
    }

    public void setOnShowClick(OnShowclick onShowclick) {
        this.onShowclick = onShowclick;
    }

    public interface OnShowclick {

        void getPosition(String userId);

    }

    @Override
    protected void onBindViewHolder(@NonNull final ViewHolder holder, final int position, @NonNull final Drivers model) {

        Picasso picasso = Picasso.get();
        picasso.setIndicatorsEnabled(false);
        picasso.load(model.getImage()).placeholder(R.drawable.avatar).into(holder.profile);
        holder.name.setText(model.getUser_name());
        holder.email.setText(model.getUser_email());

        holder.assign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick.getPosition(model.getPush_id());
            }
        });

        db.collection("AssignedRoutes").document(model.getPush_id())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable final DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if (documentSnapshot.exists())
                        {
                            holder.unassign.setVisibility(View.VISIBLE);
                            holder.assign.setText("See route");
                            holder.assign.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    onShowclick.getPosition(model.getPush_id());
                                }
                            });
                        }
                    }
                });

        holder.unassign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("Routes").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot doc : task.getResult()){
                                HashMap<String, Object> hashMap1 = new HashMap<>();
                                hashMap1.put("isAssigned", false);

                                db.collection("Routes").document(doc.get("uid").toString()).update(hashMap1);
                            }
                        }
                    }
                });
                db.collection("AssignedRoutes").document(model.getPush_id()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        holder.unassign.setVisibility(View.GONE);
                        holder.assign.setText("Assign Route");
                        Toast.makeText(mContext, "unassigned", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.single_driver_item, viewGroup, false);

        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView profile;
        private TextView name, email;
        private CardView cardView;
        private Button assign, unassign;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.driver_name);
            cardView = itemView.findViewById(R.id.student_card);
            profile = itemView.findViewById(R.id.driver_image);
            email = itemView.findViewById(R.id.driver_email);
            assign = itemView.findViewById(R.id.assign_route_btn);
            unassign = itemView.findViewById(R.id.unassign_route_btn);

        }

    }
}
