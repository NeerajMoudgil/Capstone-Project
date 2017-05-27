package com.example.moudgil.gifzone;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.moudgil.gifzone.fragments.TopGifsFragment;

public class TopGifActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_gif);
        if (savedInstanceState == null) {
            Log.d("savedInstance State","null");
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, new TopGifsFragment())
                    .commit();

        }
    }
}
