import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class InventoryTablePanel extends JPanel {

    private JComboBox<String> timeWindowBox;
    private JComboBox<String> ingredientTypeBox;
    private JTable usageTable;
    private DefaultTableModel tableModel;

    private DBConnection connect; // Assume this is the backend connection object

    public InventoryTablePanel(DBConnection connect, ArrayList<String> ingredients) {
        this.connect = connect;

        // Set layout
        setLayout(new BorderLayout());

        // Time window selection (e.g., Last week, Last month)
        String[] timeWindows = {"Last Week", "Last Month", "Last Year"};
        timeWindowBox = new JComboBox<>(timeWindows);

        ingredientTypeBox = new JComboBox<>(ingredients.toArray(new String[0]));

        JPanel selectionPanel = new JPanel();
        selectionPanel.setLayout(new FlowLayout());
        selectionPanel.add(new JLabel("Select Time Window: "));
        selectionPanel.add(timeWindowBox);
        selectionPanel.add(new JLabel("Select Ingredient Type: "));
        selectionPanel.add(ingredientTypeBox);

        // Table for displaying usage
        String[] columnNames = {"Day", "Amount Used"};
        tableModel = new DefaultTableModel(columnNames, 0);
        usageTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(usageTable);

        // Add components to the panel
        add(selectionPanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);

        // Add action listeners for the JComboBoxes to update the table
        timeWindowBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateTable();
            }
        });

        ingredientTypeBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateTable();
            }
        });

        // Initial table population
        updateTable();
    }

    // Method to populate the table based on selected time window and ingredient type
    private void updateTable() {
        // Clear existing rows
        tableModel.setRowCount(0);

        // Get the selected time window and ingredient type
        String selectedTimeWindow = (String) timeWindowBox.getSelectedItem();
        String selectedIngredientType = (String) ingredientTypeBox.getSelectedItem();

        // Calculate the start and end dates based on the selected time window
        Date[] dateRange = getTimeRange(selectedTimeWindow);
        Date startDate = dateRange[0];
        Date endDate = dateRange[1];

        // Array to hold the data populated by the DBConnection method
        ArrayList<HashMap<String, Object>> usageData = new ArrayList<>();

        connect.getIngredientInTimeframe(startDate, endDate, selectedIngredientType, usageData);

        // Populate the table with the fetched data
        if (usageData != null) {
            for (HashMap<String, Object> dailyUsage : usageData) {
                String day = (String) dailyUsage.get("Day");
                Integer amount = (Integer) dailyUsage.get("Amount");
                tableModel.addRow(new Object[]{day, amount});
            }
        }
    }

    // Helper method to determine the start and end dates based on the selected time window
    private Date[] getTimeRange(String timeWindow) {
        Calendar calendar = Calendar.getInstance();
        Date endDate = calendar.getTime();  // End date is today
        Date startDate = null;

        switch (timeWindow) {
            case "Last Week":
                calendar.add(Calendar.WEEK_OF_YEAR, -1);
                startDate = calendar.getTime();
                break;
            case "Last Month":
                calendar.add(Calendar.MONTH, -1);
                startDate = calendar.getTime();
                break;
            case "Last Year":
                calendar.add(Calendar.YEAR, -1);
                startDate = calendar.getTime();
                break;
            default:
                startDate = endDate; // Just in case something goes wrong
        }

        return new Date[]{startDate, endDate};
    }
}