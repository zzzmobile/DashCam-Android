package com.whdiyo.dashcam.loopcam.activity;

import android.content.DialogInterface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.whdiyo.dashcam.loopcam.LoopCamApplication;
import com.whdiyo.dashcam.loopcam.R;
import com.whdiyo.dashcam.loopcam.adapter.SettingListAdapter;
import com.whdiyo.dashcam.loopcam.dialog.SelectAutoRecordDialog;
import com.whdiyo.dashcam.loopcam.dialog.SelectLoopTimeDialog;

public class SettingsActivity extends AppCompatActivity {
    ImageButton btnBack = null;
    SettingListAdapter listAdapter = null;
    ListView listView = null;

    String[] settingArray = {
            "Unit of Speed",
            "Loop Time(seconds)",
            "Auto Record(coming soon)",
            "Video Setting",
            "Delete All Videos",
            "Advanced(coming soon)"
    };

    private static int SECTION_UNIT_SPEED = 0;
    private static int SECTION_LOOP_TIME = 1;
    private static int SECTION_AUTO_RECORD = 2;
    private static int SECTION_VIDEO_SETTING = 3;
    private static int SECTION_DELETE_ALL = 4;
    private static int SECTION_ADVANCED = 5;

    SelectLoopTimeDialog dialogLoopTime = null;
    AlertDialog.Builder unitBuilder = null;
    SelectAutoRecordDialog dialogAutoRecord = null;
    AlertDialog.Builder qualityBuilder = null;
    AlertDialog.Builder confirmBuilder = null;

    CharSequence units[] = new CharSequence[] { "Meter of Hour(MPH)", "Kilometer of Hour(KPH)" };
    CharSequence qualities[] = new CharSequence[] { "Low", "High", "QVGA", "480p", "720p", "1080p", "2160p" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        setContentView(R.layout.activity_settings);
        initialize();
    }

    private void initialize() {
        AdView adView = findViewById(R.id.adView);
        adView.setVisibility(View.VISIBLE);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // initialize list
        listAdapter = new SettingListAdapter(this, settingArray);
        listView = findViewById(R.id.lst_menu);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onSelect(position);
            }
        });

        listView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                listView.getChildAt(2).setEnabled(false);
                listView.getChildAt(5).setEnabled(false);
            }
        });
        listAdapter.notifyDataSetChanged();

        // initialize dialogs
        dialogLoopTime = new SelectLoopTimeDialog(this);
        qualityBuilder = new AlertDialog.Builder(this);
        qualityBuilder.setTitle(getResources().getString(R.string.video_quality));
        qualityBuilder.setItems(qualities, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectVideoQuality(which);
            }
        });
        dialogAutoRecord = new SelectAutoRecordDialog(this);
        unitBuilder = new AlertDialog.Builder(this);
        unitBuilder.setTitle(getResources().getString(R.string.unit_speed));
        unitBuilder.setItems(units, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectSpeedUnit(which);
            }
        });
        confirmBuilder = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.delete_all))
                .setMessage(getResources().getString(R.string.delete_sentence))
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteAllVideos();
                    }
                })
                .setNegativeButton(R.string.cancel, null);
    }

    private void onSelect(int position) {
        if (position == SECTION_UNIT_SPEED) {
            unitBuilder.show();
        } else if (position == SECTION_LOOP_TIME) {
            dialogLoopTime.show();
        } else if (position == SECTION_AUTO_RECORD) {
            dialogAutoRecord.show();
        } else if (position == SECTION_VIDEO_SETTING) {
            qualityBuilder.show();
        } else if (position == SECTION_DELETE_ALL) {
            confirmBuilder.show();
        } else {
            // Advanced
        }
    }

    private void selectSpeedUnit(int position) {
        LoopCamApplication application = (LoopCamApplication)LoopCamApplication.getApplication();
        application.setSpeedUnit(position);
        reloadSettingMenu();
    }

    private void selectVideoQuality(int position) {
        LoopCamApplication application = (LoopCamApplication)LoopCamApplication.getApplication();
        application.setVideoSetting(position);
        reloadSettingMenu();
    }

    public void reloadSettingMenu() {
        listAdapter.notifyDataSetChanged();
    }

    private void deleteAllVideos() {

    }
}
