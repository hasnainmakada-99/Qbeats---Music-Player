package com.example.qbeats_musicplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Currency;

public class playsong extends AppCompatActivity {
    TextView song_name, startDuration, endDuration;
    ImageButton prev, play, next;
    String songname;
    SeekBar seekBar;
    MediaPlayer mediaPlayer;
    ArrayList<File> song;
    Thread updateSeekbar;
    int position;
    int totalDuration;
    int startDurationTime;
    public void onDestroy(){
        super.onDestroy();
        mediaPlayer.stop();
        updateSeekbar.interrupt();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playsong);
        song_name=findViewById(R.id.song_name);
        prev=findViewById(R.id.prev);
        play=findViewById(R.id.play);
        next=findViewById(R.id.next);
        startDuration=findViewById(R.id.startDuration);
        endDuration=findViewById(R.id.endDuration);
        seekBar=findViewById(R.id.seekBar);
        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        song=(ArrayList) bundle.getParcelableArrayList("getsongs");
        songname=intent.getStringExtra("currentsongs");
        song_name.setText(songname);
        song_name.setSelected(true);
        position=intent.getIntExtra("position", 0);
        Uri uri= Uri.parse(song.get(position).toString());
        mediaPlayer=MediaPlayer.create(getApplicationContext(), uri);
        mediaPlayer.start();
        play.setImageResource(R.drawable.pause_foreground);
        seekBar.setMax(mediaPlayer.getDuration());
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



        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mediaPlayer!=null){
                    try {
                        if(mediaPlayer.isPlaying()) {
                        Message msg=new Message();
                        msg.what=mediaPlayer.getCurrentPosition();
                        handler.sendMessage(msg);
                        Thread.sleep(1000);
                        }
                        }
                        catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }
        }).start();

        updateSeekbar=new Thread(){
            @Override
            public void run() {
                int currentPosition=0;
                try{
                    while(currentPosition<mediaPlayer.getDuration()){
                        currentPosition=mediaPlayer.getCurrentPosition();
                        seekBar.setProgress(currentPosition);
                        sleep(200);
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        };

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                String totalTime=createTimeLabel(mediaPlayer.getDuration());
                endDuration.setText(totalTime);
                updateSeekbar.start();
            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                    play.setImageResource(R.drawable.play_foreground);
                }
                else{
                    play.setImageResource(R.drawable.pause_foreground);
                    mediaPlayer.start();
                }
            }
        });

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();
                if(position!=0){
                    position=position-1;
                }
                else{
                    position=song.size()-1;
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                }
                Uri uri=Uri.parse(song.get(position).toString());
                mediaPlayer=MediaPlayer.create(playsong.this, uri);
                mediaPlayer.start();
                play.setImageResource(R.drawable.pause_foreground);
                songname=song.get(position).getName();
                song_name.setText(songname);
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();
                if(position<song.size()-1){
                    position=position+1;
                }
                else{
                    position=0;
                }
                Uri uri=Uri.parse(song.get(position).toString());
                mediaPlayer=MediaPlayer.create(playsong.this, uri);
                seekBar.setProgress(mediaPlayer.getCurrentPosition());
                mediaPlayer.start();
                play.setImageResource(R.drawable.pause_foreground);
                songname=song.get(position).getName();
                song_name.setText(songname);

            }
        });
    }

    @SuppressLint("HandlerLeak")
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
                int Current=msg.what;
                String ctim=createTimeLabel(Current);
                startDuration.setText(ctim);
        }
    };

    public String createTimeLabel(int duration) {
        String timeLabel = "";
        int min = duration / 1000 / 60;
        int sec = duration / 1000 % 60;
        timeLabel += min + ":";
        if (sec < 10) timeLabel += "0";
        timeLabel += sec;

        return timeLabel;
    }
}