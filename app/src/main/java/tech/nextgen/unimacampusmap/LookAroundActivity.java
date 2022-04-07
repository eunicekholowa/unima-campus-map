package tech.nextgen.unimacampusmap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class LookAroundActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_look_around);


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
                        startActivity(new Intent(getApplicationContext(), FindRoute.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.lookAroundMenu:
                       return true;

                    case R.id.myPlacesMenu:
                        startActivity(new Intent(getApplicationContext(), MyPlaces.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.settingsMenu:
                        startActivity(new Intent(getApplicationContext(), Settings.class));
                        overridePendingTransition(0,0);
                        return true;

                }

                return false;
            }
        });
    }
}