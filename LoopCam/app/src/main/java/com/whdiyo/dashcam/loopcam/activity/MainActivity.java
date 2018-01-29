package com.whdiyo.dashcam.loopcam.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.StatFs;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraLogger;
import com.otaliastudios.cameraview.CameraOptions;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.VideoQuality;
import com.sasank.roundedhorizontalprogress.RoundedHorizontalProgressBar;
import com.whdiyo.dashcam.loopcam.LoopCamApplication;
import com.whdiyo.dashcam.loopcam.R;
import com.whdiyo.dashcam.loopcam.util.Const;
import com.whdiyo.dashcam.loopcam.util.Utils;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private CameraView camera;
    ImageButton btnMenu = null;
    ImageButton btnRecord = null;
    ImageButton btnSave = null;
    ImageButton btnSound = null;
    ImageButton btnRotate = null;
    TextView txtStorageUsage = null;
    TextView txtRecCounter = null;
    TextView txtSpeed = null;
    RoundedHorizontalProgressBar progressBar = null;

    InterstitialAd interstitialAd;

    CountDownTimer recordTimer = null;
    File videoFile = null;

    long freeDiskSize = 0;  // bytes
    long totalDiskSize = 0; // bytes

    private boolean isMute = false;
    private boolean isRecording = false;
    private int recordCount = 0;
    private VideoQuality videoSetting = VideoQuality.MAX_720P;
    private float speed = 0.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);

        setContentView(R.layout.activity_main);
        initialize();
    }

    private void initialize() {
        AdView adView = findViewById(R.id.adView);
        adView.setVisibility(View.VISIBLE);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit_id));
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                showMenu();
            }
        });
        AdRequest adRequest1 = new AdRequest.Builder().build();
        interstitialAd.loadAd(adRequest1);

        btnMenu = findViewById(R.id.btn_menu);
        btnRecord = findViewById(R.id.btn_record);
        btnSave = findViewById(R.id.btn_save);
        btnSound = findViewById(R.id.btn_mute);
        btnRotate = findViewById(R.id.btn_rotate);
        btnMenu.setOnClickListener(this);
        btnRecord.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        btnSound.setOnClickListener(this);
        btnRotate.setOnClickListener(this);

        progressBar = findViewById(R.id.progress_bar);
        txtStorageUsage = findViewById(R.id.txt_storage_usage);
        txtRecCounter = findViewById(R.id.txt_record_counter);
        txtSpeed = findViewById(R.id.txt_speed);

        CameraLogger.setLogLevel(CameraLogger.LEVEL_VERBOSE);
        camera = findViewById(R.id.camera);
        camera.addCameraListener(new CameraListener() {
            @Override
            public void onCameraOpened(CameraOptions options) {
                super.onCameraOpened(options);
            }

            @Override
            public void onVideoTaken(File video) {

            }
        });

        setLoopingTime();
        recordTimer = new CountDownTimer(recordCount * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
                int seconds = (int)(recordCount - millisUntilFinished / 1000);
                txtRecCounter.setText(Utils.getTimerString(seconds * 1000));
            }

            public void onFinish() {
                txtRecCounter.setText("00:00");
                initRecordTimer();
            }
        };

        setVideoSetting();
        calcFreeSpace();
        refreshStorageBar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        camera.start();
        setLoopingTime();
        setVideoSetting();
    }

    @Override
    protected void onPause() {
        super.onPause();
        camera.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        camera.destroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean valid = true;
        for (int grantResult : grantResults) {
            valid = valid && grantResult == PackageManager.PERMISSION_GRANTED;
        }
        if (valid && !camera.isStarted()) {
            camera.start();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_menu) {
            onMenu();
        } else if (v.getId() == R.id.btn_record) {
            onRecord();
        } else if (v.getId() == R.id.btn_save) {
            onSave();
        } else if (v.getId() == R.id.btn_mute) {
            onMute();
        } else if (v.getId() == R.id.btn_rotate) {
            onRotateCamera();
        }
    }

    private void showInterstitial() {
        // Show the ad if it's ready. Otherwise toast and restart the game.
        if (interstitialAd != null && interstitialAd.isLoaded()) {
            interstitialAd.show();
        } else {
            Toast.makeText(this, "Google InterstitialAd load failed.", Toast.LENGTH_SHORT).show();
            showMenu();
        }
    }

    private String makeAppDirectory() {
        File dataDir = getFilesDir();
        String appPath = dataDir.toString() + "/video/";
        File appDir = new File(appPath);
        if (!appDir.exists())
            appDir.mkdir();

        return appDir.getAbsolutePath();
    }

    private void startVideoRecord() {
        String path = makeAppDirectory();
        Calendar calendar = Calendar.getInstance();
        String sYear = String.format(Locale.US, "%02d", calendar.get(Calendar.YEAR));
        String sMonth = String.format(Locale.US, "%02d", calendar.get(Calendar.MONTH));
        String sDate = String.format(Locale.US, "%02d", calendar.get(Calendar.DATE));
        String sHour = String.format(Locale.US, "%02d", calendar.get(Calendar.HOUR_OF_DAY));
        String sMinute = String.format(Locale.US, "%02d", calendar.get(Calendar.MINUTE));
        String sSecond = String.format(Locale.US, "%02d", calendar.get(Calendar.SECOND));

        String fileName = sYear + "-" + sMonth + "-" + sDate + " " + sHour + ":" + sMinute + ":" + sSecond + ".mp4";
        String filePath = path + "/" + fileName;

        videoFile = new File(filePath);
        camera.setPlaySounds(!isMute);

        setVideoSetting();
        camera.startCapturingVideo(videoFile, recordCount * 1000);
    }

    private void stopVideoRecord() {
        camera.stopCapturingVideo();
    }

    private void onMenu() {
        showInterstitial();
    }

    private void showMenu() {
        Intent i = new Intent(MainActivity.this, MenuActivity.class);
        startActivity(i);
    }

    private void onRecord() {
        isRecording = !isRecording;
        if (isRecording) {
            btnRecord.setImageResource(R.drawable.button_recording);
            btnSound.setVisibility(View.GONE);
            btnRotate.setVisibility(View.GONE);
        } else {
            btnRecord.setImageResource(R.drawable.button_record);
            btnSound.setVisibility(View.VISIBLE);
            btnRotate.setVisibility(View.VISIBLE);
        }

        if (isRecording) {
            startVideoRecord();
            initRecordTimer();
        } else {
            stopVideoRecord();
            recordTimer.cancel();
            txtRecCounter.setText("00:00");
        }
    }

    private void onSave() {
        stopVideoRecord();
        startVideoRecord();
        initRecordTimer();
    }

    private void onMute() {
        isMute = !isMute;
        if (isMute) {
            btnSound.setImageResource(R.drawable.button_sound_off);
        } else {
            btnSound.setImageResource(R.drawable.button_sound_on);
        }
    }

    private void onRotateCamera() {
        camera.toggleFacing();
    }

    private void initRecordTimer() {
        recordTimer.cancel();
        recordTimer.onTick(recordCount * 1000);
        recordTimer.start();
    }

    private void setLoopingTime() {
        LoopCamApplication application = (LoopCamApplication)LoopCamApplication.getApplication();
        recordCount = application.appSetting.getLoopSeconds();
    }

    private void setVideoSetting() {
        LoopCamApplication application = (LoopCamApplication)LoopCamApplication.getApplication();
        int vs = application.appSetting.getVideoSetting();
        if (vs == Const.VIDEO_SETTING_LOW)
            videoSetting = VideoQuality.LOWEST;
        else if (vs == Const.VIDEO_SETTING_HIGH)
            videoSetting = VideoQuality.HIGHEST;
        else if (vs == Const.VIDEO_SETTING_QVGA)
            videoSetting = VideoQuality.MAX_QVGA;
        else if (vs == Const.VIDEO_SETTING_480P)
            videoSetting = VideoQuality.MAX_480P;
        else if (vs == Const.VIDEO_SETTING_720P)
            videoSetting = VideoQuality.MAX_720P;
        else if (vs == Const.VIDEO_SETTING_1080P)
            videoSetting = VideoQuality.MAX_1080P;
        else if (vs == Const.VIDEO_SETTING_2160P)
            videoSetting = VideoQuality.MAX_2160P;
        camera.setVideoQuality(videoSetting);
    }

    private void calcFreeSpace() {
        File pathOS = Environment.getExternalStorageDirectory();
        StatFs statOS = new StatFs(pathOS.getPath());
        totalDiskSize = statOS.getTotalBytes();
        freeDiskSize = statOS.getFreeBytes();
    }

    private void refreshStorageBar() {
        progressBar.setProgress((int)((freeDiskSize * 1.0f / totalDiskSize) * 100));

        float freeSize = freeDiskSize / 1024.0f / 1024.0f / 1024.0f;
        String strFreeSize = String.format(Locale.US, "Available: %.1fGB", freeSize);
        txtStorageUsage.setText(strFreeSize);
    }
}
