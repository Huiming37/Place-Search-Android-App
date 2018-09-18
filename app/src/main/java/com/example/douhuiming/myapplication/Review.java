package com.example.douhuiming.myapplication;

import java.io.Serializable;
import java.util.Comparator;

public class Review implements Serializable {

    public String reviewUrl;

    public String reviewProfileUrl;

    public String reviewName;

    public int reviewRating;

    public String reviewText;

    public String reviewTime;

    public static Comparator TimeIncreasingComparator = new Comparator<Review>() {
        @Override
        public int compare(Review a, Review b) {
            return (a.reviewTime.compareTo(b.reviewTime));
        }
    };
    public static Comparator TimedecreasingComparator = new Comparator<Review>() {
        @Override
        public int compare(Review a, Review b) {
            return (b.reviewTime.compareTo(a.reviewTime));
        }
    };

    public static Comparator RatingIncreasingComparator = new Comparator<Review>() {
        @Override
        public int compare(Review a, Review b) {
            return (a.reviewRating - b.reviewRating);
        }
    };
    public static Comparator RatingdecreasingComparator = new Comparator<Review>() {
        @Override
        public int compare(Review a, Review b) {
            return (b.reviewRating - a.reviewRating);
        }
    };



}
