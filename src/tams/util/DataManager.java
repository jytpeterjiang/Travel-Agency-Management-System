package tams.util;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import tams.model.*;

/**
 * Utility class for handling data persistence operations.
 * Manages loading and saving data to JSON files.
 */
public class DataManager {
    
    private static final String DATA_DIR = "src/data";
    private static final String CUSTOMERS_FILE = DATA_DIR + File.separator + "customers.json";
    private static final String PACKAGES_FILE = DATA_DIR + File.separator + "packages.json";
    private static final String BOOKINGS_FILE = DATA_DIR + File.separator + "bookings.json";
    private static final String REVIEWS_FILE = DATA_DIR + File.separator + "reviews.json";
    private static final String ACTIVITIES_FILE = DATA_DIR + File.separator + "activities.json";
    
    private ArrayList<Customer> customers;
    private ArrayList<TravelPackage> packages;
    private ArrayList<Booking> bookings;
    private ArrayList<Review> reviews;
    private ArrayList<Activity> activities;
    
    // Maps to quickly look up entities by ID
    private Map<String, Customer> customerMap;
    private Map<String, TravelPackage> packageMap;
    private Map<String, Activity> activityMap;
    private Map<String, Booking> bookingMap;
    private Map<String, Review> reviewMap;
    
    /**
     * Constructor that initializes the collections and creates data directory if needed.
     */
    public DataManager() {
        customers = new ArrayList<>();
        packages = new ArrayList<>();
        bookings = new ArrayList<>();
        reviews = new ArrayList<>();
        activities = new ArrayList<>();
        
        customerMap = new HashMap<>();
        packageMap = new HashMap<>();
        activityMap = new HashMap<>();
        bookingMap = new HashMap<>();
        reviewMap = new HashMap<>();
        
        // Ensure data directory exists
        File dataDir = new File(DATA_DIR);
        if (!dataDir.exists()) {
            dataDir.mkdir();
        }
    }
    
    /**
     * Load all data from files.
     */
    public void loadData() {
        // Clear collections before loading
        customers.clear();
        packages.clear();
        bookings.clear();
        reviews.clear();
        activities.clear();
        
        // Clear maps before loading
        customerMap.clear();
        packageMap.clear();
        bookingMap.clear();
        reviewMap.clear();
        activityMap.clear();
        
        // Load all data from files
        loadActivities();
        loadCustomers();
        loadPackages();
        loadBookings();
        loadReviews();
        
        System.out.println("Data loaded: " + packages.size() + " packages, " + 
                 customers.size() + " customers, " + 
                 bookings.size() + " bookings, " + 
                 activities.size() + " activities, " + 
                 reviews.size() + " reviews");
    }
    
    /**
     * Save all data to files.
     */
    public void saveData() {
        saveActivities();
        saveCustomers();
        savePackages();
        saveBookings();
        saveReviews();
    }
    
    /**
     * Load activities from file.
     */
    @SuppressWarnings("unchecked")
    private void loadActivities() {
        activities.clear();
        activityMap.clear();
        
        try {
            File file = new File(ACTIVITIES_FILE);
            if (!file.exists()) {
                return;
            }
            
            JSONParser parser = new JSONParser();
            JSONArray activitiesArray = (JSONArray) parser.parse(new FileReader(file));
            
            for (Object obj : activitiesArray) {
                JSONObject activityJson = (JSONObject) obj;
                
                String id = (String) activityJson.get("id");
                String name = (String) activityJson.get("name");
                String location = (String) activityJson.get("location");
                int duration = ((Long) activityJson.get("duration")).intValue();
                double cost = ((Number) activityJson.get("cost")).doubleValue();
                
                Activity activity = new Activity(id, name, location, duration, cost);
                activities.add(activity);
                activityMap.put(id, activity);
            }
            System.out.println("Loaded " + activities.size() + " activities");
        } catch (IOException | org.json.simple.parser.ParseException e) {
            System.err.println("Error loading activities: " + e.getMessage());
        }
    }
    
