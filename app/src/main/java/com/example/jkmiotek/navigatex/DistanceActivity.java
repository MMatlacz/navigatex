package com.example.jkmiotek.navigatex;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jkmiotek.navigatex.Connector.Callback;
import com.example.jkmiotek.navigatex.Connector.Requester;

import org.json.JSONException;
import org.json.JSONObject;


public class DistanceActivity extends Activity implements SensorEventListener {

    boolean activityRunning;
    private SensorManager sensorManager;
    private Integer distance;
    private TextView count;

    public void getDistanceToNextToken() {
        String url = "https://navigatex.herokuapp.com/api/last_step";

        Callback callback = new Callback() {
            @Override
            public void call(JSONObject jsonObject) {
                try {
                    count.setText((String) jsonObject.get("token_id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        Requester requester = new Requester(this.getApplicationContext(), url, callback);
        requester.startCalling();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distance);
        count = findViewById(R.id.estimated_distance);
        getDistanceToNextToken();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
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
    public void onSensorChanged(SensorEvent event) {
        if (activityRunning) {
            count.setText(String.valueOf(event.values[0]));
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
