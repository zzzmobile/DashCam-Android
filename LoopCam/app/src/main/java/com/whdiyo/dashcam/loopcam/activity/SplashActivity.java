package com.whdiyo.dashcam.loopcam.activity;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.whdiyo.dashcam.loopcam.R;

public class SplashActivity extends AppCompatActivity implements View.OnClickListener, RewardedVideoAdListener {
    ImageButton btnStart = null;
    TextView txtCounter = null;
    ProgressBar progressBar = null;
    boolean bStart = false;
    boolean bFailed = false;
    private RewardedVideoAd rewardedVideoAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        setContentView(R.layout.activity_splash);
        initialize();
    }

    private void initialize() {
        MobileAds.initialize(this, "ca-app-pub-6714239015427657~6642668315");

        btnStart = findViewById(R.id.btn_start);
        btnStart.setOnClickListener(this);
        txtCounter = findViewById(R.id.txt_counter);
        progressBar = findViewById(R.id.progress_bar);

        new CountDownTimer(30000, 1000) {
            public void onTick(long millisUntilFinished) {
                txtCounter.setText(String.valueOf(millisUntilFinished / 1000));
            }

            public void onFinish() {
                startApp();
            }
        }.start();

        rewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        rewardedVideoAd.setRewardedVideoAdListener(this);
        loadRewardedVideoAd();
    }

    @Override
    protected void onResume() {
        rewardedVideoAd.resume(this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        rewardedVideoAd.pause(this);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        rewardedVideoAd.destroy(this);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_start) {
            startApp();
        }
    }

    private void startApp() {
        if (bStart) return;
        bStart = true;

        if (bFailed)
            startMainActivity();
        else {
            progressBar.setVisibility(View.VISIBLE);
            showRewardedVideo();
        }
    }

    private void startMainActivity() {
        progressBar.setVisibility(View.VISIBLE);
        Intent i = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }

    private void loadRewardedVideoAd() {
        rewardedVideoAd.loadAd("ca-app-pub-6714239015427657/7871989509",
                new AdRequest.Builder().build());
    }

    private void showRewardedVideo() {
        if (rewardedVideoAd.isLoaded()) {
            rewardedVideoAd.show();
        }
    }

    @Override
    public void onRewardedVideoAdLoaded() {
        showRewardedVideo();
    }

    @Override
    public void onRewardedVideoAdOpened() {

    }

    @Override
    public void onRewardedVideoStarted() {

    }

    @Override
    public void onRewardedVideoAdClosed() {
        startMainActivity();
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {

    }

    @Override
    public void onRewardedVideoAdLeftApplication() {

    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {
        Toast.makeText(this, "Google Reward Video load failed.", Toast.LENGTH_SHORT).show();
        bFailed = true;

        if (bStart)
            startMainActivity();
    }
}
