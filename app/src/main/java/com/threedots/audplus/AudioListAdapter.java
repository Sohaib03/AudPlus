package com.threedots.audplus;

import android.graphics.Color;
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
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AudioListAdapter extends RecyclerView.Adapter<AudioListAdapter.AudioViewHolder> {

    private List<File> allFiles;
    private OnItemListClick onItemListClick;

    public boolean multiselect = false;
    public List<File> selectedItems = new ArrayList<>();

    public AudioListAdapter(List<File> allFiles, OnItemListClick onItemListClick){
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
    public void onBindViewHolder(@NonNull final AudioViewHolder holder, final int position) {

        holder.audioTitleText.setText(nameModifier(allFiles.get(position).getName(), 22));
        holder.dateText.setText(TimeFormatter.getTime(allFiles.get(position).lastModified()));

        if (selectedItems.contains(allFiles.get(position))) {
            holder.cardView.setCardBackgroundColor(Color.parseColor("#575870"));
        }else {
            holder.cardView.setCardBackgroundColor(Color.parseColor("#393B5B"));
        }

        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!multiselect) {
                    multiselect = true;
                    selectItem(holder, allFiles.get(position));
                    onItemListClick.onMultiSelectStarted();
                }
                return true;
            }
        });

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (multiselect)
                    selectItem(holder, allFiles.get(position));
                else
                    onItemListClick.onClickListener(allFiles.get(position), position);
            }
        });
    }



    public void selectItem(AudioViewHolder holder, File file) {
        if (selectedItems.contains(file)) {
            selectedItems.remove(file);
            holder.cardView.setCardBackgroundColor(Color.parseColor("#393B5B"));

            if (selectedItems.size() == 0) {
                multiselect = false;
                onItemListClick.onMultiSelectEnded();
            }
        } else {
            selectedItems.add(file);
            holder.cardView.setCardBackgroundColor(Color.parseColor("#575870"));
        }
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
        return allFiles.size();
    }

    public class AudioViewHolder extends RecyclerView.ViewHolder {

        private TextView audioTitleText;
        private TextView dateText;
        public CardView cardView;
        public AudioViewHolder(@NonNull View itemView) {
            super(itemView);

            audioTitleText = itemView.findViewById(R.id.audioTitle);
            dateText = itemView.findViewById(R.id.dateTextView);
            cardView = itemView.findViewById(R.id.cardView);
        }

    }

    public interface OnItemListClick {
        void onClickListener(File file, int position);
        void onMultiSelectStarted();
        void onMultiSelectEnded();
    }
}
