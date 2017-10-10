package mcgyvers.mobitrip;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;



import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
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


    MapView map;

    @Override public void onCreate(Bundle savedInstanceState) {
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
            this.mMyLocationOverlay = new SimpleLocationOverlay(((BitmapDrawable)getResources().getDrawable(org.osmdroid.library.R.drawable.person)).getBitmap());
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
        IMapController mapController = mMapView.getController();
        mapController.setZoom(13);
        GeoPoint startPoint = new GeoPoint(48.13, -1.6);
        mapController.setCenter(startPoint);

        // testing markers
        Marker startMaker = new Marker(mMapView);
        startMaker.setPosition(startPoint);
        startMaker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        mMapView.getOverlays().add(startMaker);
        startMaker.setIcon(getResources().getDrawable(R.drawable.ic_tourist_spots));
        startMaker.setTitle("Start point");
        mMapView.invalidate();

        GeoPoint endPoint = new GeoPoint(48.4, -1.9);

        drawPath(startPoint, endPoint);



        // PathOverlay pathOverlay = new PathOverlay(Color.RED, this);
        // pathOverlay.addPoint(new GeoPoint(40.714623, -74.006605));
        // pathOverlay.addPoint(new GeoPoint(38.8951118, -77.0363658));
        // pathOverlay.addPoint(new GeoPoint(34.052186, -118.243932));
        // pathOverlay.getPaint().setStrokeWidth(50.0f);
        // pathOverlay.setAlpha(100);
        // this.mMapView.getOverlays().add(pathOverlay);

        this.setContentView(rl);




    }


    /**
     * Method for drawing a basic waypoint between a start and end
     * point of a trip. It implements the path into the global {@link MapView}
     * mMapView of the activity
     *
     * @param start a GeoPoint object with start coordinates
     * @param end GeoPoint with end coordinates
     * @return void
     *
     */

    public void drawPath(GeoPoint start, GeoPoint end){
        RoadManager roadManager = new OSRMRoadManager(this);

        ArrayList<GeoPoint> waypoints = new ArrayList<GeoPoint>();
        waypoints.add(start);
        waypoints.add(end);

        Road road = roadManager.getRoad(waypoints);
        Polyline roadOverlay = RoadManager.buildRoadOverlay(road);
        mMapView.getOverlays().add(roadOverlay);
        mMapView.invalidate();

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
