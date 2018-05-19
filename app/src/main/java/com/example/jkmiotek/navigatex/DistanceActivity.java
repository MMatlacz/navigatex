package com.example.jkmiotek.navigatex;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jkmiotek.navigatex.Connector.Callback;
import com.example.jkmiotek.navigatex.Connector.Requester;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;


public class DistanceActivity extends Activity implements SensorEventListener {

    boolean activityRunning;
    private SensorManager sensorManager;
    private Integer distance;
    private Integer half_distance;
    private String destinationId;
    private TextView count;
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

    public void getDistanceToNextToken() {
        String url = getResources().getString(R.string.baseUrl) + "/api/last_step";

        Callback callback = new Callback() {
            @Override
            public void call(JSONObject jsonObject) {
                try {
                    String currentTokenId = jsonObject.getString("id");
                    Log.v("CURRENTOKEN", distance.toString());
                    Log.v("CURRENTOKEN", currentTokenId);
                    if (currentTokenId.equals(destinationId)) {
                        Intent intent = new Intent(getApplicationContext(), DirectionsActivity.class);
                        intent.putExtra("lastTag", jsonObject.toString());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        getApplicationContext().startActivity(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        Requester requester = new Requester(this.getApplicationContext(), url, callback);
        requester.startCalling();
    }

    public void postSelectedToken(String tokenId) {
        String url = getResources().getString(R.string.baseUrl) + "/api/publish_step?token_id=" + tokenId;

        Requester requester = new Requester(this.getApplicationContext(), url, null);
        requester.startCalling();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distance);
        count = findViewById(R.id.estimated_distance);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        distance = getIntent().getIntExtra("distance", 0);
        half_distance = distance / 2;
        destinationId = getIntent().getStringExtra("destinationId");
        count.setText(distance.toString() + " steps left");

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
                        tts.speak(count.getText().toString(), TextToSpeech.QUEUE_ADD, null, null);
                    }
                }, 1000);

    }

    @Override
    protected void onResume() {
        super.onResume();
        activityRunning = true;
        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (countSensor != null) {
            sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_FASTEST);
        } else {
            Toast.makeText(this, "Count sensor not available!", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        activityRunning = false;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        getApplicationContext().startActivity(intent);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (activityRunning) {
            if (distance > 0)
                distance -= 1;

            else if (distance < 0)
                distance = 0;

            if (distance <= half_distance) {
                getDistanceToNextToken();
            }

            if (distance % 10 == 0) {
                count.setText(distance.toString() + " steps left");
                tts.speak(count.getText().toString(), TextToSpeech.QUEUE_FLUSH, null, null);
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
