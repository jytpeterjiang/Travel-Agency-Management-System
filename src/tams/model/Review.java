package tams.model;

import java.util.Date;

/**
 * Represents a review from a customer for a travel service.
 */
public class Review {
    
    private String reviewId;
    private Customer customer;
    private int rating; // 1-5 stars
    private String comment;
    private Date date;
    
    /**
     * Constructor for creating a new review.
     * 
     * @param reviewId the unique identifier for this review
     * @param customer the customer who submitted the review
     * @param rating the rating on a scale of 1-5
     * @param comment the review comment
     */
    public Review(String reviewId, Customer customer, int rating, String comment) {
        this.reviewId = reviewId;
        this.customer = customer;
        // Ensure rating is between 1 and 5
        this.rating = Math.max(1, Math.min(5, rating));
        this.comment = comment;
        this.date = new Date();
    }
    
    /**
     * Get the rating for this review.
     * 
     * @return the rating as an integer
     */
    public int getRating() {
        return rating;
    }
    
    /**
     * Get the comment for this review.
     * 
     * @return the comment as a string
     */
    public String getComment() {
        return comment;
    }
    
    // Getters and setters
    
    public String getReviewId() {
        return reviewId;
    }
    
    public Customer getCustomer() {
        return customer;
    }
    
    public void setRating(int rating) {
        // Ensure rating is between 1 and 5
        this.rating = Math.max(1, Math.min(5, rating));
    }
    
    public void setComment(String comment) {
        this.comment = comment;
    }
    
    public Date getDate() {
        return new Date(date.getTime());
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Review other = (Review) obj;
        return reviewId.equals(other.reviewId);
    }
    
    @Override
    public int hashCode() {
        return reviewId.hashCode();
    }
    
    @Override
    public String toString() {
        return String.format("%d stars - %s - by %s on %s", 
            rating, comment, customer.getName(), date);
    }
} 