package com.example.jkmiotek.navigatex;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jkmiotek.navigatex.Connector.Callback;
import com.example.jkmiotek.navigatex.Connector.Requester;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


class SearchEntity {
    public String name;
    public String description;

    public SearchEntity(String name, String description){
        this.name = name;
        this.description = description;
    }
}

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
                    intent.putExtra("destinationTagId", nodeData.getString("id"));
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

    private ArrayList<SearchEntity> prepareFilteredList() throws JSONException {
        ArrayList<SearchEntity> filteredList = new ArrayList<>();
        for (int i=0; i< filteredNodes.length(); i++ ) {
            JSONObject json_data = filteredNodes.getJSONObject(i);
            filteredList.add(
                    new SearchEntity(
                            json_data.getString("name"),
                            json_data.getString("description")
                    )
            );
        }

        return filteredList;
    }

    public void prepareSearchResults(String searchText)  {
        try {
            filterNodes(searchText);
            final ArrayList<SearchEntity> filteredLists = prepareFilteredList();
            ArrayAdapter<SearchEntity> adapter = new ArrayAdapter<SearchEntity>(
                    this,
                    android.R.layout.simple_list_item_2 ,
                    android.R.id.text1,
                    filteredLists
            ){
                @Override
                public View getView(int position, View convertView, ViewGroup parent){
                    View view = super.getView(position, convertView, parent);
                    TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                    TextView text2 = (TextView) view.findViewById(android.R.id.text2);

                    text1.setText(filteredLists.get(position).name);
                    text2.setText(filteredLists.get(position).description);
                    return view;
                }
            };
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
