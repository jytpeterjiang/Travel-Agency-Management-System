package tams.model.interfaces;

import java.util.ArrayList;
import tams.model.Review;

/**
 * Interface defining methods that any reviewable service must implement.
 */
public interface Reviewable {
    
    /**
     * Add a review to this reviewable entity.
     * 
     * @param review the review to add
     */
    void addReview(Review review);
    
    /**
     * Get the average rating of all reviews for this entity.
     * 
     * @return the average rating as a double
     */
    double getAverageRating();
    
    /**
     * Get all reviews for this entity.
     * 
     * @return an ArrayList containing all reviews
     */
    ArrayList<Review> getReviews();
} 