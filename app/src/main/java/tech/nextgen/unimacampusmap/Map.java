package tech.nextgen.unimacampusmap;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.esri.arcgisruntime.mapping.view.MapView;

public class Map extends AppCompatActivity {
    private MapView mMapView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        mMapView = findViewById(R.id.mapView);


        mMapView.setOnClickListener(v-> {
            startActivity(new Intent(this, MainActivity.class));
        });
//
    }
}