package com.wayne.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

/**
 * @author sunbowen
 */
public class DownloadThreadAdapter extends RecyclerView.Adapter<DownloadThreadAdapter.ViewHolder> {

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView itemTextView;

        TextView progressTextView;

        ProgressBar progressBar;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            itemTextView = itemView.findViewById(R.id.itemTextView);
            progressTextView = itemView.findViewById(R.id.progressTextView);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }
    @NonNull
    @NotNull
    @Override
    public DownloadThreadAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.thread_view_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull DownloadThreadAdapter.ViewHolder holder, int position) {
         holder.itemTextView.setText(String.valueOf(position));
    }

    @Override
    public int getItemCount() {
        return DataManager.getThreadNum();
    }

}
