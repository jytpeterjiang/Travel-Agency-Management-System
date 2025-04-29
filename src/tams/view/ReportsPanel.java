package tams.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

import tams.controller.TravelAgencyController;
import tams.model.*;

/**
 * Panel for generating and displaying various reports and statistics.
 */
public class ReportsPanel extends BasePanel {
    
    private JComboBox<String> reportTypeComboBox;
    private JPanel reportContentPanel;
    private JButton generateButton;
    private JPanel parameterPanel;
    
    private boolean isInitialized = false;
    
    /**
     * Constructor for creating a new ReportsPanel.
     * 
     * @param controller the travel agency controller
     * @param mainWindow the main window
     */
    public ReportsPanel(TravelAgencyController controller, MainWindow mainWindow) {
        super(controller, mainWindow);
        isInitialized = true;
    }
    
    @Override
    protected void createFilterPanel() {
        // Create the filter panel
        filterPanel = new JPanel(new BorderLayout());
        filterPanel.setBorder(BorderFactory.createTitledBorder("Report Options"));
        filterPanel.setPreferredSize(new Dimension(200, 0)); // Set to 200px width
        
        JPanel selectPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        selectPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JLabel reportTypeLabel = new JLabel("Report Type:");
        
        // Define the available report types
        String[] reportTypes = {
            "Revenue by Package",
            "Bookings by Status",
            "Top Rated Packages", 
            "Customer Booking History",
            "Package Popularity"
        };
        
        reportTypeComboBox = new JComboBox<>(reportTypes);
        generateButton = new JButton("Generate Report");
        
        selectPanel.add(reportTypeLabel);
        selectPanel.add(reportTypeComboBox);
        selectPanel.add(generateButton);
        
        parameterPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        parameterPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        filterPanel.add(selectPanel, BorderLayout.NORTH);
        filterPanel.add(parameterPanel, BorderLayout.CENTER);
        
        // Add action listener to the report type combo box to update parameters
        reportTypeComboBox.addActionListener(e -> updateParameterPanel());
        
        // Add action listener to the generate button
        generateButton.addActionListener(e -> generateReport());
        
        // Initialize the parameter panel
        updateParameterPanel();
    }
    
    /**
     * Update the parameter panel based on the selected report type.
     */
    private void updateParameterPanel() {
        parameterPanel.removeAll();
        
        String selectedReportType = (String) reportTypeComboBox.getSelectedItem();
        
        if ("Customer Booking History".equals(selectedReportType)) {
            // Create a more compact panel with FlowLayout instead of GridLayout
            parameterPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
            
            // Add customer selection dropdown
            ArrayList<Customer> customers = controller.getAllCustomers();
            if (!customers.isEmpty()) {
                JLabel customerLabel = new JLabel("Select Customer:");
                
                // Create custom renderer for the combo box to limit the width
                JComboBox<Customer> customerComboBox = new JComboBox<>(
                    customers.toArray(new Customer[0]));
                customerComboBox.setName("customerComboBox");
                
                // Set smaller dimensions for the combo box
                customerComboBox.setPreferredSize(new Dimension(160, 25));
                customerComboBox.setMaximumSize(new Dimension(160, 25));
                
                // Add a custom renderer to limit the display width of customer names
                customerComboBox.setRenderer(new DefaultListCellRenderer() {
                    @Override
                    public Component getListCellRendererComponent(JList<?> list, Object value, 
                            int index, boolean isSelected, boolean cellHasFocus) {
                        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                        
                        if (value instanceof Customer) {
                            Customer customer = (Customer) value;
                            String name = customer.getName();
                            if (name.length() > 16) {
                                name = name.substring(0, 13) + "...";
                            }
                            setText(name);
                        }
                        return this;
                    }
                });
                
                parameterPanel.add(customerLabel);
                parameterPanel.add(customerComboBox);
            } else {
                parameterPanel.add(new JLabel("No customers available"));
            }
        } else if ("Bookings by Status".equals(selectedReportType)) {
            // Create a more compact panel with FlowLayout instead of GridLayout
            parameterPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
            
            // Add status selection dropdown
            JLabel statusLabel = new JLabel("Status:");
            JComboBox<BookingStatus> statusComboBox = new JComboBox<>(BookingStatus.values());
            statusComboBox.setName("statusComboBox");
            statusComboBox.setPreferredSize(new Dimension(160, 25));
            statusComboBox.setMaximumSize(new Dimension(160, 25));
            parameterPanel.add(statusLabel);
            parameterPanel.add(statusComboBox);
        } else {
            // Reset to default grid layout for other report types
            parameterPanel.setLayout(new GridLayout(0, 1, 5, 5));
        }
        
        parameterPanel.revalidate();
        parameterPanel.repaint();
    }
    
