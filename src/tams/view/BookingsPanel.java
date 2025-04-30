package tams.view;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

import tams.controller.TravelAgencyController;
import tams.exceptions.*;
import tams.model.*;

/**
 * Panel for managing travel bookings.
 */
public class BookingsPanel extends BasePanel {
    
    // Table components
    private JTable bookingsTable;
    private DefaultTableModel tableModel;
    
    // Filter components
    private JTextField customerField;
    private JTextField packageField;
    private JComboBox<BookingStatus> statusComboBox;
    private JButton searchButton;
    private JButton clearButton;
    
    // Action buttons
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton viewButton;
    private JButton updateStatusButton;
    private JButton processPaymentButton;
    
    // Date formatter
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    
    // Currently selected booking
    private Booking selectedBooking;
    private boolean isInitialized = false;
    
    /**
     * Constructor for the bookings panel.
     * 
     * @param controller the controller
     * @param mainWindow the main window
     */
    public BookingsPanel(TravelAgencyController controller, MainWindow mainWindow) {
        super(controller, mainWindow);
        isInitialized = true;
    }
    
    @Override
    protected void createFilterPanel() {
        filterPanel = new JPanel();
        filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.Y_AXIS));
        filterPanel.setBorder(BorderFactory.createTitledBorder("Search & Filters"));
        filterPanel.setPreferredSize(new Dimension(250, 0));
        
        // Customer filter
        JPanel customerPanel = new JPanel(new BorderLayout(5, 5));
        customerPanel.setBorder(BorderFactory.createTitledBorder("Customer"));
        customerField = new JTextField(20);
        customerPanel.add(customerField, BorderLayout.CENTER);
        
        // Package filter
        JPanel packagePanel = new JPanel(new BorderLayout(5, 5));
        packagePanel.setBorder(BorderFactory.createTitledBorder("Travel Package"));
        packageField = new JTextField(20);
        packagePanel.add(packageField, BorderLayout.CENTER);
        
        // Status filter
        JPanel statusPanel = new JPanel(new BorderLayout(5, 5));
        statusPanel.setBorder(BorderFactory.createTitledBorder("Status"));
        statusComboBox = new JComboBox<>();
        statusComboBox.addItem(null); // Add "All" option
        for (BookingStatus status : BookingStatus.values()) {
            statusComboBox.addItem(status);
        }
        statusComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, 
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value == null) {
                    setText("All Statuses");
                } else {
                    setText(((BookingStatus) value).getDisplayName());
                }
                return this;
            }
        });
        statusPanel.add(statusComboBox, BorderLayout.CENTER);
        
        // Search and clear buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        searchButton = new JButton("Search");
        clearButton = new JButton("Clear");
        
        searchButton.addActionListener(e -> applyFilters());
        clearButton.addActionListener(e -> clearFilters());
        
        buttonPanel.add(searchButton);
        buttonPanel.add(clearButton);
        
        // Add all sections to the filter panel
        filterPanel.add(customerPanel);
        filterPanel.add(Box.createVerticalStrut(10));
        filterPanel.add(packagePanel);
        filterPanel.add(Box.createVerticalStrut(10));
        filterPanel.add(statusPanel);
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
        headerPanel.add(new JLabel("Bookings", JLabel.LEFT), BorderLayout.WEST);
        
        // Create the table
        String[] columnNames = {"ID", "Customer", "Package", "Status", "Booking Date", "Price"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table cells non-editable
            }
        };
        
        bookingsTable = new JTable(tableModel);
        bookingsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bookingsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = bookingsTable.getSelectedRow();
                if (selectedRow != -1) {
                    String bookingId = (String) tableModel.getValueAt(selectedRow, 0);
                    selectedBooking = findBookingById(bookingId);
                    updateButtonStates();
                }
            }
        });
        
        // Set column widths
        bookingsTable.getColumnModel().getColumn(0).setPreferredWidth(80);   // ID
        bookingsTable.getColumnModel().getColumn(1).setPreferredWidth(150);  // Customer
        bookingsTable.getColumnModel().getColumn(2).setPreferredWidth(200);  // Package
        bookingsTable.getColumnModel().getColumn(3).setPreferredWidth(100);  // Status
        bookingsTable.getColumnModel().getColumn(4).setPreferredWidth(120);  // Booking Date
        bookingsTable.getColumnModel().getColumn(5).setPreferredWidth(100);  // Price
        
        // Add table to a scroll pane
        JScrollPane scrollPane = createScrollPane(bookingsTable);
        
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
        updateStatusButton = createButton("Update Status");
        processPaymentButton = createButton("Process Payment");
        
        // Button action listeners
        addButton.addActionListener(e -> showAddBookingDialog());
        editButton.addActionListener(e -> showEditBookingDialog());
        deleteButton.addActionListener(e -> deleteSelectedBooking());
        viewButton.addActionListener(e -> viewBookingDetails());
        updateStatusButton.addActionListener(e -> showUpdateStatusDialog());
        processPaymentButton.addActionListener(e -> showProcessPaymentDialog());
        
        // Initially disable buttons that require selection
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
        viewButton.setEnabled(false);
        updateStatusButton.setEnabled(false);
        processPaymentButton.setEnabled(false);
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(viewButton);
        buttonPanel.add(updateStatusButton);
        buttonPanel.add(processPaymentButton);
    }
    
    /**
     * Update the enabled state of buttons based on selection.
     */
    private void updateButtonStates() {
        boolean hasSelection = selectedBooking != null;
        
        editButton.setEnabled(hasSelection);
        deleteButton.setEnabled(hasSelection);
        viewButton.setEnabled(hasSelection);
        updateStatusButton.setEnabled(hasSelection);
        
        // Only enable payment processing for bookings that are PENDING
        processPaymentButton.setEnabled(hasSelection && 
            selectedBooking.getStatus() == BookingStatus.PENDING);
    }
    
    /**
     * Refresh the table with current booking data.
     */
    @Override
    public void refreshData() {
        if (isInitialized && mainWindow != null) {
            // Clear the table
            tableModel.setRowCount(0);
            
            // Get bookings filtered by current filter settings
            ArrayList<Booking> bookings = getFilteredBookings();
            
            // Add bookings to the table
            for (Booking booking : bookings) {
                Customer customer = booking.getCustomer();
                TravelPackage pkg = booking.getPackage();
                
                Object[] row = {
                    booking.getBookingId(),
                    customer.getName(),
                    pkg.getName(),
                    booking.getStatus().getDisplayName(),
                    dateFormat.format(booking.getBookingDate()),
                    String.format("$%.2f", booking.getTotalPrice())
                };
                tableModel.addRow(row);
            }
            
            // Update status
            updateStatus("Bookings loaded: " + bookings.size());
            
            // Clear selection
            selectedBooking = null;
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
        customerField.setText("");
        packageField.setText("");
        statusComboBox.setSelectedIndex(0);
        
        refreshData();
    }
    
    /**
     * Get bookings filtered by the current filter settings.
     * 
     * @return filtered list of bookings
     */
    private ArrayList<Booking> getFilteredBookings() {
        ArrayList<Booking> allBookings = controller.getAllBookings();
        ArrayList<Booking> filteredBookings = new ArrayList<>();
        
        // Get filter values
        String customerFilter = customerField.getText().trim().toLowerCase();
        String packageFilter = packageField.getText().trim().toLowerCase();
        BookingStatus statusFilter = (BookingStatus) statusComboBox.getSelectedItem();
        
        // Apply filters
        for (Booking booking : allBookings) {
            boolean includeBooking = true;
            
            // Filter by customer
            if (!customerFilter.isEmpty() && 
                !booking.getCustomer().getName().toLowerCase().contains(customerFilter)) {
                includeBooking = false;
            }
            
            // Filter by package
            if (!packageFilter.isEmpty() && 
                !booking.getPackage().getName().toLowerCase().contains(packageFilter)) {
                includeBooking = false;
            }
            
            // Filter by status
            if (statusFilter != null && booking.getStatus() != statusFilter) {
                includeBooking = false;
            }
            
            if (includeBooking) {
                filteredBookings.add(booking);
            }
        }
        
        return filteredBookings;
    }
    
    /**
     * Find a booking by its ID.
     * 
     * @param bookingId the booking ID to find
     * @return the found Booking or null
     */
    private Booking findBookingById(String bookingId) {
        for (Booking booking : controller.getAllBookings()) {
            if (booking.getBookingId().equals(bookingId)) {
                return booking;
            }
        }
        return null;
    }
    
    /**
     * Show dialog to add a new booking.
     */
    private void showAddBookingDialog() {
        // Get available customers and packages
        ArrayList<Customer> customers = controller.getAllCustomers();
        ArrayList<TravelPackage> packages = controller.getAllTravelPackages();
        
        if (customers.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No customers available. Please add a customer first.",
                "Add Booking", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (packages.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No travel packages available. Please add a travel package first.",
                "Add Booking", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        JDialog dialog = new JDialog(mainWindow, "Add New Booking", true);
        dialog.setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Create combo boxes for customer and package selection
        JComboBox<Customer> customerComboBox = new JComboBox<>(customers.toArray(new Customer[0]));
        customerComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, 
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value != null) {
                    setText(((Customer) value).getName());
                }
                return this;
            }
        });
        
        JComboBox<TravelPackage> packageComboBox = new JComboBox<>(packages.toArray(new TravelPackage[0]));
        packageComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, 
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value != null) {
                    TravelPackage pkg = (TravelPackage) value;
                    setText(pkg.getName() + " - $" + pkg.getPrice());
                }
                return this;
            }
        });
        
        // Number of travelers
        JSpinner travelersSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 20, 1));
        
        // Status selection
        JComboBox<BookingStatus> statusComboBox = new JComboBox<>(
            new BookingStatus[]{BookingStatus.CONFIRMED, BookingStatus.PENDING});
        statusComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, 
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value != null) {
                    setText(((BookingStatus) value).getDisplayName());
                }
                return this;
            }
        });
        
        // Special requests
        JTextField specialRequestsField = new JTextField(20);
        
        // Add components to form
        formPanel.add(new JLabel("Customer:"));
        formPanel.add(customerComboBox);
        formPanel.add(new JLabel("Travel Package:"));
        formPanel.add(packageComboBox);
        formPanel.add(new JLabel("Number of Travelers:"));
        formPanel.add(travelersSpinner);
        formPanel.add(new JLabel("Status:"));
        formPanel.add(statusComboBox);
        formPanel.add(new JLabel("Special Requests:"));
        formPanel.add(specialRequestsField);
        
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
                // Get selected values
                Customer customer = (Customer) customerComboBox.getSelectedItem();
                TravelPackage travelPackage = (TravelPackage) packageComboBox.getSelectedItem();
                int numTravelers = (int) travelersSpinner.getValue();
                BookingStatus status = (BookingStatus) statusComboBox.getSelectedItem();
                String specialRequests = specialRequestsField.getText().trim();
                
                if (customer == null || travelPackage == null) {
                    JOptionPane.showMessageDialog(dialog, 
                        "Please select a customer and travel package.",
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Create the booking
                Booking newBooking = controller.createBooking(
                    customer,
                    travelPackage,
                    numTravelers,
                    status,
                    specialRequests
                );
                
                dialog.dispose();
                refreshData();
                updateStatus("Booking created for " + customer.getName());
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "Error creating booking: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        dialog.pack();
        dialog.setLocationRelativeTo(mainWindow);
        dialog.setVisible(true);
    }
    
    /**
     * Show dialog to edit the selected booking.
     */
    private void showEditBookingDialog() {
        if (selectedBooking == null) {
            return;
        }
        
        // Check if the booking can be edited
        if (selectedBooking.getStatus() == BookingStatus.COMPLETED ||
            selectedBooking.getStatus() == BookingStatus.CANCELLED) {
            JOptionPane.showMessageDialog(this, 
                "Cannot edit a " + selectedBooking.getStatus().getDisplayName().toLowerCase() + " booking.",
                "Edit Booking", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        JDialog dialog = new JDialog(mainWindow, "Edit Booking", true);
        dialog.setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Number of travelers - Handle safely to prevent IllegalArgumentException
        int currentTravelers = 1; // Default value
        try {
            int numTravelers = selectedBooking.getNumTravelers();
            // Ensure we have a valid value (at least 1)
            currentTravelers = Math.max(1, numTravelers);
        } catch (Exception e) {
            // If there's any error retrieving the number of travelers, use default
            System.out.println("Warning: Error getting number of travelers, using default: " + e.getMessage());
        }
        
        // Set maximum travelers to a safe large value
        int maxTravelers = Math.max(100, currentTravelers);
        
        // Create spinner with safe values
        JSpinner travelersSpinner = new JSpinner(
            new SpinnerNumberModel(currentTravelers, 1, maxTravelers, 1));
        
        // Status selection - Include all possible statuses to prevent issues
        JComboBox<BookingStatus> statusComboBox = new JComboBox<>(BookingStatus.values());
        
        // Set the selected status safely
        try {
            BookingStatus currentStatus = selectedBooking.getStatus();
            if (currentStatus != null) {
                statusComboBox.setSelectedItem(currentStatus);
            } else {
                statusComboBox.setSelectedItem(BookingStatus.PENDING); // Default to PENDING if null
            }
        } catch (Exception e) {
            // If there's any error setting the status, select PENDING as default
            statusComboBox.setSelectedItem(BookingStatus.PENDING);
            System.out.println("Warning: Error setting booking status, using default: " + e.getMessage());
        }
        
        // Add renderer for status display
        statusComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, 
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value != null) {
                    setText(((BookingStatus) value).getDisplayName());
                }
                return this;
            }
        });
        
        // Special requests - Handle safely
        String specialRequests = "";
        try {
            String requests = selectedBooking.getSpecialRequests();
            if (requests != null) {
                specialRequests = requests;
            }
        } catch (Exception e) {
            System.out.println("Warning: Error getting special requests: " + e.getMessage());
        }
        JTextField specialRequestsField = new JTextField(specialRequests, 20);
        
        // Add components to form
        formPanel.add(new JLabel("Number of Travelers:"));
        formPanel.add(travelersSpinner);
        formPanel.add(new JLabel("Status:"));
        formPanel.add(statusComboBox);
        formPanel.add(new JLabel("Special Requests:"));
        formPanel.add(specialRequestsField);
        
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
                // Get values
                int numTravelers = (int) travelersSpinner.getValue();
                BookingStatus status = (BookingStatus) statusComboBox.getSelectedItem();
                String requestsText = specialRequestsField.getText().trim();
                
                // Update the booking
                controller.updateBooking(
                    selectedBooking,
                    numTravelers,
                    status,
                    requestsText
                );
                
                // Store booking details before refreshing
                String customerName = selectedBooking.getCustomer().getName();
                String packageName = selectedBooking.getPackage().getName();
                
                dialog.dispose();
                refreshData();
                updateStatus("Booking updated for " + customerName + " - " + packageName);
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "Error updating booking: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        dialog.pack();
        dialog.setLocationRelativeTo(mainWindow);
        dialog.setVisible(true);
    }
    
    /**
     * Delete the selected booking.
     */
    private void deleteSelectedBooking() {
        if (selectedBooking == null) {
            return;
        }
        
        // Check if the booking can be deleted
        if (selectedBooking.getStatus() == BookingStatus.COMPLETED ||
            selectedBooking.getStatus() == BookingStatus.CANCELLED) {
            JOptionPane.showMessageDialog(this, 
                "Cannot delete a " + selectedBooking.getStatus().getDisplayName().toLowerCase() + " booking.",
                "Delete Booking", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this booking?\n" +
            "Customer: " + selectedBooking.getCustomer().getName() + "\n" +
            "Package: " + selectedBooking.getPackage().getName() + "\n" +
            "Date: " + dateFormat.format(selectedBooking.getBookingDate()),
            "Delete Booking", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // Delete the booking
                boolean success = controller.deleteBooking(selectedBooking);
                
                if (success) {
                    String customerName = selectedBooking.getCustomer().getName();
                    String packageName = selectedBooking.getPackage().getName();
                    refreshData();
                    updateStatus("Booking deleted for " + customerName + " - " + packageName);
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Failed to delete booking.",
                        "Delete Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Error deleting booking: " + e.getMessage(),
                    "Delete Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * View details of the selected booking.
     */
    private void viewBookingDetails() {
        if (selectedBooking == null) {
            return;
        }
        
        Customer customer = selectedBooking.getCustomer();
        TravelPackage pkg = selectedBooking.getPackage();
        
        StringBuilder details = new StringBuilder();
        details.append("Booking ID: ").append(selectedBooking.getBookingId()).append("\n\n");
        details.append("Status: ").append(selectedBooking.getStatus().getDisplayName()).append("\n\n");
        details.append("Booking Date: ").append(dateFormat.format(selectedBooking.getBookingDate())).append("\n\n");
        
        details.append("Customer Information:\n")
              .append("  Name: ").append(customer.getName()).append("\n")
              .append("  Email: ").append(customer.getEmail()).append("\n")
              .append("  Phone: ").append(customer.getPhone()).append("\n\n");
        
        details.append("Package Information:\n")
              .append("  Name: ").append(pkg.getName()).append("\n")
              .append("  Destination: ").append(pkg.getDestination()).append("\n")
              .append("  Duration: ").append(pkg.getDuration()).append(" days\n")
              .append("  Base Price: $").append(String.format("%.2f", pkg.getPrice())).append("\n\n");
        
        details.append("Booking Details:\n")
              .append("  Number of Travelers: ").append(selectedBooking.getNumTravelers()).append("\n")
              .append("  Total Price: $").append(String.format("%.2f", selectedBooking.getTotalPrice())).append("\n");
        
        // Add payment details if available
        if (selectedBooking.getPayment() != null) {
            Payment payment = selectedBooking.getPayment();
            details.append("\nPayment Information:\n")
                  .append("  Payment ID: ").append(payment.getPaymentId()).append("\n")
                  .append("  Amount: $").append(String.format("%.2f", payment.getAmount())).append("\n")
                  .append("  Method: ").append(payment.getMethod()).append("\n")
                  .append("  Status: ").append(payment.getStatus()).append("\n")
                  .append("  Date: ").append(dateFormat.format(payment.getPaymentDate())).append("\n");
        }
        
        if (!selectedBooking.getSpecialRequests().isEmpty()) {
            details.append("\nSpecial Requests:\n")
                  .append(selectedBooking.getSpecialRequests()).append("\n");
        }
        
        JTextArea textArea = new JTextArea(details.toString());
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));
        
        JOptionPane.showMessageDialog(this, scrollPane,
            "Booking Details", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Show dialog to update the status of the selected booking.
     */
    private void showUpdateStatusDialog() {
        if (selectedBooking == null) {
            return;
        }
        
        JDialog dialog = new JDialog(mainWindow, "Update Booking Status", true);
        dialog.setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel(new BorderLayout(10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Current booking info
        JPanel infoPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        infoPanel.setBorder(BorderFactory.createTitledBorder("Booking Information"));
        infoPanel.add(new JLabel("Customer: " + selectedBooking.getCustomer().getName()));
        infoPanel.add(new JLabel("Package: " + selectedBooking.getPackage().getName()));
        infoPanel.add(new JLabel("Current Status: " + selectedBooking.getStatus().getDisplayName()));
        
        // Status selection
        JPanel statusPanel = new JPanel(new BorderLayout(5, 5));
        statusPanel.setBorder(BorderFactory.createTitledBorder("New Status"));
        
        JComboBox<BookingStatus> statusComboBox = new JComboBox<>(BookingStatus.values());
        statusComboBox.setSelectedItem(selectedBooking.getStatus());
        statusComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, 
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value != null) {
                    setText(((BookingStatus) value).getDisplayName());
                }
                return this;
            }
        });
        
        statusPanel.add(statusComboBox, BorderLayout.CENTER);
        
        formPanel.add(infoPanel, BorderLayout.NORTH);
        formPanel.add(statusPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton updateButton = new JButton("Update");
        JButton cancelButton = new JButton("Cancel");
        
        buttonPanel.add(updateButton);
        buttonPanel.add(cancelButton);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        // Set button actions
        cancelButton.addActionListener(e -> dialog.dispose());
        
        updateButton.addActionListener(e -> {
            try {
                BookingStatus newStatus = (BookingStatus) statusComboBox.getSelectedItem();
                
                if (newStatus == selectedBooking.getStatus()) {
                    dialog.dispose();
                    return;
                }
                
                // Update the booking status
                controller.updateBookingStatus(selectedBooking, newStatus);
                
                // Store information before refreshing
                String statusName = newStatus.getDisplayName();
                String customerName = selectedBooking.getCustomer().getName();
                
                dialog.dispose();
                refreshData();
                updateStatus("Booking status for " + customerName + " updated to " + statusName);
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "Error updating status: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        dialog.pack();
        dialog.setLocationRelativeTo(mainWindow);
        dialog.setVisible(true);
    }
    
    /**
     * Show dialog to process payment for the selected booking.
     */
    private void showProcessPaymentDialog() {
        if (selectedBooking == null) {
            return;
        }
        
        // Check if payment already processed
        if (selectedBooking.getPayment() != null && 
            selectedBooking.getPayment().getStatus() == PaymentStatus.COMPLETED) {
            JOptionPane.showMessageDialog(this,
                "Payment has already been processed for this booking.",
                "Payment Information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Check if the booking status is appropriate
        if (selectedBooking.getStatus() != BookingStatus.PENDING) {
            JOptionPane.showMessageDialog(this,
                "Payment can only be processed for pending bookings.",
                "Payment Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        JDialog dialog = new JDialog(mainWindow, "Process Payment", true);
        dialog.setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel(new BorderLayout(10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Booking info panel
        JPanel infoPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        infoPanel.setBorder(BorderFactory.createTitledBorder("Booking Information"));
        infoPanel.add(new JLabel("Booking ID: " + selectedBooking.getBookingId()));
        infoPanel.add(new JLabel("Customer: " + selectedBooking.getCustomer().getName()));
        infoPanel.add(new JLabel("Package: " + selectedBooking.getPackage().getName()));
        infoPanel.add(new JLabel("Total Amount: $" + String.format("%.2f", selectedBooking.getTotalPrice())));
        
        // Payment form
        JPanel paymentPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        paymentPanel.setBorder(BorderFactory.createTitledBorder("Payment Details"));
        
        // Payment amount
        JPanel amountPanel = new JPanel(new BorderLayout(5, 5));
        amountPanel.add(new JLabel("Amount ($): "), BorderLayout.WEST);
        JTextField amountField = new JTextField(String.format("%.2f", selectedBooking.getTotalPrice()));
        amountPanel.add(amountField, BorderLayout.CENTER);
        
        // Payment method
        JPanel methodPanel = new JPanel(new BorderLayout(5, 5));
        methodPanel.add(new JLabel("Payment Method: "), BorderLayout.WEST);
        JComboBox<PaymentMethod> methodComboBox = new JComboBox<>(PaymentMethod.values());
        methodComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, 
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value != null) {
                    setText(value.toString());
                }
                return this;
            }
        });
        methodPanel.add(methodComboBox, BorderLayout.CENTER);
        
        paymentPanel.add(amountPanel);
        paymentPanel.add(methodPanel);
        
        formPanel.add(infoPanel, BorderLayout.NORTH);
        formPanel.add(paymentPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton processButton = new JButton("Process Payment");
        JButton cancelButton = new JButton("Cancel");
        
        buttonPanel.add(processButton);
        buttonPanel.add(cancelButton);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        // Set button actions
        cancelButton.addActionListener(e -> dialog.dispose());
        
        processButton.addActionListener(e -> {
            try {
                double amount = Double.parseDouble(amountField.getText());
                PaymentMethod method = (PaymentMethod) methodComboBox.getSelectedItem();
                
                if (amount <= 0) {
                    JOptionPane.showMessageDialog(dialog,
                        "Payment amount must be greater than zero.",
                        "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                boolean success = controller.processPayment(selectedBooking, amount, method);
                
                if (success) {
                    String customerName = selectedBooking.getCustomer().getName();
                    dialog.dispose();
                    refreshData();
                    updateStatus("Payment processed successfully for " + customerName);
                } else {
                    JOptionPane.showMessageDialog(dialog,
                        "Payment processing failed.",
                        "Payment Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Please enter a valid payment amount.",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (PaymentProcessException ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Payment processing error: " + ex.getMessage(),
                    "Payment Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        dialog.pack();
        dialog.setLocationRelativeTo(mainWindow);
        dialog.setVisible(true);
    }
} 