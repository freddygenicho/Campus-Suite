package com.thegads.uliza.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.util.AtomicFile;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thegads.uliza.R;
import com.thegads.uliza.util.ActionManager;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final String url = getString(R.string.app_website_url);

        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView app_url = (TextView) findViewById(R.id.app_wesite_url);
        assert app_url != null;
        app_url.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActionManager.openWebPage(getApplicationContext(),url);
            }
        });
    }
}
