package com.example.jkmiotek.navigatex;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.jkmiotek.navigatex.Connector.Callback;
import com.example.jkmiotek.navigatex.Connector.Requester;

import org.json.JSONObject;


class WaitingForTagCallback implements Callback {
    private WaitingForTagActivity activity;
    private String destinationTag;

    public WaitingForTagCallback(WaitingForTagActivity activity, String destinationTag) {
        this.activity = activity;
        this.destinationTag = destinationTag;
    }

    @Override
    public void call(JSONObject jsonObject) {

        if (jsonObject.length() == 0){
            this.activity.getLastStep();
            return;
        }

        Intent intent = new Intent(activity, DirectionsActivity.class);
        intent.putExtra("lastTag", jsonObject.toString());
        intent.putExtra("destinationTagId", destinationTag);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
    }
}


public class WaitingForTagActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_for_tag);

        textAnimationStart((TextView) findViewById(R.id.waitingForTag));

        getLastStep();
    }

    public void getLastStep() {
        WaitingForTagCallback callback = new WaitingForTagCallback(
                this, getIntent().getStringExtra("destinationTagId")
        );
        String url = getResources().getString(R.string.baseUrl) + "/api/last_step";

        Requester requester = new Requester(
                this.getApplicationContext(),
                url,
                callback
        );
        requester.startCalling();
    }

    private void textAnimationStart(final TextView view) {
        final Handler handler = new Handler(Looper.getMainLooper());
        final Runnable runnable = new Runnable() {
            int loadingIndex = 0;

            @Override
            public void run() {
                switch (loadingIndex % 3) {
                    case 0:
                        view.setText("Waiting for tag.  ");
                        break;
                    case 1:
                        view.setText("Waiting for tag.. ");
                        break;
                    case 2:
                        view.setText("Waiting for tag...");
                        break;
                }
                loadingIndex++;
                handler.postDelayed(this, 500);
            }
        };
        handler.post(runnable);
    }
}
