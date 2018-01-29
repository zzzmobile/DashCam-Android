package com.whdiyo.dashcam.loopcam.setting;

import android.content.Context;
import android.content.SharedPreferences;

import com.whdiyo.dashcam.loopcam.util.Const;

/**
 * Created by Wang on 1/28/2018
 */

public class AppSetting {
    private int speedUnit;
    private int loopSeconds;
    private boolean autoRecord;
    private int videoSetting;

    public int getSpeedUnit() {
        return speedUnit;
    }

    public void setSpeedUnit(int speedUnit) {
        this.speedUnit = speedUnit;
    }

    public int getLoopSeconds() {
        return loopSeconds;
    }

    public void setLoopSeconds(int loopSeconds) {
        this.loopSeconds = loopSeconds;
    }

    public boolean isAutoRecord() {
        return autoRecord;
    }

    public void setAutoRecord(boolean autoRecord) {
        this.autoRecord = autoRecord;
    }

    public int getVideoSetting() {
        return videoSetting;
    }

    public void setVideoSetting(int videoSetting) {
        this.videoSetting = videoSetting;
    }
}
