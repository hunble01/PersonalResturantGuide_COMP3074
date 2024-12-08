package ca.gbc.personalrestaurantguide;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.common.collect.Maps;

import java.util.Locale;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    ImageView ivBack, ivHome, ivAboutUs;
    private MapView mapView;
    private GoogleMap googleMap;
    private String restaurantAddress;
    private LatLng restaurantLatLng;
    private LatLng currentLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        if (getIntent().getExtras() != null) {
            restaurantAddress = getIntent().getStringExtra("address");
        }

        ivBack = findViewById(R.id.ivBack);
        ivHome = findViewById(R.id.ivHome);
        ivAboutUs = findViewById(R.id.ivAboutUs);
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        ivBack.setOnClickListener(v -> finish());
        ivHome.setOnClickListener(v -> {
            startActivity(new Intent(MapActivity.this, MainActivity.class));
            finishAffinity();
        });
        ivAboutUs.setOnClickListener(v -> startActivity(new Intent(MapActivity.this, AboutUsActivity.class)));
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;

        // Geocode the restaurant address
        GeocodingUtil.getLatLngFromAddress(this, restaurantAddress)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        restaurantLatLng = task.getResult();
                        if (restaurantLatLng != null) {
                            googleMap.addMarker(new MarkerOptions()
                                    .position(restaurantLatLng)
                                    .title("Restaurant Location"));
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(restaurantLatLng, 15));

                            // Enable location button
                            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                return;
                            }
                            googleMap.setMyLocationEnabled(true);
                            googleMap.setOnMyLocationChangeListener(location -> {
                                currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                            });

                            googleMap.setOnMarkerClickListener(marker -> {
                                if (currentLatLng != null) {
                                    openDirectionsInGoogleMaps(currentLatLng, restaurantLatLng);
                                } else {
                                    Toast.makeText(MapActivity.this, "Current location not available", Toast.LENGTH_SHORT).show();
                                }
                                return true;
                            });
                        } else {
                            Toast.makeText(MapActivity.this, "Unable to locate restaurant", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MapActivity.this, "Failed to fetch coordinates", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void openDirectionsInGoogleMaps(LatLng start, LatLng destination) {
        String uri = String.format(Locale.ENGLISH,
                "https://www.google.com/maps/dir/?api=1&origin=%f,%f&destination=%f,%f&travelmode=driving",
                start.latitude, start.longitude, destination.latitude, destination.longitude);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setPackage("com.google.android.apps.maps");
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

}
