package com.example.moudgil.gifzone;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.moudgil.gifzone.app.Config;
import com.example.moudgil.gifzone.fragments.FavoritesFragment;
import com.example.moudgil.gifzone.fragments.TopGifsFragment;

public class FavoritesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        if (savedInstanceState == null) {

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, new FavoritesFragment())
                    .commit();

        }
    }
}
