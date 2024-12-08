package ca.gbc.personalrestaurantguide.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ca.gbc.personalrestaurantguide.R;
import ca.gbc.personalrestaurantguide.model.OnClick;
import ca.gbc.personalrestaurantguide.model.RestaurantModel;

public class RestaurantsAdapter extends RecyclerView.Adapter<RestaurantsAdapter.ViewHolder> {
    List<RestaurantModel> list;
    Context context;
    OnClick onClick;

    public RestaurantsAdapter(List<RestaurantModel> list, Context context, OnClick onClick) {
        this.list = list;
        this.context = context;
        this.onClick = onClick;
    }

    public void setList(List<RestaurantModel> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_restaurant, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        RestaurantModel restaurantModel = list.get(position);
        holder.tvName.setText("Name: " + restaurantModel.getRestaurantName());
        holder.tvPhone.setText("Phone: " + restaurantModel.getRestaurantPhone());
        holder.ratingBar.setRating(Float.parseFloat(restaurantModel.getRestaurantRating()));
        holder.tvAddress.setText("Address: " + restaurantModel.getRestaurantAddress());

        // Join tags with a comma and set to tvTags
        List<String> tags = restaurantModel.getListOfTags();
        if (tags != null && !tags.isEmpty()) {
            holder.tvTags.setText("Tags: " + String.join(", ", tags));
        } else {
            holder.tvTags.setText("Tags: No tags available");
        }

        holder.tvDescription.setText("Description: " + restaurantModel.getRestaurantDescription());
        holder.ivShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick.clicked("share", position);
            }
        });
        holder.ivEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick.clicked("edit", position);
            }
        });
        holder.ivMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick.clicked("map", position);
            }
        });
        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick.clicked("delete", position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPhone, tvAddress, tvTags, tvDescription;
        RatingBar ratingBar;
        ImageView ivShare, ivEdit, ivMap, ivDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvTags = itemView.findViewById(R.id.tvTags);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            ivShare = itemView.findViewById(R.id.ivShare);
            ivEdit = itemView.findViewById(R.id.ivEdit);
            ivMap = itemView.findViewById(R.id.ivMap);
            ivDelete = itemView.findViewById(R.id.ivDelete);
        }
    }
}
