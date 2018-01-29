package com.whdiyo.dashcam.loopcam.util;

import java.util.Locale;

/**
 * Created by Wang on 1/29/2018
 */

public class Utils {
    public static String getTimerString(long milliseconds) {
        int minutes = 0;
        int seconds = (int)(milliseconds / 1000);
        String strMinute, strSecond;

        if (seconds > 59) {
            minutes = seconds / 60;
            seconds = seconds - (minutes * 60);
        }

        strMinute = String.format(Locale.US, "%02d", minutes);
        strSecond = String.format(Locale.US, "%02d", seconds);

        return strMinute + ":" + strSecond;
    }

    public static String getStorageString(long bytes) {
        String strUnit = "Bytes";
        double value = (double)bytes;
        if (value > 1023.0) {
            value = value / 1024;
            strUnit = "KB";
        }

        if (value > 1023.0) {
            value = value / 1024;
            strUnit = "MB";
        }

        if (value > 1023.0) {
            value = value / 1024;
            strUnit = "GB";
        }

        String strValue = String.format(Locale.US, "%.1f", value);
        return strValue + strUnit;
    }
}
