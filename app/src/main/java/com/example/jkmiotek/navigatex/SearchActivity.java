package com.example.jkmiotek.navigatex;

import android.content.Context;
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

import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Arrays;

public class SearchActivity extends AppCompatActivity {

    private ArrayList<String> availableValues =  new ArrayList<>(
            Arrays.asList(
                    "Platform 1",
                    "Platform 2",
                    "Ticket office",
                    "Train information"
            )
    );
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        listView = findViewById(R.id.searchListView);

        prepareSearchResults(".*");
        setupSearchEdit(this.getApplicationContext());

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // ListView Clicked item index
                int itemPosition     = position;

                // ListView Clicked item value
                String  itemValue    = (String) listView.getItemAtPosition(position);

                // Show Alert
                Toast.makeText(getApplicationContext(),
                        "Position :"+itemPosition+"  ListItem : " +itemValue , Toast.LENGTH_LONG)
                        .show();
            }
        });
    }

    private void prepareSearchResults(String searchText){
        ArrayList<String> filteredList = Lists.newArrayList(Collections2.filter(
                availableValues, Predicates.containsPattern(searchText)));

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                android.R.id.text1,
                filteredList
        );

        listView.setAdapter(adapter);

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
