package tech.nextgen.unimacampusmap;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.TileCache;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.ArcGISTiledLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.BasemapStyle;
import com.esri.arcgisruntime.mapping.MobileMapPackage;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.Callout;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.tasks.geocode.LocatorTask;
import com.esri.arcgisruntime.tasks.geocode.ReverseGeocodeParameters;
import com.esri.arcgisruntime.tasks.networkanalysis.RouteParameters;
import com.esri.arcgisruntime.tasks.networkanalysis.RouteTask;
import com.esri.arcgisruntime.tasks.tilecache.ExportTileCacheJob;
import com.esri.arcgisruntime.tasks.tilecache.ExportTileCacheParameters;
import com.esri.arcgisruntime.tasks.tilecache.ExportTileCacheTask;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import tech.nextgen.unimacampusmap.spinner.ItemData;
import tech.nextgen.unimacampusmap.spinner.SpinnerAdapter;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int SCALE = 5000;
    private MapView mMapView;

    private LocationDisplay mLocationDisplay;
    private Spinner mSpinner;

    private final int requestCode = 2;
    private final String[] reqPermissions = { Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission
            .ACCESS_COARSE_LOCATION };

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigator);
        bottomNavigationView.setSelectedItemId(R.id.mapView);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.arNavMenu:
                        startActivity(new Intent(getApplicationContext(), MainARNavigateActivity.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.mapMenu:
                        return true;

                    case R.id.lookAroundMenu:
                        startActivity(new Intent(getApplicationContext(), LookAroundActivity.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.myPlacesMenu:
                        startActivity(new Intent(getApplicationContext(), MyPlaces.class));
                        overridePendingTransition(0, 0);
                        return true;

                }

                return false;
            }
        });
        // Get the Spinner from layout
        mSpinner = findViewById(R.id.spinner);

        // Get the MapView from layout and set a map with the BasemapType Imagery
        mMapView = findViewById(R.id.mapView);
        ArcGISMap map = new ArcGISMap(BasemapStyle.OSM_STANDARD);
        mMapView.setViewpoint(new Viewpoint( -15.3900322357 ,35.3376117365,7000));
        mMapView.setMap(map);

        // get the MapView's LocationDisplay
        mLocationDisplay = mMapView.getLocationDisplay();

        // Listen to changes in the status of the location data source.
        mLocationDisplay.addDataSourceStatusChangedListener(dataSourceStatusChangedEvent -> {

            // If LocationDisplay started OK, then continue.
            if (dataSourceStatusChangedEvent.isStarted())
                return;

            // No error is reported, then continue.
            if (dataSourceStatusChangedEvent.getError() == null)
                return;

            // If an error is found, handle the failure to start.
            // Check permissions to see if failure may be due to lack of permissions.
            boolean permissionCheck1 = ContextCompat.checkSelfPermission(this, reqPermissions[0]) ==
                    PackageManager.PERMISSION_GRANTED;
            boolean permissionCheck2 = ContextCompat.checkSelfPermission(this, reqPermissions[1]) ==
                    PackageManager.PERMISSION_GRANTED;

            if (!(permissionCheck1 && permissionCheck2)) {
                // If permissions are not already granted, request permission from the user.
                ActivityCompat.requestPermissions(this, reqPermissions, requestCode);
            } else {
                // Report other unknown failure types to the user - for example, location services may not
                // be enabled on the device.
                String message = String.format("Error in DataSourceStatusChangedListener: %s", dataSourceStatusChangedEvent
                        .getSource().getLocationDataSource().getError().getMessage());
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();

                // Update UI to reflect that the location display did not actually start
                mSpinner.setSelection(0, true);
            }
        });

        // Populate the list for the Location display options for the spinner's Adapter
        ArrayList<ItemData> list = new ArrayList<>();
        list.add(new ItemData("Stop", R.drawable.locationdisplaydisabled));
        list.add(new ItemData("On", R.drawable.locationdisplayon));
        list.add(new ItemData("Re-Center", R.drawable.locationdisplayrecenter));
        list.add(new ItemData("Navigation", R.drawable.ic_baseline_navigation_24));
        list.add(new ItemData("Compass", R.drawable.locationdisplayheading));

        SpinnerAdapter adapter = new SpinnerAdapter(this, R.layout.spinner_layout, R.id.txt, list);
        mSpinner.setAdapter(adapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {
                    case 0:
                        // Stop Location Display
                        if (mLocationDisplay.isStarted())
                            mLocationDisplay.stop();
                        break;
                    case 1:
                        // Start Location Display
                        if (!mLocationDisplay.isStarted())
                            mLocationDisplay.startAsync();
                        break;
                    case 2:
                        // Re-Center MapView on Location
                        mLocationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.RECENTER);
                        if (!mLocationDisplay.isStarted())
                            mLocationDisplay.startAsync();
                        break;
                    case 3:
                        // Start Navigation Mode
                        Intent intent = new Intent(getApplicationContext(),   FindRoute.class);
                        startActivity(intent);
                    case 4:
                        // Start Compass Mode
                        // This mode is better suited for waypoint navigation when the user is walking.
                        mLocationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.COMPASS_NAVIGATION);
                        if (!mLocationDisplay.isStarted())
                            mLocationDisplay.startAsync();
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // If request is cancelled, the result arrays are empty.
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Location permission was granted. This would have been triggered in response to failing to start the
            // LocationDisplay, so try starting this again.
            mLocationDisplay.startAsync();
        } else {
            // If permission was denied, show toast to inform user what was chosen. If LocationDisplay is started again,
            // request permission UX will be shown again, option should be shown to allow never showing the UX again.
            // Alternative would be to disable functionality so request is not shown again.
            Toast.makeText(this, getString(R.string.location_permission_denied), Toast.LENGTH_SHORT).show();

            // Update UI to reflect that the location display did not actually start
            mSpinner.setSelection(0, true);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.dispose();
    }
}

