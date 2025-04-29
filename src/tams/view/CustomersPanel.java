package tams.view;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

import tams.controller.TravelAgencyController;
import tams.model.*;

/**
 * Panel for managing customer information.
 */
public class CustomersPanel extends BasePanel {
    
    // Table components
    private JTable customersTable;
    private DefaultTableModel tableModel;
    
    // Filter components
    private JTextField nameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JButton searchButton;
    private JButton clearButton;
    
    // Action buttons
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton viewButton;
    
    // Currently selected customer
    private Customer selectedCustomer;
    private boolean isInitialized = false;

    /**
     * Constructor for the customers panel.
     * 
     * @param controller the controller
     * @param mainWindow the main window
     */
    public CustomersPanel(TravelAgencyController controller, MainWindow mainWindow) {
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
        namePanel.setBorder(BorderFactory.createTitledBorder("Customer Name"));
        nameField = new JTextField(20);
        namePanel.add(nameField, BorderLayout.CENTER);
        
        // Email filter
        JPanel emailPanel = new JPanel(new BorderLayout(5, 5));
        emailPanel.setBorder(BorderFactory.createTitledBorder("Email"));
        emailField = new JTextField(20);
        emailPanel.add(emailField, BorderLayout.CENTER);
        
        // Phone filter
        JPanel phonePanel = new JPanel(new BorderLayout(5, 5));
        phonePanel.setBorder(BorderFactory.createTitledBorder("Phone"));
        phoneField = new JTextField(20);
        phonePanel.add(phoneField, BorderLayout.CENTER);
        
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
        filterPanel.add(emailPanel);
        filterPanel.add(Box.createVerticalStrut(10));
        filterPanel.add(phonePanel);
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
        headerPanel.add(new JLabel("Customers", JLabel.LEFT), BorderLayout.WEST);
        
        // Create the table
        String[] columnNames = {"ID", "Name", "Email", "Phone", "Bookings"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table cells non-editable
            }
        };
        
