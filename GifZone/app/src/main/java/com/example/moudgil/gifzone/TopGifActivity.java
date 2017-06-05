package com.example.moudgil.gifzone;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.moudgil.gifzone.app.Config;
import com.example.moudgil.gifzone.fragments.TopGifsFragment;

public class TopGifActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_gif);
        if (savedInstanceState == null) {
            Log.d("savedInstance State", "null");
            Bundle bundle = new Bundle();
            Intent intent = getIntent();

            if (intent.hasExtra(Config.NAV_TYPE)) {
                String navType = intent.getStringExtra(Config.NAV_TYPE);
                if (navType.equals(Config.NAV_TRENDING)) {
                    bundle.putString(Config.URL_TYPE, Config.TRENDING);

                } else if (navType.equals(Config.NAV_CATEGORIES)) {
                    bundle.putString(Config.URL_TYPE, Config.SEARCH);
                    String categoryType = intent.getStringExtra(Config.CATEGORY_TYPE);
                    bundle.putString(Config.CATEGORY_TYPE, categoryType);

                }
            }

            TopGifsFragment topGifsFragment = new TopGifsFragment();
            topGifsFragment.setArguments(bundle);

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, topGifsFragment)
                    .commit();

        }
    }
}
