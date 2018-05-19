package com.example.jkmiotek.navigatex;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

    public static JSONObject getJsonObject(JSONObject jsonObject, String key) {
        JSONObject jsonObject1 = null;
        try {
            jsonObject1 = (JSONObject) jsonObject.get(key);
        } catch (JSONException | NullPointerException | ClassCastException e) {
            jsonObject1 = null;
        }
        return jsonObject1;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directions);

        try {
            lastTag = new JSONObject(getIntent().getStringExtra("lastTag"));
        } catch (JSONException | NullPointerException e) {
            e.printStackTrace();
        }

        Log.v("TOKEN", lastTag.toString());

        ahead_btn = findViewById(R.id.ahead_btn);
        left_btn = findViewById(R.id.left_btn);
        right_btn = findViewById(R.id.right_btn);
        back_btn = findViewById(R.id.back_btn);


        JSONObject sides = getJsonObject(getJsonObject(lastTag, "node_description"), "sides");
        Log.v("TOKEN", sides != null ? sides.toString() : "");


        JSONObject side;
        if (sides != null) {
            Log.v("TOKEN", "In sides");
            try {
                side = getJsonObject(sides, "back");
                back_btn.setContentDescription(side.getString("description"));
            } catch (JSONException | NullPointerException e) {
                Log.v("TOKEN", "Error in back");
                back_btn.setVisibility(View.GONE);
            }

            try {
                side = getJsonObject(sides, "front");
                ahead_btn.setContentDescription(side.getString("description"));
            } catch (JSONException | NullPointerException e) {
                Log.v("TOKEN", "Error in front");
                ahead_btn.setVisibility(View.GONE);
            }

            try {
                side = getJsonObject(sides, "left");
                left_btn.setContentDescription(side.getString("description"));
            } catch (JSONException | NullPointerException e) {
                Log.v("TOKEN", "Error in left");
                left_btn.setVisibility(View.GONE);
            }

            try {
                side = getJsonObject(sides, "right");
                right_btn.setContentDescription(side.getString("description"));
            } catch (JSONException | NullPointerException e) {
                Log.v("TOKEN", "Error in right");
                right_btn.setVisibility(View.GONE);
            }
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
