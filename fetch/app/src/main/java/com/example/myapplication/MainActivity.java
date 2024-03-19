package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    List<Item> itemList = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerView.Adapter recyclerViewAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //establish recyclerview to store data
        recyclerView = findViewById(R.id.itemsRecyclerView);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerViewAdapter = new RecyclerViewAdapter(itemList, this);
        recyclerView.setAdapter(recyclerViewAdapter);

        fillItemList();
    }

    private void fillItemList() {
        FetchDataThread fetchDataThread = new FetchDataThread(itemList);
        fetchDataThread.start();
    }

    // run data extraction and filtering on a thread
    public class FetchDataThread extends Thread {

        private static final String TAG = "FetchDataThread";
        private List<Item> itemList;

        public FetchDataThread(List<Item> itemList) {
            this.itemList = itemList;
        }

        @Override
        public void run() {
            try {
                URL url = new URL("https://fetch-hiring.s3.amazonaws.com/hiring.json");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                StringBuilder response = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JSONArray jsonArray = new JSONArray(response.toString());

                // parse json array
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    int id = jsonObject.getInt("id");
                    int listId = jsonObject.getInt("listId");
                    String name = jsonObject.getString("name");

                    // filter out blank/null item
                    if (name != "null" && !name.isEmpty())  {
                        // create Item object and add each item to the itemList
                        Item item = new Item(id, listId, name);
                        itemList.add(item);
                    }
                }

                // group items by mapping listId
                Map<Integer, List<Item>> groupedItems = new HashMap<>();
                for (Item item : itemList) {
                    if (!groupedItems.containsKey(item.getListId())) {
                        groupedItems.put(item.getListId(), new ArrayList<>());
                    }
                    groupedItems.get(item.getListId()).add(item);
                }

                // sort items within each group by name
                for (List<Item> group : groupedItems.values()) {
                    Collections.sort(group, new Comparator<Item>() {
                        @Override
                        public int compare(Item item1, Item item2) {
                            return item1.getName().compareTo(item2.getName());
                        }
                    });
                }

                // update itemList with grouped and sorted items
                itemList.clear();
                for (List<Item> group : groupedItems.values()) {
                    itemList.addAll(group);
                }

                // notify adapter when data changed
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recyclerViewAdapter.notifyDataSetChanged();
                    }
                });

            } catch (IOException | JSONException e) {
                Log.e(TAG, "Error fetching or parsing data: " + e.getMessage());
            }
        }
    }
}
