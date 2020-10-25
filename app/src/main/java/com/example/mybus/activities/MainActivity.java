package com.example.mybus.activities;

import androidx.activity.OnBackPressedDispatcherOwner;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Icon;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.mybus.R;
import com.example.mybus.fragments.AddLocation;
import com.example.mybus.fragments.HomeNav;
import com.example.mybus.models.Pickup;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.android.gestures.MoveGestureDetector;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.FillManager;
import com.mapbox.mapboxsdk.plugins.annotation.FillOptions;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;
import com.mapbox.mapboxsdk.style.layers.CircleLayer;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.location.GpsStatus.GPS_EVENT_STARTED;
import static android.location.GpsStatus.GPS_EVENT_STOPPED;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;

public class MainActivity extends AppCompatActivity implements LocationListener, OnMapReadyCallback, PermissionsListener,
        MapboxMap.OnFlingListener, MapboxMap.OnMoveListener, MapboxMap.OnCameraMoveListener{

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private static final int WELCOME_TIMEOUT = 250;
    LocationManager locationManager;
    String provider;
    public String currentFrame = "", placeName = "";
    public String lat = "", lng = "";
    private PermissionsManager permissionsManager;
    private static MapView mapView;
    private MapboxMap mapboxMap;

    private ImageButton zoom;
    private LocationEngine locationEngine;
    private LocationComponent locationComponent;
    public static Boolean isvis = false;
    public Location forfrag;

    private BottomSheetBehavior bottomSheetBehavior;

    private LocationChangeListeningActivityLocationCallback callback =
            new LocationChangeListeningActivityLocationCallback(this);

    private static final int REQUEST_CODE_AUTOCOMPLETE = 1;
    private String geojsonSourceLayerId = "geojsonSourceLayerId";
    private String symbolIconId = "symbolIconId";
    public int count123 = 0;
    FirebaseAuth auth;
    public final  List<LatLng> ACTIVE_POINTS = new ArrayList<>();
    public final  List<LatLng> NONACTIVE_POINTS = new ArrayList<>();
    private static final String SOURCE_ID = "SOURCE_ID";
    private static final String ICON_ID = "ICON_ID";
    private static final String LAYER_ID = "LAYER_ID";
    List<Feature> symbolLayerIconFeatureList;
    Style style;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        View bottomSheet = findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        zoom = findViewById(R.id.zoom_btn);
        zoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    if(mapboxMap.getLocationComponent().getLastKnownLocation()!=null) {
                        com.mapbox.mapboxsdk.geometry.LatLng abc = new com.mapbox.mapboxsdk.geometry.LatLng();
                        abc.setLatitude(mapboxMap.getLocationComponent().getLastKnownLocation().getLatitude());
                        abc.setLongitude(mapboxMap.getLocationComponent().getLastKnownLocation().getLongitude());
                        mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                                .target(abc)
                                .zoom(14f)
                                .padding(0, 0, 0, 750)
                                .build()), 500);

                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"Please Turn on Location...", Toast.LENGTH_SHORT).show();
                    }
                }
                catch(Exception e)
                {
                    Toast.makeText(getApplicationContext(),"Please Turn on Location...", Toast.LENGTH_SHORT).show();
                }
            }
        });

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_bottom,
                new HomeNav()).commit();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                onMapReady(mapboxMap);
            }
        }, 3000);
    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {

        MainActivity.this.mapboxMap = mapboxMap;
        currentFrame = "search";

        symbolLayerIconFeatureList = new ArrayList<>();
        drawLayer();

        mapboxMap.setStyle(new Style.Builder().fromUri("mapbox://styles/uchiha-itachi/ckgotitik0d7819lata9c7x82")
                , new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {

                enableLocationComponent(style);

                checklivelocation();

                resetcamera();

                style.addSource(new GeoJsonSource(SOURCE_ID,
                        FeatureCollection.fromFeatures(symbolLayerIconFeatureList)));
                style.addLayer(new CircleLayer(LAYER_ID, SOURCE_ID)
                        .withProperties(
                                iconImage(ICON_ID),
                                iconAllowOverlap(true),
                                iconIgnorePlacement(true)
                        ));

                mapboxMap.addOnFlingListener(MainActivity.this);
                mapboxMap.addOnMoveListener(MainActivity.this);
                mapboxMap.addOnCameraMoveListener(MainActivity.this);

            }
        });
    }

    @Override
    public void onBackPressed() {


        super.onBackPressed();

        if (currentFrame.equals("search2")) {
            for(int i=0;i< getSupportFragmentManager().getBackStackEntryCount();i++)
            {
                getSupportFragmentManager().popBackStack();
            }
            currentFrame = "search";
            expandState();
            mapboxMap.clear();
        }

    }

    public void resetcamera()
    {
        Handler h = new Handler();

        h.postDelayed(() -> {
            count123++;
            if(mapboxMap.getLocationComponent().getLastKnownLocation()!=null)
            {
                try {

                    if(mapboxMap.getLocationComponent().getLastKnownLocation()!=null) {
                        com.mapbox.mapboxsdk.geometry.LatLng abc = new com.mapbox.mapboxsdk.geometry.LatLng();
                        abc.setLatitude(mapboxMap.getLocationComponent().getLastKnownLocation().getLatitude());
                        abc.setLongitude(mapboxMap.getLocationComponent().getLastKnownLocation().getLongitude());
                        mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                                .target(abc)
                                .zoom(14f)
                                .bearing(0)
                                .padding(0, 0, 0, 500)
                                .build()), 500);

                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"Please Turn on Location...", Toast.LENGTH_SHORT).show();
                    }
                }
                catch(Exception e)
                {
                    Toast.makeText(getApplicationContext(),"Please Turn on Location...", Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                if(count123<=10) {
                    resetcamera();
                }
            }
        }, 500);


    }

    public void drawLayer(){
        if (mapboxMap != null) {
            symbolLayerIconFeatureList.clear();

            try {

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Pickups");
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Pickup pickup = snapshot.getValue(Pickup.class);
                            if (auth.getCurrentUser().getUid().equals(pickup.getOwnerId())) {
                                String lat = pickup.getLatitude();
                                String lng = pickup.getLongitude();
                                double dLat = Double.parseDouble(lat);
                                double dLng = Double.parseDouble(lng);
                                if (pickup.getStatus().equals("true")) {
                                    symbolLayerIconFeatureList.add(Feature.fromGeometry(
                                            Point.fromLngLat(dLng, dLat)));
                                }
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            } catch (Exception e){
                // Exception
            }
        }
    }

    public void expandState() {
        bottomSheetBehavior.setHideable(false);

        if (currentFrame.equals("search")) {
            zoom.setVisibility(View.VISIBLE);
            final float scale = getResources().getDisplayMetrics().density;
            final float GESTURE_THRESHOLD_DP = 110.0f;
            int mGestureThreshold2 = (int) (GESTURE_THRESHOLD_DP * scale + 0.5f);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            bottomSheetBehavior.setPeekHeight(mGestureThreshold2);
        } else {
            zoom.setVisibility(View.VISIBLE);
            final float scale = getResources().getDisplayMetrics().density;
            final float GESTURE_THRESHOLD_DP = 110.0f;
            int mGestureThreshold2 = (int) (GESTURE_THRESHOLD_DP * scale + 0.5f);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            bottomSheetBehavior.setPeekHeight(mGestureThreshold2);
        }
    }

    public void collapseState() {
        bottomSheetBehavior.setHideable(false);

        if (currentFrame.equals("search")) {
            zoom.setVisibility(View.VISIBLE);
            final float scale = getResources().getDisplayMetrics().density;
            final float GESTURE_THRESHOLD_DP = 110.0f;
            int mGestureThreshold2 = (int) (GESTURE_THRESHOLD_DP * scale + 0.5f);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            bottomSheetBehavior.setPeekHeight(mGestureThreshold2);
        } else {
            zoom.setVisibility(View.VISIBLE);
            final float scale = getResources().getDisplayMetrics().density;
            final float GESTURE_THRESHOLD_DP = 110.0f;
            int mGestureThreshold2 = (int) (GESTURE_THRESHOLD_DP * scale + 0.5f);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            bottomSheetBehavior.setPeekHeight(mGestureThreshold2);
        }
    }

    public void searchLocation() {
        Point myPoint = Point.fromLngLat(77.5946, 12.9716);

        Intent intent = new PlaceAutocomplete.IntentBuilder()
                .accessToken(Mapbox.getAccessToken() != null ? Mapbox.getAccessToken() : getString(R.string.mapbox_access_token))
                .placeOptions(PlaceOptions.builder()
                        .backgroundColor(Color.parseColor("#EEEEEE"))
                        .toolbarColor(getResources().getColor(R.color.colorPrimary))
                        .limit(10)
                        .proximity(myPoint)
                        .country("IN")
                        .build(PlaceOptions.MODE_CARDS))
                .build(MainActivity.this);
        startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
    }

    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationComponent(@NonNull Style style) {

        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {

            try {
                // Get an instance of the component
                LocationComponent locationComponent = mapboxMap.getLocationComponent();

                // Activate with a built LocationComponentActivationOptions object
                locationComponent.activateLocationComponent(LocationComponentActivationOptions.builder(this, style).build());

                // Enable to make component visible
                locationComponent.setLocationComponentEnabled(true);

                // Set the component's camera mode
                locationComponent.setCameraMode(CameraMode.TRACKING);

                // Set the component's render mode
                locationComponent.setRenderMode(RenderMode.COMPASS);
                
                initLocationEngine();
            } catch (Exception e) {
                Toast.makeText(this, "Please Turn on Location...", Toast.LENGTH_SHORT).show();
            }

        } else {

            permissionsManager = new PermissionsManager(this);

            permissionsManager.requestLocationPermissions(this);

        }
    }

    @SuppressLint("MissingPermission")
    public void checklivelocation()
    {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

    }

    @Override
    @SuppressWarnings( {"MissingPermission"})
    protected void onStart() {
        super.onStart();
        mapView.onStart();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                expandState();
            }
        }, WELCOME_TIMEOUT);

    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                onMapReady(mapboxMap);
            }
        }, 3000);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle(R.string.title_location_permission)
                        .setMessage(R.string.text_location_permission)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_AUTOCOMPLETE) {

            mapboxMap.clear();
            CarmenFeature selectedCarmenFeature = PlaceAutocomplete.getPlace(data);
            Log.d("PLACE", selectedCarmenFeature.placeName());

            if (mapboxMap != null) {
                Style style = mapboxMap.getStyle();
                if (style != null) {
                    GeoJsonSource source = style.getSourceAs(geojsonSourceLayerId);
                    if (source != null) {
                        source.setGeoJson(FeatureCollection.fromFeatures(
                                new Feature[] {Feature.fromJson(selectedCarmenFeature.toJson())}));
                    }

                    mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder()
                                    .target(new LatLng(((Point) selectedCarmenFeature.geometry()).latitude(),
                                            ((Point) selectedCarmenFeature.geometry()).longitude()))
                                    .zoom(14)
                                    .build()), 4000);
                    placeName = selectedCarmenFeature.placeName();

                    LatLng loc = new LatLng(((Point) selectedCarmenFeature.geometry()).latitude(),
                            ((Point) selectedCarmenFeature.geometry()).longitude());

                    lat = Double.toString(loc.getLatitude());
                    lng = Double.toString(loc.getLongitude());

//                    IconFactory iconFactory = IconFactory.getInstance(MainActivity.this);
//                    Icon icon = iconFactory.fromResource(R.drawable.blue_marker);

                    mapboxMap.addMarker(new MarkerOptions()
                            .position(new LatLng(loc.getLatitude(), loc.getLongitude()))
                            .title(placeName));

                    addFragment(new AddLocation(), false, "HELLO");
                }
            }
        }
    }

    public void addFragment(Fragment fragment, boolean addToBackStack, String tag) {

        currentFrame = "search2";
        collapseState();

        Handler h2 = new Handler();
        h2.postDelayed(() -> {
            FragmentManager manager2 = getSupportFragmentManager();
            FragmentTransaction ft2 = manager2.beginTransaction();
            ft2.addToBackStack(null);
            ft2.replace(R.id.fragment_container_bottom, fragment, tag);
            ft2.commitAllowingStateLoss();
        }, 250);

    }

    @Override
    public void onLocationChanged(Location location) {
        Double lat = location.getLatitude();
        Double lng = location.getLongitude();

        Log.d("Location info: Lat", lat.toString());
        Log.d("Location info: Lng", lng.toString());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("LOCATION STATUS", String.valueOf(status));
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("LOCATION", provider);
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("LOCATION", provider);
    }


    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, R.string.user_location_permission_explanation, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            mapboxMap.getStyle(new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    enableLocationComponent(style);
                }
            });
        } else {
            Toast.makeText(this, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show();
            finish();
        }
    }


    @SuppressLint("MissingPermission")
    private void initLocationEngine() {
        locationEngine = LocationEngineProvider.getBestLocationEngine(this);

        LocationEngineRequest request = new LocationEngineRequest.Builder(2000)
                .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                .setMaxWaitTime(2000)
                .setFastestInterval(100)
                .build();

        locationEngine.requestLocationUpdates(request, callback, getMainLooper());
        locationEngine.getLastLocation(callback);
    }

    @Override
    public void onFling() {
        collapseState();
    }

    @Override
    public void onMoveBegin(@NonNull MoveGestureDetector detector) {
        collapseState();
    }

    @Override
    public void onMove(@NonNull MoveGestureDetector detector) {

    }

    @Override
    public void onMoveEnd(@NonNull MoveGestureDetector detector) {

    }

    @Override
    public void onCameraMove() {

    }


    private static class LocationChangeListeningActivityLocationCallback
            implements LocationEngineCallback<LocationEngineResult> {

        private final WeakReference<MainActivity> activityWeakReference;

        LocationChangeListeningActivityLocationCallback(MainActivity activity) {
            this.activityWeakReference = new WeakReference<>(activity);
        }

        /**
         * The LocationEngineCallback interface's method which fires when the device's location has changed.
         *
         * @param result the LocationEngineResult object which has the last known location within it.
         */
        @Override
        public void onSuccess(LocationEngineResult result) {
            MainActivity activity = activityWeakReference.get();

            if (activity != null) {
                Location location = result.getLastLocation();

                if (location == null) {
                    return;
                }

// Create a Toast which displays the new location's coordinates
                Log.d("getplace", String.valueOf(result.getLastLocation().getLatitude()) + " " + String.valueOf(result.getLastLocation().getLongitude()));


// Pass the new location to the Maps SDK's LocationComponent
                if (activity.mapboxMap != null && result.getLastLocation() != null) {
                    activity.mapboxMap.getLocationComponent().forceLocationUpdate(result.getLastLocation());
                    activity.forfrag=result.getLastLocation();
                    // Log.d("getlegs",String.valueOf(result.getLastLocation().getBearing()));
                    try {
                        if (!isvis) {

                            mapView.setVisibility(mapView.INVISIBLE);

                            Handler h = new Handler();

                            h.postDelayed(() -> {
                                mapView.setVisibility(mapView.VISIBLE);
                                isvis = true;
                            }, 750);

                        }
                    } catch (Exception e) {
                        Log.d("getplace", e.getMessage());
                    }
                }
            }
        }

        /**
         * The LocationEngineCallback interface's method which fires when the device's location can't be captured
         *
         * @param exception the exception message
         */
        @Override
        public void onFailure(@NonNull Exception exception) {
            Log.d("LocationChangeActivity", exception.getLocalizedMessage());
            MainActivity activity = activityWeakReference.get();
            if (activity != null) {
                Toast.makeText(activity, exception.getLocalizedMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

}