        customersTable = new JTable(tableModel);
        customersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        customersTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = customersTable.getSelectedRow();
                if (selectedRow != -1) {
                    String customerId = (String) tableModel.getValueAt(selectedRow, 0);
                    selectedCustomer = findCustomerById(customerId);
                    updateButtonStates();
                }
            }
        });
        
        // Set column widths
        customersTable.getColumnModel().getColumn(0).setPreferredWidth(100);  // ID
        customersTable.getColumnModel().getColumn(1).setPreferredWidth(200);  // Name
        customersTable.getColumnModel().getColumn(2).setPreferredWidth(200);  // Email
        customersTable.getColumnModel().getColumn(3).setPreferredWidth(150);  // Phone
        customersTable.getColumnModel().getColumn(4).setPreferredWidth(80);   // Bookings
        
        // Add table to a scroll pane
        JScrollPane scrollPane = createScrollPane(customersTable);
        
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
        addButton.addActionListener(e -> showAddCustomerDialog());
        editButton.addActionListener(e -> showEditCustomerDialog());
        deleteButton.addActionListener(e -> deleteSelectedCustomer());
        viewButton.addActionListener(e -> viewCustomerDetails());
        
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
        boolean hasSelection = (selectedCustomer != null);
        
        editButton.setEnabled(hasSelection);
        viewButton.setEnabled(hasSelection);
        
        // Only enable delete button if the customer is not in use
        if (hasSelection) {
            boolean canDelete = !controller.isCustomerInUse(selectedCustomer);
            deleteButton.setEnabled(canDelete);
        } else {
            deleteButton.setEnabled(false);
        }
    }
    
    /**
     * Refresh the table with current customer data.
     */
    @Override
    public void refreshData() {
        if (isInitialized && mainWindow != null) {
            // Clear the table
            tableModel.setRowCount(0);
            
            // Get customers filtered by current filter settings
            ArrayList<Customer> customers = getFilteredCustomers();
            
            // Add customers to the table
            for (Customer customer : customers) {
                // Get booking count for this customer
                int bookingCount = controller.getBookingsForCustomer(customer).size();
                
                Object[] row = {
                    customer.getCustomerId(),
                    customer.getName(),
                    customer.getEmail(),
                    customer.getPhone(),
                    bookingCount
                };
                tableModel.addRow(row);
            }
            
            // Update status
            updateStatus("Customers loaded: " + customers.size());
            
            // Clear selection
            selectedCustomer = null;
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
        emailField.setText("");
        phoneField.setText("");
        
        refreshData();
    }
    
    /**
     * Get customers filtered by the current filter settings.
     * 
     * @return filtered list of customers
     */
    private ArrayList<Customer> getFilteredCustomers() {
        ArrayList<Customer> allCustomers = controller.getAllCustomers();
        ArrayList<Customer> filteredCustomers = new ArrayList<>();
        
        // Get filter values
        String nameFilter = nameField.getText().trim().toLowerCase();
        String emailFilter = emailField.getText().trim().toLowerCase();
        String phoneFilter = phoneField.getText().trim().toLowerCase();
        
        // Apply filters
        for (Customer customer : allCustomers) {
            boolean includeCustomer = true;
            
            // Filter by name
            if (!nameFilter.isEmpty() && 
                !customer.getName().toLowerCase().contains(nameFilter)) {
                includeCustomer = false;
            }
            
            // Filter by email
            if (!emailFilter.isEmpty() && 
                !customer.getEmail().toLowerCase().contains(emailFilter)) {
                includeCustomer = false;
            }
            
            // Filter by phone
            if (!phoneFilter.isEmpty() && 
                !customer.getPhone().contains(phoneFilter)) {
                includeCustomer = false;
            }
            
            if (includeCustomer) {
                filteredCustomers.add(customer);
            }
        }
        
        return filteredCustomers;
    }
    
    /**
     * Find a customer by their ID.
     * 
     * @param customerId the customer ID to find
     * @return the found Customer or null
     */
    private Customer findCustomerById(String customerId) {
        for (Customer customer : controller.getAllCustomers()) {
            if (customer.getCustomerId().equals(customerId)) {
                return customer;
            }
        }
        return null;
    }
    
    /**
     * Show dialog to add a new customer.
     */
    private void showAddCustomerDialog() {
        JDialog dialog = new JDialog(mainWindow, "Add New Customer", true);
        dialog.setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JTextField nameField = new JTextField(20);
        JTextField emailField = new JTextField(20);
        JTextField phoneField = new JTextField(20);
        JTextField addressField = new JTextField(20);
        
        formPanel.add(new JLabel("Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);
        formPanel.add(new JLabel("Phone:"));
        formPanel.add(phoneField);
        formPanel.add(new JLabel("Address:"));
        formPanel.add(addressField);
        
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
                String email = emailField.getText().trim();
                String phone = phoneField.getText().trim();
                String address = addressField.getText().trim();
                
                if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, 
                        "All fields are required.",
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Basic email validation
                if (!email.matches(".+@.+\\..+")) {
                    JOptionPane.showMessageDialog(dialog, 
                        "Please enter a valid email address.",
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Create the customer
                Customer newCustomer = controller.createCustomer(name, email, phone, address);
                
                dialog.dispose();
                refreshData();
                updateStatus("Customer created: " + newCustomer.getName());
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "Error creating customer: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        dialog.pack();
        dialog.setLocationRelativeTo(mainWindow);
        dialog.setVisible(true);
    }
    
    /**
     * Show dialog to edit the selected customer.
     */
    private void showEditCustomerDialog() {
        if (selectedCustomer == null) {
            return;
        }
        
        JDialog dialog = new JDialog(mainWindow, "Edit Customer", true);
        dialog.setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JTextField nameField = new JTextField(selectedCustomer.getName(), 20);
        JTextField emailField = new JTextField(selectedCustomer.getEmail(), 20);
        JTextField phoneField = new JTextField(selectedCustomer.getPhone(), 20);
        JTextField addressField = new JTextField(selectedCustomer.getAddress(), 20);
        
        formPanel.add(new JLabel("Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);
        formPanel.add(new JLabel("Phone:"));
        formPanel.add(phoneField);
        formPanel.add(new JLabel("Address:"));
        formPanel.add(addressField);
        
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
                String email = emailField.getText().trim();
                String phone = phoneField.getText().trim();
                String address = addressField.getText().trim();
                
                if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, 
                        "All fields are required.",
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Basic email validation
                if (!email.matches(".+@.+\\..+")) {
                    JOptionPane.showMessageDialog(dialog, 
                        "Please enter a valid email address.",
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Update the customer
                controller.updateCustomer(
                    selectedCustomer,
                    name,
                    email,
                    phone,
                    address
                );
                
                dialog.dispose();
                refreshData();
                updateStatus("Customer updated: " + selectedCustomer.getName());
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "Error updating customer: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        dialog.pack();
        dialog.setLocationRelativeTo(mainWindow);
        dialog.setVisible(true);
    }
    
    /**
     * Delete the selected customer.
     */
    private void deleteSelectedCustomer() {
        if (selectedCustomer == null) {
            return;
        }
        
        // Check if the customer is in use
        if (controller.isCustomerInUse(selectedCustomer)) {
            JOptionPane.showMessageDialog(this, 
                "Cannot delete customer '" + selectedCustomer.getName() + "' because they have existing bookings.",
                "Cannot Delete Customer", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this customer?\n" + 
            "Name: " + selectedCustomer.getName() + "\n" +
            "Email: " + selectedCustomer.getEmail(),
            "Delete Customer", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // Delete the customer
                boolean success = controller.deleteCustomer(selectedCustomer);
                
                if (success) {
                    refreshData();
                    updateStatus("Customer deleted");
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Failed to delete customer.",
                        "Delete Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Error deleting customer: " + e.getMessage(),
                    "Delete Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * View details of the selected customer.
     */
    private void viewCustomerDetails() {
        if (selectedCustomer == null) {
            return;
        }
        
        StringBuilder details = new StringBuilder();
        details.append("Customer ID: ").append(selectedCustomer.getCustomerId()).append("\n\n");
        details.append("Name: ").append(selectedCustomer.getName()).append("\n\n");
        details.append("Email: ").append(selectedCustomer.getEmail()).append("\n\n");
        details.append("Phone: ").append(selectedCustomer.getPhone()).append("\n\n");
        details.append("Address: ").append(selectedCustomer.getAddress()).append("\n\n");
        
        // Get bookings for this customer
        List<Booking> bookings = controller.getBookingsForCustomer(selectedCustomer);
        details.append("Bookings: ").append(bookings.size()).append("\n");
        
        if (!bookings.isEmpty()) {
            details.append("\nBooking Details:\n");
            for (Booking booking : bookings) {
                TravelPackage pkg = booking.getPackage();
                details.append("- ").append(pkg.getName())
                      .append(" (").append(booking.getStatus().getDisplayName()).append(")\n")
                      .append("  ").append(pkg.getDestination())
                      .append(", $").append(String.format("%.2f", booking.getTotalPrice())).append("\n");
            }
        }
        
        JTextArea textArea = new JTextArea(details.toString());
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 400));
        
        JOptionPane.showMessageDialog(this, scrollPane,
            "Customer Details", JOptionPane.INFORMATION_MESSAGE);
    }
} 