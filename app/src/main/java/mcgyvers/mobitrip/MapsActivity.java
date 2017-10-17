package mcgyvers.mobitrip;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;


import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.location.NominatimPOIProvider;
import org.osmdroid.bonuspack.location.POI;
import org.osmdroid.bonuspack.routing.MapQuestRoadManager;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.FolderOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.MinimapOverlay;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.mylocation.SimpleLocationOverlay;

import java.util.ArrayList;

import mcgyvers.mobitrip.interfaces.OpenStreetMapConstants;

public class MapsActivity extends AppCompatActivity implements OpenStreetMapConstants {


    // ===========================================================
    // Constants
    // ===========================================================

    private static final int MENU_ZOOMIN_ID = Menu.FIRST;
    private static final int MENU_ZOOMOUT_ID = MENU_ZOOMIN_ID + 1;
    private static final int MENU_TILE_SOURCE_ID = MENU_ZOOMOUT_ID + 1;
    private static final int MENU_ANIMATION_ID = MENU_TILE_SOURCE_ID + 1;
    private static final int MENU_MINIMAP_ID = MENU_ANIMATION_ID + 1;

    // ===========================================================
    // Fields
    // ===========================================================

    private MapView mMapView;
    private IMapController mOsmvController;
    private SimpleLocationOverlay mMyLocationOverlay;
    private ScaleBarOverlay mScaleBarOverlay;
    private MinimapOverlay mMiniMapOverlay;


    // ===========================================================
    // Constructors
    // ===========================================================


    final int TWO_MINUTES = 1000 * 60 * 2; // interval between update requests
    Location lastKnownLocation = null; // last known location for estimation purposes
    LocationManager locationManager;
    LocationListener locationListener;
    IMapController mapController;
    MapView map;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context ctx = getApplicationContext();
        //important! set your user agent to prevent getting banned from the osm servers
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        setContentView(R.layout.activity_osmap);


        final RelativeLayout rl = new RelativeLayout(this);

