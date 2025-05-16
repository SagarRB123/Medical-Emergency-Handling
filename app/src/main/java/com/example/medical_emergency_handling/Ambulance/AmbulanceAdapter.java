package com.example.medical_emergency_handling.Ambulance;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.medical_emergency_handling.R;
import java.util.List;

public class AmbulanceAdapter extends RecyclerView.Adapter<AmbulanceAdapter.AmbulanceViewHolder> {
    private List<AmbulanceInfo> ambulances;
    private OnAmbulanceClickListener listener;

    public interface OnAmbulanceClickListener {
        void onAmbulanceClick(AmbulanceInfo ambulance);
    }

    public AmbulanceAdapter(List<AmbulanceInfo> ambulances, OnAmbulanceClickListener listener) {
        this.ambulances = ambulances;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AmbulanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ambulance_item, parent, false);
        return new AmbulanceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AmbulanceViewHolder holder, int position) {
        AmbulanceInfo ambulance = ambulances.get(position);
        holder.nameTextView.setText(ambulance.getName());
        holder.vicinityTextView.setText(ambulance.getVicinity());
        holder.statusTextView.setText("Status: " + ambulance.getStatus());

        holder.itemView.setOnClickListener(v -> {
            // Call the listener method to center the map (existing functionality)
            if (listener != null) {
                listener.onAmbulanceClick(ambulance);
            }

            // Launch the detail activity
            Intent intent = new Intent(holder.itemView.getContext(), AmbulanceDetailsActivity.class);
            intent.putExtra("place_id", ambulance.getReference());
            intent.putExtra("latitude", ambulance.getLatitude());
            intent.putExtra("longitude", ambulance.getLongitude());
            intent.putExtra("name", ambulance.getName());
            intent.putExtra("status", ambulance.getStatus());
            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return ambulances.size();
    }

    static class AmbulanceViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView vicinityTextView;
        TextView statusTextView;

        AmbulanceViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.ambulance_name);
            vicinityTextView = itemView.findViewById(R.id.ambulance_vicinity);
            statusTextView = itemView.findViewById(R.id.ambulance_status);
        }
    }

    public void updateAmbulances(List<AmbulanceInfo> newAmbulances) {
        this.ambulances = newAmbulances;
        notifyDataSetChanged();
    }
}