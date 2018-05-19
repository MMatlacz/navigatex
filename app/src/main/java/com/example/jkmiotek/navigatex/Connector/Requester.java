package com.example.jkmiotek.navigatex.Connector;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

public class Requester {

    private String request_url;
    private RequestQueue requestQueue;
    private Callback callback;

    public Requester(final Context context, String url, Callback callback) {
        this.callback = callback;
        request_url = url;
        requestQueue = QueueSingleton.getInstance(context).getRequestQueue();

    }

    public void startCalling() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, request_url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        callback.call(response);
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
