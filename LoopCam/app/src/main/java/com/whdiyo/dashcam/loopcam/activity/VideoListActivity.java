package com.whdiyo.dashcam.loopcam.activity;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.whdiyo.dashcam.loopcam.R;
import com.whdiyo.dashcam.loopcam.adapter.VideoListAdapter;
import com.whdiyo.dashcam.loopcam.adapter.VideoListItem;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Locale;

public class VideoListActivity extends AppCompatActivity {
    ImageButton btnBack = null;
    TextView txtTitle = null;
    ListView listView = null;
    VideoListAdapter adapter = null;
    ArrayList<VideoListItem> videoListItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        setContentView(R.layout.activity_video_list);
        initialize();
        loadVideoList();
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
        txtTitle = findViewById(R.id.txt_title);
        listView = findViewById(R.id.lst_video);
    }

    private void loadVideoList() {
        File dataDir = getFilesDir();
        String appPath = dataDir.toString() + "/video/";
        File videoDirectory = new File(appPath);
        File[] videoFiles = videoDirectory.listFiles();

        txtTitle.setText(String.format(Locale.US, "%d Videos", videoFiles.length));

        for (File file : videoFiles) {
            VideoListItem item = new VideoListItem();
            item.setVideoPath(this, file.getAbsolutePath());
            videoListItems.add(item);
        }

        adapter = new VideoListAdapter(this, videoListItems);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
        adapter.notifyDataSetChanged();
    }
}
