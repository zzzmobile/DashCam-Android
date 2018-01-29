package com.whdiyo.dashcam.loopcam.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.whdiyo.dashcam.loopcam.LoopCamApplication;
import com.whdiyo.dashcam.loopcam.R;
import com.whdiyo.dashcam.loopcam.activity.VideoListActivity;

import java.io.File;

/**
 * Created by Wang on 1/29/2018
 */

public class RenameVideoDialog extends Dialog {
    private Context context;
    private Button btnRename = null;
    private Button btnCancel = null;
    private EditText txtRenameVideo = null;

    private String fileName = "";

    public RenameVideoDialog(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_rename);
        initialize();
    }

    private void initialize() {
        btnRename = findViewById(R.id.btn_rename);
        btnRename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Rename();
            }
        });
        btnCancel = findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });

        txtRenameVideo = findViewById(R.id.txt_rename_video);
        txtRenameVideo.setText(fileName);
        txtRenameVideo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0)
                    btnRename.setEnabled(false);
                else
                    btnRename.setEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    private void Rename() {
        File dataDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        String appPath = dataDir.toString() + "/video/";
        String newName = txtRenameVideo.getText().toString();
        File oldFile = new File(appPath + fileName);
        File newFile = new File(appPath + newName);
        if (!oldFile.renameTo(newFile)) {
            Toast.makeText(context, "You can't rename current file because there is same name, or you have not write permission.", Toast.LENGTH_LONG).show();
        }
        hide();

        VideoListActivity activity = (VideoListActivity)context;
        activity.refreshList(newName, appPath + newName);
    }
}
