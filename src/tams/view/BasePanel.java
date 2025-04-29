package tams.view;

import javax.swing.*;
import java.awt.*;
import tams.controller.TravelAgencyController;

/**
 * Base panel class that all tab panels extend.
 * Provides common functionality and layout.
 */
public abstract class BasePanel extends JPanel {
    
    protected TravelAgencyController controller;
    protected MainWindow mainWindow;
    
    // Common UI components
    protected JPanel filterPanel;
    protected JPanel contentPanel;
    protected JPanel buttonPanel;
    
    /**
     * Constructor for the base panel.
     * 
     * @param controller the controller
     * @param mainWindow the main window
     */
    public BasePanel(TravelAgencyController controller, MainWindow mainWindow) {
        this.controller = controller;
        this.mainWindow = mainWindow;
        
        setLayout(new BorderLayout());
        
        // Create common panel components
        createFilterPanel();
        createContentPanel();
        createButtonPanel();
        
        // Add panels to the layout
        add(filterPanel, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Don't refresh data here - will be called externally after all UI components are ready
    }
    
    /**
     * Create the filter panel on the left side.
     * Subclasses will override this to provide specific filters.
     */
    protected void createFilterPanel() {
        filterPanel = new JPanel();
        filterPanel.setBorder(BorderFactory.createTitledBorder("Search & Filters"));
        filterPanel.setPreferredSize(new Dimension(200, 0));
    }
    
    /**
     * Create the content panel in the center.
     * Subclasses will override this to provide specific content.
     */
    protected void createContentPanel() {
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }
    
    /**
     * Create the button panel at the bottom.
     * Subclasses will override this to provide specific buttons.
     */
    protected void createButtonPanel() {
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }
    
    /**
     * Refresh the data displayed in the panel.
     * Subclasses must implement this method.
     */
    public abstract void refreshData();
    
    /**
     * Utility method to create a standardized button.
     * 
     * @param text the button text
     * @return the created JButton
     */
    protected JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(100, 30));
        return button;
    }
    
    /**
     * Utility method to create a standardized label.
     * 
     * @param text the label text
     * @return the created JLabel
     */
    protected JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setPreferredSize(new Dimension(100, 25));
        return label;
    }
    
    /**
     * Utility method to create a standardized text field.
     * 
     * @param columns the number of columns
     * @return the created JTextField
     */
    protected JTextField createTextField(int columns) {
        JTextField textField = new JTextField(columns);
        return textField;
    }
    
    /**
     * Utility method to create a standardized combo box.
     * 
     * @param items the items for the combo box
     * @return the created JComboBox
     */
    protected <T> JComboBox<T> createComboBox(T[] items) {
        JComboBox<T> comboBox = new JComboBox<>(items);
        return comboBox;
    }
    
    /**
     * Utility method to create a standardized scroll pane.
     * 
     * @param component the component to wrap
     * @return the created JScrollPane
     */
    protected JScrollPane createScrollPane(Component component) {
        JScrollPane scrollPane = new JScrollPane(component);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        return scrollPane;
    }
    
    /**
     * Utility method to update the status in the main window.
     * 
     * @param message the status message
     */
    protected void updateStatus(String message) {
        if (mainWindow != null) {
            mainWindow.updateStatus(message);
        }
    }
} 