    /**
     * Save activities to file.
     */
    @SuppressWarnings("unchecked")
    private void saveActivities() {
        JSONArray activitiesArray = new JSONArray();
        
        for (Activity activity : activities) {
            JSONObject activityJson = new JSONObject();
            activityJson.put("id", activity.getActivityId());
            activityJson.put("name", activity.getName());
            activityJson.put("location", activity.getLocation());
            activityJson.put("duration", activity.getDuration());
            activityJson.put("cost", activity.getCost());
            
            activitiesArray.add(activityJson);
        }
        
        try (FileWriter file = new FileWriter(ACTIVITIES_FILE)) {
            file.write(activitiesArray.toJSONString());
        } catch (IOException e) {
            System.err.println("Error saving activities: " + e.getMessage());
        }
    }
    
    /**
     * Load customers from file.
     */
    @SuppressWarnings("unchecked")
    private void loadCustomers() {
        customers.clear();
        customerMap.clear();
        
        try {
            File file = new File(CUSTOMERS_FILE);
            if (!file.exists()) {
                return;
            }
            
            JSONParser parser = new JSONParser();
            JSONArray customersArray = (JSONArray) parser.parse(new FileReader(file));
            
            for (Object obj : customersArray) {
                JSONObject customerJson = (JSONObject) obj;
                
                String id = (String) customerJson.get("id");
                String name = (String) customerJson.get("name");
                String email = (String) customerJson.get("email");
                String phone = (String) customerJson.get("phone");
                String address = (String) customerJson.get("address");
                
                // Use empty string as default if address is null
                if (address == null) {
                    address = "";
                }
                
                Customer customer = new Customer(id, name, email, phone, address);
                customers.add(customer);
                customerMap.put(id, customer);
            }
            System.out.println("Loaded " + customers.size() + " customers");
        } catch (IOException | org.json.simple.parser.ParseException e) {
            System.err.println("Error loading customers: " + e.getMessage());
        }
    }
    
    /**
     * Save customers to file.
     */
    @SuppressWarnings("unchecked")
    private void saveCustomers() {
        JSONArray customersArray = new JSONArray();
        
        for (Customer customer : customers) {
            JSONObject customerJson = new JSONObject();
            customerJson.put("id", customer.getCustomerId());
            customerJson.put("name", customer.getName());
            customerJson.put("email", customer.getEmail());
            customerJson.put("phone", customer.getPhone());
            customerJson.put("address", customer.getAddress());
            
            customersArray.add(customerJson);
        }
        
        try (FileWriter file = new FileWriter(CUSTOMERS_FILE)) {
            file.write(customersArray.toJSONString());
        } catch (IOException e) {
            System.err.println("Error saving customers: " + e.getMessage());
        }
    }
    
    /**
     * Load packages from file.
     */
    @SuppressWarnings("unchecked")
    private void loadPackages() {
        packages.clear();
        packageMap.clear();
        
        try {
            File file = new File(PACKAGES_FILE);
            if (!file.exists()) {
                return;
            }
            
            JSONParser parser = new JSONParser();
            JSONArray packagesArray = (JSONArray) parser.parse(new FileReader(file));
            
            for (Object obj : packagesArray) {
                JSONObject packageJson = (JSONObject) obj;
                
                String id = (String) packageJson.get("id");
                String name = (String) packageJson.get("name");
                String description = (String) packageJson.get("description");
                double basePrice = ((Number) packageJson.get("basePrice")).doubleValue();
                String destination = (String) packageJson.get("destination");
                int duration = ((Long) packageJson.get("duration")).intValue();
                String accommodation = (String) packageJson.get("accommodation");
                
                TravelPackage travelPackage = new TravelPackage(
                    id, name, description, basePrice, destination, duration, accommodation);
                
                // Add activities if present
                JSONArray activitiesArray = (JSONArray) packageJson.get("activities");
                if (activitiesArray != null) {
                    for (Object actObj : activitiesArray) {
                        String activityId = (String) actObj;
                        Activity activity = activityMap.get(activityId);
                        if (activity != null) {
                            travelPackage.addActivity(activity);
                        }
                    }
                }
                
                // Load itinerary if present
                JSONObject itineraryJson = (JSONObject) packageJson.get("itinerary");
                if (itineraryJson != null) {
                    String itineraryId = (String) itineraryJson.get("id");
                    String itineraryName = (String) itineraryJson.get("name");
                    
                    // Create a new itinerary
                    Itinerary itinerary = new Itinerary(itineraryId, itineraryName);
                    
                    // Load days
                    JSONArray daysArray = (JSONArray) itineraryJson.get("days");
                    if (daysArray != null) {
                        for (Object dayObj : daysArray) {
                            JSONObject dayJson = (JSONObject) dayObj;
                            
                            int dayNumber = ((Long) dayJson.get("dayNumber")).intValue();
                            String notes = (String) dayJson.get("notes");
                            
                            // Create a new day
                            ItineraryDay day = new ItineraryDay(dayNumber, notes);
                            
                            // Add activities to the day
                            JSONArray dayActivitiesArray = (JSONArray) dayJson.get("activities");
                            if (dayActivitiesArray != null) {
                                for (Object actObj : dayActivitiesArray) {
                                    String activityId = (String) actObj;
                                    Activity activity = activityMap.get(activityId);
                                    if (activity != null) {
                                        day.addActivity(activity);
                                    }
                                }
                            }
                            
                            // Add the day to the itinerary
                            itinerary.addDay(day);
                        }
                    }
                    
                    // Replace the default itinerary with our loaded one
                    travelPackage.getItinerary().getDays().clear(); // Clear default days
                    for (ItineraryDay day : itinerary.getDays()) {
                        travelPackage.getItinerary().addDay(day);
                    }
                }
                
                packages.add(travelPackage);
                packageMap.put(id, travelPackage);
            }
            System.out.println("Loaded " + packages.size() + " packages");
        } catch (IOException | org.json.simple.parser.ParseException e) {
            System.err.println("Error loading packages: " + e.getMessage());
        }
    }
    
