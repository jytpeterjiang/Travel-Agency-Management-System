package tams.model;

import java.util.ArrayList;

/**
 * Represents an itinerary for a travel package.
 * Contains day-by-day activities and details.
 */
public class Itinerary {
    
    private String itineraryId;
    private String name;
    private ArrayList<ItineraryDay> days;
    
    /**
     * Constructor for creating a new itinerary.
     * 
     * @param itineraryId the unique identifier for this itinerary
     * @param name the name of this itinerary
     */
    public Itinerary(String itineraryId, String name) {
        this.itineraryId = itineraryId;
        this.name = name;
        this.days = new ArrayList<>();
    }
    
    /**
     * Add a day to this itinerary.
     * 
     * @param day the ItineraryDay to add
     */
    public void addDay(ItineraryDay day) {
        days.add(day);
        // Sort days by day number to ensure they're in order
        days.sort((day1, day2) -> Integer.compare(day1.getDayNumber(), day2.getDayNumber()));
    }
    
    /**
     * Remove a day from this itinerary.
     * 
     * @param day the ItineraryDay to remove
     * @return true if the day was removed, false if it was not found
     */
    public boolean removeDay(ItineraryDay day) {
        return days.removeIf(d -> d.getDayNumber() == day.getDayNumber());
    }
    
    /**
     * Remove a day by its day number.
     * 
     * @param dayNumber the day number to remove
     * @return true if the day was removed, false if it was not found
     */
    public boolean removeDayByNumber(int dayNumber) {
        return days.removeIf(d -> d.getDayNumber() == dayNumber);
    }
    
    /**
     * Get the total duration of this itinerary in days.
     * 
     * @return the number of days
     */
    public int getTotalDuration() {
        return days.size();
    }
    
    /**
     * Get all days in this itinerary.
     * 
     * @return an ArrayList of ItineraryDay objects
     */
    public ArrayList<ItineraryDay> getDays() {
        return new ArrayList<>(days);
    }
    
    /**
     * Get a directly modifiable reference to the internal days list.
     * WARNING: Use with caution, as this allows direct modifications to the internal list.
     * 
     * @return the internal ArrayList of ItineraryDay objects
     */
    public ArrayList<ItineraryDay> getModifiableDays() {
        return days;
    }
    
    /**
     * Get a specific day in the itinerary by day number.
     * 
     * @param dayNumber the day number to retrieve
     * @return the ItineraryDay if found, null otherwise
     */
    public ItineraryDay getDay(int dayNumber) {
        for (ItineraryDay day : days) {
            if (day.getDayNumber() == dayNumber) {
                return day;
            }
        }
        return null;
    }
    
    // Getters and setters
    
    public String getItineraryId() {
        return itineraryId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Calculate the total activity hours in the itinerary.
     * 
     * @return total hours as an integer
     */
    public int getTotalActivityHours() {
        int totalHours = 0;
        for (ItineraryDay day : days) {
            for (Activity activity : day.getActivities()) {
                totalHours += activity.getDuration();
            }
        }
        return totalHours;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(" (").append(getTotalDuration()).append(" days):\n");
        
        for (ItineraryDay day : days) {
            sb.append("  Day ").append(day.getDayNumber()).append(": ");
            sb.append(day.getActivities().size()).append(" activities\n");
        }
        
        return sb.toString();
    }
} 