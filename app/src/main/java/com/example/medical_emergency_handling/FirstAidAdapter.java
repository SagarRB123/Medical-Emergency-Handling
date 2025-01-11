package com.example.medical_emergency_handling;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class FirstAidAdapter extends RecyclerView.Adapter<FirstAidAdapter.FirstAidViewHolder> {
    private List<FirstAidItem> firstAidItems;
    private List<FirstAidItem> filteredList;

    public FirstAidAdapter(List<FirstAidItem> firstAidItems) {
        this.firstAidItems = firstAidItems;
        this.filteredList = new ArrayList<>(firstAidItems);
    }

    @Override
    public FirstAidViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_first_aid_item, parent, false);
        return new FirstAidViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FirstAidViewHolder holder, int position) {
        FirstAidItem item = filteredList.get(position);
        holder.titleText.setText(item.getTitle());
        holder.descriptionText.setText(item.getDescription());
        holder.iconImage.setImageResource(item.getIconResource());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), FirstAidDetailActivity.class);
            intent.putExtra("title", item.getTitle());
            intent.putExtra("description", item.getDescription());
            intent.putExtra("steps", item.getSteps());
            intent.putExtra("symptoms", item.getSymptoms());
            intent.putExtra("image", item.getImageResource());
            intent.putExtra("video", item.getVideoResource());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    public void filter(String text) {
        filteredList.clear();
        if (text.isEmpty()) {
            filteredList.addAll(firstAidItems);
        } else {
            text = text.toLowerCase();
            for (FirstAidItem item : firstAidItems) {
                if (item.getTitle().toLowerCase().contains(text) ||
                        item.getDescription().toLowerCase().contains(text)) {
                    filteredList.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    static class FirstAidViewHolder extends RecyclerView.ViewHolder {
        ImageView iconImage;
        TextView titleText;
        TextView descriptionText;

        FirstAidViewHolder(View itemView) {
            super(itemView);
            iconImage = itemView.findViewById(R.id.iconImage);
            titleText = itemView.findViewById(R.id.titleText);
            descriptionText = itemView.findViewById(R.id.descriptionText);
        }
    }
}