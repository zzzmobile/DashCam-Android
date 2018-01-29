package com.whdiyo.dashcam.loopcam.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.whdiyo.dashcam.loopcam.LoopCamApplication;
import com.whdiyo.dashcam.loopcam.R;
import com.whdiyo.dashcam.loopcam.activity.SettingsActivity;
import com.whdiyo.dashcam.loopcam.util.Const;

/**
 * Created by Wang on 1/28/2018
 */

public class SelectAutoRecordDialog extends Dialog {
    private Context context;
    private Button btnOkay = null;
    private RadioGroup btnGroup = null;
    private RadioButton radioYes = null;
    private RadioButton radioNo = null;

    public SelectAutoRecordDialog(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_autorecord);
        initialize();
    }

    private void initialize() {
        btnOkay = findViewById(R.id.btn_okay);
        btnOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDialog();
            }
        });

        btnGroup = findViewById(R.id.radio_group);
        radioYes = findViewById(R.id.rdo_yes);
        radioNo = findViewById(R.id.rdo_no);

        LoopCamApplication application = (LoopCamApplication)LoopCamApplication.getApplication();
        radioYes.setChecked(application.appSetting.isAutoRecord());
        radioNo.setChecked(!application.appSetting.isAutoRecord());
    }

    private void closeDialog() {
        LoopCamApplication application = (LoopCamApplication)LoopCamApplication.getApplication();
        if (btnGroup.getCheckedRadioButtonId() == R.id.rdo_yes)
            application.setAutoRecord(true);
        else
            application.setAutoRecord(false);
        hide();

        SettingsActivity activity = (SettingsActivity)context;
        activity.reloadSettingMenu();

    }

}
