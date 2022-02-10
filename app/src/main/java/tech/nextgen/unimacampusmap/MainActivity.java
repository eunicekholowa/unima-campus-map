package tech.nextgen.unimacampusmap;

import static tech.nextgen.unimacampusmap.R.id;
import static tech.nextgen.unimacampusmap.R.layout;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.BasemapStyle;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    private MapView mMapView;
    private ImageView imageView;
    private NavigationView navigationView;
    DrawerLayout drawerLayout;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_main);
        imageView = findViewById(id.menu);
        navigationView = findViewById(id.nav_view);
        drawerLayout = findViewById(id.drawer_layout);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        /*
        authentication with API key required to access basemap services
        and other location services
        */
        ArcGISRuntimeEnvironment.setApiKey("AAPKae2e0e4cb2814af49d6346db0343b8f9NyzRssRkbMgKM_Oc-amCwMPW__5G4lDx9CTq4dP3T7vXSpLv1uM22fr7t50GNn-a");

        //inflate map view from layout
        mMapView = findViewById(id.mapView);

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
    }@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_file, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Handle item selection
        switch (item.getItemId()) {
            case id.i1:
                //perform any action;
                return true;
            case id.map:
                //perform any action;
                return true;
            case id.places:
                //perform any action;
                return true;
            case id.look:
                //perform any action;
                return true;
            case id.navbar:
                //perform any action;
                return true;
            case id.settings:
                //perform any action;
                return true;
            case id.help:
                //perform any action;
                return true;
            case id.feedback:
                //perform any action;
                return true;
            case id.search:
                //perform any action;
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}