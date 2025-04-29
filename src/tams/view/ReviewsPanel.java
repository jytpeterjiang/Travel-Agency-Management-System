package tams.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import tams.controller.TravelAgencyController;
import tams.model.Customer;
import tams.model.Review;
import tams.model.TravelPackage;

/**
 * Panel for displaying and managing reviews.
 */
public class ReviewsPanel extends BasePanel {
    
    private JTable reviewsTable;
    private DefaultTableModel tableModel;
    
    private JComboBox<String> packageComboBox;
    private JComboBox<Integer> ratingComboBox;
    private JButton searchButton;
    private JButton clearButton;
    
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton viewButton;
    
    private Review selectedReview;
    private boolean isInitialized = false;
    
    /**
     * Constructor for creating a new ReviewsPanel.
     * 
     * @param controller the travel agency controller
     * @param mainWindow the main window
     */
    public ReviewsPanel(TravelAgencyController controller, MainWindow mainWindow) {
        super(controller, mainWindow);
        
        // Call refreshData explicitly to load reviews when panel is created
        SwingUtilities.invokeLater(() -> {
            refreshData();
        });
        
        isInitialized = true;
    }
    
    @Override
    protected void createFilterPanel() {
        // Create a thinner filter panel with vertical layout to save horizontal space
        filterPanel = new JPanel(new BorderLayout());
        filterPanel.setBorder(BorderFactory.createTitledBorder("Filter Reviews"));
        filterPanel.setPreferredSize(new Dimension(200, 0)); // Reduce width to 200px
        
        // Use a vertical layout for filter controls
        JPanel controlsPanel = new JPanel(new GridLayout(6, 1, 5, 5));
        controlsPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        // Create a list of package names including "All Packages"
        ArrayList<TravelPackage> allPackages = controller.getAllTravelPackages();
        String[] packageNames = new String[allPackages.size() + 1];
        packageNames[0] = "All Packages";
        for (int i = 0; i < allPackages.size(); i++) {
            packageNames[i + 1] = allPackages.get(i).getName();
        }
        packageComboBox = new JComboBox<>(packageNames);
        
        // Create rating filter options
        Integer[] ratings = {0, 1, 2, 3, 4, 5}; // 0 means all ratings
        ratingComboBox = new JComboBox<>(ratings);
        ratingComboBox.setSelectedIndex(0);
        
        searchButton = new JButton("Search");
        clearButton = new JButton("Clear");
        
        // Add labels and components with proper spacing
        controlsPanel.add(new JLabel("Package:"));
        controlsPanel.add(packageComboBox);
        controlsPanel.add(new JLabel("Min Rating:"));
        controlsPanel.add(ratingComboBox);
        controlsPanel.add(searchButton);
        controlsPanel.add(clearButton);
        
        // Set action listeners
        searchButton.addActionListener(e -> applyFilters());
        clearButton.addActionListener(e -> clearFilters());
        
        // Add to filter panel
        filterPanel.add(controlsPanel, BorderLayout.NORTH);
    }
    
