package com.example.swiftslotz.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.swiftslotz.R;
import com.example.swiftslotz.utilities.AppointmentManager;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    private List<String> imageUrls;
    private Context context;
    private AppointmentManager appointmentManager;
    private String appointmentKey;

    public ImageAdapter(Context context, List<String> imageUrls, AppointmentManager appointmentManager, String appointmentKey) {
        this.context = context;
        this.imageUrls = imageUrls;
        this.appointmentManager = appointmentManager;
        this.appointmentKey = appointmentKey;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String imageUrl = imageUrls.get(position);
        Glide.with(context)
                .load(imageUrl)
                .into(holder.imageView);

        holder.deleteButton.setOnClickListener(v -> {
            // Delete the image from Firebase Storage and update the imgUrl list
            appointmentManager.deleteImageFromFirebaseStorage(imageUrl, appointmentKey, new AppointmentManager.ImageDeleteCallback() {
                @Override
                public void onSuccess(String message) {
                    // Remove the image URL from the list and notify the adapter
                    imageUrls.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, imageUrls.size());
                }

                @Override
                public void onError(String error) {
                    // Handle the error, e.g., show a Toast
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        FloatingActionButton deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_item_view);
            deleteButton = itemView.findViewById(R.id.fab_remove_image); // Replace with your delete button's ID
        }
    }
}