    /**
     * Save packages to file.
     */
    @SuppressWarnings("unchecked")
    private void savePackages() {
        JSONArray packagesArray = new JSONArray();
        
        System.out.println("Saving " + packages.size() + " packages to file");
        
        for (TravelPackage travelPackage : packages) {
            JSONObject packageJson = new JSONObject();
            packageJson.put("id", travelPackage.getServiceId());
            packageJson.put("name", travelPackage.getName());
            packageJson.put("description", travelPackage.getDescription());
            packageJson.put("basePrice", travelPackage.getBasePrice());
            packageJson.put("destination", travelPackage.getDestination());
            packageJson.put("duration", travelPackage.getDuration());
            packageJson.put("accommodation", travelPackage.getAccommodation());
            
            // Save activities
            JSONArray activitiesArray = new JSONArray();
            for (Activity activity : travelPackage.getActivities()) {
                activitiesArray.add(activity.getActivityId());
            }
            packageJson.put("activities", activitiesArray);
            
            // Save itinerary data
            Itinerary itinerary = travelPackage.getItinerary();
            if (itinerary != null) {
                JSONObject itineraryJson = new JSONObject();
                itineraryJson.put("id", itinerary.getItineraryId());
                itineraryJson.put("name", itinerary.getName());
                
                // Save itinerary days
                JSONArray daysArray = new JSONArray();
                for (ItineraryDay day : itinerary.getDays()) {
                    JSONObject dayJson = new JSONObject();
                    dayJson.put("dayNumber", day.getDayNumber());
                    dayJson.put("notes", day.getNotes());
                    
                    // Save day activities
                    JSONArray dayActivitiesArray = new JSONArray();
                    for (Activity activity : day.getActivities()) {
                        dayActivitiesArray.add(activity.getActivityId());
                    }
                    dayJson.put("activities", dayActivitiesArray);
                    
                    daysArray.add(dayJson);
                }
                itineraryJson.put("days", daysArray);
                
                packageJson.put("itinerary", itineraryJson);
            }
            
            packagesArray.add(packageJson);
        }
        
        try (FileWriter file = new FileWriter(PACKAGES_FILE)) {
            file.write(packagesArray.toJSONString());
            System.out.println("Successfully wrote " + packages.size() + " packages to file");
        } catch (IOException e) {
            System.err.println("Error saving packages: " + e.getMessage());
        }
    }
    
