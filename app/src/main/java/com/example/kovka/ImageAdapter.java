package com.example.kovka;

//адаптер для списка

import android.view.LayoutInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
    private List<ImageModel> images;
    private OnImageActionListener listener;

    public interface OnImageActionListener {
        void onDelete(ImageModel image, int position);
        void onRename(ImageModel image, int position);
    }

    public ImageAdapter(List<ImageModel> images, OnImageActionListener listener) {
        this.images = images;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ImageModel image = images.get(position);
        holder.tvFileName.setText(image.getName());

        // Загрузка изображения через Glide
        Glide.with(holder.itemView.getContext())
                .load(ApiService.getBaseUrl() + image.getUrl())
                .into(holder.ivImage);

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDelete(image, position);
            }
        });

        holder.btnRename.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRename(image, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public void updateItem(int position, ImageModel newImage) {
        images.set(position, newImage);
        notifyItemChanged(position);
    }

    public void removeItem(int position) {
        images.remove(position);
        notifyItemRemoved(position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvFileName;
        Button btnDelete, btnRename;

        ViewHolder(View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivImage);
            tvFileName = itemView.findViewById(R.id.tvFileName);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnRename = itemView.findViewById(R.id.btnRename);
        }
    }
}