    @Override
    protected void createContentPanel() {
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Create the report content panel
        reportContentPanel = new JPanel(new BorderLayout());
        
        // Initial message
        JLabel initialLabel = new JLabel("Select a report type and click Generate Report to view statistics");
        initialLabel.setHorizontalAlignment(JLabel.CENTER);
        initialLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        reportContentPanel.add(initialLabel, BorderLayout.CENTER);
        
        // Add the report content panel to the main content panel
        contentPanel.add(reportContentPanel, BorderLayout.CENTER);
    }
    
    @Override
    protected void createButtonPanel() {
        // This panel doesn't need additional buttons at the bottom
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    }
    
    @Override
    public void refreshData() {
        if (isInitialized) {
            updateParameterPanel();
            updateStatus("Reports panel refreshed");
        }
    }
    
    /**
     * Generate and display the selected report.
     */
    private void generateReport() {
        String reportType = (String) reportTypeComboBox.getSelectedItem();
        
        // Clear the content panel
        reportContentPanel.removeAll();
        
        try {
            switch (reportType) {
                case "Revenue by Package":
                    displayRevenueByPackageReport();
                    break;
                case "Bookings by Status":
                    displayBookingsByStatusReport();
                    break;
                case "Top Rated Packages":
                    displayTopRatedPackagesReport();
                    break;
                case "Customer Booking History":
                    displayCustomerBookingHistoryReport();
                    break;
                case "Package Popularity":
                    displayPackagePopularityReport();
                    break;
                default:
                    JLabel errorLabel = new JLabel("Unknown report type");
                    reportContentPanel.add(errorLabel, BorderLayout.CENTER);
            }
        } catch (Exception e) {
            JLabel errorLabel = new JLabel("Error generating report: " + e.getMessage());
            errorLabel.setForeground(Color.RED);
            reportContentPanel.add(errorLabel, BorderLayout.CENTER);
        }
        
        reportContentPanel.revalidate();
        reportContentPanel.repaint();
        updateStatus("Generated " + reportType + " report");
    }
    
