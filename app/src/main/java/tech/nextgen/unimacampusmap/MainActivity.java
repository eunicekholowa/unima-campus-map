package tech.nextgen.unimacampusmap;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.BasemapStyle;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.MapView;

public class MainActivity extends AppCompatActivity {

    private MapView mMapView;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
        authentication with API key required to access basemap services
        and other location services
        */
        ArcGISRuntimeEnvironment.setApiKey("AAPKae2e0e4cb2814af49d6346db0343b8f9NyzRssRkbMgKM_Oc-amCwMPW__5G4lDx9CTq4dP3T7vXSpLv1uM22fr7t50GNn-a");

        //inflate map view from layout
        mMapView = findViewById(R.id.mapView);

        //create a map with topographic basemap
        ArcGISMap map = new ArcGISMap(BasemapStyle.ARCGIS_TOPOGRAPHIC);

        //set the map to be displayed in this view
        mMapView.setMap(map);
        mMapView.setViewpoint(new Viewpoint( -15.3920597,35.3399277,10000));
    }
    @Override
    protected void onPause(){
        mMapView.pause();
        super.onPause();
    }

    @Override
    protected void onResume(){
        super.onResume();
        mMapView.resume();
    }

    @Override
    protected void onDestroy() {
        mMapView.dispose();
        super.onDestroy();
    }

}