    /**
     * Load bookings from file.
     */
    @SuppressWarnings("unchecked")
    private void loadBookings() {
        bookings.clear();
        bookingMap.clear();
        
        try {
            File file = new File(BOOKINGS_FILE);
            if (!file.exists()) {
                return;
            }
            
            JSONParser parser = new JSONParser();
            JSONArray bookingsArray = (JSONArray) parser.parse(new FileReader(file));
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            
            for (Object obj : bookingsArray) {
                JSONObject bookingJson = (JSONObject) obj;
                
                String id = (String) bookingJson.get("id");
                String customerId = (String) bookingJson.get("customerId");
                String serviceId = (String) bookingJson.get("serviceId");
                String statusStr = (String) bookingJson.get("status");
                String dateStr = (String) bookingJson.get("date");
                
                Customer customer = customerMap.get(customerId);
                TravelService service = packageMap.get(serviceId);
                
                if (customer != null && service != null) {
                    Date date = null;
                    try {
                        date = dateFormat.parse(dateStr);
                    } catch (ParseException e) {
                        date = new Date(); // Use current date as fallback
                    }
                    
                    BookingStatus status = BookingStatus.valueOf(statusStr);
                    
                    Booking booking = new Booking(id, customer, service);
                    booking.setDate(date);
                    booking.updateStatus(status);
                    
                    // Add payment if present
                    String paymentId = (String) bookingJson.get("paymentId");
                    if (paymentId != null) {
                        // In a real implementation, we would load payment details
                        // For now, we'll create a dummy payment
                        Payment payment = new Payment(
                            paymentId, service.calculateTotalPrice(), PaymentMethod.CREDIT_CARD);
                        payment.setStatus(PaymentStatus.COMPLETED);
                        booking.addPayment(payment);
                    }
                    
                    bookings.add(booking);
                    bookingMap.put(id, booking);
                    
                    // Add booking to customer
                    customer.addBooking(booking);
                }
            }
            System.out.println("Loaded " + bookings.size() + " bookings");
        } catch (IOException | org.json.simple.parser.ParseException e) {
            System.err.println("Error loading bookings: " + e.getMessage());
        }
    }
    
    /**
     * Save bookings to file.
     */
    @SuppressWarnings("unchecked")
    private void saveBookings() {
        JSONArray bookingsArray = new JSONArray();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        
        for (Booking booking : bookings) {
            JSONObject bookingJson = new JSONObject();
            bookingJson.put("id", booking.getBookingId());
            bookingJson.put("customerId", booking.getCustomer().getCustomerId());
            bookingJson.put("serviceId", booking.getService().getServiceId());
            bookingJson.put("status", booking.getStatus().name());
            bookingJson.put("date", dateFormat.format(booking.getDate()));
            
            if (booking.getPayment() != null) {
                bookingJson.put("paymentId", booking.getPayment().getPaymentId());
            } else {
                bookingJson.put("paymentId", null);
            }
            
            bookingsArray.add(bookingJson);
        }
        
        try (FileWriter file = new FileWriter(BOOKINGS_FILE)) {
            file.write(bookingsArray.toJSONString());
        } catch (IOException e) {
            System.err.println("Error saving bookings: " + e.getMessage());
        }
    }
    
    /**
     * Load reviews from file.
     */
    @SuppressWarnings("unchecked")
    private void loadReviews() {
        reviews.clear();
        reviewMap.clear();
        
        try {
            File file = new File(REVIEWS_FILE);
            if (!file.exists()) {
                return;
            }
            
            JSONParser parser = new JSONParser();
            JSONArray reviewsArray = (JSONArray) parser.parse(new FileReader(file));
            
            for (Object obj : reviewsArray) {
                JSONObject reviewJson = (JSONObject) obj;
                
                String id = (String) reviewJson.get("id");
                String customerId = (String) reviewJson.get("customerId");
                int rating = ((Long) reviewJson.get("rating")).intValue();
                String comment = (String) reviewJson.get("comment");
                String packageId = (String) reviewJson.get("packageId");
                
                Customer customer = customerMap.get(customerId);
                TravelPackage travelPackage = packageMap.get(packageId);
                
                if (customer != null) {
                    Review review = new Review(id, customer, rating, comment);
                    
                    reviews.add(review);
                    reviewMap.put(id, review);
                    
                    // Add the review to the correct package if found
                    if (travelPackage != null) {
                        travelPackage.addReview(review);
                    }
                }
            }
            System.out.println("Loaded " + reviews.size() + " reviews");
        } catch (IOException | org.json.simple.parser.ParseException e) {
            System.err.println("Error loading reviews: " + e.getMessage());
        }
    }
    
