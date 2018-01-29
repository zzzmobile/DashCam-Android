package com.whdiyo.dashcam.loopcam.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.whdiyo.dashcam.loopcam.LoopCamApplication;
import com.whdiyo.dashcam.loopcam.R;
import com.whdiyo.dashcam.loopcam.activity.SettingsActivity;

/**
 * Created by Wang on 1/28/2018
 */

public class SelectLoopTimeDialog extends Dialog {
    private Context context;
    private Button btnSet = null;
    private Button btnCancel = null;
    private EditText txtLoopTime = null;

    public SelectLoopTimeDialog(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_looptime);
        initialize();
    }

    private void initialize() {
        btnSet = findViewById(R.id.btn_set);
        btnSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                set();
            }
        });
        btnCancel = findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });

        txtLoopTime = findViewById(R.id.txt_loop_time);

        LoopCamApplication application = (LoopCamApplication)LoopCamApplication.getApplication();
        txtLoopTime.setText(String.valueOf(application.appSetting.getLoopSeconds()));
    }

    private void set() {
        LoopCamApplication application = (LoopCamApplication)LoopCamApplication.getApplication();
        application.setLoopTime(Integer.parseInt(txtLoopTime.getText().toString()));
        hide();

        SettingsActivity activity = (SettingsActivity)context;
        activity.reloadSettingMenu();
    }
}
