package tams.view;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import tams.controller.TravelAgencyController;
import tams.model.*;

/**
 * Panel for managing activities.
 */
public class ActivitiesPanel extends BasePanel {
    
    // Table components
    private JTable activitiesTable;
    private DefaultTableModel tableModel;
    
    // Filter components
    private JTextField nameField;
    private JTextField locationField;
    private JSlider costRangeSlider;
    private JTextField minDurationField;
    private JTextField maxDurationField;
    private JButton searchButton;
    private JButton clearButton;
    
    // Action buttons
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton viewButton;
    
    // Currently selected activity
    private Activity selectedActivity;
    private boolean isInitialized = false;
    
    /**
     * Constructor for the activities panel.
     * 
     * @param controller the controller
     * @param mainWindow the main window
     */
    public ActivitiesPanel(TravelAgencyController controller, MainWindow mainWindow) {
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
        namePanel.setBorder(BorderFactory.createTitledBorder("Activity Name"));
        nameField = new JTextField(20);
        namePanel.add(nameField, BorderLayout.CENTER);
        
        // Location filter
        JPanel locationPanel = new JPanel(new BorderLayout(5, 5));
        locationPanel.setBorder(BorderFactory.createTitledBorder("Location"));
        locationField = new JTextField(20);
        locationPanel.add(locationField, BorderLayout.CENTER);
        
        // Cost range filter
        JPanel costPanel = new JPanel(new BorderLayout(5, 5));
        costPanel.setBorder(BorderFactory.createTitledBorder("Cost Range"));
        costRangeSlider = new JSlider(JSlider.HORIZONTAL, 0, 200, 200);
        costRangeSlider.setMajorTickSpacing(50);
        costRangeSlider.setMinorTickSpacing(25);
        costRangeSlider.setPaintTicks(true);
        costRangeSlider.setPaintLabels(true);
        JLabel costLabel = new JLabel("Max Cost: $200");
        costRangeSlider.addChangeListener(e -> {
            costLabel.setText("Max Cost: $" + costRangeSlider.getValue());
        });
        costPanel.add(costRangeSlider, BorderLayout.CENTER);
        costPanel.add(costLabel, BorderLayout.SOUTH);
        
        // Duration filter
        JPanel durationPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        durationPanel.setBorder(BorderFactory.createTitledBorder("Duration (Hours)"));
        durationPanel.add(new JLabel("Min: "));
        minDurationField = new JTextField("1", 5);
        durationPanel.add(minDurationField);
        durationPanel.add(new JLabel("Max: "));
        maxDurationField = new JTextField("24", 5);
        durationPanel.add(maxDurationField);
        
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
        filterPanel.add(locationPanel);
        filterPanel.add(Box.createVerticalStrut(10));
        filterPanel.add(costPanel);
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
        headerPanel.add(new JLabel("Activities", JLabel.LEFT), BorderLayout.WEST);
        
        // Create the table
        String[] columnNames = {"ID", "Name", "Location", "Duration (Hours)", "Cost"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table cells non-editable
            }
        };
        
