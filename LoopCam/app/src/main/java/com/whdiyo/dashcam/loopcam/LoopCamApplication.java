package com.whdiyo.dashcam.loopcam;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.whdiyo.dashcam.loopcam.setting.AppSetting;
import com.whdiyo.dashcam.loopcam.util.Const;

/**
 * Created by Wang on 1/28/2018
 */

public class LoopCamApplication extends Application {

    private static Application application;
    public AppSetting appSetting;

    public static Application getApplication() {
        return application;
    }

    public static Context getContext() {
        return getApplication().getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        appSetting = new AppSetting();
        loadSetting();
    }

    private void loadSetting() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("LoopCam", 0);

        if (pref.contains("key_speed_unit")) {
            Integer speedUnit = pref.getInt("key_speed_unit", Const.METER_PER_HOUR);
            Integer loopTime = pref.getInt("key_loop_time", 600);
            Boolean autoRecord = pref.getBoolean("key_auto_record", false);
            Integer videoSetting = pref.getInt("key_video_setting", Const.VIDEO_SETTING_MEDIUM);

            appSetting.setSpeedUnit(speedUnit);
            appSetting.setLoopSeconds(loopTime);
            appSetting.setAutoRecord(autoRecord);
            appSetting.setVideoSetting(videoSetting);
        } else {
            initSetting();
        }
    }

    private void initSetting() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("LoopCam", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("key_speed_unit", Const.METER_PER_HOUR);
        editor.putInt("key_loop_time", 600);
        editor.putBoolean("key_auto_record", false);
        editor.putInt("key_video_setting", Const.VIDEO_SETTING_MEDIUM);
        editor.apply();
    }

    public void setSpeedUnit(int speedUnit) {
        appSetting.setSpeedUnit(speedUnit);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("LoopCam", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("key_speed_unit", speedUnit);
        editor.apply();
    }

    public void setLoopTime(int loopTime) {
        appSetting.setLoopSeconds(loopTime);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("LoopCam", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("key_loop_time", loopTime);
        editor.apply();
    }

    public void setAutoRecord(boolean autoRecord) {
        appSetting.setAutoRecord(autoRecord);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("LoopCam", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("key_auto_record", autoRecord);
        editor.apply();
    }

    public void setVideoSetting(int videoSetting) {
        appSetting.setVideoSetting(videoSetting);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("LoopCam", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("key_video_setting", videoSetting);
        editor.apply();
    }
}
