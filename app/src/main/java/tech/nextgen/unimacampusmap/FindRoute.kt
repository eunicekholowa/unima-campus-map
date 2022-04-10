package tech.nextgen.unimacampusmap

import android.R.layout
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.esri.arcgisruntime.ArcGISRuntimeEnvironment
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.esri.arcgisruntime.mapping.BasemapStyle
import com.esri.arcgisruntime.mapping.Viewpoint
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener
import com.esri.arcgisruntime.mapping.view.Graphic
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay
import com.esri.arcgisruntime.mapping.view.MapView
import com.esri.arcgisruntime.symbology.SimpleLineSymbol
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol
import com.esri.arcgisruntime.tasks.networkanalysis.RouteTask
import com.esri.arcgisruntime.tasks.networkanalysis.Stop
import tech.nextgen.unimacampusmap.databinding.ActivityFindRouteBinding
import kotlin.math.roundToInt


class FindRoute : AppCompatActivity() {

    private val activityMainBinding: ActivityFindRouteBinding by lazy {
        ActivityFindRouteBinding.inflate(layoutInflater)
    }

    private val mapView: MapView by lazy {
        activityMainBinding.mapView
    }

    private val listView: ListView by lazy {
        activityMainBinding.listView
    }

    private val directionsList: MutableList<String> by lazy {
        mutableListOf("Tap to add two points to the map to find a route between them.")
    }

    private val arrayAdapter by lazy {
        ArrayAdapter(this, layout.simple_list_item_1, directionsList)
    }

    private val routeStops: MutableList<Stop> by lazy {
        mutableListOf()
    }

    private val graphicsOverlay: GraphicsOverlay by lazy {
        GraphicsOverlay()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityMainBinding.root)

        listView.adapter = arrayAdapter

        setApiKeyForApp()

        setupMap()

    }

    override fun onPause() {
        mapView.pause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        mapView.resume()
    }

    override fun onDestroy() {
        mapView.dispose()
        super.onDestroy()
    }

    private fun setApiKeyForApp(){
        // set your API key
        // Note: it is not best practice to store API keys in source code. The API key is referenced
        // here for the convenience of this tutorial.

        ArcGISRuntimeEnvironment.setApiKey("AAPKae2e0e4cb2814af49d6346db0343b8f9NyzRssRkbMgKM_Oc-amCwMPW__5G4lDx9CTq4dP3T7vXSpLv1uM22fr7t50GNn-a")

    }

    // set up your map here. You will call this method from onCreate()
    private fun setupMap() {

        val map = ArcGISMap(BasemapStyle.OSM_STREETS)

        mapView.apply {
            // set the map on the map view
            this.map = map

            setViewpoint(Viewpoint(-15.3895 ,35.3373, 10500.20392))

            graphicsOverlays.add(graphicsOverlay)

            onTouchListener =
                    object : DefaultMapViewOnTouchListener(this@FindRoute, mapView) {
                        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                            val screenPoint = android.graphics.Point(e.x.roundToInt(), e.y.roundToInt())
                            when (routeStops.size) {
                                // on first tap, add a stop
                                0 -> {
                                    addStop(Stop(mapView.screenToLocation(screenPoint)))
                                }
                                // on second tap, add a stop and find route between them
                                1 -> {
                                    addStop(Stop(mapView.screenToLocation(screenPoint)))
                                    findRoute()
                                    Toast.makeText(
                                            applicationContext,
                                            "Calculating route.",
                                            Toast.LENGTH_SHORT
                                    ).show()
                                }
                                // on a further tap, clear and add a new first stop
                                else -> {
                                    clear()
                                    addStop(Stop(mapView.screenToLocation(screenPoint)))
                                }
                            }
                            return true
                        }
                    }

        }

    }

    private fun addStop(stop: Stop) {

        routeStops.add(stop)

        // create a blue circle symbol for the stop
        val stopMarker = SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.BLUE, 20f)
        // get the stop's geometry
        val routeStopGeometry = stop.geometry
        // add graphic to graphics overlay
        graphicsOverlay.graphics.add(Graphic(routeStopGeometry, stopMarker))

    }

    private fun findRoute() {

        val routeTask = RouteTask(
                this,
                "https://route-api.arcgis.com/arcgis/rest/services/World/Route/NAServer/Route_World"
        )

        val routeParametersFuture = routeTask.createDefaultParametersAsync()
        routeParametersFuture.addDoneListener {
            try {
                val routeParameters = routeParametersFuture.get().apply {
                    isReturnDirections = true
                    setStops(routeStops)
                }

                // get the route and display it
                val routeResultFuture = routeTask.solveRouteAsync(routeParameters)
                routeResultFuture.addDoneListener {
                    try {

                        val result = routeResultFuture.get()
                        val routes = result.routes
                        if (routes.isNotEmpty()) {
                            val route = routes[0]

                            val shape = route.routeGeometry
                            val routeGraphic = Graphic(
                                    shape,
                                    SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.BLUE, 2f)
                            )
                            graphicsOverlay.graphics.add(routeGraphic)

                            // get the direction text for each maneuver and display it as a list in the UI
                            directionsList.clear()
                            route.directionManeuvers.forEach { directionsList.add(it.directionText) }
                            arrayAdapter.notifyDataSetChanged()

                        }

                    } catch (e: Exception) {
                        val error = "Error solving route: " + e.message
                        Toast.makeText(this, error, Toast.LENGTH_LONG).show()
                        Log.e(MainActivity::class.simpleName, error)
                    }
                }

            } catch (e: Exception) {
                val error = "Error creating default route parameters: " + e.message
                Toast.makeText(this, error, Toast.LENGTH_LONG).show()
                Log.e(MainActivity::class.simpleName, error)
            }
        }

    }

    private fun clear() {
        routeStops.clear()
        graphicsOverlay.graphics.clear()
        directionsList.clear()
        directionsList.add("Tap to add two points to the map to find a route between them.")
        arrayAdapter.notifyDataSetChanged()
    }

}