    /**
     * Save reviews to file.
     */
    @SuppressWarnings("unchecked")
    private void saveReviews() {
        JSONArray reviewsArray = new JSONArray();
        
        for (Review review : reviews) {
            JSONObject reviewJson = new JSONObject();
            reviewJson.put("id", review.getReviewId());
            reviewJson.put("customerId", review.getCustomer().getCustomerId());
            reviewJson.put("rating", review.getRating());
            reviewJson.put("comment", review.getComment());
            
            // Find which package this review belongs to
            String packageId = null;
            for (TravelPackage pkg : packages) {
                if (pkg.getReviews().contains(review)) {
                    packageId = pkg.getServiceId();
                    break;
                }
            }
            
            reviewJson.put("packageId", packageId);
            reviewsArray.add(reviewJson);
        }
        
        try (FileWriter file = new FileWriter(REVIEWS_FILE)) {
            file.write(reviewsArray.toJSONString());
        } catch (IOException e) {
            System.err.println("Error saving reviews: " + e.getMessage());
        }
    }
    
    // Getters for the collections
    
    public ArrayList<Customer> getCustomers() {
        return new ArrayList<>(customers);
    }
    
    public ArrayList<TravelPackage> getPackages() {
        return new ArrayList<>(packages);
    }
    
    public ArrayList<Booking> getBookings() {
        return new ArrayList<>(bookings);
    }
    
    public ArrayList<Review> getReviews() {
        return new ArrayList<>(reviews);
    }
    
    public ArrayList<Activity> getActivities() {
        return new ArrayList<>(activities);
    }
    
    // Methods to add items to collections
    
    public void addCustomer(Customer customer) {
        customers.add(customer);
        customerMap.put(customer.getCustomerId(), customer);
    }
    
    public void addPackage(TravelPackage travelPackage) {
        packages.add(travelPackage);
        packageMap.put(travelPackage.getServiceId(), travelPackage);
    }
    
    public void addBooking(Booking booking) {
        bookings.add(booking);
        bookingMap.put(booking.getBookingId(), booking);
    }
    
    public void addReview(Review review) {
        reviews.add(review);
        reviewMap.put(review.getReviewId(), review);
    }
    
    public void addActivity(Activity activity) {
        activities.add(activity);
        activityMap.put(activity.getActivityId(), activity);
    }
    
    /**
     * Remove a package from the system.
     * 
     * @param travelPackage the package to remove
     * @return true if the package was successfully removed
     */
    public boolean removePackage(TravelPackage travelPackage) {
        if (travelPackage == null) {
            return false;
        }
        
        String packageId = travelPackage.getServiceId();
        boolean removed = packages.removeIf(p -> p.getServiceId().equals(packageId));
        if (removed) {
            packageMap.remove(packageId);
            savePackages(); // Save changes immediately
        }
        return removed;
    }
    
    /**
     * Remove a customer from the system.
     * 
     * @param customer the customer to remove
     * @return true if the customer was successfully removed
     */
    public boolean removeCustomer(Customer customer) {
        if (customer == null) {
            return false;
        }
        
        String customerId = customer.getCustomerId();
        boolean removed = customers.removeIf(c -> c.getCustomerId().equals(customerId));
        if (removed) {
            customerMap.remove(customerId);
        }
        return removed;
    }
    
    /**
     * Get all bookings.
     * 
     * @return an ArrayList of all bookings
     */
    public ArrayList<Booking> getAllBookings() {
        return getBookings();
    }
    
    /**
     * Remove a booking.
     * 
     * @param booking the booking to remove
     * @return true if successfully removed, false otherwise
     */
    public boolean removeBooking(Booking booking) {
        if (booking == null) {
            return false;
        }
        
        String bookingId = booking.getBookingId();
        bookingMap.remove(bookingId);
        boolean result = bookings.remove(booking);
        
        if (result) {
            saveBookings();
        }
        
        return result;
    }
    
    /**
     * Remove a review.
     * 
     * @param review the review to remove
     * @return true if successfully removed, false otherwise
     */
    public boolean removeReview(Review review) {
        if (review == null) {
            return false;
        }
        
        String reviewId = review.getReviewId();
        reviewMap.remove(reviewId);
        boolean result = reviews.remove(review);
        
        if (result) {
            saveReviews();
        }
        
        return result;
    }
    
    /**
     * Remove an activity from the system.
     * 
     * @param activity the activity to remove
     * @return true if the activity was successfully removed
     */
    public boolean removeActivity(Activity activity) {
        if (activity == null) {
            return false;
        }
        
        String activityId = activity.getActivityId();
        boolean removed = activities.removeIf(a -> a.getActivityId().equals(activityId));
        if (removed) {
            activityMap.remove(activityId);
            saveActivities();
        }
        return removed;
    }
} 