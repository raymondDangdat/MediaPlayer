package comq.example.raymond.mediaplayer;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {
    private Button btnNext, btnPrevious, btnPause;
    private TextView songTextLabel;
    SeekBar songSeekBar;

    Thread updateSeekBar;

    String sName;


    static MediaPlayer myMediaPlayer;
    int position;
    ArrayList<File> mysSongs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        btnNext = findViewById(R.id.next);
        btnPause = findViewById(R.id.pause);
        btnPrevious = findViewById(R.id.previous);
        songTextLabel = findViewById(R.id.songLabel);
        songSeekBar = findViewById(R.id.seekBar);


        getSupportActionBar().setTitle("Now Playing");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

      updateSeekBar = new Thread(){
          @Override
          public void run() {
              int totalDuration = myMediaPlayer.getDuration();
              int currentPosition = 0;

              while (currentPosition<totalDuration){
                  try {

                      sleep(500);
                      currentPosition = myMediaPlayer.getCurrentPosition();
                      songSeekBar.setProgress(currentPosition);

                  }
                  catch (InterruptedException e){
                      e.printStackTrace();
                  }
              }
          }
      };

      if(myMediaPlayer != null){
          myMediaPlayer.stop();
          myMediaPlayer.release();
      }
        Intent i = getIntent();
      Bundle bundle = i.getExtras();

      mysSongs = (ArrayList) bundle.getParcelableArrayList("song");
      sName = mysSongs.get(position).getName().toString();
      String songName = i.getStringExtra("songName");

      songTextLabel.setText(songName);
      songTextLabel.setSelected(true);

      position = bundle.getInt("pos", 0);

        Uri uri = Uri.parse(mysSongs.get(position).toString());

        myMediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
        myMediaPlayer.start();
        songSeekBar.setMax(myMediaPlayer.getDuration());

        updateSeekBar.start();

        songSeekBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY );
        songSeekBar.getThumb().setColorFilter(getResources().getColor(R.color.colorPrimary),PorterDuff.Mode.SRC_IN);

        songSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

                myMediaPlayer.seekTo(seekBar.getProgress());
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                songSeekBar.setMax(myMediaPlayer.getDuration());
                if (myMediaPlayer.isPlaying()){
                    btnPause.setBackgroundResource(R.drawable.icon_play);
                    myMediaPlayer.pause();
                }else {
                    btnPause.setBackgroundResource(R.drawable.icon_pause);
                    myMediaPlayer.start();
                }
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myMediaPlayer.stop();
                myMediaPlayer.release();
                position = ((position +1 ) %mysSongs.size());

                Uri u = Uri.parse(mysSongs.get(position).toString());

                myMediaPlayer = MediaPlayer.create(getApplicationContext(), u);

                sName = mysSongs.get(position).getName().toString();
                songTextLabel.setText(sName);

                myMediaPlayer.start();
            }
        });

        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myMediaPlayer.stop();
                myMediaPlayer.release();

                position = ((position - 1)>0)?(mysSongs.size()-1):(position -1);

                Uri u = Uri.parse(mysSongs.get(position).toString());
                myMediaPlayer = MediaPlayer.create(getApplicationContext(), u);

                sName = mysSongs.get(position).getName().toString();
                songTextLabel.setText(sName);

                myMediaPlayer.start();

            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}
