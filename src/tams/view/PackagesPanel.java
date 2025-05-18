package tams.view;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Vector;

import tams.controller.TravelAgencyController;
import tams.model.*;

/**
 * Panel for managing travel packages.
 */
public class PackagesPanel extends BasePanel {
    
    // Table components
    private JTable packagesTable;
    private DefaultTableModel tableModel;
    
    // Filter components
    private JTextField destinationField;
    private JSlider priceRangeSlider;
    private JTextField minDaysField;
    private JTextField maxDaysField;
    private JButton searchButton;
    private JButton clearButton;
    
    // Action buttons
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton viewButton;
    private JButton manageItineraryButton;
    private JButton manageActivitiesButton;
    
    // Currently selected package
    private TravelPackage selectedPackage;
    private boolean isInitialized = false;
    
    /**
     * Constructor for the packages panel.
     * 
     * @param controller the controller
     * @param mainWindow the main window
     */
    public PackagesPanel(TravelAgencyController controller, MainWindow mainWindow) {
        super(controller, mainWindow);
        isInitialized = true;
    }
    
    @Override
    protected void createFilterPanel() {
        filterPanel = new JPanel();
        filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.Y_AXIS));
        filterPanel.setBorder(BorderFactory.createTitledBorder("Search & Filters"));
        filterPanel.setPreferredSize(new Dimension(250, 0));
        
        // Destination filter
        JPanel destinationPanel = new JPanel(new BorderLayout(5, 5));
        destinationPanel.setBorder(BorderFactory.createTitledBorder("Destination"));
        destinationField = new JTextField(20);
        destinationPanel.add(destinationField, BorderLayout.CENTER);
        
        // Price range filter
        JPanel pricePanel = new JPanel(new BorderLayout(5, 5));
        pricePanel.setBorder(BorderFactory.createTitledBorder("Price Range"));
        priceRangeSlider = new JSlider(JSlider.HORIZONTAL, 0, 5000, 5000);
        priceRangeSlider.setMajorTickSpacing(1000);
        priceRangeSlider.setMinorTickSpacing(500);
        priceRangeSlider.setPaintTicks(true);
        priceRangeSlider.setPaintLabels(true);
        JLabel priceLabel = new JLabel("Max Price: $5000");
        priceRangeSlider.addChangeListener(e -> {
            priceLabel.setText("Max Price: $" + priceRangeSlider.getValue());
        });
        pricePanel.add(priceRangeSlider, BorderLayout.CENTER);
        pricePanel.add(priceLabel, BorderLayout.SOUTH);
        
        // Duration filter
        JPanel durationPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        durationPanel.setBorder(BorderFactory.createTitledBorder("Duration (Days)"));
        durationPanel.add(new JLabel("Min: "));
        minDaysField = new JTextField("1", 5);
        durationPanel.add(minDaysField);
        durationPanel.add(new JLabel("Max: "));
        maxDaysField = new JTextField("30", 5);
        durationPanel.add(maxDaysField);
        
        // Search and clear buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        searchButton = new JButton("Search");
        clearButton = new JButton("Clear");
        
        searchButton.addActionListener(e -> applyFilters());
        clearButton.addActionListener(e -> clearFilters());
        
        buttonPanel.add(searchButton);
        buttonPanel.add(clearButton);
        
        // Add all sections to the filter panel
        filterPanel.add(destinationPanel);
        filterPanel.add(Box.createVerticalStrut(10));
        filterPanel.add(pricePanel);
        filterPanel.add(Box.createVerticalStrut(10));
        filterPanel.add(durationPanel);
        filterPanel.add(Box.createVerticalStrut(10));
        filterPanel.add(buttonPanel);
        filterPanel.add(Box.createVerticalGlue());
    }
    
    @Override
    protected void createContentPanel() {
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        // Create the table header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        headerPanel.add(new JLabel("Travel Packages", JLabel.LEFT), BorderLayout.WEST);
        
        // Create the table
        String[] columnNames = {"ID", "Name", "Destination", "Duration", "Price"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table cells non-editable
            }
        };
        
        packagesTable = new JTable(tableModel);
        packagesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        packagesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = packagesTable.getSelectedRow();
                if (selectedRow != -1) {
                    String packageId = (String) tableModel.getValueAt(selectedRow, 0);
                    selectedPackage = findPackageById(packageId);
                    updateButtonStates();
                }
            }
        });
        
        // Set column widths
        packagesTable.getColumnModel().getColumn(0).setPreferredWidth(100);  // ID
        packagesTable.getColumnModel().getColumn(1).setPreferredWidth(200);  // Name
        packagesTable.getColumnModel().getColumn(2).setPreferredWidth(150);  // Destination
        packagesTable.getColumnModel().getColumn(3).setPreferredWidth(80);   // Duration
        packagesTable.getColumnModel().getColumn(4).setPreferredWidth(100);  // Price
        
        // Add table to a scroll pane
        JScrollPane scrollPane = createScrollPane(packagesTable);
        
        // Add components to content panel
        contentPanel.add(headerPanel, BorderLayout.NORTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
    }
    
    @Override
    protected void createButtonPanel() {
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        
        addButton = createButton("Add");
        editButton = createButton("Edit");
        deleteButton = createButton("Delete");
        viewButton = createButton("View");
        manageItineraryButton = createButton("Manage Itinerary");
        manageActivitiesButton = createButton("Manage Activities");
        
        // Button action listeners
        addButton.addActionListener(e -> showAddPackageDialog());
        editButton.addActionListener(e -> showEditPackageDialog());
        deleteButton.addActionListener(e -> deleteSelectedPackage());
        viewButton.addActionListener(e -> viewPackageDetails());
        manageItineraryButton.addActionListener(e -> showItineraryDialog());
        manageActivitiesButton.addActionListener(e -> showManageActivitiesDialog());
        
        // Initially disable buttons that require selection
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
        viewButton.setEnabled(false);
        manageItineraryButton.setEnabled(false);
        manageActivitiesButton.setEnabled(false);
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(viewButton);
        buttonPanel.add(manageActivitiesButton);
        buttonPanel.add(manageItineraryButton);
    }
    
    /**
     * Update the enabled state of buttons based on selection.
     */
    private void updateButtonStates() {
        boolean hasSelection = (selectedPackage != null);
        
        editButton.setEnabled(hasSelection);
        deleteButton.setEnabled(hasSelection);
        viewButton.setEnabled(hasSelection);
        manageItineraryButton.setEnabled(hasSelection);
        
        // Find and update the manage activities button
        for (Component comp : buttonPanel.getComponents()) {
            if (comp instanceof JButton && ((JButton) comp).getText().equals("Manage Activities")) {
                comp.setEnabled(hasSelection);
                break;
            }
        }
    }
    
    /**
     * Refresh the table with current package data.
     */
    @Override
    public void refreshData() {
        if (isInitialized && mainWindow != null) {
            // Clear the table
            tableModel.setRowCount(0);
            
            // Get packages filtered by current filter settings
            ArrayList<TravelPackage> packages = getFilteredPackages();
            
            // Add packages to the table
            for (TravelPackage pkg : packages) {
                Object[] row = {
                    pkg.getServiceId(),
                    pkg.getName(),
                    pkg.getDestination(),
                    pkg.getDuration() + " days",
                    String.format("$%.2f", pkg.getBasePrice())
                };
                tableModel.addRow(row);
            }
            
            // Update status
            updateStatus("Packages loaded: " + packages.size());
            
            // Clear selection
            selectedPackage = null;
            updateButtonStates();
        }
    }
    
    /**
     * Apply the current filters and refresh the data.
     */
    private void applyFilters() {
        refreshData();
    }
    
    /**
     * Clear all filters and refresh the data.
     */
    private void clearFilters() {
        destinationField.setText("");
        priceRangeSlider.setValue(5000);
        minDaysField.setText("1");
        maxDaysField.setText("30");
        
        refreshData();
    }
    
    /**
     * Get packages filtered by the current filter settings.
     * 
     * @return filtered list of packages
     */
    private ArrayList<TravelPackage> getFilteredPackages() {
        ArrayList<TravelPackage> allPackages = controller.getAllTravelPackages();
        ArrayList<TravelPackage> filteredPackages = new ArrayList<>();
        
        // Get filter values
        String destination = destinationField.getText().trim().toLowerCase();
        int maxPrice = priceRangeSlider.getValue();
        
        int minDays;
        try {
            minDays = Integer.parseInt(minDaysField.getText().trim());
        } catch (NumberFormatException e) {
            minDays = 1;
        }
        
        int maxDays;
        try {
            maxDays = Integer.parseInt(maxDaysField.getText().trim());
        } catch (NumberFormatException e) {
            maxDays = 30;
        }
        
        // Apply filters
        for (TravelPackage pkg : allPackages) {
            boolean includePackage = true;
            
            // Filter by destination
            if (!destination.isEmpty() && 
                !pkg.getDestination().toLowerCase().contains(destination)) {
                includePackage = false;
            }
            
            // Filter by price
            if (pkg.getBasePrice() > maxPrice) {
                includePackage = false;
            }
            
            // Filter by duration
            if (pkg.getDuration() < minDays || pkg.getDuration() > maxDays) {
                includePackage = false;
            }
            
            if (includePackage) {
                filteredPackages.add(pkg);
            }
        }
        
        return filteredPackages;
    }
    
    /**
     * Find a package by its ID.
     * 
     * @param packageId the package ID to find
     * @return the found TravelPackage or null
     */
    private TravelPackage findPackageById(String packageId) {
        for (TravelPackage pkg : controller.getAllTravelPackages()) {
            if (pkg.getServiceId().equals(packageId)) {
                return pkg;
            }
        }
        return null;
    }
    
    /**
     * Show dialog to add a new package.
     */
    private void showAddPackageDialog() {
        // Create dialog components
        JDialog dialog = new JDialog(mainWindow, "Add New Package", true);
        dialog.setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JTextField nameField = new JTextField(20);
        JTextField destinationField = new JTextField(20);
        JTextField descriptionField = new JTextField(20);
        JTextField priceField = new JTextField(20);
        JTextField durationField = new JTextField(20);
        JTextField accommodationField = new JTextField(20);
        
        formPanel.add(new JLabel("Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Destination:"));
        formPanel.add(destinationField);
        formPanel.add(new JLabel("Description:"));
        formPanel.add(descriptionField);
        formPanel.add(new JLabel("Price ($):"));
        formPanel.add(priceField);
        formPanel.add(new JLabel("Duration (days):"));
        formPanel.add(durationField);
        formPanel.add(new JLabel("Accommodation:"));
        formPanel.add(accommodationField);
        
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
                // Validate inputs
                String name = nameField.getText().trim();
                String destination = destinationField.getText().trim();
                String description = descriptionField.getText().trim();
                String accommodation = accommodationField.getText().trim();
                
                if (name.isEmpty() || destination.isEmpty() || description.isEmpty() || accommodation.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, 
                        "All fields are required.",
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                double price;
                try {
                    price = Double.parseDouble(priceField.getText().trim());
                    if (price <= 0) {
                        throw new NumberFormatException();
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, 
                        "Price must be a positive number.",
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                int duration;
                try {
                    duration = Integer.parseInt(durationField.getText().trim());
                    if (duration <= 0) {
                        throw new NumberFormatException();
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, 
                        "Duration must be a positive integer.",
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Create the package
                TravelPackage newPackage = controller.createTravelPackage(
                    name, description, price, destination, duration, accommodation);
                
                dialog.dispose();
                refreshData();
                updateStatus("Package added: " + newPackage.getName());
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "Error creating package: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        dialog.pack();
        dialog.setLocationRelativeTo(mainWindow);
        dialog.setVisible(true);
    }
    
    /**
     * Show dialog to edit the selected package.
     */
    private void showEditPackageDialog() {
        if (selectedPackage == null) {
            return;
        }
        
        // Create dialog components
        JDialog dialog = new JDialog(mainWindow, "Edit Package", true);
        dialog.setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JTextField nameField = new JTextField(selectedPackage.getName(), 20);
        JTextField destinationField = new JTextField(selectedPackage.getDestination(), 20);
        JTextField descriptionField = new JTextField(selectedPackage.getDescription(), 20);
        JTextField priceField = new JTextField(String.valueOf(selectedPackage.getBasePrice()), 20);
        JTextField durationField = new JTextField(String.valueOf(selectedPackage.getDuration()), 20);
        JTextField accommodationField = new JTextField(selectedPackage.getAccommodation(), 20);
        
        formPanel.add(new JLabel("Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Destination:"));
        formPanel.add(destinationField);
        formPanel.add(new JLabel("Description:"));
        formPanel.add(descriptionField);
        formPanel.add(new JLabel("Price ($):"));
        formPanel.add(priceField);
        formPanel.add(new JLabel("Duration (days):"));
        formPanel.add(durationField);
        formPanel.add(new JLabel("Accommodation:"));
        formPanel.add(accommodationField);
        
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
                // Validate inputs
                String name = nameField.getText().trim();
                String destination = destinationField.getText().trim();
                String description = descriptionField.getText().trim();
                String accommodation = accommodationField.getText().trim();
                
                if (name.isEmpty() || destination.isEmpty() || description.isEmpty() || accommodation.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, 
                        "All fields are required.",
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                double price;
                try {
                    price = Double.parseDouble(priceField.getText().trim());
                    if (price <= 0) {
                        throw new NumberFormatException();
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, 
                        "Price must be a positive number.",
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                int duration;
                try {
                    duration = Integer.parseInt(durationField.getText().trim());
                    if (duration <= 0) {
                        throw new NumberFormatException();
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, 
                        "Duration must be a positive integer.",
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Update the package
                selectedPackage.setName(name);
                selectedPackage.setDescription(description);
                selectedPackage.setBasePrice(price);
                selectedPackage.setDestination(destination);
                selectedPackage.setDuration(duration);
                selectedPackage.setAccommodation(accommodation);
                
                // Save data
                controller.saveData();
                
                // Store the package ID before refreshing
                String packageId = selectedPackage.getServiceId();
                String packageName = selectedPackage.getName();
                
                dialog.dispose();
                refreshData();
                
                // Find and reselect the edited package
                TravelPackage editedPackage = findPackageById(packageId);
                if (editedPackage != null) {
                    selectedPackage = editedPackage;
                    // Find the row index of the edited package
                    for (int i = 0; i < tableModel.getRowCount(); i++) {
                        if (tableModel.getValueAt(i, 0).equals(packageId)) {
                            packagesTable.setRowSelectionInterval(i, i);
                            break;
                        }
                    }
                }
                
                updateButtonStates();
                updateStatus("Package updated: " + packageName);
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "Error updating package: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        dialog.pack();
        dialog.setLocationRelativeTo(mainWindow);
        dialog.setVisible(true);
    }
    
    /**
     * Delete the selected package.
     */
    private void deleteSelectedPackage() {
        if (selectedPackage == null) {
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this package?\n" + selectedPackage.getName(),
            "Delete Package", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // Check if there are any bookings for this package
                boolean hasBookings = false;
                for (Booking booking : controller.getAllBookings()) {
                    if (booking.getService().equals(selectedPackage)) {
                        hasBookings = true;
                        break;
                    }
                }
                
                if (hasBookings) {
                    JOptionPane.showMessageDialog(this, 
                        "Cannot delete package because it has active bookings.",
                        "Delete Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Store package name before deletion
                String packageName = selectedPackage.getName();
                
                // Delete the package from the controller
                controller.deletePackage(selectedPackage);
                
                refreshData();
                updateStatus("Package deleted: " + packageName);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Error deleting package: " + e.getMessage(),
                    "Delete Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * View details of the selected package.
     */
    private void viewPackageDetails() {
        if (selectedPackage == null) {
            return;
        }
        
        StringBuilder details = new StringBuilder();
        details.append("Package ID: ").append(selectedPackage.getServiceId()).append("\n\n");
        details.append("Name: ").append(selectedPackage.getName()).append("\n\n");
        details.append("Destination: ").append(selectedPackage.getDestination()).append("\n\n");
        details.append("Duration: ").append(selectedPackage.getDuration()).append(" days\n\n");
        details.append("Price: $").append(String.format("%.2f", selectedPackage.getPrice())).append("\n\n");
        details.append("Accommodation: ").append(selectedPackage.getAccommodation()).append("\n\n");
        details.append("Description: ").append(selectedPackage.getDescription()).append("\n\n");
        
        // Display activities
        details.append("Activities:\n");
        if (selectedPackage.getActivities().isEmpty()) {
            details.append(" - No activities added yet\n\n");
        } else {
            for (Activity activity : selectedPackage.getActivities()) {
                details.append(" - ").append(activity.getName()).append(" (")
                      .append(activity.getLocation()).append(") - $")
                      .append(String.format("%.2f", activity.getCost())).append("\n");
            }
            details.append("\n");
        }
        
        // Display itinerary
        details.append("Itinerary:\n");
        Itinerary itinerary = selectedPackage.getItinerary();
        if (itinerary == null || itinerary.getDays().isEmpty()) {
            details.append(" - No itinerary details added yet\n");
        } else {
            for (ItineraryDay day : itinerary.getDays()) {
                details.append("Day ").append(day.getDayNumber()).append(":\n");
                details.append("  Description: ").append(day.getNotes()).append("\n");
                details.append("  Activities:\n");
                
                if (day.getActivities().isEmpty()) {
                    details.append("   - No activities scheduled\n");
                } else {
                    for (Activity activity : day.getActivities()) {
                        details.append("   - ").append(activity.getName())
                              .append(" (").append(activity.getDuration()).append(" hours)\n");
                    }
                }
                details.append("\n");
            }
        }
        
        // Display reviews if available
        if (!selectedPackage.getReviews().isEmpty()) {
            details.append("Reviews (Average Rating: ")
                  .append(String.format("%.1f", selectedPackage.getAverageRating()))
                  .append("/5):\n");
            
            for (Review review : selectedPackage.getReviews()) {
                details.append(" - ").append(review.getRating()).append("/5 from ")
                      .append(review.getCustomer().getName()).append(": ")
                      .append(review.getComment()).append("\n");
            }
        }
        
        JTextArea textArea = new JTextArea(details.toString());
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(600, 500));
        
        JOptionPane.showMessageDialog(this, scrollPane,
            "Package Details", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Show the itinerary management dialog for the selected package.
     */
    private void showItineraryDialog() {
        if (selectedPackage == null) {
            JOptionPane.showMessageDialog(this, 
                "No package selected. Please select a package first.",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Store a reference to the selected package that won't change
        final TravelPackage packageRef = selectedPackage;
        
        // Debug: Print information about available activities
        ArrayList<Activity> availableActivities = controller.getActivities();
        System.out.println("Available activities from controller: " + availableActivities.size());
        for (Activity activity : availableActivities) {
            System.out.println(" - " + activity.getName() + " (" + activity.getActivityId() + ")");
        }
        
        // Create the dialog
        JDialog dialog = new JDialog(mainWindow, "Manage Itinerary: " + packageRef.getName(), true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(800, 600);
        dialog.setLocationRelativeTo(mainWindow);
        
        // Get the current itinerary from the selected package
        Itinerary itinerary = packageRef.getItinerary();
        
        // Make a final reference to the itinerary for use in lambda expressions
        final Itinerary finalItinerary = itinerary;
        
        // Create a list to display the days
        DefaultListModel<ItineraryDay> dayListModel = new DefaultListModel<>();
        for (ItineraryDay day : finalItinerary.getDays()) {
            dayListModel.addElement(day);
        }
        
        JList<ItineraryDay> dayList = new JList<>(dayListModel);
        dayList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, 
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof ItineraryDay) {
                    ItineraryDay day = (ItineraryDay) value;
                    setText("Day " + day.getDayNumber() + ": " + day.getNotes());
                }
                return this;
            }
        });
        
        // Create scroll pane for the list
        JScrollPane listScrollPane = new JScrollPane(dayList);
        listScrollPane.setPreferredSize(new Dimension(250, 0));
        
        // Create a panel for day details
        JPanel dayDetailsPanel = new JPanel(new BorderLayout());
        dayDetailsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Create the form for day details
        JPanel formPanel = new JPanel(new GridLayout(0, 1, 5, 10));
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Day Details"),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        
        // Day number field
        JPanel dayNumberPanel = new JPanel(new BorderLayout(5, 0));
        dayNumberPanel.add(new JLabel("Day Number:"), BorderLayout.WEST);
        JTextField dayNumberField = new JTextField(10);
        dayNumberPanel.add(dayNumberField, BorderLayout.CENTER);
        
        // Description field
        JPanel descriptionPanel = new JPanel(new BorderLayout(5, 0));
        descriptionPanel.add(new JLabel("Description:"), BorderLayout.NORTH);
        JTextArea descriptionField = new JTextArea(3, 30);
        descriptionField.setLineWrap(true);
        descriptionField.setWrapStyleWord(true);
        JScrollPane descScrollPane = new JScrollPane(descriptionField);
        descriptionPanel.add(descScrollPane, BorderLayout.CENTER);
        
        // Activities list
        JPanel activitiesPanel = new JPanel(new BorderLayout(5, 0));
        activitiesPanel.add(new JLabel("Activities:"), BorderLayout.NORTH);
        
        // Available activities
        DefaultListModel<Activity> availableActivitiesModel = new DefaultListModel<>();
        
        // Get activities associated with the selected package instead of all activities
        ArrayList<Activity> packageActivities = packageRef.getActivities();
        System.out.println("Adding " + packageActivities.size() + " package activities to the list");
        
        for (Activity activity : packageActivities) {
            availableActivitiesModel.addElement(activity);
        }
        
        JList<Activity> availableActivitiesList = new JList<>(availableActivitiesModel);
        availableActivitiesList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, 
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Activity) {
                    Activity activity = (Activity) value;
                    setText(activity.getName() + " - " + activity.getLocation() + " (" + activity.getDuration() + " hrs)");
                }
                return this;
            }
        });
        
        // Selected activities for the day
        DefaultListModel<Activity> selectedActivitiesModel = new DefaultListModel<>();
        JList<Activity> selectedActivitiesList = new JList<>(selectedActivitiesModel);
        selectedActivitiesList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, 
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Activity) {
                    Activity activity = (Activity) value;
                    setText(activity.getName() + " - " + activity.getDuration() + " hrs, $" + activity.getCost());
                }
                return this;
            }
        });
        
        // Activity selection buttons
        JButton addActivityButton = new JButton(">>");
        JButton removeActivityButton = new JButton("<<");
        
        JPanel activityButtonsPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        activityButtonsPanel.add(addActivityButton);
        activityButtonsPanel.add(removeActivityButton);
        
        JPanel availableActivitiesPanel = new JPanel(new BorderLayout());
        availableActivitiesPanel.setBorder(BorderFactory.createTitledBorder("Available Activities"));
        availableActivitiesPanel.add(new JScrollPane(availableActivitiesList), BorderLayout.CENTER);
        
        JPanel selectedActivitiesPanel = new JPanel(new BorderLayout());
        selectedActivitiesPanel.setBorder(BorderFactory.createTitledBorder("Activities for this Day"));
        selectedActivitiesPanel.add(new JScrollPane(selectedActivitiesList), BorderLayout.CENTER);
        
        JPanel activitiesSelectionPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        activitiesSelectionPanel.add(availableActivitiesPanel);
        activitiesSelectionPanel.add(activityButtonsPanel);
        activitiesSelectionPanel.add(selectedActivitiesPanel);
        
        activitiesPanel.add(activitiesSelectionPanel, BorderLayout.CENTER);
        
        // Add all components to the form
        formPanel.add(dayNumberPanel);
        formPanel.add(descriptionPanel);
        formPanel.add(activitiesPanel);
        
        dayDetailsPanel.add(formPanel, BorderLayout.CENTER);
        
        // Day action buttons
        JPanel dayButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save Day");
        JButton clearButton = new JButton("Clear Form");
        dayButtonsPanel.add(clearButton);
        dayButtonsPanel.add(saveButton);
        
        dayDetailsPanel.add(dayButtonsPanel, BorderLayout.SOUTH);
        
        // Create a split pane with the list and details
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listScrollPane, dayDetailsPanel);
        splitPane.setResizeWeight(0.3);
        
        // Create top buttons for the dialog
        JPanel topButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addDayButton = new JButton("Add New Day");
        JButton removeDayButton = new JButton("Remove Day");
        topButtonPanel.add(addDayButton);
        topButtonPanel.add(removeDayButton);
        
        // Create bottom buttons for the dialog
        JPanel bottomButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton doneButton = new JButton("Done");
        bottomButtonPanel.add(doneButton);
        
        // Add components to the dialog
        dialog.add(topButtonPanel, BorderLayout.NORTH);
        dialog.add(splitPane, BorderLayout.CENTER);
        dialog.add(bottomButtonPanel, BorderLayout.SOUTH);
        
        // Event handling for activity selection
        addActivityButton.addActionListener(e -> {
            int[] indices = availableActivitiesList.getSelectedIndices();
            if (indices.length > 0) {
                for (int i = 0; i < indices.length; i++) {
                    Activity selected = availableActivitiesModel.getElementAt(indices[i]);
                    // Check if it's already in the selected list before adding
                    boolean alreadyAdded = false;
                    for (int j = 0; j < selectedActivitiesModel.size(); j++) {
                        Activity existing = selectedActivitiesModel.getElementAt(j);
                        if (existing.getActivityId().equals(selected.getActivityId())) {
                            alreadyAdded = true;
                            break;
                        }
                    }
                    if (!alreadyAdded) {
                        selectedActivitiesModel.addElement(selected);
                    }
                }
                // Auto-save if a day is selected
                if (dayList.getSelectedIndex() != -1) {
                    saveButton.doClick();
                }
            }
        });
        
        removeActivityButton.addActionListener(e -> {
            int[] indices = selectedActivitiesList.getSelectedIndices();
            if (indices.length > 0) {
                // Remove in reverse order to avoid index shifting problems
                for (int i = indices.length - 1; i >= 0; i--) {
                    selectedActivitiesModel.remove(indices[i]);
                }
                
                // Auto-save if a day is selected
                if (dayList.getSelectedIndex() != -1) {
                    saveButton.doClick();
                }
            }
        });
        
        // Event handling for list selection
        dayList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                ItineraryDay selectedDay = dayList.getSelectedValue();
                if (selectedDay != null) {
                    dayNumberField.setText(String.valueOf(selectedDay.getDayNumber()));
                    descriptionField.setText(selectedDay.getNotes());
                    
                    // Update selected activities
                    selectedActivitiesModel.clear();
                    for (Activity activity : selectedDay.getActivities()) {
                        selectedActivitiesModel.addElement(activity);
                    }
                }
            }
        });
        
        // Event handling for clear button
        clearButton.addActionListener(e -> {
            dayNumberField.setText("");
            descriptionField.setText("");
            selectedActivitiesModel.clear();
            dayList.clearSelection();
        });
        
        // Event handling for add day button
        addDayButton.addActionListener(e -> {
            clearButton.doClick();
            
            // Suggest the next day number
            int nextDayNumber = finalItinerary.getDays().size() + 1;
            dayNumberField.setText(String.valueOf(nextDayNumber));
            
            dayList.clearSelection();
        });
        
        // Event handling for remove day button
        removeDayButton.addActionListener(e -> {
            int selectedIndex = dayList.getSelectedIndex();
            if (selectedIndex != -1) {
                ItineraryDay dayToRemove = dayListModel.getElementAt(selectedIndex);
                if (JOptionPane.showConfirmDialog(dialog,
                    "Are you sure you want to remove Day " + dayToRemove.getDayNumber() + "?",
                    "Confirm Removal", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    
                    // Remove the day from the itinerary using the removeDay method
                    finalItinerary.removeDay(dayToRemove);
                    
                    // Remove from list model
                    dayListModel.remove(selectedIndex);
                    
                    // Save the data after removal
                    try {
                        controller.saveData();
                    } catch (Exception ex) {
                        System.err.println("Error saving data after day removal: " + ex.getMessage());
                        ex.printStackTrace();
                    }
                    
                    dayList.repaint();
                    clearButton.doClick();
                }
            }
        });
        
        // Event handling for save button
        saveButton.addActionListener(e -> {
            try {
                // Validate form
                String dayNumberStr = dayNumberField.getText().trim();
                if (dayNumberStr.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, 
                        "Please enter a day number.",
                        "Form Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                int dayNumber = Integer.parseInt(dayNumberStr);
                if (dayNumber < 1) {
                    JOptionPane.showMessageDialog(dialog, 
                        "Day number must be a positive integer.",
                        "Form Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                String description = descriptionField.getText().trim();
                if (description.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, 
                        "Please enter a description for the day.",
                        "Form Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Check if we're updating an existing day or creating a new one
                ItineraryDay existingDay = null;
                int existingIndex = -1;
                
                // Look for a day with the same number
                for (int i = 0; i < dayListModel.size(); i++) {
                    ItineraryDay dayInList = dayListModel.getElementAt(i);
                    if (dayInList.getDayNumber() == dayNumber) {
                        existingDay = dayInList;
                        existingIndex = i;
                        break;
                    }
                }
                
                try {
                    // Get a reference to the actual days list in the itinerary for direct modification
                    ArrayList<ItineraryDay> daysInItinerary = finalItinerary.getModifiableDays();
                    
                    // If we're updating an existing day, remove it first
                    if (existingDay != null) {
                        // Remove old day from itinerary
                        finalItinerary.removeDay(existingDay);
                    }
                    
                    // Create a new day with the description (notes)
                    ItineraryDay newDay = new ItineraryDay(dayNumber, description);
                    
                    // Add all selected activities to the new day
                    for (int i = 0; i < selectedActivitiesModel.size(); i++) {
                        Activity activity = selectedActivitiesModel.getElementAt(i);
                        newDay.addActivity(activity);
                    }
                    
                    // Add the new day directly to the itinerary
                    // The addDay method will handle sorting by day number
                    finalItinerary.addDay(newDay);
                    
                    // Update UI list
                    if (existingIndex != -1) {
                        // Replace existing day in list model
                        dayListModel.set(existingIndex, newDay);
                    } else {
                        // Add as a new day to the list model
                        dayListModel.addElement(newDay);
                        // Sort the day list according to day number
                        Vector<ItineraryDay> days = new Vector<>();
                        for (int i = 0; i < dayListModel.size(); i++) {
                            days.add(dayListModel.get(i));
                        }
                        days.sort((day1, day2) -> Integer.compare(day1.getDayNumber(), day2.getDayNumber()));
                        
                        dayListModel.clear();
                        for (ItineraryDay day : days) {
                            dayListModel.addElement(day);
                        }
                    }
                    
                    // Save the data
                    controller.saveData();
                    
                    // Select the newly added day
                    for (int i = 0; i < dayListModel.size(); i++) {
                        if (dayListModel.get(i).getDayNumber() == dayNumber) {
                            dayList.setSelectedIndex(i);
                            break;
                        }
                    }
                    
                    JOptionPane.showMessageDialog(dialog, 
                        "Day " + dayNumber + " has been saved!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    System.err.println("Error saving day: " + ex.getMessage());
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(dialog, 
                        "Error saving day: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "Please enter a valid number for the day number.",
                    "Form Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                System.err.println("Unexpected error: " + ex.getMessage());
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog, 
                    "Unexpected error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        // Event handling for done button
        doneButton.addActionListener(e -> {
            try {
                // Make sure that any pending changes are saved before closing
                if (dayList.getSelectedValue() != null) {
                    int response = JOptionPane.showConfirmDialog(dialog,
                        "Do you want to save your changes to the current day?",
                        "Save Changes", JOptionPane.YES_NO_CANCEL_OPTION);
                    
                    if (response == JOptionPane.YES_OPTION) {
                        saveButton.doClick();
                    } else if (response == JOptionPane.CANCEL_OPTION) {
                        return;
                    }
                }
                
                // Make sure to save all data including itinerary changes
                controller.saveData();
                
                dialog.dispose();
                refreshData();
                updateStatus("Itinerary updated for " + packageRef.getName());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "Error saving itinerary: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace(); // Print stack trace for debugging
            }
        });
        
        dialog.setVisible(true);
    }
    
    /**
     * Show a dialog to manage the activities associated with a package.
     * This allows adding/removing activities from the package independent of the itinerary.
     */
    private void showManageActivitiesDialog() {
        if (selectedPackage == null) {
            JOptionPane.showMessageDialog(this, 
                "No package selected. Please select a package first.",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Create the dialog
        JDialog dialog = new JDialog(mainWindow, "Manage Activities: " + selectedPackage.getName(), true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(700, 500);
        dialog.setLocationRelativeTo(mainWindow);
        
        // Create panel with table and list views
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Package info panel
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        infoPanel.add(new JLabel("Package: " + selectedPackage.getName()));
        infoPanel.add(new JLabel(" | Destination: " + selectedPackage.getDestination()));
        infoPanel.add(new JLabel(" | Duration: " + selectedPackage.getDuration() + " days"));
        mainPanel.add(infoPanel, BorderLayout.NORTH);
        
        // Create two list panels
        JPanel listsPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        
        // Available activities panel
        JPanel availablePanel = new JPanel(new BorderLayout());
        availablePanel.setBorder(BorderFactory.createTitledBorder("Available Activities"));
        
        // Get all activities from controller
        ArrayList<Activity> allActivities = controller.getActivities();
        
        // Create model for available activities (activities not in package)
        DefaultListModel<Activity> availableActivitiesModel = new DefaultListModel<>();
        // Get activities already in the package
        ArrayList<Activity> packageActivities = selectedPackage.getActivities();
        
        // Add activities that aren't in the package to available list
        for (Activity activity : allActivities) {
            boolean isInPackage = false;
            for (Activity packageActivity : packageActivities) {
                if (packageActivity.getActivityId().equals(activity.getActivityId())) {
                    isInPackage = true;
                    break;
                }
            }
            if (!isInPackage) {
                availableActivitiesModel.addElement(activity);
            }
        }
        
        JList<Activity> availableActivitiesList = new JList<>(availableActivitiesModel);
        availableActivitiesList.setCellRenderer(new ActivityListCellRenderer());
        JScrollPane availableScrollPane = new JScrollPane(availableActivitiesList);
        availablePanel.add(availableScrollPane, BorderLayout.CENTER);
        
        // Package activities panel
        JPanel packagePanel = new JPanel(new BorderLayout());
        packagePanel.setBorder(BorderFactory.createTitledBorder("Activities in this Package"));
        
        // Create model for package activities
        DefaultListModel<Activity> packageActivitiesModel = new DefaultListModel<>();
        for (Activity activity : packageActivities) {
            packageActivitiesModel.addElement(activity);
        }
        
        JList<Activity> packageActivitiesList = new JList<>(packageActivitiesModel);
        packageActivitiesList.setCellRenderer(new ActivityListCellRenderer());
        JScrollPane packageScrollPane = new JScrollPane(packageActivitiesList);
        packagePanel.add(packageScrollPane, BorderLayout.CENTER);
        
        // Add/Remove buttons panel
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(20, 5, 20, 5));
        
        JButton addToPackageButton = new JButton(">>");
        addToPackageButton.setToolTipText("Add selected activities to package");
        addToPackageButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        addToPackageButton.setMaximumSize(new Dimension(50, 30));
        
        JButton removeFromPackageButton = new JButton("<<");
        removeFromPackageButton.setToolTipText("Remove selected activities from package");
        removeFromPackageButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        removeFromPackageButton.setMaximumSize(new Dimension(50, 30));
        
        buttonsPanel.add(Box.createVerticalGlue());
        buttonsPanel.add(addToPackageButton);
        buttonsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonsPanel.add(removeFromPackageButton);
        buttonsPanel.add(Box.createVerticalGlue());
        
        // Action panel with the two lists and transfer buttons
        JPanel actionPanel = new JPanel(new BorderLayout());
        actionPanel.add(availablePanel, BorderLayout.WEST);
        actionPanel.add(buttonsPanel, BorderLayout.CENTER);
        actionPanel.add(packagePanel, BorderLayout.EAST);
        
        mainPanel.add(actionPanel, BorderLayout.CENTER);
        
        // Bottom button panel
        JPanel bottomButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton doneButton = new JButton("Done");
        bottomButtonPanel.add(doneButton);
        
        // Add panels to dialog
        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.add(bottomButtonPanel, BorderLayout.SOUTH);
        
        // Add button action - move activities from available to package
        addToPackageButton.addActionListener(e -> {
            int[] indices = availableActivitiesList.getSelectedIndices();
            if (indices.length > 0) {
                for (int i = indices.length - 1; i >= 0; i--) {
                    Activity activity = availableActivitiesModel.get(indices[i]);
                    // Add to package
                    selectedPackage.addActivity(activity);
                    // Update models
                    packageActivitiesModel.addElement(activity);
                    availableActivitiesModel.remove(indices[i]);
                }
                // Save changes
                controller.saveData();
                updateStatus("Activities added to package: " + selectedPackage.getName());
            }
        });
        
        // Remove button action - move activities from package back to available
        removeFromPackageButton.addActionListener(e -> {
            int[] indices = packageActivitiesList.getSelectedIndices();
            if (indices.length > 0) {
                // Check if any selected activities are used in itinerary
                boolean itineraryConflict = false;
                StringBuilder conflictingActivities = new StringBuilder();
                
                // Get the itinerary to check if activities are used
                Itinerary itinerary = selectedPackage.getItinerary();
                
                // Create a list to hold activities to remove
                ArrayList<Activity> activitiesToRemove = new ArrayList<>();
                
                // First pass: check which activities can be removed
                for (int i = 0; i < indices.length; i++) {
                    Activity activity = packageActivitiesModel.getElementAt(indices[i]);
                    
                    // Check if the activity is used in any itinerary day
                    boolean isUsed = false;
                    if (itinerary != null) {
                        for (ItineraryDay day : itinerary.getDays()) {
                            if (day.getActivities().contains(activity)) {
                                isUsed = true;
                                itineraryConflict = true;
                                conflictingActivities.append("\n- ").append(activity.getName())
                                    .append(" (used in Day ").append(day.getDayNumber()).append(")");
                                break;
                            }
                        }
                    }
                    
                    if (!isUsed) {
                        activitiesToRemove.add(activity);
                    }
                }
                
                // Second pass: remove activities that are not in the itinerary
                if (!activitiesToRemove.isEmpty()) {
                    // Use the controller to remove activities
                    for (Activity activity : activitiesToRemove) {
                        // Get the original list of activities from the package
                        ArrayList<Activity> currentPackageActivities = selectedPackage.getActivities();
                        
                        // Find and remove the activity with the matching ID
                        for (int i = 0; i < currentPackageActivities.size(); i++) {
                            if (currentPackageActivities.get(i).getActivityId().equals(activity.getActivityId())) {
                                // Use controller method to remove the activity
                                controller.removeActivityFromPackage(selectedPackage, currentPackageActivities.get(i));
                                break;
                            }
                        }
                        
                        // Add to available model
                        availableActivitiesModel.addElement(activity);
                    }
                    
                    // Update the package list model
                    packageActivitiesModel.clear();
                    for (Activity activity : selectedPackage.getActivities()) {
                        packageActivitiesModel.addElement(activity);
                    }
                    
                    // Save changes
                    controller.saveData();
                }
                
                // Show warning if there were conflicts
                if (itineraryConflict) {
                    JOptionPane.showMessageDialog(dialog,
                        "Some activities could not be removed because they are used in the itinerary:" +
                        conflictingActivities.toString() + 
                        "\n\nPlease remove them from the itinerary first.",
                        "Itinerary Conflict",
                        JOptionPane.WARNING_MESSAGE);
                } else {
                    updateStatus("Activities removed from package: " + selectedPackage.getName());
                }
            }
        });
        
        // Done button action
        doneButton.addActionListener(e -> {
            dialog.dispose();
            // Refresh the package data
            refreshData();
        });
        
        // Show the dialog
        dialog.setVisible(true);
    }
    
    /**
     * Custom cell renderer for activities.
     */
    private class ActivityListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, 
                int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Activity) {
                Activity activity = (Activity) value;
                setText(activity.getName() + " - " + activity.getLocation() + 
                      " (" + activity.getDuration() + " hrs, $" + 
                      String.format("%.2f", activity.getCost()) + ")");
            }
            return this;
        }
    }
} 