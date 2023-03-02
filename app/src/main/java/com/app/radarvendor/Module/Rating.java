package com.app.radarvendor.Module;

import java.util.List;

public class Rating {
    private int avg_rating;
    private int total_reviews;
    private List<Review> reviews;

    public Rating() {
    }

    public Rating(int avg_rating, int total_reviews, List<Review> reviews) {
        this.avg_rating = avg_rating;
        this.total_reviews = total_reviews;
        this.reviews = reviews;
    }

    public int getAvg_rating() {
        return avg_rating;
    }

    public void setAvg_rating(int avg_rating) {
        this.avg_rating = avg_rating;
    }

    public int getTotal_reviews() {
        return total_reviews;
    }

    public void setTotal_reviews(int total_reviews) {
        this.total_reviews = total_reviews;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }
}
