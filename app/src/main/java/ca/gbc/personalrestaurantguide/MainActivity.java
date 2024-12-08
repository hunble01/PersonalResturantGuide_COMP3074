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
    ImageView ivAboutUs;
    EditText etSearchQuery;
    TextView tvNoDataFound;
    RecyclerView restaurantRecyclerView;
    FloatingActionButton fabAddRestaurant;
    RestaurantsAdapter restaurantsAdapter;
    ArrayList<RestaurantModel> restaurantsList = new ArrayList<>();
    DatabaseHelper databaseHelper;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etSearchQuery = findViewById(R.id.etSearchQuery);
        tvNoDataFound = findViewById(R.id.tvNoDataFound);
        restaurantRecyclerView = findViewById(R.id.restaurantRecyclerView);
        fabAddRestaurant = findViewById(R.id.fabAddRestaurant);
        ivAboutUs = findViewById(R.id.ivAboutUs);
        databaseHelper = new DatabaseHelper(this);

        if (!hasLocationPermission()) {
            requestLocationPermission();
        }

        setRestaurantsAdapter();

        // Add TextWatcher for search functionality
        etSearchQuery.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterRestaurants(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        fabAddRestaurant.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AddRestaurantActivity.class);
            startActivity(intent);
        });

        ivAboutUs.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, AboutUsActivity.class)));

    }

    @Override
    protected void onResume() {
        super.onResume();

        getDataFromDatabase();

    }

    private void getDataFromDatabase() {
        restaurantsList.clear();
        restaurantsList.addAll(databaseHelper.getAllRestaurants());
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
        restaurantRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        restaurantsAdapter = new RestaurantsAdapter(restaurantsList, this, new OnClick() {
            @Override
            public void clicked(String from, int pos) {
                RestaurantModel restaurantModel = restaurantsList.get(pos);
                if (Objects.equals(from, "share")) {
                    shareDialog(restaurantModel);
                } else if (Objects.equals(from, "edit")) {
                    Intent intent = new Intent(MainActivity.this, AddRestaurantActivity.class);
                    intent.putExtra("restaurant", restaurantModel);
                    startActivity(intent);
                } else if (Objects.equals(from, "map")) {
                    Intent intent = new Intent(MainActivity.this, MapActivity.class);
                    intent.putExtra("address", restaurantModel.getRestaurantAddress());
                    startActivity(intent);
                } else if (Objects.equals(from, "delete")) {
                    databaseHelper.deleteRestaurant(restaurantModel.getId());
                    Toast.makeText(MainActivity.this, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                    getDataFromDatabase();
                }
            }
        });
        restaurantRecyclerView.setAdapter(restaurantsAdapter);
    }

    private void shareDialog(RestaurantModel restaurantModel) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.item_share_dialog);

        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(window.getAttributes());
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(layoutParams);
            window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        ImageView ivGmail = dialog.findViewById(R.id.ivGmail);
        ImageView ivFacebook = dialog.findViewById(R.id.ivFacebook);
        ImageView ivTwitter = dialog.findViewById(R.id.ivTwitter);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);

        List<String> tags = restaurantModel.getListOfTags();

        String shareContent = "Restaurant Details:\n\n" +
                "Name: " + restaurantModel.getRestaurantName() + "\n" +
                "Address: " + restaurantModel.getRestaurantAddress() + "\n" +
                "Phone: " + restaurantModel.getRestaurantPhone() + "\n" +
                "Rating: " + restaurantModel.getRestaurantRating() + "\n" +
                "Description: " + restaurantModel.getRestaurantDescription() + "\n" +
                "Tags: " + String.join(", ", tags);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        ivGmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
            }
        });

        ivFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent facebookIntent = new Intent(Intent.ACTION_SEND);
                facebookIntent.setType("text/plain");
                facebookIntent.putExtra(Intent.EXTRA_TEXT, shareContent);
                facebookIntent.setPackage("com.facebook.katana");
                try {
                    startActivity(facebookIntent);
                    dialog.dismiss();
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Facebook app is not installed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ivTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent twitterIntent = new Intent(Intent.ACTION_SEND);
                twitterIntent.setType("text/plain");
                twitterIntent.putExtra(Intent.EXTRA_TEXT, shareContent);
                twitterIntent.setPackage("com.twitter.android");
                try {
                    startActivity(twitterIntent);
                    dialog.dismiss();
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Twitter app is not installed", Toast.LENGTH_SHORT).show();
                }
            }
        });

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