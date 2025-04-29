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
        
        // Button action listeners
        addButton.addActionListener(e -> showAddPackageDialog());
        editButton.addActionListener(e -> showEditPackageDialog());
        deleteButton.addActionListener(e -> deleteSelectedPackage());
        viewButton.addActionListener(e -> viewPackageDetails());
        
        // Initially disable buttons that require selection
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
        viewButton.setEnabled(false);
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(viewButton);
    }
    
    /**
     * Update the enabled state of buttons based on selection.
     */
    private void updateButtonStates() {
        boolean hasSelection = (selectedPackage != null);
        
        editButton.setEnabled(hasSelection);
        deleteButton.setEnabled(hasSelection);
        viewButton.setEnabled(hasSelection);
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
        details.append("Package ID: ").append(selectedPackage.getServiceId()).append("\n");
        details.append("Name: ").append(selectedPackage.getName()).append("\n");
        details.append("Destination: ").append(selectedPackage.getDestination()).append("\n");
        details.append("Description: ").append(selectedPackage.getDescription()).append("\n");
        details.append("Base Price: $").append(String.format("%.2f", selectedPackage.getBasePrice())).append("\n");
        details.append("Duration: ").append(selectedPackage.getDuration()).append(" days\n");
        details.append("Accommodation: ").append(selectedPackage.getAccommodation()).append("\n\n");
        
        // Show activities
        details.append("Activities:\n");
        if (selectedPackage.getActivities().isEmpty()) {
            details.append("- No activities added yet\n");
        } else {
            for (Activity activity : selectedPackage.getActivities()) {
                details.append("- ").append(activity.getName())
                      .append(" (").append(activity.getLocation()).append(")")
                      .append(" - ").append(activity.getDuration()).append(" hours")
                      .append(" - $").append(String.format("%.2f", activity.getCost()))
                      .append("\n");
            }
        }
        
        details.append("\nTotal Price: $").append(String.format("%.2f", selectedPackage.calculateTotalPrice()));
        
        JTextArea textArea = new JTextArea(details.toString());
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 400));
        
        JOptionPane.showMessageDialog(this, scrollPane,
            "Package Details", JOptionPane.INFORMATION_MESSAGE);
    }
} 