package com.rafaeltimbo.appetite;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.rafaeltimbo.appetite.broadcast.BusinessListReadyReceiver;
import com.rafaeltimbo.appetite.broadcast.FiltersUpdateReceiver;
import com.rafaeltimbo.appetite.broadcast.RangeUpdateReceiver;
import com.rafaeltimbo.appetite.model.Business;
import com.rafaeltimbo.appetite.service.BusinessServiceClient;
import com.rafaeltimbo.appetite.service.CustomizedPinService;
import com.rafaeltimbo.appetite.utils.Constants;

import java.util.ArrayList;

import static com.rafaeltimbo.appetite.utils.Constants.BUSINESS_DISTANCE;
import static com.rafaeltimbo.appetite.utils.Constants.BUSINESS_ID;
import static com.rafaeltimbo.appetite.utils.Helper.OpenFilterDialog;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener, GoogleMap.OnInfoWindowClickListener {

    private final String TAG = "Appetite.Maps";

    private static final int DIALOG_REQUEST = 9001;
    private GoogleMap mMap;
    private BusinessListReadyReceiver businessListReadyReceiver;
    private RangeUpdateReceiver rangeUpdateReceiver;
    private FiltersUpdateReceiver filtersUpdateReceiver;

    private BusinessServiceClient businessServiceClient;
    //private ArrayList<Business> businessList;
    private LocationManager locationManager;

    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        businessServiceClient = new BusinessServiceClient(this);

        if (mapServicesAvailable() ) {
            initMap();
        }
    }

    public void handleLocationPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                Constants.MY_PERMISSIONS_ACCESS_FINE_LOCATION);
            ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                Constants.MY_PERMISSIONS_ACCESS_COARSE_LOCATION);

            handleLocationPermissions();
        } else {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

            mFusedLocationClient.getLastLocation()
                .addOnSuccessListener( MapsActivity.this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            onLocationChanged(location);
                        }
                    }
            });
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMinZoomPreference(14.0f);

        broadcastReceivers();
        handleLocationPermissions();
        //onLocationChanged(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.business_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent next;

        switch (id){
            case R.id.id_range:
                OpenRangeDialog();
                break;
            case R.id.id_list:
                next = new Intent( MapsActivity.this, BusinessListActivity.class);
                startActivity(next);
                break;
            case R.id.id_map:
                break;
            case R.id.action_filter:
                OpenFilterDialog(this);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(businessListReadyReceiver);
        unregisterReceiver(filtersUpdateReceiver);
        unregisterReceiver(rangeUpdateReceiver);
    }

    // PRIVATE METHODS
    public boolean mapServicesAvailable() {
        int result = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (result == ConnectionResult.SUCCESS) {
            return true;
        } else if (GooglePlayServicesUtil.isUserRecoverableError(result)) {
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(result, this, DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, getString(R.string.error_connect_to_services), Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void initMap() {
        if (mMap == null) {
            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }
        //return (mMap != null);
    }

    public void OpenRangeDialog() {
        BusinessListRangeDialog rangeDialog = new BusinessListRangeDialog();
        rangeDialog.show(getSupportFragmentManager(), "Range Dialog");
    }

    private void drawRestaurants(GoogleMap googleMap, ArrayList<Business> businessList) {
        for (Business business: businessList) {
            drawRestaurant(googleMap, business);
        }
    }

    private void drawRestaurant(GoogleMap googleMap, Business business) {
        CustomizedPinService customizedPinService = new CustomizedPinService(business);
        LatLng latLng = new LatLng(business.getLatitude(), business.getLongitude());
        BitmapDescriptor icon = customizedPinService.getCustomMapPin(); //BitmapDescriptorFactory.fromResource(R.drawable.pin_fork_spoon_cross);
        MarkerOptions markerOptions = new MarkerOptions()
            .position( latLng )
            .title(business.getName())
            .snippet(business.getName())
            .icon(icon)
            .anchor(0.5f, 0.5f);

        googleMap
            .addMarker(markerOptions)
            .setTag(business.getId() + ";" + business.getFormattedDistance());

        googleMap.setOnInfoWindowClickListener(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location == null) {
            Log.v(TAG, "onLocationChanged null");
            return;
        }
        //Log.v(TAG, "onLocationChanged " + location.getLatitude() + " " + location.getLongitude());
        //Toast.makeText(this,
        //location.getLatitude()+",\n"+location.getLongitude(), Toast.LENGTH_SHORT).show();
        double currentLat = location.getLatitude();
        double currentLong = location.getLongitude();
        if(mMap != null){
            //Toast.makeText(this,  location.getLatitude()+",\n"+location.getLongitude(),
            //Toast.LENGTH_SHORT).show();

            // Add an initial marker and move camera to that point
            LatLng currentLatLong = new LatLng(currentLat, currentLong);
            BitmapDescriptor bluePin = CustomizedPinService.herePin();
            mMap.addMarker(new MarkerOptions().position(currentLatLong).icon(bluePin)); //.title("Centennial College"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLatLong));
            //mMap.resetMinMaxZoomPreference();
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        Log.v(TAG,"onStatusChanged " + s + i);
    }

    @Override
    public void onProviderEnabled(String s) {
        handleLocationPermissions();
    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        String[] tags = marker.getTag().toString().split(";");

        Intent detailsIntent = new Intent(this,BusinessDetailsActivity.class);
        detailsIntent.putExtra(BUSINESS_ID, tags[0]);
        detailsIntent.putExtra(BUSINESS_DISTANCE, tags[1]);

        startActivity(detailsIntent);
    }

    private void broadcastReceivers() {
        businessListReadyReceiver = new BusinessListReadyReceiver(new BusinessListReadyReceiver.OnReceive() {
            @Override
            public void onReceive(ArrayList<Business> businessList) {
                drawRestaurants(mMap, businessList);
            }
        });
        registerReceiver(businessListReadyReceiver, businessListReadyReceiver.getIntentFilter());
        rangeUpdateReceiver = new RangeUpdateReceiver(new RangeUpdateReceiver.OnReceive() {

            @Override
            public void onReceive() {
                mMap.clear();
                businessServiceClient.refreshBusinessList();
            }
        });
        registerRangeUpdateBroadcastReceiver();
        filtersUpdateReceiver = new FiltersUpdateReceiver(new FiltersUpdateReceiver.OnReceive() {
            @Override
            public void onReceive() {
                mMap.clear();
                businessServiceClient.refreshBusinessList();
            }
        });
        registerFilterUpdateBroadcastReceiver();
        businessServiceClient.refreshBusinessList();
    }

    private void registerRangeUpdateBroadcastReceiver() {
        registerReceiver(rangeUpdateReceiver, rangeUpdateReceiver.getIntentFilter());
    }

    private void registerFilterUpdateBroadcastReceiver() {
        registerReceiver(filtersUpdateReceiver, filtersUpdateReceiver.getIntentFilter());
    }
}