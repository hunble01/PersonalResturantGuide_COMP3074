package ca.gbc.personalrestaurantguide;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ca.gbc.personalrestaurantguide.adapter.TagsAdapter;
import ca.gbc.personalrestaurantguide.database.DatabaseHelper;
import ca.gbc.personalrestaurantguide.model.OnClick;
import ca.gbc.personalrestaurantguide.model.RestaurantModel;

public class AddRestaurantActivity extends AppCompatActivity {
    ImageView ivHome, ivAboutUs;
    TextView tvLabel;
    ImageView ivBack;
    TextInputEditText etRestaurantName, etRestaurantAddress, etRestaurantPhone,
            etRestaurantDescription, etTag;
    RecyclerView rvTags;
    RatingBar ratingBar;
    AppCompatButton btnAddTag, btnSave;
    TagsAdapter tagsAdapter;
    ArrayList<String> listOfTags = new ArrayList<>();
    String restaurantName, restaurantAddress, restaurantPhone, restaurantDescription;
    float restaurantRating;
    RestaurantModel previousModel;
    DatabaseHelper databaseHelper;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_restaurant);

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyCxMjBVZv-SaZn9f-8KljiSb8dHZARrKX4");
        }

        ivHome = findViewById(R.id.ivHome);
        ivAboutUs = findViewById(R.id.ivAboutUs);
        tvLabel = findViewById(R.id.tvLabel);
        ivBack = findViewById(R.id.ivBack);
        etRestaurantName = findViewById(R.id.etRestaurantName);
        etRestaurantAddress = findViewById(R.id.etRestaurantAddress);
        etRestaurantPhone = findViewById(R.id.etRestaurantPhone);
        ratingBar = findViewById(R.id.ratingBar);
        etRestaurantDescription = findViewById(R.id.etRestaurantDescription);
        etTag = findViewById(R.id.etTag);
        rvTags = findViewById(R.id.rvTags);
        btnAddTag = findViewById(R.id.btnAddTag);
        btnSave = findViewById(R.id.btnSave);
        databaseHelper = new DatabaseHelper(this);

        etRestaurantAddress.setOnClickListener(view -> openPlacePicker());

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ivHome.setOnClickListener(view -> finish());
        ivAboutUs.setOnClickListener(view -> startActivity(new Intent(AddRestaurantActivity.this, AboutUsActivity.class)));


        if (getIntent().getExtras() != null) {
            previousModel = (RestaurantModel) getIntent().getSerializableExtra("restaurant");
        }

        if (previousModel != null) {
            tvLabel.setText("Update Restaurant");
            btnSave.setText("Update");
            etRestaurantName.setText(previousModel.getRestaurantName());
            etRestaurantAddress.setText(previousModel.getRestaurantAddress());
            etRestaurantPhone.setText(previousModel.getRestaurantPhone());
            ratingBar.setRating(Float.parseFloat(previousModel.getRestaurantRating()));
            etRestaurantDescription.setText(previousModel.getRestaurantDescription());
            listOfTags.clear();
            listOfTags.addAll(previousModel.getListOfTags());
        }

        rvTags.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        tagsAdapter = new TagsAdapter(listOfTags, this, new OnClick() {
            @Override
            public void clicked(String from, int pos) {
                listOfTags.remove(pos);
                tagsAdapter.setList(listOfTags);
            }
        });
        rvTags.setAdapter(tagsAdapter);

        btnAddTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tag = etTag.getText().toString();
                if (tag.isEmpty()) {
                    Toast.makeText(AddRestaurantActivity.this, "Please enter tag first", Toast.LENGTH_SHORT).show();
                } else {
                    listOfTags.add(tag);
                    tagsAdapter.setList(listOfTags);
                    etTag.setText("");
                }
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                restaurantName = etRestaurantName.getText().toString();
                restaurantAddress = etRestaurantAddress.getText().toString();
                restaurantPhone = etRestaurantPhone.getText().toString();
                restaurantRating = ratingBar.getRating();
                restaurantDescription = etRestaurantDescription.getText().toString();

                if (restaurantName.isEmpty() || restaurantAddress.isEmpty() || restaurantPhone.isEmpty()
                        || restaurantRating == 0 || restaurantDescription.isEmpty()) {
                    Toast.makeText(AddRestaurantActivity.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (restaurantPhone.length() < 10 ) {
                    Toast.makeText(AddRestaurantActivity.this, "Please enter valid phone number", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (listOfTags.isEmpty()) {
                    Toast.makeText(AddRestaurantActivity.this, "Please add at least one tag", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (previousModel != null) {
                    previousModel.setRestaurantName(restaurantName);
                    previousModel.setRestaurantAddress(restaurantAddress);
                    previousModel.setRestaurantPhone(restaurantAddress);
                    previousModel.setRestaurantRating(String.valueOf(restaurantRating));
                    previousModel.setRestaurantDescription(restaurantDescription);
                    previousModel.setListOfTags(listOfTags);
                    databaseHelper.updateRestaurant(previousModel);
                    Toast.makeText(AddRestaurantActivity.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                } else {
                    RestaurantModel restaurantModel = new RestaurantModel(restaurantName, restaurantAddress, restaurantPhone, String.valueOf(restaurantRating), restaurantDescription, listOfTags);
                    databaseHelper.addRestaurant(restaurantModel);
                    Toast.makeText(AddRestaurantActivity.this, "Restaurant added Successfully", Toast.LENGTH_SHORT).show();
                }

                finish();
            }
        });

    }

    private void openPlacePicker() {
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS);

        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                .build(this);

        startActivityForResult(intent, 1001);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1001) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                etRestaurantAddress.setText(place.getAddress());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Toast.makeText(this, "Error: " + status.getStatusMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }


}