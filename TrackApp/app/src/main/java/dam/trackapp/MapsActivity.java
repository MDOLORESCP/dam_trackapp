package dam.trackapp;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    public final static String PARAM_LAT = "LAT";
    public final static String PARAM_LON = "LON";
    public final static String PARAM_NOMBRE = "NOMBRE";

    private final int PERMISOS = 1;

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LatLng latLng;
    private String nombreUbicacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Intent intent = getIntent();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



        latLng = new LatLng(intent.getDoubleExtra(PARAM_LAT, 0), intent.getDoubleExtra(PARAM_LON, 0));


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        findViewById(R.id.map_seleccionar_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra(PARAM_LAT, latLng.latitude);
                returnIntent.putExtra(PARAM_LON, latLng.longitude);
                returnIntent.putExtra(PARAM_NOMBRE, nombreUbicacion);
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
        });

    }

    private void cargarMapa() {
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {

                // Creating a marker
                MarkerOptions markerOptions = new MarkerOptions();

                // Setting the position for the marker
                markerOptions.position(latLng);

                MapsActivity.this.latLng = latLng;

                // Setting the title for the marker.
                // This will be displayed on taping the marker
                try {

                    Geocoder geo = new Geocoder(getApplicationContext(), Locale.getDefault());
                    List<Address> addresses = geo.getFromLocation(latLng.latitude, latLng.longitude, 1);

                    markerOptions.title(addresses.get(0).getAddressLine(0));

                    nombreUbicacion = addresses.get(0).getAddressLine(0);
                } catch (Exception e) {
                    markerOptions.title(latLng.latitude + " : " + latLng.longitude);
                }

                // Clears the previously touched position
                mMap.clear();

                // Animating to the touched position
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                // Placing a marker on the touched position
                mMap.addMarker(markerOptions);
            }
        });

        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                mMap.setOnCameraIdleListener(null);

                ActivityCompat.requestPermissions(MapsActivity.this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION }, PERMISOS);


            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISOS: {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    gps();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.error_permiso_GPS), Toast.LENGTH_LONG).show();

                    finish();

                    return;
                }

                return;
            }
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
        cargarMapa();
    }

    private void gps() {
        if (latLng != null && latLng.latitude != 0 && latLng.longitude != 0) {
            // crear marcador
            try {
                Geocoder geo = new Geocoder(getApplicationContext(), Locale.getDefault());
                List<Address> addresses = geo.getFromLocation(latLng.latitude, latLng.longitude, 1);

                mMap.addMarker(new MarkerOptions().position(latLng).title(addresses.get(0).getAddressLine(0)));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
            } catch (Exception ex) {

            }
        } else {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(MapsActivity.this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());

                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 17));
                            }
                        }
                    });
        }
    }
}
