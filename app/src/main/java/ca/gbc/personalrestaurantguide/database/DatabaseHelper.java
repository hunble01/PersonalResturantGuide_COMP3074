package ca.gbc.personalrestaurantguide.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import ca.gbc.personalrestaurantguide.model.RestaurantModel;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "personal_restaurant_guide_app.db";

    // Columns for the Restaurant table
    private static final String TABLE_RESTAURANTS = "restaurants_table";
    private static final String KEY_ID = "restaurant_id";
    private static final String KEY_NAME = "restaurant_name";
    private static final String KEY_ADDRESS = "restaurant_address";
    private static final String KEY_PHONE = "restaurant_phone";
    private static final String KEY_RATING = "restaurant_rating";
    private static final String KEY_DESCRIPTION = "restaurant_description";
    private static final String KEY_TAGS = "restaurant_tags";

    private final Gson gson = new Gson();

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the Restaurants table
        String CREATE_RESTAURANTS_TABLE = "CREATE TABLE " + TABLE_RESTAURANTS + " (" +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_NAME + " TEXT NOT NULL, " +
                KEY_ADDRESS + " TEXT NOT NULL, " +
                KEY_PHONE + " TEXT NOT NULL, " +
                KEY_RATING + " TEXT, " +
                KEY_DESCRIPTION + " TEXT," +
                KEY_TAGS + " TEXT" +
                ");";
        db.execSQL(CREATE_RESTAURANTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop existing table if it exists
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESTAURANTS);
        // Recreate the table
        onCreate(db);
    }

    // Add a restaurant
    public void addRestaurant(RestaurantModel restaurant) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, restaurant.getRestaurantName());
        values.put(KEY_ADDRESS, restaurant.getRestaurantAddress());
        values.put(KEY_PHONE, restaurant.getRestaurantPhone());
        values.put(KEY_RATING, restaurant.getRestaurantRating());
        values.put(KEY_DESCRIPTION, restaurant.getRestaurantDescription());
        values.put(KEY_TAGS, gson.toJson(restaurant.getListOfTags()));

        db.insert(TABLE_RESTAURANTS, null, values);
        db.close();
    }

    // Get all restaurants
    public List<RestaurantModel> getAllRestaurants() {
        List<RestaurantModel> restaurantList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_RESTAURANTS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                RestaurantModel restaurant = new RestaurantModel();
                restaurant.setId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)));
                restaurant.setRestaurantName(cursor.getString(cursor.getColumnIndexOrThrow(KEY_NAME)));
                restaurant.setRestaurantAddress(cursor.getString(cursor.getColumnIndexOrThrow(KEY_ADDRESS)));
                restaurant.setRestaurantPhone(cursor.getString(cursor.getColumnIndexOrThrow(KEY_PHONE)));
                restaurant.setRestaurantRating(cursor.getString(cursor.getColumnIndexOrThrow(KEY_RATING)));
                restaurant.setRestaurantDescription(cursor.getString(cursor.getColumnIndexOrThrow(KEY_DESCRIPTION)));

                // Deserialize tags JSON to a list
                String tagsJson = cursor.getString(cursor.getColumnIndexOrThrow(KEY_TAGS));
                Type type = new TypeToken<ArrayList<String>>() {}.getType();
                ArrayList<String> tags = gson.fromJson(tagsJson, type);
                restaurant.setListOfTags(tags);

                restaurantList.add(restaurant);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return restaurantList;
    }

    // Update a restaurant
    public void updateRestaurant(RestaurantModel restaurant) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, restaurant.getRestaurantName());
        values.put(KEY_ADDRESS, restaurant.getRestaurantAddress());
        values.put(KEY_PHONE, restaurant.getRestaurantPhone());
        values.put(KEY_RATING, restaurant.getRestaurantRating());
        values.put(KEY_DESCRIPTION, restaurant.getRestaurantDescription());
        values.put(KEY_TAGS, gson.toJson(restaurant.getListOfTags()));

        db.update(TABLE_RESTAURANTS, values, KEY_ID + " = ?", new String[]{String.valueOf(restaurant.getId())});
        db.close();
    }

    // Delete a restaurant
    public void deleteRestaurant(int restaurantId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_RESTAURANTS, KEY_ID + " = ?", new String[]{String.valueOf(restaurantId)});
        db.close();
    }
}
