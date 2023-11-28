package com.example.swiftslotz.adapters;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
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



        final Dialog nagDialog = new Dialog(context,android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        nagDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        nagDialog.setCancelable(false);
        nagDialog.setContentView(R.layout.preview_image);
        ImageButton btnClose = (ImageButton) nagDialog.findViewById(R.id.close_preview);
        ImageView ivPreview = (ImageView)nagDialog.findViewById(R.id.iv_preview_image);
        Glide.with(context)
                .load(imageUrl)
                .into(ivPreview);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                nagDialog.show();
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nagDialog.dismiss();
            }
        });


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

//    private void showPreviewDialog(Context context, String imageUrl) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
//
//        LayoutInflater inflater = LayoutInflater.from(context);
//        View dialogView = inflater.inflate(R.layout.preview_image, null);
//
//        ImageView ivPreview = dialogView.findViewById(R.id.iv_preview_image);
//        ImageButton btnClose = dialogView.findViewById(R.id.close_preview);
//
//        Glide.with(context).load(imageUrl).into(ivPreview);
//
//        builder.setView(dialogView);
//        AlertDialog dialog = builder.create();
//
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        // Set other window properties as required
//
//        btnClose.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dialog.dismiss();
//            }
//        });
//
//        dialog.show();
//    }


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
