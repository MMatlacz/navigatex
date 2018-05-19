package com.example.jkmiotek.navigatex.Connector;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class LastStep {

    private String last_step_url = "https://navigatex.herokuapp.com/api/last_step";
    private RequestQueue requestQueue;

//    public LastStep(RequestQueue requestQueue){
//        this.requestQueue = requestQueue;
//    }

    public void startCalling(final Context context){
        this.requestQueue =  Volley.newRequestQueue(context);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
            Request.Method.GET, last_step_url, null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Toast.makeText(context, response.toString(), Toast.LENGTH_SHORT).show();
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }
        );
        requestQueue.add(jsonObjectRequest);
    }
}
