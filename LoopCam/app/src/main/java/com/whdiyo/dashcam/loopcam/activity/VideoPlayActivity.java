package com.whdiyo.dashcam.loopcam.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

import com.afollestad.easyvideoplayer.EasyVideoCallback;
import com.afollestad.easyvideoplayer.EasyVideoPlayer;
import com.whdiyo.dashcam.loopcam.R;

import java.util.Locale;

public class VideoPlayActivity extends AppCompatActivity implements EasyVideoCallback {
    private ImageButton btnBack = null;
    private TextView txtTitle = null;
    private TextView txtStatus = null;
    private EasyVideoPlayer player = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        setContentView(R.layout.activity_video_play);
        initialize();
    }

    private void initialize() {
        btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        txtTitle = findViewById(R.id.txt_title);
        txtStatus = findViewById(R.id.txt_status);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        player = findViewById(R.id.video_player);
        player.setCallback(this);

        if (!bundle.isEmpty()) {
            String videoName = bundle.getString("video_file_name");
            txtTitle.setText(videoName);
            String videoPath = bundle.getString("video_file_path");
            player.setSource(Uri.parse(videoPath));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // Make sure the player stops playing if the user presses the home button.
        player.pause();
    }

    // Methods for the implemented EasyVideoCallback

    @Override
    public void onPreparing(EasyVideoPlayer player) {
        // TODO handle if needed
    }

    @Override
    public void onPrepared(EasyVideoPlayer player) {
        // TODO handle
    }

    @Override
    public void onBuffering(int percent) {
        // TODO handle if needed
        txtStatus.setText(String.format(Locale.US, "Buffering %d percent", percent));
    }

    @Override
    public void onError(EasyVideoPlayer player, Exception e) {
        // TODO handle
        txtStatus.setText(e.getMessage());
    }

    @Override
    public void onCompletion(EasyVideoPlayer player) {
        // TODO handle if needed
        txtStatus.setVisibility(View.GONE);
        player.start();
    }

    @Override
    public void onRetry(EasyVideoPlayer player, Uri source) {
        // TODO handle if used
    }

    @Override
    public void onSubmit(EasyVideoPlayer player, Uri source) {
        // TODO handle if used
    }

    @Override
    public void onStarted(EasyVideoPlayer player) {
        // TODO handle if needed
    }

    @Override
    public void onPaused(EasyVideoPlayer player) {
        // TODO handle if needed
    }
}
