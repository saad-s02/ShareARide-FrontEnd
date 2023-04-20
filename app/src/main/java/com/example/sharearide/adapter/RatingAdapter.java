package com.example.sharearide.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sharearide.R;
import com.example.sharearide.utils.RatingCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RatingAdapter extends RecyclerView.Adapter<RatingAdapter.ViewHolder> {

    private ArrayList<String> userIds;
    private Map<String, Float> rating_result;
    private RatingCallback mCallback;


    public RatingAdapter(ArrayList<String> userIds, RatingCallback callback) {
        this.userIds = userIds;
        this.rating_result = new HashMap<>();
        mCallback = callback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rating_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String userId = userIds.get(position);
        holder.userIdTextView.setText(userId);
        holder.ratingBar.setOnRatingBarChangeListener(null); // remove previous listener
        if (rating_result.containsKey(userId)) {
            holder.ratingBar.setRating(rating_result.get(userId));
        } else {
            holder.ratingBar.setRating(0);
        }
        holder.ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                // Save the rating for this user
                rating_result.put(userId, rating);
                mCallback.onRatingChanged(position, rating);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userIds.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView userIdTextView;
        private RatingBar ratingBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userIdTextView = itemView.findViewById(R.id.user_id);
            ratingBar = itemView.findViewById(R.id.rating_rating_bar);
        }
    }
}

