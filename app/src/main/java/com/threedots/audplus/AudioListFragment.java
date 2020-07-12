package com.threedots.audplus;

import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.File;
import java.io.IOException;


public class AudioListFragment extends Fragment implements AudioListAdapter.OnItemListClick{

    private NavController navController;
    private ConstraintLayout playerSheet;
    private BottomSheetBehavior bottomSheetBehavior;
    private RecyclerView audioListRecyclerView;
    private File[] allFiles;
    private AudioListAdapter audioListAdapter;

    private MediaPlayer mediaPlayer = null;
    private boolean isPlaying = false;

    private ImageView play_btn;
    private ImageView rewind_btn;
    private ImageView forward_btn;

    private TextView fileNameTextView;
    private TextView playerStatus;

    private SeekBar playerSeekbar;
    private Handler seekbarHandler;
    private Runnable updateSeekbar;

    File playingFile;

    public AudioListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_audio_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);

        audioListRecyclerView = view.findViewById(R.id.audio_list_view);
        play_btn = view.findViewById(R.id.play_btn);
        rewind_btn = view.findViewById(R.id.rewind_btn);
        forward_btn = view.findViewById(R.id.forward_btn);

        fileNameTextView = view.findViewById(R.id.player_header_name);
        playerStatus = view.findViewById(R.id.player_header_title);

        playerSeekbar = view.findViewById(R.id.playerSeekBar);

        String recordFilePath = getActivity().getExternalFilesDir("/").getAbsolutePath();
        File directory = new File(recordFilePath);
        allFiles = directory.listFiles();

        audioListAdapter = new AudioListAdapter(allFiles, this);
        audioListRecyclerView.setHasFixedSize(true);
        audioListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        audioListRecyclerView.setAdapter(audioListAdapter);

        playerSheet = view.findViewById(R.id.player_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(playerSheet);
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN)
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                navController.navigate(R.id.action_audioListFragment_to_recordFragment);
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        play_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlaying) {
                    pauseAudio();
                } else if (playingFile != null) {
                    resumeAudio();
                }

            }
        });

        playerSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

                if (isPlaying) {
                    pauseAudio();
                } else if (playingFile != null) {
                    playAudio(playingFile);
                    pauseAudio();
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                mediaPlayer.seekTo(progress);
                resumeAudio();
            }
        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(audioListRecyclerView);
    }

    @Override
    public void onClickListener(File file, int position) {
        playingFile = file;
        if (isPlaying) {
            stopPlaying();
            playAudio(playingFile);
        } else {
            playAudio(playingFile);
        }

    }

    private void pauseAudio() {
        mediaPlayer.pause();
        play_btn.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_play, null));
        isPlaying = false;
        seekbarHandler.removeCallbacks(updateSeekbar);
    }
    private void resumeAudio() {
        mediaPlayer.start();
        isPlaying = true;
        play_btn.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_pause, null));

        updateHandler();
        seekbarHandler.postDelayed(updateSeekbar, 0);
    }

    private void stopPlaying() {
        isPlaying = false;
        mediaPlayer.stop();
        play_btn.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_play, null));
        playerStatus.setText("Stopped");
        seekbarHandler.removeCallbacks(updateSeekbar);
    }

    private void playAudio(File playingFile)  {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopPlaying();
                playerStatus.setText("Finished");
            }
        });

        try {
            mediaPlayer.setDataSource(playingFile.getAbsolutePath());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        fileNameTextView.setText(titleModifier(playingFile.getName()));
        playerStatus.setText("Playing");
        play_btn.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_pause, null));

        playerSeekbar.setMax(mediaPlayer.getDuration());
        seekbarHandler = new Handler();
        updateHandler();
        seekbarHandler.postDelayed(updateSeekbar, 0);

        isPlaying = true;
    }

    private String titleModifier(String name) {
        if (name.length() > 42) {
            return name.substring(0, 40) + "...";
        } else {
            return name;
        }
    }

    private void updateHandler() {
        updateSeekbar = new Runnable() {
            @Override
            public void run() {
                playerSeekbar.setProgress(mediaPlayer.getCurrentPosition());
                seekbarHandler.postDelayed(this, 100);
            }
        };
    }

    @Override
    public void onStop() {
        super.onStop();
        if (isPlaying) {
            stopPlaying();
        }
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT ) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            if (direction == ItemTouchHelper.LEFT) {
                Log.i("Swiped:", "onSwiped: " + position);
            }
        }
    };
}