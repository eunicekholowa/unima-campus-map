package tech.nextgen.unimacampusmap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MyPlaces extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_places);

        bottomNavigationView = findViewById(R.id.bottom_navigator);
        bottomNavigationView.setSelectedItemId(R.id.myPlacesMenu);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.arNavMenu:
                        startActivity(new Intent(getApplicationContext(), ARNavigateActivity.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.mapMenu:
                        startActivity(new Intent(getApplicationContext(), MyPlaces.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.lookAroundMenu:
                        startActivity(new Intent(getApplicationContext(), LookAroundActivity.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.myPlacesMenu:
                        return true;

                    case R.id.settingsMenu:
                        startActivity(new Intent(getApplicationContext(), Settings.class));
                        overridePendingTransition(0, 0);
                        return true;

                }

                return false;
            }
        });
    }}