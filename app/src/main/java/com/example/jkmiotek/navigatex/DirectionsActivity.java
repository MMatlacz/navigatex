package com.example.jkmiotek.navigatex;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class DirectionsActivity extends Activity {
    JSONObject lastTag;
    static String destinationTagId;
    String currentTagId;
    Button ahead_btn;
    Button left_btn;
    Button right_btn;
    Button back_btn;
    private TextToSpeech tts;

    public static JSONObject getJsonObject(JSONObject jsonObject, String key) {
        JSONObject jsonObject1 = null;
        try {
            jsonObject1 = (JSONObject) jsonObject.get(key);
        } catch (JSONException | NullPointerException | ClassCastException e) {
            jsonObject1 = null;
        }
        return jsonObject1;
    }

    void setButtonInfo(Button button, JSONObject sides, String key) {
        JSONObject side;
        try {
            side = getJsonObject(sides, key);
            button.setContentDescription(side.getString("description"));
            ArrayList<String> parameters = new ArrayList<>();
            parameters.add(side.get("distance").toString());
            parameters.add(getJsonObject(side, "destination").getString("id"));
            button.setTag(parameters);
        } catch (JSONException | NullPointerException e) {
            Log.v("TOKEN", "Error in back");
            button.setVisibility(View.GONE);
        }
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

        try {
            destinationTagId = getIntent().getStringExtra("destinationTagId");
        } catch (Exception e) {
            destinationTagId = null;
        }

        try {
            currentTagId = lastTag.getString("id");
        } catch (JSONException e) {
            currentTagId = null;
        }

        Log.v("TAGS", currentTagId + " " + destinationTagId);
        if (Objects.equals(currentTagId, destinationTagId)) {
            tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status != TextToSpeech.ERROR) {
                        tts.setLanguage(Locale.UK);
                    }
                }
            });
            final Handler handler = new Handler();
            handler.postDelayed(
                    new Runnable() {
                        @Override
                        public void run() {
                            AlertDialog.Builder alertDialog = new AlertDialog
                                    .Builder(DirectionsActivity.this)
                                    .setMessage("You have reached your destination!")
                                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                                        @Override
                                        public void onDismiss(DialogInterface dialog) {
                                            DirectionsActivity.this.onBackPressed();
                                        }
                                    });
                            alertDialog.show();
                        }
                    }, 1200);

        }


        Log.v("TOKEN", lastTag.toString());

        ahead_btn = findViewById(R.id.ahead_btn);
        left_btn = findViewById(R.id.left_btn);
        right_btn = findViewById(R.id.right_btn);
        back_btn = findViewById(R.id.back_btn);


        JSONObject sides = getJsonObject(getJsonObject(lastTag, "node_description"), "sides");
        Log.v("TOKEN", sides != null ? sides.toString() : "");


        JSONObject side = null;
        if (sides != null) {
            Log.v("TOKEN", "In sides");
            setButtonInfo(ahead_btn, sides, "front");
            setButtonInfo(left_btn, sides, "left");
            setButtonInfo(right_btn, sides, "right");
            setButtonInfo(back_btn, sides, "back");
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
        Button button = (Button) view;
        ArrayList<String> parameters = (ArrayList<String>) button.getTag();

        Intent intent = new Intent(DirectionsActivity.this, DistanceActivity.class);
        Log.v("DISTANCE", parameters.get(0));
        Log.v("DISTANCE", parameters.get(1));
        intent.putExtra("distance", Integer.valueOf(parameters.get(0)));
        intent.putExtra("destinationId", parameters.get(1));
        startActivity(intent);
    }
}
