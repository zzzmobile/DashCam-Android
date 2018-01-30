package com.whdiyo.dashcam.loopcam.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
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
import com.otaliastudios.cameraview.Audio;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraLogger;
import com.otaliastudios.cameraview.CameraOptions;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.VideoQuality;
import com.sasank.roundedhorizontalprogress.RoundedHorizontalProgressBar;
import com.whdiyo.dashcam.loopcam.LoopCamApplication;
import com.whdiyo.dashcam.loopcam.R;
import com.whdiyo.dashcam.loopcam.util.Const;
import com.whdiyo.dashcam.loopcam.util.GPSTracker;
import com.whdiyo.dashcam.loopcam.util.Utils;

import java.io.File;
import java.util.Calendar;
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
    GPSTracker gpsTracker = null;
    AlertDialog gpsSettingDialog = null;
    double currentLatitude = 0.0;
    double currentLongitude = 0.0;
    float currentSpeed = 0.0f;
    float currentBearing = 0.0f;
    String currentFileName = "";

    CountDownTimer recordTimer = null;
    File videoFile = null;

    long freeDiskSize = 0;  // bytes
    long totalDiskSize = 0; // bytes

    private boolean isMute = false;
    private boolean isRecording = false;
    private int recordCount = 0;
    private VideoQuality videoSetting = VideoQuality.MAX_720P;

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

        gpsTracker = new GPSTracker(this);
        gpsSettingDialog = new AlertDialog.Builder(this)
                .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).create();
        gpsSettingDialog.setTitle("GPS unavailable");
        gpsSettingDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        btnMenu = findViewById(R.id.btn_menu);
        btnRecord = findViewById(R.id.btn_record);
        btnSave = findViewById(R.id.btn_save);
        btnSave.setEnabled(false);
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
                super.onVideoTaken(video);
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

        if (Build.VERSION.SDK_INT >= 23) {
            //do your check here
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA};

        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

        updateLocationInfo();
        updateTrafficInfo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        camera.start();
        updateLocationInfo();
        updateTrafficInfo();
        setLoopingTime();
        setVideoSetting();
        calcFreeSpace();
        refreshStorageBar();
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

    private boolean hasPermissions(Context context, String...permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
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
        File dataDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        String appPath = dataDir.toString() + "/video/";
        File appDir = new File(appPath);
        if (!appDir.exists())
            appDir.mkdir();

        return appDir.getAbsolutePath();
    }

    private void startVideoRecord() {
        btnSave.setEnabled(true);
        updateLocationInfo();
        String path = makeAppDirectory();
        Calendar calendar = Calendar.getInstance();
        String sYear = String.format(Locale.US, "%02d", calendar.get(Calendar.YEAR));
        String sMonth = String.format(Locale.US, "%02d", calendar.get(Calendar.MONTH) + 1);
        String sDate = String.format(Locale.US, "%02d", calendar.get(Calendar.DATE));
        String sHour = String.format(Locale.US, "%02d", calendar.get(Calendar.HOUR_OF_DAY));
        String sMinute = String.format(Locale.US, "%02d", calendar.get(Calendar.MINUTE));
        String sSecond = String.format(Locale.US, "%02d", calendar.get(Calendar.SECOND));

        currentFileName = sYear + sMonth + sDate + sHour + sMinute + sSecond + ".mp4";
        String filePath = path + "/" + currentFileName;

        videoFile = new File(filePath);
        camera.setPlaySounds(!isMute);
        if (isMute)
            camera.setAudio(Audio.OFF);
        else
            camera.setAudio(Audio.ON);

        setVideoSetting();
        camera.startCapturingVideo(videoFile, recordCount * 1000);
        saveStartLocationInfo(currentFileName);
        calcFreeSpace();
        refreshStorageBar();
    }

    private void stopVideoRecord() {
        btnSave.setEnabled(false);
        updateLocationInfo();
        saveEndLocationInfo();
        camera.stopCapturingVideo();
    }

    private void saveStartLocationInfo(String filename) {
        SharedPreferences pref = getSharedPreferences("VideoList", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putFloat(filename + "_slat", (float)currentLatitude);
        editor.putFloat(filename + "_slon", (float)currentLongitude);
        editor.apply();
    }

    private void saveEndLocationInfo() {
        SharedPreferences pref = getSharedPreferences("VideoList", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putFloat(currentFileName + "_elat", (float)currentLatitude);
        editor.putFloat(currentFileName + "_elon", (float)currentLongitude);
        editor.apply();
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
        else if (vs == Const.VIDEO_SETTING_MEDIUM)
            videoSetting = VideoQuality.MAX_720P;
        else if (vs == Const.VIDEO_SETTING_HIGH)
            videoSetting = VideoQuality.HIGHEST;
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

        LoopCamApplication application = (LoopCamApplication)LoopCamApplication.getApplication();
        freeSize = freeSize * 1024;
        int minutes = 0;
        if (application.appSetting.getVideoSetting() == Const.VIDEO_SETTING_LOW) {
            minutes = (int) (freeSize / 2.5);
        } else if (application.appSetting.getVideoSetting() == Const.VIDEO_SETTING_MEDIUM) {
            minutes = (int)(freeSize / 75);
        } else if (application.appSetting.getVideoSetting() == Const.VIDEO_SETTING_HIGH ) {
            minutes = (int)(freeSize / 112);
        }

        String strMinute = String.format(Locale.US, " (%d minutes)", minutes);
        txtStorageUsage.setText(strFreeSize + strMinute);
    }

    private void updateLocationInfo() {
        if (gpsTracker.canGetLocation()) {
            currentLatitude = gpsTracker.getLatitude();
            currentLongitude = gpsTracker.getLongitude();
            currentSpeed = gpsTracker.getSpeed();
            currentBearing = gpsTracker.getBearing();
        } else {
            if (!gpsSettingDialog.isShowing())
                gpsSettingDialog.show();
        }
    }

    private void updateTrafficInfo() {
        float speed = currentSpeed * 3600.0f;
        String strUnit = " MPH";
        LoopCamApplication application = (LoopCamApplication)LoopCamApplication.getApplication();
        if (application.appSetting.getSpeedUnit() == Const.KILOMETER_PER_HOUR) {
            speed = speed / 1000.0f;
            strUnit = " KPH";
        }
        String value = String.format(Locale.US, "%.1f", speed);
        txtSpeed.setText(value + strUnit);
    }
}
