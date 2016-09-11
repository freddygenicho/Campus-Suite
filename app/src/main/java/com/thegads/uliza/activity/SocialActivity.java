package com.thegads.uliza.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.thegads.uliza.R;
import com.thegads.uliza.adapter.SocialAdapter;

import jp.wasabeef.recyclerview.animators.ScaleInTopAnimator;
import jp.wasabeef.recyclerview.animators.adapters.AlphaInAnimationAdapter;

public class SocialActivity extends AppCompatActivity {

    private String links[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.links = getResources().getStringArray(R.array.links);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.social_recycler);
        assert recyclerView != null;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        SocialAdapter adapter = new SocialAdapter(this);

        AlphaInAnimationAdapter alphaInAnimationAdapter = new AlphaInAnimationAdapter(adapter);
        recyclerView.setAdapter(alphaInAnimationAdapter);
        adapter.setItemClickListener(new SocialAdapter.ItemClickListener() {
            @Override
            public void onClick(View v, int position) {
                openWebsite(links[position]);
            }
        });
    }

    private void openWebsite(String url){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        if(intent.resolveActivity(getPackageManager())!=null){
            startActivity(intent);
        }else {
            Toast.makeText(SocialActivity.this, "No app found to perform this operation", Toast.LENGTH_SHORT).show();
        }

    }

}
