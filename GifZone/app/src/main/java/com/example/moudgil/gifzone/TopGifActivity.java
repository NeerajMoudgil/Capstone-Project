package com.example.moudgil.gifzone;

import android.app.SearchManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.moudgil.gifzone.app.Config;
import com.example.moudgil.gifzone.fragments.TopGifsFragment;
import com.google.firebase.analytics.FirebaseAnalytics;

public class TopGifActivity extends AppCompatActivity {

    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_gif);
        mFirebaseAnalytics=FirebaseAnalytics.getInstance(this);
        if (savedInstanceState == null) {
            Log.d("savedInstance State", "null");

            handleIntent(getIntent());




        }
    }

    private void handleIntent(Intent intent) {
        Bundle bundle = new Bundle();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

            Bundle bundleanaltics = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.SEARCH_TERM, query);
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SEARCH, bundleanaltics);

            bundle.putString(Config.URL_TYPE, Config.SEARCH);
            bundle.putString(Config.CATEGORY_TYPE, query);

        }else if(intent.hasExtra(Config.NAV_TYPE)) {
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

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(getIntent());


    }
}