    @Override
    protected void createContentPanel() {
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        // Create the table model with column names
        String[] columnNames = {"ID", "Package", "Customer", "Rating", "Date", "Comment"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make the table non-editable
            }
            
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 3) {
                    return Integer.class; // Make the rating column sortable as integers
                }
                return String.class;
            }
        };
        
        // Create the table and add it to a scroll pane
        reviewsTable = new JTable(tableModel);
        reviewsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        reviewsTable.setRowHeight(25);
        
        // Set custom column widths - optimize for better display
        reviewsTable.getColumnModel().getColumn(0).setMaxWidth(70); // ID column
        reviewsTable.getColumnModel().getColumn(1).setPreferredWidth(120); // Package
        reviewsTable.getColumnModel().getColumn(2).setPreferredWidth(120); // Customer
        reviewsTable.getColumnModel().getColumn(3).setMaxWidth(60);  // Rating
        reviewsTable.getColumnModel().getColumn(4).setPreferredWidth(100); // Date
        reviewsTable.getColumnModel().getColumn(5).setPreferredWidth(300); // Comment
        
        // Create a sorter for the table
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        reviewsTable.setRowSorter(sorter);
        
        // Add a listener to detect row selection
        reviewsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && reviewsTable.getSelectedRow() != -1) {
                int selectedRow = reviewsTable.convertRowIndexToModel(reviewsTable.getSelectedRow());
                String reviewId = (String) tableModel.getValueAt(selectedRow, 0);
                selectedReview = findReviewById(reviewId);
                updateButtonStates();
            }
        });
        
        // Add double-click listener to view review details
        reviewsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && reviewsTable.getSelectedRow() != -1) {
                    viewReviewDetails();
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(reviewsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        
        contentPanel.add(scrollPane, BorderLayout.CENTER);
    }
    
    @Override
    protected void createButtonPanel() {
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        
        addButton = new JButton("Add Review");
        editButton = new JButton("Edit Review");
        deleteButton = new JButton("Delete Review");
        viewButton = new JButton("View Details");
        
        // Set action listeners
        addButton.addActionListener(e -> showAddReviewDialog());
        editButton.addActionListener(e -> showEditReviewDialog());
        deleteButton.addActionListener(e -> deleteSelectedReview());
        viewButton.addActionListener(e -> viewReviewDetails());
        
        // Add buttons to panel
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(viewButton);
        
        // Initially disable edit, delete, and view buttons until a review is selected
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
        viewButton.setEnabled(false);
    }
    
    /**
     * Update the enabled/disabled state of buttons based on selection.
     */
    private void updateButtonStates() {
        boolean hasSelection = selectedReview != null;
        editButton.setEnabled(hasSelection);
        deleteButton.setEnabled(hasSelection);
        viewButton.setEnabled(hasSelection);
    }
    
    @Override
    public void refreshData() {
        // Clear the table
        if (tableModel != null) {
            tableModel.setRowCount(0);
            
            // Update package filter dropdown to include any new packages
            if (packageComboBox != null) {
                String selectedPackage = (String) packageComboBox.getSelectedItem();
                packageComboBox.removeAllItems();
                
                // Add "All Packages" as the first item
                packageComboBox.addItem("All Packages");
                
                // Add all current packages
                for (TravelPackage pkg : controller.getAllTravelPackages()) {
                    packageComboBox.addItem(pkg.getName());
                }
                
                // Restore previous selection if it still exists
                if (selectedPackage != null) {
                    boolean found = false;
                    for (int i = 0; i < packageComboBox.getItemCount(); i++) {
                        if (selectedPackage.equals(packageComboBox.getItemAt(i))) {
                            packageComboBox.setSelectedIndex(i);
                            found = true;
                            break;
                        }
                    }
                    // If previous selection not found, default to "All Packages"
                    if (!found) {
                        packageComboBox.setSelectedIndex(0);
                    }
                }
            }
            
            // Get all reviews from the controller
            ArrayList<Review> reviews = controller.getReviews();
            
            // Populate the table with review data
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            for (Review review : reviews) {
                // Find which package this review belongs to
                String packageName = "Unknown";
                for (TravelPackage pkg : controller.getAllTravelPackages()) {
                    if (pkg.getReviews().contains(review)) {
                        packageName = pkg.getName();
                        break;
                    }
                }
                
                Object[] rowData = {
                    review.getReviewId(),
                    packageName,
                    review.getCustomer().getName(),
                    review.getRating(),
                    dateFormat.format(review.getDate()),
                    formatComment(review.getComment())
                };
                tableModel.addRow(rowData);
            }
            
            // Reset selection
            selectedReview = null;
            updateButtonStates();
            updateStatus(reviews.size() + " reviews");
        }
    }
    
    /**
     * Format a potentially long comment for display in the table.
     * 
     * @param comment the full comment
     * @return a truncated version if necessary
     */
    private String formatComment(String comment) {
        if (comment == null) return "";
        if (comment.length() <= 50) return comment;
        return comment.substring(0, 47) + "...";
    }
    
    /**
     * Apply filters to the reviews table.
     */
    private void applyFilters() {
        // Get filter values
        String packageName = (String) packageComboBox.getSelectedItem();
        int minRating = (Integer) ratingComboBox.getSelectedItem();
        
        // Clear the table
        tableModel.setRowCount(0);
        
        // Get filtered reviews
        ArrayList<Review> filteredReviews = getFilteredReviews(packageName, minRating);
        
        // Populate the table
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        for (Review review : filteredReviews) {
            // Find which package this review belongs to
            String pkgName = "Unknown";
            for (TravelPackage pkg : controller.getAllTravelPackages()) {
                if (pkg.getReviews().contains(review)) {
                    pkgName = pkg.getName();
                    break;
                }
            }
            
            // Only add reviews that match the package filter (if not "All Packages")
            if ("All Packages".equals(packageName) || pkgName.equals(packageName)) {
                Object[] rowData = {
                    review.getReviewId(),
                    pkgName,
                    review.getCustomer().getName(),
                    review.getRating(),
                    dateFormat.format(review.getDate()),
                    formatComment(review.getComment())
                };
                tableModel.addRow(rowData);
            }
        }
        
        updateStatus(filteredReviews.size() + " reviews found");
    }
    
    /**
     * Clear all filters and refresh the data.
     */
    private void clearFilters() {
        packageComboBox.setSelectedIndex(0);
        ratingComboBox.setSelectedIndex(0);
        refreshData();
    }
    
    /**
     * Get a filtered list of reviews based on selected criteria.
     * 
     * @param packageName the selected package name or "All Packages"
     * @param minRating the minimum rating
     * @return a filtered list of reviews
     */
    private ArrayList<Review> getFilteredReviews(String packageName, int minRating) {
        ArrayList<Review> allReviews = controller.getReviews();
        ArrayList<Review> filteredReviews = new ArrayList<>();
        
        for (Review review : allReviews) {
            // Skip reviews with ratings lower than the minimum
            if (minRating > 0 && review.getRating() < minRating) {
                continue;
            }
            
            // If package filter is specified, check if review belongs to that package
            if (!"All Packages".equals(packageName)) {
                boolean found = false;
                for (TravelPackage pkg : controller.getAllTravelPackages()) {
                    if (pkg.getName().equals(packageName) && pkg.getReviews().contains(review)) {
                        found = true;
                        break;
                    }
                }
                if (!found) continue;
            }
            
            filteredReviews.add(review);
        }
        
        return filteredReviews;
    }
    
    /**
     * Find a review by its ID.
     * 
     * @param reviewId the ID to search for
     * @return the Review object, or null if not found
     */
    private Review findReviewById(String reviewId) {
        for (Review review : controller.getReviews()) {
            if (review.getReviewId().equals(reviewId)) {
                return review;
            }
        }
        return null;
    }
    
    /**
     * Show dialog to add a new review.
     */
    private void showAddReviewDialog() {
        JDialog dialog = new JDialog(mainWindow, "Add Review", true);
        dialog.setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Create combo boxes for customer and package selection
        ArrayList<Customer> customers = controller.getAllCustomers();
        ArrayList<TravelPackage> packages = controller.getAllTravelPackages();
        
        if (customers.isEmpty() || packages.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "You need to have at least one customer and one travel package to add a review.",
                "Cannot Add Review", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        JComboBox<Customer> customerComboBox = new JComboBox<>(customers.toArray(new Customer[0]));
        JComboBox<TravelPackage> packageComboBox = new JComboBox<>(packages.toArray(new TravelPackage[0]));
        
        // Create rating selection combo box
        Integer[] ratings = {1, 2, 3, 4, 5};
        JComboBox<Integer> ratingComboBox = new JComboBox<>(ratings);
        ratingComboBox.setSelectedIndex(4); // Default to 5 stars
        
        // Create comment text area
        JTextArea commentArea = new JTextArea(5, 20);
        JScrollPane commentScrollPane = new JScrollPane(commentArea);
        
        formPanel.add(new JLabel("Customer:"));
        formPanel.add(customerComboBox);
        formPanel.add(new JLabel("Package:"));
        formPanel.add(packageComboBox);
        formPanel.add(new JLabel("Rating:"));
        formPanel.add(ratingComboBox);
        formPanel.add(new JLabel("Comment:"));
        formPanel.add(commentScrollPane);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        // Set button actions
        cancelButton.addActionListener(e -> dialog.dispose());
        
        saveButton.addActionListener(e -> {
            try {
                // Get the selected values
                Customer customer = (Customer) customerComboBox.getSelectedItem();
                TravelPackage travelPackage = (TravelPackage) packageComboBox.getSelectedItem();
                int rating = (Integer) ratingComboBox.getSelectedItem();
                String comment = commentArea.getText().trim();
                
                if (comment.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, 
                        "Please enter a comment.",
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Create the review
                Review newReview = controller.addReview(travelPackage, customer, rating, comment);
                
                // Store customer and package names before refreshing
                String customerName = customer.getName();
                String packageName = travelPackage.getName();
                
                dialog.dispose();
                refreshData();
                updateStatus("Review added for " + customerName + " on " + packageName);
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "Error adding review: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        dialog.pack();
        dialog.setLocationRelativeTo(mainWindow);
        dialog.setVisible(true);
    }
    
    /**
     * Show dialog to edit the selected review.
     */
    private void showEditReviewDialog() {
        if (selectedReview == null) {
            return;
        }
        
        JDialog dialog = new JDialog(mainWindow, "Edit Review", true);
        dialog.setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Create rating selection combo box
        Integer[] ratings = {1, 2, 3, 4, 5};
        JComboBox<Integer> ratingComboBox = new JComboBox<>(ratings);
        ratingComboBox.setSelectedItem(selectedReview.getRating());
        
        // Create comment text area
        JTextArea commentArea = new JTextArea(5, 20);
        commentArea.setText(selectedReview.getComment());
        JScrollPane commentScrollPane = new JScrollPane(commentArea);
        
        // Display customer name (non-editable)
        JTextField customerField = new JTextField(selectedReview.getCustomer().getName());
        customerField.setEditable(false);
        
        formPanel.add(new JLabel("Customer:"));
        formPanel.add(customerField);
        formPanel.add(new JLabel("Rating:"));
        formPanel.add(ratingComboBox);
        formPanel.add(new JLabel("Comment:"));
        formPanel.add(commentScrollPane);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        // Set button actions
        cancelButton.addActionListener(e -> dialog.dispose());
        
        saveButton.addActionListener(e -> {
            try {
                // Get the selected values
                int rating = (Integer) ratingComboBox.getSelectedItem();
                String comment = commentArea.getText().trim();
                
                if (comment.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, 
                        "Please enter a comment.",
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Update the review
                selectedReview.setRating(rating);
                selectedReview.setComment(comment);
                
                // Store customer name before refreshing
                String customerName = selectedReview.getCustomer().getName();
                
                dialog.dispose();
                refreshData();
                updateStatus("Review updated for " + customerName);
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "Error updating review: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        dialog.pack();
        dialog.setLocationRelativeTo(mainWindow);
        dialog.setVisible(true);
    }
    
    /**
     * Delete the selected review.
     */
    private void deleteSelectedReview() {
        if (selectedReview == null) {
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this review?",
            "Delete Review", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // Store customer name before deleting the review
                String customerName = selectedReview.getCustomer().getName();
                
                // Use the controller to delete the review
                boolean success = controller.deleteReview(selectedReview);
                
                if (success) {
                    refreshData();
                    updateStatus("Review deleted for " + customerName);
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Failed to delete review.",
                        "Delete Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Error deleting review: " + e.getMessage(),
                    "Delete Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * View details of the selected review.
     */
    private void viewReviewDetails() {
        if (selectedReview == null) {
            return;
        }
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        // Find which package this review belongs to
        String packageName = "Unknown";
        for (TravelPackage pkg : controller.getAllTravelPackages()) {
            if (pkg.getReviews().contains(selectedReview)) {
                packageName = pkg.getName();
                break;
            }
        }
        
        StringBuilder details = new StringBuilder();
        details.append("Review ID: ").append(selectedReview.getReviewId()).append("\n\n");
        details.append("Package: ").append(packageName).append("\n\n");
        details.append("Customer: ").append(selectedReview.getCustomer().getName()).append("\n\n");
        details.append("Rating: ").append(selectedReview.getRating()).append(" stars\n\n");
        details.append("Date: ").append(dateFormat.format(selectedReview.getDate())).append("\n\n");
        details.append("Comment:\n").append(selectedReview.getComment());
        
        JTextArea textArea = new JTextArea(details.toString());
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        
        JOptionPane.showMessageDialog(this, scrollPane, 
            "Review Details", JOptionPane.INFORMATION_MESSAGE);
    }
} 