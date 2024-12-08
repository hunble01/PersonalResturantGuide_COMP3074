package ca.gbc.personalrestaurantguide;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GeocodingUtil {

    public static Task<LatLng> getLatLngFromAddress(Context context, String address) {
        TaskCompletionSource<LatLng> taskCompletionSource = new TaskCompletionSource<>();
        new Thread(() -> {
            try {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocationName(address, 1);
                if (addresses != null && !addresses.isEmpty()) {
                    Address location = addresses.get(0);
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    taskCompletionSource.setResult(latLng);
                } else {
                    taskCompletionSource.setResult(null);
                }
            } catch (IOException e) {
                taskCompletionSource.setException(e);
            }
        }).start();
        return taskCompletionSource.getTask();
    }
}
