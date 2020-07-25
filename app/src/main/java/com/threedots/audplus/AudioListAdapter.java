package com.threedots.audplus;

import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;

public class AudioListAdapter extends RecyclerView.Adapter<AudioListAdapter.AudioViewHolder> {

    private File[] allFiles;
    private OnItemListClick onItemListClick;


    public AudioListAdapter(File[] allFiles, OnItemListClick onItemListClick){
        this.allFiles = allFiles;
        this.onItemListClick = onItemListClick;
    }

    @NonNull
    @Override
    public AudioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.singlelistitem, parent, false);
        return new AudioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AudioViewHolder holder, int position) {

        holder.audioTitleText.setText(nameModifier(allFiles[position].getName(), 22));
        holder.dateText.setText(TimeFormatter.getTime(allFiles[position].lastModified()));
    }

    private String nameModifier(String name, int len) {
        if (name.length() > len+2) {
            return name.substring(0, len) + "...";
        } else {
            return name;
        }
    }

    @Override
    public int getItemCount() {
        return allFiles.length;
    }

    public class AudioViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView audioTitleText;
        private TextView dateText;
        private CardView cardView;
        public AudioViewHolder(@NonNull View itemView) {
            super(itemView);

            audioTitleText = itemView.findViewById(R.id.audioTitle);
            dateText = itemView.findViewById(R.id.dateTextView);
            cardView = itemView.findViewById(R.id.cardView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onItemListClick.onClickListener(allFiles[getAdapterPosition()], getAdapterPosition());
        }
    }

    public interface OnItemListClick {
        void onClickListener(File file, int position);
    }
}
