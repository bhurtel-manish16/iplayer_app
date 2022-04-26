package com.example.iplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class PlaySong extends AppCompatActivity {

    TextView textView, startTime, endTime;
    ImageView previous, pause, next, random_play, loop_play;
    MediaPlayer mediaPlayer;
    ArrayList<File> songs;
    String textContent;
    int position;
    SeekBar seekBar;
    Thread updateSeek, updateTime;
    int count = 0;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
        updateSeek.interrupt();
        updateTime.interrupt();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_song);


        textView = findViewById(R.id.textView);
        previous = findViewById(R.id.previous);
        pause = findViewById(R.id.pause);
        next = findViewById(R.id.next);
        seekBar = findViewById(R.id.seekBar);
        random_play = findViewById(R.id.random_play);
        loop_play = findViewById(R.id.loop_play);
        startTime = findViewById(R.id.startTime);
        endTime = findViewById(R.id.endTime);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        songs = (ArrayList)bundle.getParcelableArrayList("songList");
        textContent = intent.getStringExtra("currentSong");
        textView.setText(textContent);
        textView.setSelected(true);
        position = intent.getIntExtra("position",0);
        Uri uri = Uri.parse(songs.get(position).toString());
        mediaPlayer = MediaPlayer.create(this, uri);
        mediaPlayer.start();
        seekBar.setMax(mediaPlayer.getDuration());
       endTime.setText(song_duration());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });
        updateSeek = new Thread(){
            @Override
            public void run() {
                int currentPosition = 0;
                try {
                    while (currentPosition<mediaPlayer.getDuration()){
                        currentPosition = mediaPlayer.getCurrentPosition();
                        seekBar.setProgress(currentPosition);
                        sleep(800);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        updateSeek.start();
        updateTime = new Thread(){
            @Override
            public void run() {
                super.run();
                int current_time = mediaPlayer.getCurrentPosition();
                startTime.setText(current_duration(current_time));
                if (current_time == mediaPlayer.getDuration()){
                    next_song();
                }
                new Handler().postDelayed(this, 100);
            }
        };
    updateTime.run();

    pause.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(mediaPlayer.isPlaying()){
                pause.setImageResource(R.drawable.play_button);
                mediaPlayer.pause();
            }else{
                pause.setImageResource(R.drawable.pause_button);
                mediaPlayer.start();
            }
        }
    });
    previous.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();
                if (position != 0) {
                    position -= 1;
                } else {
                    position = songs.size() - 1;
                }
                Uri uri = Uri.parse(songs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                mediaPlayer.start();
                pause.setImageResource(R.drawable.pause_button);
                seekBar.setMax(mediaPlayer.getDuration());
                textContent = songs.get(position).getName();
                textView.setText(textContent);
                endTime.setText(song_duration());
        }
    });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    if (position <= songs.size() - 1) {
                        position += 1;
                    } else {
                        position = 0;
                    }
                    Uri uri = Uri.parse(songs.get(position).toString());
                    mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                    mediaPlayer.start();
                    pause.setImageResource(R.drawable.pause_button);
                    seekBar.setMax(mediaPlayer.getDuration());
                    textContent = songs.get(position).getName();
                    textView.setText(textContent);
                    endTime.setText(song_duration());

            }
        });
        loop_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    mediaPlayer.setLooping(true);
                    mediaPlayer.seekTo(seekBar.getProgress());
                    loop_play.setImageResource(R.drawable.loop_lock_button);
            }
        });
    }
    public String song_duration() {
        // Setting time of timer according to duration of song
        int duration = mediaPlayer.getDuration();
        @SuppressLint("DefaultLocale") String time = String.format("%2d:%02d ",
                TimeUnit.MILLISECONDS.toMinutes(duration),
                TimeUnit.MILLISECONDS.toSeconds(duration) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
        );
        return time;
    }
    public String current_duration(int currentTime) {
        // Setting time of timer according to duration of song
        @SuppressLint("DefaultLocale") String time = String.format("%2d:%02d ",
                TimeUnit.MILLISECONDS.toMinutes(currentTime),
                TimeUnit.MILLISECONDS.toSeconds(currentTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(currentTime))
        );
        return time;
    }
    public  void next_song() {
        if (position!=songs.size()-1) {
            Log.d("pos1","Val :"+position);
           // mediaPlayer.reset();
            position += 1;
            Log.d("pos1","Val2 :"+position);
            Uri uri = Uri.parse(songs.get(position).toString());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            mediaPlayer.start();
            mediaPlayer.seekTo(seekBar.getProgress());
            textContent = songs.get(position).getName();
            textView.setText(textContent);
            endTime.setText(song_duration());
        }else{
            position = 0;
            Uri uri = Uri.parse(songs.get(position).toString());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            mediaPlayer.start();
            mediaPlayer.seekTo(seekBar.getProgress());
            textContent = songs.get(position).getName();
            textView.setText(textContent);
            endTime.setText(song_duration());
        }
    }

}