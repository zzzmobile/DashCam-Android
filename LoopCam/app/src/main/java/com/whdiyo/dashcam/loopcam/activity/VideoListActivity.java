package com.whdiyo.dashcam.loopcam.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.whdiyo.dashcam.loopcam.R;
import com.whdiyo.dashcam.loopcam.adapter.VideoListAdapter;
import com.whdiyo.dashcam.loopcam.adapter.VideoListItem;
import com.whdiyo.dashcam.loopcam.dialog.RenameVideoDialog;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Locale;

public class VideoListActivity extends AppCompatActivity {
    ImageButton btnBack = null;
    TextView txtTitle = null;
    TextView txtNoVideo = null;
    ListView listView = null;
    VideoListAdapter adapter = null;
    ArrayList<VideoListItem> videoListItems = new ArrayList<>();

    AlertDialog.Builder videoMenuBuilder = null;
    CharSequence menuItems[] = new CharSequence[] { "View", "Rename", "GPS Info", "Delete" };
    RenameVideoDialog dialogRename = null;
    AlertDialog.Builder confirmBuilder = null;
    AlertDialog gpsAlert = null;

    int currentIndex = 0;


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
        txtNoVideo = findViewById(R.id.txt_no_video);
        listView = findViewById(R.id.lst_video);

        videoMenuBuilder = new AlertDialog.Builder(this);
        videoMenuBuilder.setTitle("");
        videoMenuBuilder.setItems(menuItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                executeMenu(which);
            }
        });
        dialogRename = new RenameVideoDialog(this);
        confirmBuilder = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.delete_file))
                .setMessage(getResources().getString(R.string.delete_file_sentence))
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteVideo();
                    }
                })
                .setNegativeButton(R.string.cancel, null);
        gpsAlert = new AlertDialog.Builder(this).create();
        gpsAlert.setTitle("GPS Info");
        gpsAlert.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.okay), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
    }

    private void loadVideoList() {
        File dataDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        String appPath = dataDir.toString() + "/video/";
        File videoDirectory = new File(appPath);
        File[] videoFiles = videoDirectory.listFiles();

        if (videoFiles == null || videoFiles.length == 0) {
            txtNoVideo.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
            return;
        }

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
                currentIndex = position;
                videoMenuBuilder.show();
            }
        });
        adapter.notifyDataSetChanged();
    }

    private void executeMenu(int position) {
        switch (position) {
            case 0:     // View
            {
                Intent i = new Intent(VideoListActivity.this, VideoPlayActivity.class);
                i.putExtra("video_file_path", videoListItems.get(currentIndex).getFilePath());
                i.putExtra("video_file_name", videoListItems.get(currentIndex).getFileName());
                startActivity(i);
            }
                break;
            case 1:     // Rename
            {
                dialogRename.setFileName(videoListItems.get(currentIndex).getFileName());
                dialogRename.show();
            }
                break;
            case 2:     // GPS Info
            {
                VideoListItem selectItem = videoListItems.get(currentIndex);
                String strStart = "Start: " + getLatitudeString(selectItem.getStartLatitude()) + "  " + getLongitudeString(selectItem.getStartLongitude());
                String strEnd = "End: " + getLatitudeString(selectItem.getEndLatitude()) + "  " + getLongitudeString(selectItem.getEndLongitude());
                gpsAlert.setMessage(strStart + "\n" + strEnd);
                gpsAlert.show();
            }
                break;
            case 3:     // Delete
                confirmBuilder.show();
                break;
        }
    }

    private void deleteVideo() {
        VideoListItem selectItem = videoListItems.get(currentIndex);
        String path = selectItem.getFilePath();
        File file = new File(path);
        if (!file.delete()) {
            Toast.makeText(this, "You have not permission for delete.", Toast.LENGTH_SHORT).show();
        } else {
            selectItem.removeCoordinate(this);
            videoListItems.remove(currentIndex);
            adapter.notifyDataSetChanged();

            txtTitle.setText(String.format(Locale.US, "%d Videos", videoListItems.size()));
            if (videoListItems.size() == 0) {
                txtNoVideo.setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);
            }
        }
    }

    public void refreshList(String newName, String newPath) {
        VideoListItem item = videoListItems.get(currentIndex);
        item.removeCoordinate(this);
        item.setFileName(newName);
        item.setFilePath(newPath);
        item.updateCoordinate(this);
        videoListItems.set(currentIndex, item);
        adapter.notifyDataSetChanged();
    }

    private String getLatitudeString(float latitude) {
        String orient = "N";
        if (latitude < 0) {
            orient = "S";
            latitude = latitude * (-1);
        }
        String strLocation = Location.convert(latitude, Location.FORMAT_SECONDS);
        String[] values = strLocation.split(":");

        String minute = values[1];
        if (minute.length() < 2)
            minute = "0" + minute;
        String second = values[2];
        float secValue = Float.parseFloat(second);
        second = String.format(Locale.US, "%.1f", secValue);

        return values[0] + "° " + minute + "′ " + second + "” " + orient;
    }

    private String getLongitudeString(float longitude) {
        String orient = "E";
        if (longitude < 0) {
            orient = "W";
            longitude = longitude * (-1);
        }
        String strLocation = Location.convert(longitude, Location.FORMAT_SECONDS);
        String[] values = strLocation.split(":");

        String minute = values[1];
        if (minute.length() < 2)
            minute = "0" + minute;
        String second = values[2];
        float secValue = Float.parseFloat(second);
        second = String.format(Locale.US, "%.1f", secValue);

        return values[0] + "° " + minute + "′ " + second + "” " + orient;
    }

}
