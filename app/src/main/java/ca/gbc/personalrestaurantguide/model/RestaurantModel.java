package ca.gbc.personalrestaurantguide.model;

import java.io.Serializable;
import java.util.ArrayList;

public class RestaurantModel implements Serializable {
    int id;
    String restaurantName, restaurantAddress, restaurantPhone, restaurantRating, restaurantDescription;
    ArrayList<String> listOfTags;

    public RestaurantModel() {
    }

    public RestaurantModel(String restaurantName, String restaurantAddress, String restaurantPhone, String restaurantRating, String restaurantDescription, ArrayList<String> listOfTags) {
        this.restaurantName = restaurantName;
        this.restaurantAddress = restaurantAddress;
        this.restaurantPhone = restaurantPhone;
        this.restaurantRating = restaurantRating;
        this.restaurantDescription = restaurantDescription;
        this.listOfTags = listOfTags;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getRestaurantAddress() {
        return restaurantAddress;
    }

    public void setRestaurantAddress(String restaurantAddress) {
        this.restaurantAddress = restaurantAddress;
    }

    public String getRestaurantPhone() {
        return restaurantPhone;
    }

    public void setRestaurantPhone(String restaurantPhone) {
        this.restaurantPhone = restaurantPhone;
    }

    public String getRestaurantRating() {
        return restaurantRating;
    }

    public void setRestaurantRating(String restaurantRating) {
        this.restaurantRating = restaurantRating;
    }

    public String getRestaurantDescription() {
        return restaurantDescription;
    }

    public void setRestaurantDescription(String restaurantDescription) {
        this.restaurantDescription = restaurantDescription;
    }

    public ArrayList<String> getListOfTags() {
        return listOfTags;
    }

    public void setListOfTags(ArrayList<String> listOfTags) {
        this.listOfTags = listOfTags;
    }
}
