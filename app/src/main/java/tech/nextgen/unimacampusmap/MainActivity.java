package tech.nextgen.unimacampusmap;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.TileCache;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.layers.ArcGISTiledLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.BasemapStyle;
import com.esri.arcgisruntime.mapping.MobileMapPackage;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.Callout;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
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

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private ArrayList<MapPreview> mMapPreviews = new ArrayList<>();
    private MapView mMapView;

    private Button mExportTilesButton;
    private ConstraintLayout mTileCachePreviewLayout;
    private View mPreviewMask;

    private MapView mTileCachePreview;
    private ArcGISTiledLayer mTiledLayer;
    private ExportTileCacheJob mExportTileCacheJob;
    private ExportTileCacheTask mExportTileCacheTask;


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

                    case R.id.settingsMenu:
                        startActivity(new Intent(getApplicationContext(), Settings.class));
                        overridePendingTransition(0, 0);
                        return true;

                }

                return false;
            }
        });

        // get references to ui elements
        mTileCachePreviewLayout = findViewById(R.id.mapPreviewLayout);
        mPreviewMask = findViewById(R.id.previewMask);
        mTileCachePreview = findViewById(R.id.previewMapView);
        mMapView = findViewById(R.id.mapView);

        // authentication with an API key or named user is required
        // to access basemaps and other location services
        ArcGISRuntimeEnvironment.setApiKey("AAPKae2e0e4cb2814af49d6346db0343b8f9NyzRssRkbMgKM_Oc-amCwMPW__5G4lDx9CTq4dP3T7vXSpLv1uM22fr7t50GNn-a");

        // set the basemap of the map
        ArcGISMap map = new ArcGISMap();
        map.setBasemap(new Basemap(BasemapStyle.ARCGIS_IMAGERY));
        // set a min scale to avoid instance of downloading a tile cache that is too big
        map.setMinScale(10000000);
        mMapView.setMap(map);
        mMapView.setViewpoint(new Viewpoint(35.0, -117.0, 10000000.0));

        mExportTilesButton = findViewById(R.id.exportTilesButton);
        mExportTilesButton.setOnClickListener(v -> initiateDownload());

        Button previewCloseButton = findViewById(R.id.closeButton);
        previewCloseButton.setOnClickListener(v -> clearPreview());

        clearPreview();
    }

    private Envelope viewToExtent() {
        // upper left corner of the downloaded tile cache area
        android.graphics.Point minScreenPoint = new android.graphics.Point(mMapView.getLeft() - mMapView.getWidth(),
                mMapView.getTop() - mMapView.getHeight());
        // lower right corner of the downloaded tile cache area
        android.graphics.Point maxScreenPoint = new android.graphics.Point(minScreenPoint.x + mMapView.getWidth() * 3,
                minScreenPoint.y + mMapView.getHeight() * 3);
        // convert screen points to map points
        Point minPoint = mMapView.screenToLocation(minScreenPoint);
        Point maxPoint = mMapView.screenToLocation(maxScreenPoint);
        // use the points to define and return an envelope
        return new Envelope(minPoint, maxPoint);
    }

    /**
     * Clear preview window.
     */
    private void clearPreview() {
        // make map preview invisible
        mTileCachePreview.getChildAt(0).setVisibility(View.INVISIBLE);
        mMapView.bringToFront();
        // show red preview mask
        mPreviewMask.bringToFront();
        mExportTilesButton.setVisibility(View.VISIBLE);
    }

    /**
     * Using scale defined by the main MapView and the TiledLayer and an extent defined by viewToExtent() as parameters,
     * downloads a TileCache locally to the device.
     */
    private void initiateDownload() {

        mTiledLayer = (ArcGISTiledLayer) mMapView.getMap().getBasemap().getBaseLayers().get(0);
        // initialize the export task
        mExportTileCacheTask = new ExportTileCacheTask(mTiledLayer.getUri());
        final ListenableFuture<ExportTileCacheParameters> parametersFuture = mExportTileCacheTask
                .createDefaultExportTileCacheParametersAsync(viewToExtent(), mMapView.getMapScale(), mMapView.getMapScale() * 0.1);
        parametersFuture.addDoneListener(() -> {
            try {
                // export tile cache to directory
                ExportTileCacheParameters parameters = parametersFuture.get();
                mExportTileCacheJob = mExportTileCacheTask
                        .exportTileCache(parameters, getCacheDir() + "/file.tpkx");
            } catch (InterruptedException e) {
                Log.e(TAG, "TileCacheParameters interrupted: " + e.getMessage());
            } catch (ExecutionException e) {
                Log.e(TAG, "Error generating parameters: " + e.getMessage());
            }
            mExportTileCacheJob.start();

            createProgressDialog(mExportTileCacheJob);

            mExportTileCacheJob.addJobDoneListener(() -> {
                if (mExportTileCacheJob.getResult() != null) {
                    TileCache exportedTileCacheResult = mExportTileCacheJob.getResult();
                    showMapPreview(exportedTileCacheResult);
                } else {
                    Log.e(TAG, "Tile cache job result null. File size may be too big.");
                    Toast.makeText(this,
                            "Tile cache job result null. File size may be too big. Try zooming in before exporting tiles",
                            Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    /**
     * Show progress UI elements.
     *
     * @param exportTileCacheJob used to track progress and cancel when required
     */
    private void createProgressDialog(ExportTileCacheJob exportTileCacheJob) {

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Export Tile Cache Job");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel",
                (dialogInterface, i) -> exportTileCacheJob.cancel());
        progressDialog.show();

        exportTileCacheJob.addProgressChangedListener(() -> progressDialog.setProgress(exportTileCacheJob.getProgress()));
        exportTileCacheJob.addJobDoneListener(progressDialog::dismiss);
    }

    /**
     * Show tile cache preview window including MapView.
     *
     * @param result Takes the TileCache from the ExportTileCacheJob.
     */
    private void showMapPreview(TileCache result) {
        ArcGISTiledLayer newTiledLayer = new ArcGISTiledLayer(result);
        ArcGISMap map = new ArcGISMap(new Basemap(newTiledLayer));
        mTileCachePreview.setMap(map);
        mTileCachePreview.setViewpoint(mMapView.getCurrentViewpoint(Viewpoint.Type.CENTER_AND_SCALE));
        mTileCachePreview.setVisibility(View.VISIBLE);
        mTileCachePreviewLayout.bringToFront();
        mTileCachePreview.getChildAt(0).setVisibility(View.VISIBLE);
        mExportTilesButton.setVisibility(View.GONE);
    }

    @Override
    protected void onPause() {
        mMapView.pause();
        mTileCachePreview.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.resume();
        mTileCachePreview.resume();
    }

    @Override
    protected void onDestroy() {
        mMapView.dispose();
        mTileCachePreview.dispose();
        super.onDestroy();
    }
}
