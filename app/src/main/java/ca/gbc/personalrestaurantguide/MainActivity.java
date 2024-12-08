package ca.gbc.personalrestaurantguide;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ca.gbc.personalrestaurantguide.adapter.RestaurantsAdapter;
import ca.gbc.personalrestaurantguide.database.DatabaseHelper;
import ca.gbc.personalrestaurantguide.model.OnClick;
import ca.gbc.personalrestaurantguide.model.RestaurantModel;

public class MainActivity extends AppCompatActivity {
    // UI components
    ImageView ivAboutUs;
    EditText etSearchQuery;
    TextView tvNoDataFound;
    RecyclerView restaurantRecyclerView;
    FloatingActionButton fabAddRestaurant;

    // Adapter and Data
    RestaurantsAdapter restaurantsAdapter;
    ArrayList<RestaurantModel> restaurantsList = new ArrayList<>();

    // Database Helper
    DatabaseHelper databaseHelper;

    // Constants for permissions
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI components
        etSearchQuery = findViewById(R.id.etSearchQuery);
        tvNoDataFound = findViewById(R.id.tvNoDataFound);
        restaurantRecyclerView = findViewById(R.id.restaurantRecyclerView);
        fabAddRestaurant = findViewById(R.id.fabAddRestaurant);
        ivAboutUs = findViewById(R.id.ivAboutUs);
        databaseHelper = new DatabaseHelper(this);

        // Check and request location permissions
        if (!hasLocationPermission()) {
            requestLocationPermission();
        }

        // Set up the RecyclerView with the adapter
        setRestaurantsAdapter();

        // Add listener for search query input
        etSearchQuery.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action required before text changes
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Filter the restaurant list based on the search query
                filterRestaurants(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No action required after text changes
            }
        });

        // Set listener for adding a new restaurant
        fabAddRestaurant.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AddRestaurantActivity.class);
            startActivity(intent);
        });

        // Set listener for "About Us" icon
        ivAboutUs.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, AboutUsActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload data from the database when returning to the activity
        getDataFromDatabase();
    }

    private void getDataFromDatabase() {
        // Clear the existing list and fetch updated data from the database
        restaurantsList.clear();
        restaurantsList.addAll(databaseHelper.getAllRestaurants());

        // Update UI based on whether the list is empty
        if (restaurantsList.isEmpty()) {
            tvNoDataFound.setVisibility(View.VISIBLE);
            restaurantRecyclerView.setVisibility(View.GONE);
            etSearchQuery.setVisibility(View.GONE);
        } else {
            tvNoDataFound.setVisibility(View.GONE);
            restaurantRecyclerView.setVisibility(View.VISIBLE);
            etSearchQuery.setVisibility(View.VISIBLE);
            restaurantsAdapter.setList(restaurantsList);
        }
    }

    private void setRestaurantsAdapter() {
        // Initialize RecyclerView with a linear layout manager
        restaurantRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Set up the adapter and click handlers for different actions
        restaurantsAdapter = new RestaurantsAdapter(restaurantsList, this, new OnClick() {
            @Override
            public void clicked(String from, int pos) {
                RestaurantModel restaurantModel = restaurantsList.get(pos);

                // Handle different click actions
                switch (from) {
                    case "share":
                        shareDialog(restaurantModel);
                        break;
                    case "edit":
                        Intent editIntent = new Intent(MainActivity.this, AddRestaurantActivity.class);
                        editIntent.putExtra("restaurant", restaurantModel);
                        startActivity(editIntent);
                        break;
                    case "map":
                        Intent mapIntent = new Intent(MainActivity.this, MapActivity.class);
                        mapIntent.putExtra("address", restaurantModel.getRestaurantAddress());
                        startActivity(mapIntent);
                        break;
                    case "delete":
                        databaseHelper.deleteRestaurant(restaurantModel.getId());
                        Toast.makeText(MainActivity.this, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                        getDataFromDatabase();
                        break;
                }
            }
        });
        restaurantRecyclerView.setAdapter(restaurantsAdapter);
    }

    private void shareDialog(RestaurantModel restaurantModel) {
        // Create and configure the share dialog
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.item_share_dialog);

        // Adjust the dialog layout
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(window.getAttributes());
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(layoutParams);
            window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        // Initialize UI components in the dialog
        ImageView ivGmail = dialog.findViewById(R.id.ivGmail);
        ImageView ivFacebook = dialog.findViewById(R.id.ivFacebook);
        ImageView ivTwitter = dialog.findViewById(R.id.ivTwitter);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);

        // Prepare share content
        List<String> tags = restaurantModel.getListOfTags();
        String shareContent = "Restaurant Details:\n\n" +
                "Name: " + restaurantModel.getRestaurantName() + "\n" +
                "Address: " + restaurantModel.getRestaurantAddress() + "\n" +
                "Phone: " + restaurantModel.getRestaurantPhone() + "\n" +
                "Rating: " + restaurantModel.getRestaurantRating() + "\n" +
                "Description: " + restaurantModel.getRestaurantDescription() + "\n" +
                "Tags: " + String.join(", ", tags);

        // Set listeners for sharing actions
        btnCancel.setOnClickListener(v -> dialog.dismiss());

        ivGmail.setOnClickListener(view -> {
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("message/rfc822");
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Restaurant Details");
            emailIntent.putExtra(Intent.EXTRA_TEXT, shareContent);
            emailIntent.setPackage("com.google.android.gm");
            try {
                startActivity(emailIntent);
                dialog.dismiss();
            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "Gmail app is not installed", Toast.LENGTH_SHORT).show();
            }
        });

        // Similar listeners for Facebook and Twitter...
        dialog.show();
    }



private void filterRestaurants(String query) {
        if (query.isEmpty()) {
            restaurantsAdapter.setList(databaseHelper.getAllRestaurants());
        } else {
            ArrayList<RestaurantModel> filteredList = new ArrayList<>();
            for (RestaurantModel restaurant : databaseHelper.getAllRestaurants()) {
                if (restaurant.getRestaurantName().toLowerCase().contains(query.toLowerCase()) ||
                        (restaurant.getListOfTags() != null &&
                                restaurant.getListOfTags().toString().toLowerCase().contains(query.toLowerCase()))) {
                    filteredList.add(restaurant);
                }
            }
            restaurantsAdapter.setList(filteredList);
        }
    }

    private boolean hasLocationPermission() {
        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE);
    }

}
