package dev.ujjwal.audiomediaplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.media.MediaPlayer;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    SeekBar seekBar;
    Button play_pause, forward, backward;

    MediaPlayer mediaPlayer;
    Runnable runnable;
    Handler handler;

    TextView name, duration;

    int currentPosition, totalDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        play_pause.setOnClickListener(this);
        forward.setOnClickListener(this);
        backward.setOnClickListener(this);

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                seekBar.setMax(mediaPlayer.getDuration());
                totalDuration = mediaPlayer.getDuration();
                changeSeekBar();
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser)
                    mediaPlayer.seekTo(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private void init() {
        seekBar = findViewById(R.id.seekBar);
        play_pause = findViewById(R.id.play_pause);
        forward = findViewById(R.id.forward);
        backward = findViewById(R.id.backward);

        mediaPlayer = MediaPlayer.create(this, R.raw.alpo_chhoate);
        handler = new Handler();

        name = findViewById(R.id.name);
        Field[] fields = R.raw.class.getFields();
        name.setText(fields[0].getName());
        duration = findViewById(R.id.duration);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.play_pause:
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    play_pause.setText("PLAY");
                } else {
                    mediaPlayer.start();
                    play_pause.setText("PAUSE");
                }
                break;
            case R.id.forward:
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + 5000);
                break;
            case R.id.backward:
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - 5000);
                break;
        }
    }

    private void changeSeekBar() {
        seekBar.setProgress(mediaPlayer.getCurrentPosition());
        updatePosition();

        runnable = new Runnable() {
            @Override
            public void run() {
                changeSeekBar();
            }
        };

        handler.postDelayed(runnable, 100);
    }

    private void updatePosition() {
        currentPosition = mediaPlayer.getCurrentPosition();

        String strCurrentPos = String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(currentPosition),
                TimeUnit.MILLISECONDS.toSeconds(currentPosition) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(currentPosition)));
        String strTotalPos = String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(totalDuration),
                TimeUnit.MILLISECONDS.toSeconds(totalDuration) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(totalDuration)));

        duration.setText(strCurrentPos + " / " + strTotalPos);
    }
}
