package tams.view;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.Desktop;
import java.util.ArrayList;
import java.util.Date;

import tams.controller.TravelAgencyController;
import tams.model.*;
import tams.exceptions.*;

/**
 * The main window of the Travel Agency Management System.
 * Contains tabs for Packages, Customers, Bookings, Reports, and Reviews.
 */
public class MainWindow extends JFrame {
    
    private TravelAgencyController controller;
    
    // Tabs
    private JTabbedPane tabbedPane;
    private PackagesPanel packagesPanel;
    private CustomersPanel customersPanel;
    private BookingsPanel bookingsPanel;
    private ReportsPanel reportsPanel;
    private ReviewsPanel reviewsPanel;
    
    // Menu items
    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JMenu editMenu;
    private JMenu viewMenu;
    private JMenu helpMenu;
    
    // Status bar - initialize statusLabel immediately to prevent NullPointerException
    private JPanel statusBar;
    private JLabel statusLabel = new JLabel("Initializing...");
    
    /**
     * Constructor for the main window.
     */
    public MainWindow() {
        // Initialize controller
        controller = new TravelAgencyController();
        
        // Set up the frame
        setTitle("Travel Agency Management System");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window
        
        // Set up window layout
        setLayout(new BorderLayout());
        
        // Initialize status bar first to ensure it's ready when needed
        createStatusBar();
        add(statusBar, BorderLayout.SOUTH);
        
        // Create other UI components after status bar is ready
        createMenuBar();
        setJMenuBar(menuBar);
        
        try {
            // Create and add tabbed pane last to prevent initialization issues
            tabbedPane = new JTabbedPane();
            add(tabbedPane, BorderLayout.CENTER);
            
            // Register window closing event to save data
            addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    controller.saveData();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error initializing UI: " + e.getMessage(),
                "Initialization Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Create the tabs after the frame is displayed.
     * This prevents initialization issues.
     */
    public void createTabs() {
        SwingUtilities.invokeLater(() -> {
            try {
                updateStatus("Creating panels...");
                
                // Create the panels
                packagesPanel = new PackagesPanel(controller, this);
                customersPanel = new CustomersPanel(controller, this);
                bookingsPanel = new BookingsPanel(controller, this);
                reportsPanel = new ReportsPanel(controller, this);
                reviewsPanel = new ReviewsPanel(controller, this);
                
                // Add panels to the tabbed pane
                tabbedPane.addTab("Packages", new ImageIcon(), packagesPanel, "Manage travel packages");
                tabbedPane.addTab("Customers", new ImageIcon(), customersPanel, "Manage customers");
                tabbedPane.addTab("Bookings", new ImageIcon(), bookingsPanel, "Manage bookings");
                tabbedPane.addTab("Reports", new ImageIcon(), reportsPanel, "View reports");
                tabbedPane.addTab("Reviews", new ImageIcon(), reviewsPanel, "Manage reviews");
                
                // Add change listener to update status when tab changes
                tabbedPane.addChangeListener(e -> {
                    int index = tabbedPane.getSelectedIndex();
                    if (index != -1) {
                        updateStatus("Viewing " + tabbedPane.getTitleAt(index));
                    }
                });
                
                updateStatus("Ready");
            } catch (Exception e) {
                e.printStackTrace();
                updateStatus("Error creating tabs: " + e.getMessage());
            }
        });
    }
    
    /**
     * Create the menu bar.
     */
    private void createMenuBar() {
        menuBar = new JMenuBar();
        
        // File menu
        fileMenu = new JMenu("File");
        JMenuItem saveItem = new JMenuItem("Save");
        saveItem.addActionListener(e -> {
            controller.saveData();
            updateStatus("Data saved successfully");
        });
        
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> {
            controller.saveData();
            System.exit(0);
        });
        
        fileMenu.add(saveItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        
        // Edit menu
        editMenu = new JMenu("Edit");
        JMenuItem preferencesItem = new JMenuItem("Preferences");
        preferencesItem.addActionListener(e -> {
            // Placeholder for preferences dialog
            JOptionPane.showMessageDialog(this, "Preferences dialog would appear here.");
        });
        
        editMenu.add(preferencesItem);
        
        // View menu
        viewMenu = new JMenu("View");
        JMenuItem refreshItem = new JMenuItem("Refresh");
        refreshItem.addActionListener(e -> refreshCurrentTab());
        
        viewMenu.add(refreshItem);
        
        // Help menu
        helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        JMenuItem githubItem = new JMenuItem("GitHub Repository");
        
        aboutItem.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, 
                "OOP Project:\nTravel Agency Management System\nVersion 1.0\n2025 @Peter Jiang",
                "About", JOptionPane.INFORMATION_MESSAGE);
        });
        
        githubItem.addActionListener(e -> {
            try {
                Desktop.getDesktop().browse(new java.net.URI("https://github.com/jytpeterjiang/Travel-Agency-Management-System"));
                updateStatus("Opening GitHub repository in browser...");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Error opening URL: " + ex.getMessage() + "\n\nURL: https://github.com/jytpeterjiang/Travel-Agency-Management-System",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        helpMenu.add(aboutItem);
        helpMenu.addSeparator();
        helpMenu.add(githubItem);
        
        // Add menus to menu bar
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(viewMenu);
        menuBar.add(helpMenu);
    }
    
    /**
     * Create the status bar at the bottom of the window.
     */
    private void createStatusBar() {
        statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(BorderFactory.createEtchedBorder());
        
        // statusLabel is already initialized in the field declaration
        statusLabel.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        
        statusBar.add(statusLabel, BorderLayout.WEST);
    }
    
    /**
     * Update the status message.
     * 
     * @param message the status message to display
     */
    public void updateStatus(String message) {
        if (statusLabel != null) {
            SwingUtilities.invokeLater(() -> {
                statusLabel.setText(message);
            });
        }
    }
    
    /**
     * Refresh the current tab.
     */
    private void refreshCurrentTab() {
        if (tabbedPane == null) return;
        
        int selectedIndex = tabbedPane.getSelectedIndex();
        if (selectedIndex == -1) return;
        
        updateStatus("Refreshing data...");
        
        try {
            switch (selectedIndex) {
                case 0: // Packages
                    if (packagesPanel != null) packagesPanel.refreshData();
                    break;
                case 1: // Customers
                    if (customersPanel != null) customersPanel.refreshData();
                    break;
                case 2: // Bookings
                    if (bookingsPanel != null) bookingsPanel.refreshData();
                    break;
                case 3: // Reports
                    if (reportsPanel != null) reportsPanel.refreshData();
                    break;
                case 4: // Reviews
                    if (reviewsPanel != null) reviewsPanel.refreshData();
                    break;
            }
            
            updateStatus("Data refreshed");
        } catch (Exception e) {
            e.printStackTrace();
            updateStatus("Error refreshing data: " + e.getMessage());
        }
    }
    
    /**
     * Main method to start the application.
     */
    public static void main(String[] args) {
        // Set look and feel to system default
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Launch the application
        SwingUtilities.invokeLater(() -> {
            try {
                MainWindow window = new MainWindow();
                window.setVisible(true);
                
                // Create tabs after the main window is visible
                window.createTabs();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, 
                    "Error starting application: " + e.getMessage(),
                    "Startup Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
} 