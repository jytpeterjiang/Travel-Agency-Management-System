package tams.model;

import java.util.ArrayList;

/**
 * Represents a single day in an itinerary.
 * Contains activities scheduled for that day.
 */
public class ItineraryDay {
    
    private int dayNumber;
    private ArrayList<Activity> activities;
    private String notes;
    
    /**
     * Constructor for creating a new itinerary day.
     * 
     * @param dayNumber the number for this day in the itinerary
     */
    public ItineraryDay(int dayNumber) {
        this.dayNumber = dayNumber;
        this.activities = new ArrayList<>();
        this.notes = "";
    }
    
    /**
     * Overloaded constructor with notes.
     * 
     * @param dayNumber the number for this day in the itinerary
     * @param notes additional notes or description for this day
     */
    public ItineraryDay(int dayNumber, String notes) {
        this(dayNumber);
        this.notes = notes;
    }
    
    /**
     * Add an activity to this day.
     * 
     * @param activity the activity to add
     */
    public void addActivity(Activity activity) {
        if (!activities.contains(activity)) {
            activities.add(activity);
        }
    }
    
    /**
     * Get all activities scheduled for this day.
     * 
     * @return an ArrayList of activities
     */
    public ArrayList<Activity> getActivities() {
        return new ArrayList<>(activities);
    }
    
    /**
     * Calculate the total duration of all activities on this day.
     * 
     * @return total duration in hours
     */
    public int getTotalDuration() {
        int totalDuration = 0;
        for (Activity activity : activities) {
            totalDuration += activity.getDuration();
        }
        return totalDuration;
    }
    
    /**
     * Calculate the total cost of all activities on this day.
     * 
     * @return total cost
     */
    public double getTotalCost() {
        double totalCost = 0;
        for (Activity activity : activities) {
            totalCost += activity.getCost();
        }
        return totalCost;
    }
    
    // Getters and setters
    
    public int getDayNumber() {
        return dayNumber;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Day ").append(dayNumber).append(":\n");
        
        if (!notes.isEmpty()) {
            sb.append("  Notes: ").append(notes).append("\n");
        }
        
        sb.append("  Activities: ").append(activities.size()).append("\n");
        for (Activity activity : activities) {
            sb.append("    - ").append(activity.getName());
            sb.append(" (").append(activity.getDuration()).append(" hours)\n");
        }
        
        sb.append("  Total Duration: ").append(getTotalDuration()).append(" hours\n");
        sb.append("  Total Cost: $").append(String.format("%.2f", getTotalCost())).append("\n");
        
        return sb.toString();
    }
} 