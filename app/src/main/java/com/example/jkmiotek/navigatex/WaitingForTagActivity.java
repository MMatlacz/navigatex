package com.example.jkmiotek.navigatex;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.jkmiotek.navigatex.Connector.Callback;
import com.example.jkmiotek.navigatex.Connector.Requester;

import org.json.JSONObject;


class WaitingForTagCallback implements Callback{
    private Context context;

    public WaitingForTagCallback(Context context){
        this.context = context;
    }

    @Override
    public void call(JSONObject jsonObject) {
        Intent intent = new Intent(context, DirectionsActivity.class);
        intent.putExtra("lastTag", jsonObject.toString());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}


public class WaitingForTagActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_for_tag);

        WaitingForTagCallback callback = new WaitingForTagCallback(this.getApplicationContext());
        Requester requester = new Requester(
            this.getApplicationContext(),
            "https://navigatex.herokuapp.com/api/last_step",
            callback
        );
        requester.startCalling();
    }


}
