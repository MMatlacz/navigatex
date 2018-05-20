package com.example.jkmiotek.navigatex;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Button;

import com.example.jkmiotek.navigatex.Connector.Callback;
import com.example.jkmiotek.navigatex.Connector.Requester;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

class NavigationCallback implements Callback {
    private DirectionsActivity directionsActivity;
    public NavigationCallback(DirectionsActivity directionActivity){
        this.directionsActivity = directionActivity;
    }

    @Override
    public void call(final JSONObject jsonObject) {

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    String destinationDirection = jsonObject.getString("side");
                    switch (destinationDirection) {
                        case "front":
                            directionsActivity.ahead_btn.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED);
                            break;
                        case "left":
                            directionsActivity.left_btn.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED);
                            break;
                        case "right":
                            directionsActivity.right_btn.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED);
                            break;
                        case "back":
                            directionsActivity.back_btn.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED);
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, 1000);
    }
}


public class DirectionsActivity extends AppCompatActivity {
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
            button.setContentDescription(key + "!." + side.getString("description"));
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
            JSONObject nodeDescription = lastTag.getJSONObject("node_description");
            this.setTitle("Navigating from: " + nodeDescription.getString("name"));
        } catch (JSONException | NullPointerException e) {
            e.printStackTrace();
        }

        if (destinationTagId == null){
            destinationTagId = getIntent().getStringExtra("destinationTagId");
        }

        if (destinationTagId != null){
            fetchDestination();
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
                            try {
                                AlertDialog.Builder alertDialog = new AlertDialog
                                        .Builder(DirectionsActivity.this)
                                        .setMessage("You have reached your destination!")
                                        .setPositiveButton("Finish", null)
                                        .setOnDismissListener(new DialogInterface.OnDismissListener() {
                                            @Override
                                            public void onDismiss(DialogInterface dialog) {
                                                DirectionsActivity.this.onBackPressed();
                                            }
                                        });
                                alertDialog.show();
                            }
                            catch (Exception e) {e.printStackTrace();}
                        }
                    }, 1000);

        }


        Log.v("TOKEN", lastTag.toString());

        ahead_btn = findViewById(R.id.ahead_btn);
        left_btn = findViewById(R.id.left_btn);
        right_btn = findViewById(R.id.right_btn);
        back_btn = findViewById(R.id.back_btn);


        JSONObject sides = getJsonObject(getJsonObject(lastTag, "node_description"), "sides");
        Log.v("TOKEN", sides != null ? sides.toString() : "");


        if (sides != null) {
            Log.v("TOKEN", "In sides");
            setButtonInfo(ahead_btn, sides, "front");
            setButtonInfo(left_btn, sides, "left");
            setButtonInfo(right_btn, sides, "right");
            setButtonInfo(back_btn, sides, "back");
        }
    }

    private void fetchDestination(){
        NavigationCallback callback = new NavigationCallback(this);
        String url = getResources().getString(R.string.baseUrl) + "/api/find_path?id=" + destinationTagId;
        Requester requester = new Requester(this.getApplicationContext(), url, callback);
        requester.startCalling();
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

        destinationTagId = null;
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
