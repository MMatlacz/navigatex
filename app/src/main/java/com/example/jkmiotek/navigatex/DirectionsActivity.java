package com.example.jkmiotek.navigatex;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;

public class DirectionsActivity extends AppCompatActivity {
    JSONObject lastTag;
    Button ahead_btn;
    Button left_btn;
    Button right_btn;
    Button back_btn;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directions);

        try {
            lastTag = new JSONObject(getIntent().getStringExtra("lastTag"));
        } catch (JSONException | NullPointerException e) {
            e.printStackTrace();
        }

        ahead_btn = findViewById(R.id.ahead_btn);
        left_btn = findViewById(R.id.left_btn);
        right_btn = findViewById(R.id.right_btn);
        back_btn = findViewById(R.id.back_btn);

        try {
            ahead_btn.setContentDescription(lastTag.getString("token_id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        getApplicationContext().startActivity(intent);
    }

    public void showDistance(View view) {
        Intent intent = new Intent(DirectionsActivity.this, DistanceActivity.class);
        startActivity(intent);
    }
}