    /**
     * Display revenue by package report.
     */
    private void displayRevenueByPackageReport() {
        // Create the table model
        String[] columnNames = {"Package", "Total Revenue", "Bookings", "Average Booking Value"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Calculate revenue for each package
        Map<TravelPackage, Double> packageRevenue = new HashMap<>();
        Map<TravelPackage, Integer> packageBookingCount = new HashMap<>();
        
        for (Booking booking : controller.getAllBookings()) {
            if (booking.getPackage() != null && 
                (booking.getStatus() == BookingStatus.COMPLETED || 
                 booking.getStatus() == BookingStatus.CONFIRMED)) {
                
                TravelPackage pkg = booking.getPackage();
                
                // Increment revenue
                double bookingValue = booking.getTotalPrice();
                packageRevenue.put(pkg, packageRevenue.getOrDefault(pkg, 0.0) + bookingValue);
                
                // Increment booking count
                packageBookingCount.put(pkg, packageBookingCount.getOrDefault(pkg, 0) + 1);
            }
        }
        
        // Populate the table
        List<TravelPackage> packages = new ArrayList<>(packageRevenue.keySet());
        packages.sort((p1, p2) -> Double.compare(packageRevenue.get(p2), packageRevenue.get(p1)));
        
        double totalRevenue = 0.0;
        for (TravelPackage pkg : packages) {
            double revenue = packageRevenue.get(pkg);
            int bookingCount = packageBookingCount.get(pkg);
            double averageValue = bookingCount > 0 ? revenue / bookingCount : 0;
            
            Object[] rowData = {
                pkg.getName(),
                String.format("$%.2f", revenue),
                bookingCount,
                String.format("$%.2f", averageValue)
            };
            tableModel.addRow(rowData);
            
            totalRevenue += revenue;
        }
        
        // Create the table and add it to a scroll pane
        JTable revenueTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(revenueTable);
        
        // Add a title label
        JLabel titleLabel = new JLabel("Revenue by Package Report");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        
        // Create a panel to hold the title and table
        JPanel reportPanel = new JPanel(new BorderLayout());
        reportPanel.add(titleLabel, BorderLayout.NORTH);
        reportPanel.add(scrollPane, BorderLayout.CENTER);
        
        reportContentPanel.add(reportPanel, BorderLayout.CENTER);
    }
    
    /**
     * Display bookings by status report.
     */
    private void displayBookingsByStatusReport() {
        // Get the selected status from the parameter panel
        BookingStatus selectedStatus = null;
        for (Component comp : parameterPanel.getComponents()) {
            if (comp instanceof JComboBox && "statusComboBox".equals(comp.getName())) {
                @SuppressWarnings("unchecked")
                JComboBox<BookingStatus> comboBox = (JComboBox<BookingStatus>) comp;
                selectedStatus = (BookingStatus) comboBox.getSelectedItem();
                break;
            }
        }
        
        if (selectedStatus == null) {
            selectedStatus = BookingStatus.CONFIRMED; // Default if not found
        }
        
        // Get bookings with the selected status
        ArrayList<Booking> bookings = controller.searchBookingsByStatus(selectedStatus);
        
        // Create the table model
        String[] columnNames = {"Booking ID", "Customer", "Package", "Date", "Total Price"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Populate the table
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        double totalRevenue = 0.0;
        
        for (Booking booking : bookings) {
            TravelPackage pkg = booking.getPackage();
            if (pkg != null) {
                Object[] rowData = {
                    booking.getBookingId(),
                    booking.getCustomer().getName(),
                    pkg.getName(),
                    dateFormat.format(booking.getDate()),
                    String.format("$%.2f", booking.getTotalPrice())
                };
                tableModel.addRow(rowData);
                totalRevenue += booking.getTotalPrice();
            }
        }
        
        // Create the table and add it to a scroll pane
        JTable bookingsTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(bookingsTable);
        
        // Add a title label
        JLabel titleLabel = new JLabel("Bookings with Status: " + selectedStatus.getDisplayName());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        
        // Create a panel to hold the title and table
        JPanel reportPanel = new JPanel(new BorderLayout());
        reportPanel.add(titleLabel, BorderLayout.NORTH);
        reportPanel.add(scrollPane, BorderLayout.CENTER);
        
        reportContentPanel.add(reportPanel, BorderLayout.CENTER);
    }
    
    /**
     * Display top rated packages report.
     */
    private void displayTopRatedPackagesReport() {
        // Implementation for top rated packages report
        // Create a table showing packages sorted by average rating
        JLabel titleLabel = new JLabel("Top Rated Packages");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        
        reportContentPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Get all packages
        ArrayList<TravelPackage> packages = controller.getAllTravelPackages();
        
        // Create the table model
        String[] columnNames = {"Rank", "Package", "Average Rating", "Number of Reviews", "Price"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        
        // Filter packages with at least one review
        List<TravelPackage> ratedPackages = new ArrayList<>();
        for (TravelPackage pkg : packages) {
            if (!pkg.getReviews().isEmpty()) {
                ratedPackages.add(pkg);
            }
        }
        
        // Sort packages by average rating
        ratedPackages.sort((p1, p2) -> Double.compare(p2.getAverageRating(), p1.getAverageRating()));
        
        // Populate the table
        for (int i = 0; i < ratedPackages.size(); i++) {
            TravelPackage pkg = ratedPackages.get(i);
            Object[] rowData = {
                i + 1,
                pkg.getName(),
                String.format("%.1f", pkg.getAverageRating()),
                pkg.getReviews().size(),
                String.format("$%.2f", pkg.getPrice())
            };
            tableModel.addRow(rowData);
        }
        
        JTable ratingsTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(ratingsTable);
        reportContentPanel.add(scrollPane, BorderLayout.CENTER);
    }
    
    /**
     * Display customer booking history report.
     */
    private void displayCustomerBookingHistoryReport() {
        // Get the selected customer from the parameter panel
        Customer selectedCustomer = null;
        for (Component comp : parameterPanel.getComponents()) {
            if (comp instanceof JComboBox && "customerComboBox".equals(comp.getName())) {
                @SuppressWarnings("unchecked")
                JComboBox<Customer> comboBox = (JComboBox<Customer>) comp;
                selectedCustomer = (Customer) comboBox.getSelectedItem();
                break;
            }
        }
        
        if (selectedCustomer == null) {
            JLabel errorLabel = new JLabel("Please select a customer");
            errorLabel.setHorizontalAlignment(JLabel.CENTER);
            reportContentPanel.add(errorLabel, BorderLayout.CENTER);
            return;
        }
        
        // Get all bookings for the selected customer
        List<Booking> customerBookings = controller.getBookingsForCustomer(selectedCustomer);
        
        // Create the table model
        String[] columnNames = {"Booking ID", "Package", "Date", "Status", "Price"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        
        // Populate the table
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        
        for (Booking booking : customerBookings) {
            TravelPackage pkg = booking.getPackage();
            if (pkg != null) {
                Object[] rowData = {
                    booking.getBookingId(),
                    pkg.getName(),
                    dateFormat.format(booking.getDate()),
                    booking.getStatus().getDisplayName(),
                    String.format("$%.2f", booking.getTotalPrice())
                };
                tableModel.addRow(rowData);
            }
        }
        
        // Add a title label
        JLabel titleLabel = new JLabel("Booking History for " + selectedCustomer.getName());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        
        JTable historyTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(historyTable);
        
        JPanel reportPanel = new JPanel(new BorderLayout());
        reportPanel.add(titleLabel, BorderLayout.NORTH);
        reportPanel.add(scrollPane, BorderLayout.CENTER);
        
        reportContentPanel.add(reportPanel, BorderLayout.CENTER);
    }
    
    /**
     * Display package popularity report.
     */
    private void displayPackagePopularityReport() {
        // Implementation for package popularity report
        // Create a table showing packages sorted by number of bookings
        JLabel titleLabel = new JLabel("Package Popularity Report");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        
        // Get all packages and bookings
        ArrayList<TravelPackage> packages = controller.getAllTravelPackages();
        ArrayList<Booking> allBookings = controller.getAllBookings();
        
        // Count bookings per package
        Map<TravelPackage, Integer> packageBookingCount = new HashMap<>();
        for (Booking booking : allBookings) {
            TravelPackage pkg = booking.getPackage();
            if (pkg != null) {
                packageBookingCount.put(pkg, packageBookingCount.getOrDefault(pkg, 0) + 1);
            }
        }
        
        // Create the table model
        String[] columnNames = {"Rank", "Package", "Bookings", "Destination", "Price"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        
        // Sort packages by booking count
        List<TravelPackage> sortedPackages = new ArrayList<>(packageBookingCount.keySet());
        sortedPackages.sort((p1, p2) -> Integer.compare(
            packageBookingCount.getOrDefault(p2, 0), 
            packageBookingCount.getOrDefault(p1, 0)
        ));
        
        // Add packages with bookings
        int rank = 1;
        for (TravelPackage pkg : sortedPackages) {
            int bookingCount = packageBookingCount.getOrDefault(pkg, 0);
            Object[] rowData = {
                rank++,
                pkg.getName(),
                bookingCount,
                pkg.getDestination(),
                String.format("$%.2f", pkg.getPrice())
            };
            tableModel.addRow(rowData);
        }
        
        JTable popularityTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(popularityTable);
        
        JPanel reportPanel = new JPanel(new BorderLayout());
        reportPanel.add(titleLabel, BorderLayout.NORTH);
        reportPanel.add(scrollPane, BorderLayout.CENTER);
        
        reportContentPanel.add(reportPanel, BorderLayout.CENTER);
    }
} 