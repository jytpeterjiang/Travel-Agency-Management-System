package tams.view;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.text.NumberFormat;
import java.util.*;
import java.util.List;

import tams.controller.TravelAgencyController;
import tams.model.*;

/**
 * Panel for managing travel packages.
 */
public class TravelPackagesPanel extends BasePanel {
    
    // Table components
    private JTable packagesTable;
    private DefaultTableModel tableModel;
    
    // Filter components
    private JTextField nameField;
    private JTextField destinationField;
    private JSpinner minPriceSpinner;
    private JSpinner maxPriceSpinner;
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
     * Constructor for the travel packages panel.
     * 
     * @param controller the controller
     * @param mainWindow the main window
     */
    public TravelPackagesPanel(TravelAgencyController controller, MainWindow mainWindow) {
        super(controller, mainWindow);
        isInitialized = true;
    }
    
    @Override
    protected void createFilterPanel() {
        filterPanel = new JPanel();
        filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.Y_AXIS));
        filterPanel.setBorder(BorderFactory.createTitledBorder("Search & Filters"));
        filterPanel.setPreferredSize(new Dimension(250, 0));
        
        // Name filter
        JPanel namePanel = new JPanel(new BorderLayout(5, 5));
        namePanel.setBorder(BorderFactory.createTitledBorder("Package Name"));
        nameField = new JTextField(20);
        namePanel.add(nameField, BorderLayout.CENTER);
        
        // Destination filter
        JPanel destPanel = new JPanel(new BorderLayout(5, 5));
        destPanel.setBorder(BorderFactory.createTitledBorder("Destination"));
        destinationField = new JTextField(20);
        destPanel.add(destinationField, BorderLayout.CENTER);
        
        // Price range filter
        JPanel pricePanel = new JPanel(new GridLayout(2, 2, 5, 5));
        pricePanel.setBorder(BorderFactory.createTitledBorder("Price Range"));
        
        SpinnerNumberModel minModel = new SpinnerNumberModel(0.0, 0.0, 10000.0, 100.0);
        SpinnerNumberModel maxModel = new SpinnerNumberModel(5000.0, 0.0, 10000.0, 100.0);
        
        minPriceSpinner = new JSpinner(minModel);
        maxPriceSpinner = new JSpinner(maxModel);
        
        JSpinner.NumberEditor minEditor = new JSpinner.NumberEditor(minPriceSpinner, "$#,##0.00");
        JSpinner.NumberEditor maxEditor = new JSpinner.NumberEditor(maxPriceSpinner, "$#,##0.00");
        
        minPriceSpinner.setEditor(minEditor);
        maxPriceSpinner.setEditor(maxEditor);
        
        pricePanel.add(new JLabel("Min Price:"));
        pricePanel.add(minPriceSpinner);
        pricePanel.add(new JLabel("Max Price:"));
        pricePanel.add(maxPriceSpinner);
        
        // Search and clear buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        searchButton = new JButton("Search");
        clearButton = new JButton("Clear");
        
        searchButton.addActionListener(e -> applyFilters());
        clearButton.addActionListener(e -> clearFilters());
        
        buttonPanel.add(searchButton);
        buttonPanel.add(clearButton);
        
        // Add all sections to the filter panel
        filterPanel.add(namePanel);
        filterPanel.add(Box.createVerticalStrut(10));
        filterPanel.add(destPanel);
        filterPanel.add(Box.createVerticalStrut(10));
        filterPanel.add(pricePanel);
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
        String[] columnNames = {"ID", "Name", "Destination", "Duration", "Price", "Bookings"};
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
        packagesTable.getColumnModel().getColumn(5).setPreferredWidth(80);   // Bookings
        
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
        viewButton.setEnabled(hasSelection);
        
        // Only enable delete button if the package is not in use
        if (hasSelection) {
            boolean canDelete = !controller.isPackageInUse(selectedPackage);
            deleteButton.setEnabled(canDelete);
        } else {
            deleteButton.setEnabled(false);
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
                // Get booking count for this package
                int bookingCount = controller.getBookingsForPackage(pkg).size();
                
                Object[] row = {
                    pkg.getServiceId(),
                    pkg.getName(),
                    pkg.getDestination(),
                    pkg.getDuration() + " days",
                    String.format("$%.2f", pkg.getPrice()),
                    bookingCount
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
        nameField.setText("");
        destinationField.setText("");
        minPriceSpinner.setValue(0.0);
        maxPriceSpinner.setValue(5000.0);
        
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
        String nameFilter = nameField.getText().trim().toLowerCase();
        String destFilter = destinationField.getText().trim().toLowerCase();
        double minPrice = (Double) minPriceSpinner.getValue();
        double maxPrice = (Double) maxPriceSpinner.getValue();
        
        // Apply filters
        for (TravelPackage pkg : allPackages) {
            boolean includePackage = true;
            
            // Filter by name
            if (!nameFilter.isEmpty() && 
                !pkg.getName().toLowerCase().contains(nameFilter)) {
                includePackage = false;
            }
            
            // Filter by destination
            if (!destFilter.isEmpty() && 
                !pkg.getDestination().toLowerCase().contains(destFilter)) {
                includePackage = false;
            }
            
            // Filter by price range
            if (pkg.getPrice() < minPrice || pkg.getPrice() > maxPrice) {
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
     * Show dialog to add a new travel package.
     */
    private void showAddPackageDialog() {
        JDialog dialog = new JDialog(mainWindow, "Add New Package", true);
        dialog.setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JTextField nameField = new JTextField(20);
        JTextArea descriptionArea = new JTextArea(3, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        
        JTextField destinationField = new JTextField(20);
        
        SpinnerNumberModel durationModel = new SpinnerNumberModel(7, 1, 30, 1);
        JSpinner durationSpinner = new JSpinner(durationModel);
        
        SpinnerNumberModel priceModel = new SpinnerNumberModel(1000.0, 100.0, 10000.0, 100.0);
        JSpinner priceSpinner = new JSpinner(priceModel);
        JSpinner.NumberEditor priceEditor = new JSpinner.NumberEditor(priceSpinner, "$#,##0.00");
        priceSpinner.setEditor(priceEditor);
        
        JTextField accommodationField = new JTextField(20);
        
        formPanel.add(new JLabel("Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Description:"));
        formPanel.add(descScroll);
        formPanel.add(new JLabel("Destination:"));
        formPanel.add(destinationField);
        formPanel.add(new JLabel("Duration (days):"));
        formPanel.add(durationSpinner);
        formPanel.add(new JLabel("Price:"));
        formPanel.add(priceSpinner);
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
                String description = descriptionArea.getText().trim();
                String destination = destinationField.getText().trim();
                int duration = (Integer) durationSpinner.getValue();
                double price = (Double) priceSpinner.getValue();
                String accommodation = accommodationField.getText().trim();
                
                if (name.isEmpty() || description.isEmpty() || destination.isEmpty() || accommodation.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, 
                        "All fields are required.",
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Create the package
                TravelPackage newPackage = controller.createTravelPackage(
                    name, description, price, destination, duration, accommodation);
                
                dialog.dispose();
                refreshData();
                updateStatus("Package created: " + newPackage.getName());
                
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
     * Show dialog to edit the selected travel package.
     */
    private void showEditPackageDialog() {
        if (selectedPackage == null) {
            return;
        }
        
        JDialog dialog = new JDialog(mainWindow, "Edit Package", true);
        dialog.setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JTextField nameField = new JTextField(selectedPackage.getName(), 20);
        
        JTextArea descriptionArea = new JTextArea(selectedPackage.getDescription(), 3, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        
        JTextField destinationField = new JTextField(selectedPackage.getDestination(), 20);
        
        SpinnerNumberModel durationModel = new SpinnerNumberModel(
            selectedPackage.getDuration(), 1, 30, 1);
        JSpinner durationSpinner = new JSpinner(durationModel);
        
        SpinnerNumberModel priceModel = new SpinnerNumberModel(
            selectedPackage.getPrice(), 100.0, 10000.0, 100.0);
        JSpinner priceSpinner = new JSpinner(priceModel);
        JSpinner.NumberEditor priceEditor = new JSpinner.NumberEditor(priceSpinner, "$#,##0.00");
        priceSpinner.setEditor(priceEditor);
        
        JTextField accommodationField = new JTextField(selectedPackage.getAccommodation(), 20);
        
        formPanel.add(new JLabel("Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Description:"));
        formPanel.add(descScroll);
        formPanel.add(new JLabel("Destination:"));
        formPanel.add(destinationField);
        formPanel.add(new JLabel("Duration (days):"));
        formPanel.add(durationSpinner);
        formPanel.add(new JLabel("Price:"));
        formPanel.add(priceSpinner);
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
                String description = descriptionArea.getText().trim();
                String destination = destinationField.getText().trim();
                int duration = (Integer) durationSpinner.getValue();
                double price = (Double) priceSpinner.getValue();
                String accommodation = accommodationField.getText().trim();
                
                if (name.isEmpty() || description.isEmpty() || destination.isEmpty() || accommodation.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, 
                        "All fields are required.",
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Update the package
                controller.updateTravelPackage(
                    selectedPackage,
                    name,
                    description,
                    price,
                    destination,
                    duration,
                    accommodation
                );
                
                dialog.dispose();
                refreshData();
                updateStatus("Package updated: " + selectedPackage.getName());
                
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
     * Delete the selected travel package.
     */
    private void deleteSelectedPackage() {
        if (selectedPackage == null) {
            return;
        }
        
        // Check if the package is in use
        if (controller.isPackageInUse(selectedPackage)) {
            JOptionPane.showMessageDialog(this, 
                "Cannot delete package '" + selectedPackage.getName() + "' because it is used in existing bookings.",
                "Cannot Delete Package", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this package?\n" + 
            "Name: " + selectedPackage.getName() + "\n" +
            "Destination: " + selectedPackage.getDestination(),
            "Delete Package", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // Delete the package
                boolean success = controller.deleteTravelPackage(selectedPackage);
                
                if (success) {
                    refreshData();
                    updateStatus("Package deleted");
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Failed to delete package.",
                        "Delete Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Error deleting package: " + e.getMessage(),
                    "Delete Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * View details of the selected travel package.
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
        details.append("Accommodation: ").append(selectedPackage.getAccommodation()).append("\n\n");
        details.append("Price: $").append(String.format("%.2f", selectedPackage.getPrice())).append("\n\n");
        details.append("Description:\n").append(selectedPackage.getDescription()).append("\n\n");
        
        // Get bookings for this package
        List<Booking> bookings = controller.getBookingsForPackage(selectedPackage);
        details.append("Bookings: ").append(bookings.size()).append("\n");
        
        if (!bookings.isEmpty()) {
            details.append("\nBooked by:\n");
            for (Booking booking : bookings) {
                details.append("- ").append(booking.getCustomer().getName())
                      .append(" (").append(booking.getStatus().getDisplayName()).append(")\n");
            }
        }
        
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