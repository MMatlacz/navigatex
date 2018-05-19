package com.example.jkmiotek.navigatex;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.example.jkmiotek.navigatex.Connector.Callback;
import com.example.jkmiotek.navigatex.Connector.Requester;

import org.json.JSONObject;

class MainCallback implements Callback {
    Context context;

    MainCallback(Context context) {
        this.context = context;
    }

    @Override
    public void call(JSONObject jsonObject) {
        Toast.makeText(context, jsonObject.toString(), Toast.LENGTH_SHORT).show();
    }
}

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Callback callback = new MainCallback(this.getApplicationContext());

        Requester step = new Requester(this.getApplicationContext(), "https://navigatex.herokuapp.com/api/last_step", callback);
        step.startCalling();
    }

    public void onSearchRequest(View view){
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }

    public void startDirections(View view) {
        Intent intent = new Intent(MainActivity.this, DirectionsActivity.class);
        startActivity(intent);
    }
}
