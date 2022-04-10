package tech.nextgen.unimacampusmap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.util.ArrayList;

public class MyPlaces extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    RecyclerView recyclerView;

    //array list of building names and id
    ArrayList<String> buildingNames = new ArrayList<>();
    ArrayList<Blob> buildingImages = new ArrayList<>();
    ArrayList<String> buildingId = new ArrayList<>();


        @Override
        protected void onCreate (Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_my_places);

            //recycler widget hook
            recyclerView = findViewById(R.id.recyclerView);

            //Recyclerview Configuration
            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

            //get JSON Object from  JSON file
            try {
                JSONObject obj = new JSONObject(loadJSONfromAssets());

                //fetch JSONArray named name(building name)
                JSONArray buildingArray = obj.getJSONArray("name");

                //implementation of loop for getting buildings names
                for (int i = 0; i < buildingArray.length(); i++) {

                    //creating a json object for fetching a single building name
                    JSONObject buildingDetail = buildingArray.getJSONObject(i);

                    //fetching building name and id and storing them in arraylist
                    buildingNames.add(buildingDetail.getString("name"));
                    buildingId.add(buildingDetail.getString("@id"));


                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //calling the custom adapter
            CustomAdapter customAdapter = new CustomAdapter(buildingNames, buildingId,MyPlaces.this);
            recyclerView.setAdapter(customAdapter);

            //bottom navigation bar hooks
            bottomNavigationView = findViewById(R.id.bottom_navigator);
            bottomNavigationView.setSelectedItemId(R.id.lookAroundMenu);

            bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {

                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    switch (item.getItemId()) {
                        case R.id.arNavMenu:
                            startActivity(new Intent(getApplicationContext(), MainARNavigateActivity.class));
                            overridePendingTransition(0, 0);
                            return true;

                        case R.id.mapMenu:
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            overridePendingTransition(0, 0);
                            return true;

                        case R.id.lookAroundMenu:
                            startActivity(new Intent(getApplicationContext(), LookAroundActivity.class));
                            overridePendingTransition(0, 0);
                            return true;

                        case R.id.myPlacesMenu:

                            return true;


                    }

                    return false;
                }
            });

        }

        //method to load JSONn file from assets
        private String loadJSONfromAssets () {
            String json = null;

            try {
                InputStream is = getAssets().open("buildings.json");
                int size = is.available();

                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();

                json = new String(buffer, "UTF-8");
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            return json;
        }

    }