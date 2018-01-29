package com.whdiyo.dashcam.loopcam.activity;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.whdiyo.dashcam.loopcam.R;

public class MenuActivity extends AppCompatActivity {
    ImageButton btnBack = null;
    ArrayAdapter listAdapter = null;
    ListView listView = null;

    String[] menuArray = { "Settings", "Saved Videos", "Log/History (coming soon)" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        setContentView(R.layout.activity_menu);
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
        listAdapter = new ArrayAdapter(this, R.layout.listitem_menu, menuArray);
        listView = findViewById(R.id.lst_menu);
        listView.setAdapter(listAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectListItem(position);
            }
        });

        listView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                listView.getChildAt(2).setEnabled(false);
            }
        });
        listAdapter.notifyDataSetChanged();
    }

    private void selectListItem(int position) {
        if (position == 0) {
            Intent i = new Intent(MenuActivity.this, SettingsActivity.class);
            startActivity(i);
        } else if (position == 1) {
            Intent i = new Intent(MenuActivity.this, VideoListActivity.class);
            startActivity(i);
        } else {

        }
    }
}
