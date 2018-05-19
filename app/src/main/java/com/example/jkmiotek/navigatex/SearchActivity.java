package com.example.jkmiotek.navigatex;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.jkmiotek.navigatex.Connector.Callback;
import com.example.jkmiotek.navigatex.Connector.Requester;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


class SearchCallback implements Callback {
    private SearchActivity searchActivity;

    public SearchCallback(SearchActivity searchActivity){
        this.searchActivity = searchActivity;
    }

    @Override
    public void call(JSONObject jsonObject) {
        try {
            this.searchActivity.availableNodes = jsonObject.getJSONArray("nodes");
            this.searchActivity.prepareSearchResults("");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

public class SearchActivity extends AppCompatActivity {

    public JSONArray availableNodes = new JSONArray();
    public JSONArray filteredNodes = new JSONArray();
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        listView = findViewById(R.id.searchListView);

        setupSearchEdit(this.getApplicationContext());

        SearchCallback callback = new SearchCallback(this);
        String url = getResources().getString(R.string.baseUrl) + "/api/nodes";
        Requester requester = new Requester(
                this.getApplicationContext(),
                url,
                callback
        );
        requester.startCalling();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    JSONObject nodeData = filteredNodes.getJSONObject(position);
                    Intent intent = new Intent(parent.getContext(), WaitingForTagActivity.class);
                    intent.putExtra("destinationTag", nodeData.toString());
                    startActivity(intent);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void filterNodes(String searchText) throws JSONException {
        searchText = searchText.toLowerCase();
        filteredNodes = new JSONArray();

        for (int i=0; i< availableNodes.length(); i++ ){
            JSONObject json_data = availableNodes.getJSONObject(i);
            String node_name = json_data.getString("name").toLowerCase();
            if (node_name.contains(searchText)){
                filteredNodes.put(json_data);
            }
        }
    }

    private ArrayList<String> prepareFilteredList() throws JSONException {
        ArrayList<String> filteredList = new ArrayList<>();
        for (int i=0; i< filteredNodes.length(); i++ ) {
            JSONObject json_data = filteredNodes.getJSONObject(i);
            filteredList.add(json_data.getString("name"));
        }
        return filteredList;
    }

    public void prepareSearchResults(String searchText)  {
        try {
            filterNodes(searchText);
            ArrayList<String> filteredList = prepareFilteredList();
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_list_item_1,
                    android.R.id.text1,
                    filteredList
            );
            listView.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setupSearchEdit(final Context context) {
        EditText searchEdit = findViewById(R.id.searchEditText);
        searchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Toast.makeText(context, s.toString(), Toast.LENGTH_SHORT).show();
                prepareSearchResults(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

}