        activitiesTable = new JTable(tableModel);
        activitiesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        activitiesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = activitiesTable.getSelectedRow();
                if (selectedRow != -1) {
                    String activityId = (String) tableModel.getValueAt(selectedRow, 0);
                    selectedActivity = findActivityById(activityId);
                    updateButtonStates();
                }
            }
        });
        
        // Set column widths
        activitiesTable.getColumnModel().getColumn(0).setPreferredWidth(100);  // ID
        activitiesTable.getColumnModel().getColumn(1).setPreferredWidth(200);  // Name
        activitiesTable.getColumnModel().getColumn(2).setPreferredWidth(150);  // Location
        activitiesTable.getColumnModel().getColumn(3).setPreferredWidth(120);  // Duration
        activitiesTable.getColumnModel().getColumn(4).setPreferredWidth(100);  // Cost
        
        // Add table to a scroll pane
        JScrollPane scrollPane = createScrollPane(activitiesTable);
        
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
        addButton.addActionListener(e -> showAddActivityDialog());
        editButton.addActionListener(e -> showEditActivityDialog());
        deleteButton.addActionListener(e -> deleteSelectedActivity());
        viewButton.addActionListener(e -> viewActivityDetails());
        
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
        boolean hasSelection = (selectedActivity != null);
        
        editButton.setEnabled(hasSelection);
        deleteButton.setEnabled(hasSelection);
        viewButton.setEnabled(hasSelection);
    }
    
    @Override
    public void refreshData() {
        if (!isInitialized) {
            return;
        }
        
        // Clear the table
        tableModel.setRowCount(0);
        
        // Get activities from controller (filtered if applied)
        ArrayList<Activity> activities = getFilteredActivities();
        
        // Populate the table
        for (Activity activity : activities) {
            Object[] rowData = {
                activity.getActivityId(),
                activity.getName(),
                activity.getLocation(),
                activity.getDuration(),
                String.format("$%.2f", activity.getCost())
            };
            tableModel.addRow(rowData);
        }
        
        // Update status
        updateStatus("Loaded " + activities.size() + " activities");
        
        // Update button states
        selectedActivity = null;
        updateButtonStates();
    }
    
    /**
     * Apply the filters and refresh the data.
     */
    private void applyFilters() {
        refreshData();
    }
    
    /**
     * Clear all filters and refresh the data.
     */
    private void clearFilters() {
        // Reset all filter fields
        nameField.setText("");
        locationField.setText("");
        costRangeSlider.setValue(costRangeSlider.getMaximum());
        minDurationField.setText("1");
        maxDurationField.setText("24");
        
        // Refresh the data
        refreshData();
    }
    
    /**
     * Get activities filtered by the current filter settings.
     * 
     * @return filtered list of activities
     */
    private ArrayList<Activity> getFilteredActivities() {
        ArrayList<Activity> allActivities = controller.getActivities();
        ArrayList<Activity> filteredActivities = new ArrayList<>();
        
        // Get filter values
        String name = nameField.getText().trim().toLowerCase();
        String location = locationField.getText().trim().toLowerCase();
        double maxCost = costRangeSlider.getValue();
        
        int minDuration = 0;
        int maxDuration = Integer.MAX_VALUE;
        
        try {
            minDuration = Integer.parseInt(minDurationField.getText().trim());
        } catch (NumberFormatException e) {
            minDuration = 0;
        }
        
        try {
            maxDuration = Integer.parseInt(maxDurationField.getText().trim());
        } catch (NumberFormatException e) {
            maxDuration = Integer.MAX_VALUE;
        }
        
        // Apply filters
        for (Activity activity : allActivities) {
            // Skip if name doesn't match
            if (!name.isEmpty() && !activity.getName().toLowerCase().contains(name)) {
                continue;
            }
            
            // Skip if location doesn't match
            if (!location.isEmpty() && !activity.getLocation().toLowerCase().contains(location)) {
                continue;
            }
            
            // Skip if cost is too high
            if (activity.getCost() > maxCost) {
                continue;
            }
            
            // Skip if duration is outside range
            if (activity.getDuration() < minDuration || activity.getDuration() > maxDuration) {
                continue;
            }
            
            // Activity passed all filters
            filteredActivities.add(activity);
        }
        
        return filteredActivities;
    }
    
    /**
     * Find an activity by its ID.
     * 
     * @param activityId the ID to search for
     * @return the found Activity or null
     */
    private Activity findActivityById(String activityId) {
        for (Activity activity : controller.getActivities()) {
            if (activity.getActivityId().equals(activityId)) {
                return activity;
            }
        }
        return null;
    }
    
    /**
     * Show dialog to add a new activity.
     */
    private void showAddActivityDialog() {
        // Create the dialog
        JDialog dialog = new JDialog(
            SwingUtilities.getWindowAncestor(this), 
            "Add New Activity", 
            Dialog.ModalityType.APPLICATION_MODAL
        );
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        
        // Create form panel
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Add form fields
        JLabel nameLabel = new JLabel("Name:");
        JTextField nameField = new JTextField(20);
        
        JLabel locationLabel = new JLabel("Location:");
        JTextField locationField = new JTextField(20);
        
        JLabel durationLabel = new JLabel("Duration (Hours):");
        JSpinner durationSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 24, 1));
        
        JLabel costLabel = new JLabel("Cost ($):");
        JSpinner costSpinner = new JSpinner(new SpinnerNumberModel(50.0, 0.0, 1000.0, 5.0));
        
        // Add components to form
        formPanel.add(nameLabel);
        formPanel.add(nameField);
        formPanel.add(locationLabel);
        formPanel.add(locationField);
        formPanel.add(durationLabel);
        formPanel.add(durationSpinner);
        formPanel.add(costLabel);
        formPanel.add(costSpinner);
        
        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelButton = new JButton("Cancel");
        JButton saveButton = new JButton("Save");
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        saveButton.addActionListener(e -> {
            // Validate input
            String name = nameField.getText().trim();
            String location = locationField.getText().trim();
            int duration = (Integer) durationSpinner.getValue();
            double cost = (Double) costSpinner.getValue();
            
            if (name.isEmpty() || location.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                    "Please fill in all required fields.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Create the activity
            controller.createActivity(name, location, duration, cost);
            
            // Save data
            controller.saveData();
            
            // Refresh the table
            refreshData();
            
            // Close the dialog
            dialog.dispose();
            
            // Update status
            updateStatus("Activity added: " + name);
        });
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        
        // Add panels to dialog
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        // Show dialog
        dialog.setVisible(true);
    }
    
    /**
     * Show dialog to edit existing activity.
     */
    private void showEditActivityDialog() {
        if (selectedActivity == null) {
            return;
        }
        
        // Create the dialog
        JDialog dialog = new JDialog(
            SwingUtilities.getWindowAncestor(this), 
            "Edit Activity", 
            Dialog.ModalityType.APPLICATION_MODAL
        );
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        
        // Create form panel
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Add form fields with existing values
        JLabel nameLabel = new JLabel("Name:");
        JTextField nameField = new JTextField(selectedActivity.getName(), 20);
        
        JLabel locationLabel = new JLabel("Location:");
        JTextField locationField = new JTextField(selectedActivity.getLocation(), 20);
        
        JLabel durationLabel = new JLabel("Duration (Hours):");
        JSpinner durationSpinner = new JSpinner(new SpinnerNumberModel(
            selectedActivity.getDuration(), 1, 24, 1));
        
        JLabel costLabel = new JLabel("Cost ($):");
        JSpinner costSpinner = new JSpinner(new SpinnerNumberModel(
            selectedActivity.getCost(), 0.0, 1000.0, 5.0));
        
        // Add components to form
        formPanel.add(nameLabel);
        formPanel.add(nameField);
        formPanel.add(locationLabel);
        formPanel.add(locationField);
        formPanel.add(durationLabel);
        formPanel.add(durationSpinner);
        formPanel.add(costLabel);
        formPanel.add(costSpinner);
        
        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelButton = new JButton("Cancel");
        JButton saveButton = new JButton("Save");
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        saveButton.addActionListener(e -> {
            // Validate input
            String name = nameField.getText().trim();
            String location = locationField.getText().trim();
            int duration = (Integer) durationSpinner.getValue();
            double cost = (Double) costSpinner.getValue();
            
            if (name.isEmpty() || location.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                    "Please fill in all required fields.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Update the activity
            selectedActivity.setName(name);
            selectedActivity.setLocation(location);
            selectedActivity.setDuration(duration);
            selectedActivity.setCost(cost);
            
            // Save data
            controller.saveData();
            
            // Refresh the table
            refreshData();
            
            // Close the dialog
            dialog.dispose();
            
            // Update status
            updateStatus("Activity updated: " + name);
        });
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        
        // Add panels to dialog
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        // Show dialog
        dialog.setVisible(true);
    }
    
    /**
     * Delete the selected activity.
     */
    private void deleteSelectedActivity() {
        if (selectedActivity == null) {
            return;
        }
        
        // Ask for confirmation
        int result = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete activity: " + selectedActivity.getName() + "?",
            "Confirm Deletion",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (result == JOptionPane.YES_OPTION) {
            // Check if the activity is used in any packages
            boolean isUsed = false;
            for (TravelPackage pkg : controller.getAllTravelPackages()) {
                if (pkg.getActivities().contains(selectedActivity)) {
                    isUsed = true;
                    break;
                }
            }
            
            if (isUsed) {
                JOptionPane.showMessageDialog(this,
                    "Cannot delete this activity because it is used in one or more travel packages.",
                    "Deletion Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Perform deletion
            boolean success = controller.getActivities().remove(selectedActivity);
            
            if (success) {
                // Save data
                controller.saveData();
                
                // Refresh the table
                refreshData();
                
                // Update status
                updateStatus("Activity deleted: " + selectedActivity.getName());
                
                // Reset selected activity
                selectedActivity = null;
                updateButtonStates();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Failed to delete the activity.",
                    "Deletion Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * View details of the selected activity.
     */
    private void viewActivityDetails() {
        if (selectedActivity == null) {
            return;
        }
        
        // Create the dialog
        JDialog dialog = new JDialog(
            SwingUtilities.getWindowAncestor(this), 
            "Activity Details", 
            Dialog.ModalityType.APPLICATION_MODAL
        );
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        
        // Create content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Add details
        JLabel titleLabel = new JLabel(selectedActivity.getName());
        titleLabel.setFont(new Font(titleLabel.getFont().getName(), Font.BOLD, 16));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel idLabel = new JLabel("ID: " + selectedActivity.getActivityId());
        idLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel locationLabel = new JLabel("Location: " + selectedActivity.getLocation());
        locationLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel durationLabel = new JLabel("Duration: " + selectedActivity.getDuration() + " hours");
        durationLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel costLabel = new JLabel(String.format("Cost: $%.2f", selectedActivity.getCost()));
        costLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // List packages that include this activity
        JLabel packagesLabel = new JLabel("Used in packages:");
        packagesLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        packagesLabel.setFont(new Font(packagesLabel.getFont().getName(), Font.BOLD, 12));
        
        JPanel packagesPanel = new JPanel();
        packagesPanel.setLayout(new BoxLayout(packagesPanel, BoxLayout.Y_AXIS));
        packagesPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        boolean foundInPackage = false;
        for (TravelPackage pkg : controller.getAllTravelPackages()) {
            if (pkg.getActivities().contains(selectedActivity)) {
                JLabel packageLabel = new JLabel(pkg.getName() + " (" + pkg.getServiceId() + ")");
                packageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                packagesPanel.add(packageLabel);
                foundInPackage = true;
            }
        }
        
        if (!foundInPackage) {
            JLabel noneLabel = new JLabel("Not used in any package");
            noneLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            packagesPanel.add(noneLabel);
        }
        
        // Add components to the panel
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        contentPanel.add(idLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        contentPanel.add(locationLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        contentPanel.add(durationLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        contentPanel.add(costLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        contentPanel.add(packagesLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        contentPanel.add(packagesPanel);
        
        // Add scroll functionality
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        // Add close button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(closeButton);
        
        // Add panels to dialog
        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        // Show dialog
        dialog.setVisible(true);
    }
}
