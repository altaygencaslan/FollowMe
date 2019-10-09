package com.example.altaygencaslan.followme;

import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends FragmentActivity {

    private GoogleMap googleHarita;
    private MarkerOptions myMarkerOptions;
    private Marker myMarker;
    private LocationManager myLocationManager;
    private Location myLocation;
    private LatLng myLatLng;

    LocationListener listener;
    NavigationView navigationView;
    DrawerLayout drawerLayout;

    private boolean isSetMyLocation = true;
    private boolean isMarkerAdded = false;

    private int counterMap = 0;
    private int counterListener = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        InitializeComponents();
        SetNavigationView();
    }

    private void SetNavigationView() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.drawer_menu_Home:
                        isSetMyLocation = !isSetMyLocation;
                        googleHarita.setMyLocationEnabled(isSetMyLocation);
                        break;

                    case R.id.drawer_menu_MapHybrid:
                        googleHarita.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                        break;

                    case R.id.drawer_menu_MapNormal:
                        googleHarita.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        break;

                    case R.id.drawer_menu_MapSatallite:
                        googleHarita.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                        break;

                    case R.id.drawer_menu_MapTerrain:
                        googleHarita.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                        break;
                }

                drawerLayout.closeDrawers();
                return false;
            }
        });
    }

    private void InitializeComponents() {
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        drawerLayout = (DrawerLayout) findViewById(R.id.activity_main);
        if (googleHarita == null) {
            listener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    counterListener++;
                    ChangeMyLocation(location);
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {
                    Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                }
            };

            myLocationManager = (LocationManager) getSystemService(getApplicationContext().LOCATION_SERVICE);
            //noinspection MissingPermission
            myLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, listener);


            googleHarita = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fgMaps)).getMap();
            if (googleHarita != null) {
                googleHarita.setMyLocationEnabled(isSetMyLocation);
                googleHarita.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                googleHarita.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                    @Override
                    public void onMyLocationChange(Location location) {
                        counterMap++;
                        ChangeMyLocation(location);
                    }
                });

                SetFirstLocation();
            }
        }
    }

    private void SetFirstLocation() {
        myMarkerOptions = new MarkerOptions();
        myMarkerOptions.title("Hey its me!");
        myMarkerOptions.snippet("What is this?");
        myMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.flag));
        //BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)

        //noinspection MissingPermission
        myLocation = myLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (myLocation == null)
            //noinspection MissingPermission
            myLocation = myLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        if (myLocation != null) {
            myLatLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
            googleHarita.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 15));

            myMarkerOptions.position(myLatLng);
            myMarker = googleHarita.addMarker(myMarkerOptions);
            isMarkerAdded = true;
        }
    }

    private void ChangeMyLocation(Location location) {
        if ((googleHarita != null) && (location != null)) {
            myLocation = location;
            myLatLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
            googleHarita.animateCamera(CameraUpdateFactory.newLatLng(myLatLng));

            myMarker.setPosition(myLatLng);
            if (!isMarkerAdded) {
                myMarker = googleHarita.addMarker(myMarkerOptions);
                isMarkerAdded = true;
            }
        } else
            InitializeComponents();

        Toast.makeText(this, "Maps Counter: " + counterMap + "\nListener Counter: " + counterListener, Toast.LENGTH_SHORT).show();
    }
}