        this.mMapView = new MapView(this);
        this.mMapView.setTilesScaledToDpi(true);
        this.mOsmvController = this.mMapView.getController();
        rl.addView(this.mMapView, new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT,
                LayoutParams.FILL_PARENT));


        // disabling the stric mode for making network calls on the main thread
        // PS: REMOVE THOSE LINES OF CODE FOR THE PRODUCTION VERSION
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);




        /* Scale Bar Overlay */
        {
            this.mScaleBarOverlay = new ScaleBarOverlay(mMapView);
            this.mMapView.getOverlays().add(mScaleBarOverlay);
            // Scale bar tries to draw as 1-inch, so to put it in the top center, set x offset to
            // half screen width, minus half an inch.
            this.mScaleBarOverlay.setScaleBarOffset(
                    (int) (getResources().getDisplayMetrics().widthPixels / 2 - getResources()
                            .getDisplayMetrics().xdpi / 2), 10
            );

        }

        /* SingleLocation-Overlay */
        {
            /*
             * Create a static Overlay showing a single location. (Gets updated in
			 * onLocationChanged(Location loc)!
			 */
            this.mMyLocationOverlay = new SimpleLocationOverlay(((BitmapDrawable) getResources().getDrawable(org.osmdroid.library.R.drawable.person)).getBitmap());
            this.mMapView.getOverlays().add(mMyLocationOverlay);

        }

        /* ZoomControls */
        {
            /* Create a ImageView with a zoomIn-Icon. */
            final ImageView ivZoomIn = new ImageView(this);
            ivZoomIn.setImageResource(org.osmdroid.library.R.drawable.zoom_in);
            /* Create RelativeLayoutParams, that position it in the top right corner. */
            final RelativeLayout.LayoutParams zoominParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            zoominParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            zoominParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            rl.addView(ivZoomIn, zoominParams);

            ivZoomIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    MapsActivity.this.mOsmvController.zoomIn();
                }
            });

        }

        /* MiniMap */
        {
            mMiniMapOverlay = new MinimapOverlay(this, mMapView.getTileRequestCompleteHandler());
            this.mMapView.getOverlays().add(mMiniMapOverlay);
        }

        // Default location and zoom level
        mapController = mMapView.getController();
        mapController.setZoom(13);
        //GeoPoint startPoint = new GeoPoint(48.13, -1.6);
        //mapController.setCenter(startPoint);


        //GeoPoint endPoint = new GeoPoint(48.4, -1.9);

        //drawPath(startPoint, endPoint, false);
        //drawPrefs("Fuel", startPoint);


        // PathOverlay pathOverlay = new PathOverlay(Color.RED, this);
        // pathOverlay.addPoint(new GeoPoint(40.714623, -74.006605));
        // pathOverlay.addPoint(new GeoPoint(38.8951118, -77.0363658));
        // pathOverlay.addPoint(new GeoPoint(34.052186, -118.243932));
        // pathOverlay.getPaint().setStrokeWidth(50.0f);
        // pathOverlay.setAlpha(100);
        // this.mMapView.getOverlays().add(pathOverlay);

        this.setContentView(rl);


        // testing the GPS functionality:
        // Acquire a reference to the system Location Manager
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // getting the last known location in order to make a current best estimate
        String locationProvider = LocationManager.NETWORK_PROVIDER;
        if(lastKnownLocation == null){
            checkPermission();
            lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
        }



        // Define a listener that responds to location updates
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                //Called when a new location is found by the network location provider
                makeUseOfNewLocation(location);
                System.out.println("new location received");
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };



        // start the collection of samples
        collectLocationSamples(5000, 0);







    }

    public void checkPermission(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
    }


    /**
     * Simply puts a marker on whatever location it is given by currentLocation
     * then updates the mapview. Intended for using with GPS to set the user's current
     * location
     *
     * @param currentLocation Location object containing whatever coordinates we want to mark
     * @param center option to centralize the map to the given point
     */
    private void setCurrentLocation(Location currentLocation, boolean center){

        double lati = currentLocation.getLatitude();
        double longi = currentLocation.getLongitude();

        GeoPoint startPoint = new GeoPoint(lati, longi);

        if(center)
        mapController.setCenter(startPoint);

        Marker startMaker = new Marker(mMapView);
        startMaker.setPosition(startPoint);
        startMaker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        mMapView.getOverlays().add(startMaker);
        startMaker.setIcon(getResources().getDrawable(R.drawable.ic_tourist_spots));
        //startMaker.setTitle("Start point");
        mMapView.invalidate();




    }

    /**
     * Determines whether one Location reading is better than the current Location fix
     * @param location  The new Location that you want to evaluate
     * @param currentBestLocation  The current Location fix, to which you want to compare the new one
     * @return true or false
     */
    private boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Checke wether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;



    }

    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);

    }


    /**
     * compares two locations, the last known location and a fresh new
     * provided in the location parameter, if the new one is better than the
     * old one, set it on the map
     *
     * @param location new location fix from a location provider
     */
    private void makeUseOfNewLocation(Location location) {
        if(isBetterLocation(location, lastKnownLocation)){
            // if the new location is better, we change the reading of the last known location
            // and set the new one in the map
            setCurrentLocation(location, true);
            lastKnownLocation = location;

        } else{
            // or else we set the old one in the map
            setCurrentLocation(lastKnownLocation, true);
        }
        drawPrefs("Hospital", lastKnownLocation);
    }

    private void collectLocationSamples(long interval, float distance){
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            // Permission to access locations
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            // Register the listener with the Location Manager to receive location updates
            // First parameter is type of location provider, in this case the provider is Network location
            // for cell-tower and wifi based location
            // second and third parameters relate to the frequency at which the the listener receives updates
            // second param is the interval between notifications and third is the minimum change in distance
            // between notifications. When both are set to 0, notifications come as frequently as possible
            // the last parameter is the listener that receives callbacks for location updates

            // setting time between updates to 5 seconds
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, interval, distance, locationListener);
            // requesting updates from GPS
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, interval, distance, locationListener);

        }
    }


    /**
     * Method for drawing a basic waypoint between a start and end
     * point of a trip. It implements the path into the global {@link MapView}
     * mMapView of the activity
     *
     * @param start a Location object with start coordinates
     * @param end Location with end coordinates
     * @param bycicle if a bycicle route is required
     * @return void
     *
     */
    public void drawPath(Location start, Location end, boolean bycicle){

        double lati = start.getLatitude();
        double longi = start.getLongitude();

        double lati1 = end.getLatitude();
        double logi1 = end.getLongitude();

        GeoPoint startPoint = new GeoPoint(lati, longi);
        GeoPoint endPoint = new GeoPoint(lati1, logi1);


        ArrayList<GeoPoint> waypoints = new ArrayList<GeoPoint>();
        waypoints.add(startPoint);
        waypoints.add(endPoint);


        RoadManager roadManager;

        if(bycicle){
            roadManager = new MapQuestRoadManager("kotHZmV3AzxQP6K6AhJzl2lvneBbmOdl");
            roadManager.addRequestOption("routeType=bicycle"); // a pedestrian option could be added as well
        } else {
            roadManager = new OSRMRoadManager(this);
        }




        Road road = roadManager.getRoad(waypoints);
        Polyline roadOverlay = RoadManager.buildRoadOverlay(road);
        mMapView.getOverlays().add(roadOverlay);
        mMapView.invalidate();


    }

    /**
     * Method for drawing different points of interest(POIs) close to
     * provided coordinates
     *
     * @param prefs name of the preference to be drawn in the map
     * @param loc Location object to point where prefs should be drawn close to
     * @return void
     */

    public void drawPrefs(String prefs, Location loc){

        double lati = loc.getLatitude();
        double logi = loc.getLongitude();

        GeoPoint closeTo = new GeoPoint(lati, logi);

        NominatimPOIProvider poiProvider = new NominatimPOIProvider("OSMBonusPackTutoUserAgent");
        ArrayList<POI> pois = poiProvider.getPOICloseTo(closeTo, prefs, 50, 2.0);

        FolderOverlay poiMarkers = new FolderOverlay(this);
        mMapView.getOverlays().add(poiMarkers);

        Drawable poiIcon = getResources().getDrawable(R.drawable.ic_gas_pumps); // change this for other POIs
        for(POI poi:pois){
            Marker poiMarker = new Marker(mMapView);
            poiMarker.setTitle(poi.mType);
            poiMarker.setSnippet(poi.mDescription);
            poiMarker.setPosition(poi.mLocation);
            poiMarker.setIcon(poiIcon);
            if (poi.mThumbnail != null){
                poiMarker.setImage(new BitmapDrawable(poi.mThumbnail));
            }
            poiMarkers.add(poiMarker);
        }

        //for drawing POIs along a route


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationManager.removeUpdates(locationListener);
        System.out.println("locations disabled");
    }

    @Override
    protected void onResume() {
        super.onResume();
        collectLocationSamples(5000, 0);
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods from SuperClass/Interfaces
    // ===========================================================

    @Override
    public boolean onCreateOptionsMenu(final Menu pMenu) {
        pMenu.add(0, MENU_ZOOMIN_ID, Menu.NONE, "ZoomIn");
        pMenu.add(0, MENU_ZOOMOUT_ID, Menu.NONE, "ZoomOut");

        final SubMenu subMenu = pMenu.addSubMenu(0, MENU_TILE_SOURCE_ID, Menu.NONE,
                "Choose Tile Source");
        {
            for (final ITileSource tileSource : TileSourceFactory.getTileSources()) {
                subMenu.add(0, 1000 + tileSource.ordinal(), Menu.NONE,
                        tileSource.name());
            }
        }

        pMenu.add(0, MENU_ANIMATION_ID, Menu.NONE, "Run Animation");
        pMenu.add(0, MENU_MINIMAP_ID, Menu.NONE, "Toggle Minimap");

        return true;
    }


    public boolean onOptionsItemSelected(final int featureId, final MenuItem item){

        switch (item.getItemId()) {
            case MENU_ZOOMIN_ID:
                this.mOsmvController.zoomIn();
                return true;

            case MENU_ZOOMOUT_ID:
                this.mOsmvController.zoomOut();
                return true;

            case MENU_TILE_SOURCE_ID:
                this.mMapView.invalidate();
                return true;

            case MENU_MINIMAP_ID:
                mMiniMapOverlay.setEnabled(!mMiniMapOverlay.isEnabled());
                this.mMapView.invalidate();
                return true;

            case MENU_ANIMATION_ID:
                // this.mMapView.getController().animateTo(52370816, 9735936,
                // MapControllerOld.AnimationType.MIDDLEPEAKSPEED,
                // MapControllerOld.ANIMATION_SMOOTHNESS_HIGH,
                // MapControllerOld.ANIMATION_DURATION_DEFAULT); // Hannover
                // Stop the Animation after 500ms (just to show that it works)
                // new Handler().postDelayed(new Runnable(){
                // @Override
                // public void run() {
                // SampleExtensive.this.mMapView.getController().stopAnimation(false);
                // }
                // }, 500);
                return true;

            default:
                ITileSource tileSource = TileSourceFactory.getTileSource(item.getItemId() - 1000);
                mMapView.setTileSource(tileSource);
                mMiniMapOverlay.setTileSource(tileSource);
        }
        return false;
    }

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
// ===========================================================




}
