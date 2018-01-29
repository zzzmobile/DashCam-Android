package com.whdiyo.dashcam.loopcam.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.Image;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;

import com.whdiyo.dashcam.loopcam.util.Const;

import java.io.File;

/**
 * Created by Wang on 1/29/2018
 */

public class VideoListItem {
    private String filePath;
    private String fileName;
    private Bitmap thumbImage;
    private long timeStamp;
    private long fileSize;
    private float sLat;
    private float sLon;
    private float eLat;
    private float eLon;


    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Bitmap getThumbImage() {
        return thumbImage;
    }

    public void setThumbImage(Bitmap thumbImage) {
        this.thumbImage = thumbImage;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public float getStartLatitude() {
        return sLat;
    }

    public void setStartLatitude(float latitude) {
        this.sLat = latitude;
    }

    public float getStartLongitude() {
        return sLon;
    }

    public void setStartLongitude(float longitude) {
        this.sLon = longitude;
    }

    public float getEndLatitude() {
        return eLat;
    }

    public void setEndLatitude(float latitude) {
        this.eLat = latitude;
    }

    public float getEndLongitude() {
        return eLon;
    }

    public void setEndLongitude(float longitude) {
        this.eLon = longitude;
    }

    public void setVideoPath(Context context, String videoPath) {
        filePath = videoPath;
        thumbImage = ThumbnailUtils.createVideoThumbnail(videoPath, MediaStore.Images.Thumbnails.MINI_KIND);
        fileName = videoPath.substring(videoPath.lastIndexOf("/") + 1);
        File file = new File(videoPath);
        fileSize = file.length();

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(context, Uri.fromFile(file));
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        timeStamp = Long.parseLong(time);
        retriever.release();

        loadCoordinate(context);
    }

    private void loadCoordinate(Context context) {
        SharedPreferences pref = context.getSharedPreferences("VideoList", 0);
        sLat = pref.getFloat(fileName + "_slat", 0.0f);
        sLon = pref.getFloat(fileName + "_slon", 0.0f);
        eLat = pref.getFloat(fileName + "_elat", 0.0f);
        eLon = pref.getFloat(fileName + "_elon", 0.0f);
    }

    public void updateCoordinate(Context context) {
        SharedPreferences pref = context.getSharedPreferences("VideoList", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putFloat(fileName + "_slat", sLat);
        editor.putFloat(fileName + "_slon", sLon);
        editor.putFloat(fileName + "_elat", eLat);
        editor.putFloat(fileName + "_elon", eLon);
        editor.apply();
    }

    public void removeCoordinate(Context context) {
        SharedPreferences pref = context.getSharedPreferences("VideoList", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.remove(fileName + "_slat");
        editor.remove(fileName + "_slon");
        editor.remove(fileName + "_elat");
        editor.remove(fileName + "_elon");
        editor.apply();
    }
}
