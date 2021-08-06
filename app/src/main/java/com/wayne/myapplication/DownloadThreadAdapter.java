package com.wayne.myapplication;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

/**
 * @author sunbowen
 */
public class DownloadThreadAdapter extends RecyclerView.Adapter<DownloadThreadAdapter.ViewHolder> {
    int id;

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView itemTextView;
        TextView progressTextView;
        ProgressBar progressBar;


        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            itemTextView = itemView.findViewById(R.id.itemTextView);
            progressTextView = itemView.findViewById(R.id.progressTextView);
            progressBar = itemView.findViewById(R.id.progressBar);
        }

        @Override
        public void onClick(View v) {
            if (!DataManager.getDownloadThreads().get(id).getFinished()){
                Intent intent = new Intent("ControlThread");
                intent.setPackage(MyApplication.getContext().getPackageName());
                intent.putExtra("id",id);
                intent.putExtra("singleOrAll", DataManager.FLAG_SINGLE_CONTROL);
                MyApplication.getContext().sendBroadcast(intent);
                Log.d("Wayne", "暂停id为的线程"+id);
            }
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
        id = holder.getPosition();
    }

    @Override
    public int getItemCount() {
        return DataManager.getThreadNum();
    }

}
