package com.example.medical_emergency_handling.Hospital;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.medical_emergency_handling.R;
import java.util.List;

public class HospitalAdapter extends RecyclerView.Adapter<HospitalAdapter.HospitalViewHolder> {
    private List<HospitalInfo> hospitals;
    private OnHospitalClickListener listener;

    public interface OnHospitalClickListener {
        void onHospitalClick(HospitalInfo hospital);
    }

    public HospitalAdapter(List<HospitalInfo> hospitals, OnHospitalClickListener listener) {
        this.hospitals = hospitals;
        this.listener = listener;
    }

    @NonNull
    @Override
    public HospitalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.hospital_item, parent, false);
        return new HospitalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HospitalViewHolder holder, int position) {
        HospitalInfo hospital = hospitals.get(position);
        holder.nameTextView.setText(hospital.getName());
        holder.vicinityTextView.setText(hospital.getVicinity());
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onHospitalClick(hospital);
            }
        });
    }

    @Override
    public int getItemCount() {
        return hospitals.size();
    }

    static class HospitalViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView vicinityTextView;

        HospitalViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.hospital_name);
            vicinityTextView = itemView.findViewById(R.id.hospital_vicinity);
        }
    }

}