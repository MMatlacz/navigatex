package com.example.jkmiotek.navigatex;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class DirectionsActivity extends AppCompatActivity {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directions);
    }

    public void showDistance(View view) {
        Intent intent = new Intent(DirectionsActivity.this, DistanceActivity.class);
        startActivity(intent);
    }
}
