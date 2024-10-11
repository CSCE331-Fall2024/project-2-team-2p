import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

public class ManagerFrame {

    private JFrame frame;
    private JPanel mainPanel;
    private JLabel graphLabel;
    private JButton menuButton;
    private JButton inventoryButton;
    private JButton employeesButton;
    private JButton reportsButton;

    // data structures for the pop-ups
    private ArrayList<HashMap<String, Object>> menuItems; 
    private ArrayList<HashMap<String, Object>> ingredients;
    private ArrayList<HashMap<String, Object>> employees;
    private ArrayList<HashMap<String, Object>> orders;
    private ArrayList<HashMap<String, Object>> viewOrders;

    // data structures for removed entries
    private ArrayList<HashMap<String, Object>> removedMenuItems; 
    private ArrayList<HashMap<String, Object>> removedIngredients;
    private ArrayList<HashMap<String, Object>> removedEmployees;

    private DefaultTableModel tableModel;

    private DBConnection connect;

    //TODO: make sure user is found
    private String  placeholdUsername = "Zophous";
    private int placeholdPin = 1111;

    public ManagerFrame() {
        // initializing data structures for the popups
        menuItems = new ArrayList<>();
        ingredients = new ArrayList<>();
        employees = new ArrayList<>();
        orders = new ArrayList<>();

        //connect to database
        connect = new DBConnection(true); //true for manager view
        connect.verifyCredentials(placeholdUsername, placeholdPin);

        /* functions done in DBConnection instead. TODO: ensure correctness
        initializeMenuItems(); //DONE: call populateMenuItems(menuItems) from DBConnection class
        initializeIngredients(); //DONE: call populateIngredients(ingredients) from DBConnection class instead
        initializeEmployees(); //DONE: call populateEmployees(employees) from DBConnection class instead
        initializeOrders(); //DONE: call populateOrders(orders) from DBConnection class instead
        */
        connect.populateMenuItems(menuItems);
        connect.populateIngredients(ingredients);
        connect.populateEmployees(employees);
        connect.populateOrders(orders);


        // Set up the frame
        frame = new JFrame("Panda Express POS System - Manager");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        // Main panel
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        JPanel orderPanel = new JPanel();
        orderPanel.setLayout(new BoxLayout(orderPanel, BoxLayout.Y_AXIS));
        orderPanel.setBackground(Color.GRAY);

        displayOrders(orderPanel);

        mainPanel.add(orderPanel, BorderLayout.WEST);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());

        graphLabel = new JLabel(new ImageIcon("placeholder_graph.png"));  // Placeholder graph
        centerPanel.add(graphLabel, BorderLayout.CENTER);

        JButton orderIngredientsButton = new JButton("Order Ingredients");
        centerPanel.add(orderIngredientsButton, BorderLayout.SOUTH);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new GridLayout(3, 1));
        rightPanel.setBackground(Color.DARK_GRAY);

        menuButton = new JButton("Menu Management");
        inventoryButton = new JButton("Inventory Management");
        employeesButton = new JButton("Employee Management");

        // ####################################################
        // ACTION LISTENERS TO TRIGGER SUB VIEWS
        // ####################################################

        menuButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showMenuManagement();
            }
        });

        inventoryButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showIngredientManagement();
            }
        });

        employeesButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showEmployeeManagement();
            }
        });

        rightPanel.add(menuButton);
        rightPanel.add(inventoryButton);
        rightPanel.add(employeesButton);

        mainPanel.add(rightPanel, BorderLayout.EAST);

        // Add the main panel to the frame
        frame.add(mainPanel);
        frame.setVisible(true);
    }

    // ########################################################
    // MENU MANAGEMENT
    // ########################################################

    /* function done in DBConnection instead.
    // This needs to be edited to fetch stuff from the backend
    private void initializeMenuItems() { //DONE: call populateMenuItems(menuItems) from DBConnection class
        HashMap<String, Object> orangeChicken = new HashMap<>();
        orangeChicken.put("Name", "Orange Chicken");
        orangeChicken.put("Additional Cost", 0.0);
        orangeChicken.put("Entree", true);
        menuItems.add(orangeChicken);

        HashMap<String, Object> beefBroccoli = new HashMap<>();
        beefBroccoli.put("Name", "Beef with Broccoli");
        beefBroccoli.put("Additional Cost", 1.5);
        beefBroccoli.put("Entree", true);
        menuItems.add(beefBroccoli);
    }
    */

    private void populateTableModel() {
        tableModel.setRowCount(0); // Clear existing rows
        for (HashMap<String, Object> menuItem : menuItems) {
            String name = (String) menuItem.get("Name");
            Double additionalCost = (Double) menuItem.get("Additional Cost");
            Boolean isEntree = (Boolean) menuItem.get("Entree");
            tableModel.addRow(new Object[]{name, additionalCost, isEntree ? "Yes" : "No"});
        }
    }

    // Show form to add a new menu item
    private void showAddMenuItemForm() {
        JTextField nameField = new JTextField(10);
        JTextField costField = new JTextField(5);
        JCheckBox entreeCheckBox = new JCheckBox("Entree");

        JPanel panel = new JPanel();
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(Box.createHorizontalStrut(15)); // Spacer
        panel.add(new JLabel("Additional Cost:"));
        panel.add(costField);
        panel.add(Box.createHorizontalStrut(15)); // Spacer
        panel.add(entreeCheckBox);

        int result = JOptionPane.showConfirmDialog(frame, panel, "Add New Menu Item", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText();
            Double additionalCost = Double.parseDouble(costField.getText());
            Boolean isEntree = entreeCheckBox.isSelected();

            HashMap<String, Object> newItem = new HashMap<>();
            newItem.put("Name", name);
            newItem.put("Additional Cost", additionalCost);
            newItem.put("Entree", isEntree);
            menuItems.add(newItem);

            populateTableModel();
        }
    }

    // Show form to edit an existing menu item
    private void showEditMenuItemForm(int rowIndex) {
        HashMap<String, Object> menuItem = menuItems.get(rowIndex);

        JTextField nameField = new JTextField((String) menuItem.get("Name"), 10);
        JTextField costField = new JTextField(menuItem.get("Additional Cost").toString(), 5);
        JCheckBox entreeCheckBox = new JCheckBox("Entree", (Boolean) menuItem.get("Entree"));

        JPanel panel = new JPanel();
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(Box.createHorizontalStrut(15));
        panel.add(new JLabel("Additional Cost:"));
        panel.add(costField);
        panel.add(Box.createHorizontalStrut(15));
        panel.add(entreeCheckBox);

        int result = JOptionPane.showConfirmDialog(frame, panel, "Edit Menu Item", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText();
            Double additionalCost = Double.parseDouble(costField.getText());
            Boolean isEntree = entreeCheckBox.isSelected();

            menuItem.put("Name", name);
            menuItem.put("Additional Cost", additionalCost);
            menuItem.put("Entree", isEntree);

            populateTableModel();
        }
    }

    /*DONE: call sendMenuToBackend() in DBConnection
    // This also needs to send the entire menu to the backend 
    // For the demo, just avoid the ingredients
    private void sendMenuToBackend() {
        System.out.println("Sending menu to backend...");
        for (HashMap<String, Object> menuItem : menuItems) {
            System.out.println(menuItem);
        }
    }*/

    private void showMenuManagement() {
        String[] columnNames = {"Name", "Additional Cost", "Entree"};
    
        tableModel = new DefaultTableModel(columnNames, 0);
        populateTableModel();
    
        JTable menuTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(menuTable);
    
        JButton addButton = new JButton("Add New Item");
        JButton editButton = new JButton("Edit Selected Item");
        JButton saveButton = new JButton("Save");
    
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAddMenuItemForm();
            }
        });
    
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = menuTable.getSelectedRow();
                if (selectedRow != -1) {
                    showEditMenuItemForm(selectedRow);
                } else {
                    JOptionPane.showMessageDialog(frame, "Please select a menu item to edit.");
                }
            }
        });
    
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                connect.sendMenuToBackend(menuItems); 
            }
        });
    
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(saveButton);
    
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
    
        JOptionPane.showMessageDialog(frame, panel, "Menu Management", JOptionPane.PLAIN_MESSAGE);
    }
    
    // ########################################################
    // Ingredient Management
    // ########################################################

    /* function done in DBConnection instead.
    private void initializeIngredients() { //DONE: call populateIngredients(ingredients) from DBConnection class instead
        HashMap<String, Object> stringBeans = new HashMap<>();
        stringBeans.put("Name", "String Beans");
        stringBeans.put("threshold", 2800000);
        stringBeans.put("price", 0.02);
        stringBeans.put("unit", "g");
        stringBeans.put("quantity", 50000);
        ingredients.add(stringBeans);
    
        HashMap<String, Object> chicken = new HashMap<>();
        chicken.put("Name", "Chicken");
        chicken.put("threshold", 1500000);
        chicken.put("price", 0.05);
        chicken.put("unit", "g");
        chicken.put("quantity", 20000);
        ingredients.add(chicken);
    
        HashMap<String, Object> broccoli = new HashMap<>();
        broccoli.put("Name", "Broccoli");
        broccoli.put("threshold", 1000000);
        broccoli.put("price", 0.03);
        broccoli.put("unit", "g");
        broccoli.put("quantity", 30000);
        ingredients.add(broccoli);
    }
    */

    private void  populateIngredientTableModel() {
        tableModel.setRowCount(0);
        for (HashMap<String, Object> ingredient : ingredients) {
            String name = (String) ingredient.get("Name");
            Integer threshold = (Integer) ingredient.get("threshold");
            Double price = (Double) ingredient.get("price");
            String unit = (String) ingredient.get("unit");
            Integer quantity = (Integer) ingredient.get("quantity");
            tableModel.addRow(new Object[]{name, threshold, price, unit, quantity});
        }
    }

    private void showAddIngredientForm() {
        JTextField nameField = new JTextField(10);
        JTextField thresholdField = new JTextField(10);
        JTextField priceField = new JTextField(5);
        JTextField unitField = new JTextField(5);
        JTextField quantityField = new JTextField(5);
    
        JPanel panel = new JPanel(new GridLayout(5, 2));
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Threshold:"));
        panel.add(thresholdField);
        panel.add(new JLabel("Price:"));
        panel.add(priceField);
        panel.add(new JLabel("Unit:"));
        panel.add(unitField);
        panel.add(new JLabel("Quantity:"));
        panel.add(quantityField);
    
        int result = JOptionPane.showConfirmDialog(frame, panel, "Add New Ingredient", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText();
            Integer threshold = Integer.parseInt(thresholdField.getText());
            Double price = Double.parseDouble(priceField.getText());
            String unit = unitField.getText();
            Integer quantity = Integer.parseInt(quantityField.getText());

            HashMap<String, Object> newIngredient = new HashMap<>();
            newIngredient.put("Name", name);
            newIngredient.put("threshold", threshold);
            newIngredient.put("price", price);
            newIngredient.put("unit", unit);
            newIngredient.put("quantity", quantity);
            ingredients.add(newIngredient);
    
            populateIngredientTableModel();
        }
    }    

    private void showEditIngredientForm(int rowIndex) {
        HashMap<String, Object> ingredient = ingredients.get(rowIndex);
    
        JTextField nameField = new JTextField((String) ingredient.get("Name"), 10);
        JTextField thresholdField = new JTextField(ingredient.get("threshold").toString(), 10);
        JTextField priceField = new JTextField(ingredient.get("price").toString(), 5);
        JTextField unitField = new JTextField((String) ingredient.get("unit"), 5);
        JTextField quantityField = new JTextField(ingredient.get("quantity").toString(), 5);
    
        JPanel panel = new JPanel(new GridLayout(5, 2));
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Threshold:"));
        panel.add(thresholdField);
        panel.add(new JLabel("Price:"));
        panel.add(priceField);
        panel.add(new JLabel("Unit:"));
        panel.add(unitField);
        panel.add(new JLabel("Quantity:"));
        panel.add(quantityField);
    
        int result = JOptionPane.showConfirmDialog(frame, panel, "Edit Ingredient", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText();
            Integer threshold = Integer.parseInt(thresholdField.getText());
            Double price = Double.parseDouble(priceField.getText());
            String unit = unitField.getText();
            Integer quantity = Integer.parseInt(quantityField.getText());
    
            ingredient.put("Name", name);
            ingredient.put("threshold", threshold);
            ingredient.put("price", price);
            ingredient.put("unit", unit);
            ingredient.put("quantity", quantity);
    
            populateIngredientTableModel();
        }
    }    

    /*DONE: call sendMenuToBackend() in DBConnection
    // Again, will probably just send this to backend and just update the whole table
    private void sendIngredientsToBackend() {
        System.out.println("Sending ingredients to backend...");
        for (HashMap<String, Object> ingredient : ingredients) {
            System.out.println(ingredient);
        }
    }*/

    private void showIngredientManagement() {
        String[] columnNames = {"Name", "Threshold", "Price", "Unit", "Quantity"};
    
        tableModel = new DefaultTableModel(columnNames, 0); 
        populateIngredientTableModel(); 
    
        JTable ingredientTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(ingredientTable);

        JButton addButton = new JButton("Add New Ingredient");
        JButton editButton = new JButton("Edit Selected Ingredient");
        JButton saveButton = new JButton("Save");
    
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAddIngredientForm();
            }
        });
    
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = ingredientTable.getSelectedRow();
                if (selectedRow != -1) {
                    showEditIngredientForm(selectedRow);
                } else {
                    JOptionPane.showMessageDialog(frame, "Please select an ingredient to edit.");
                }
            }
        });
    
        // save button action listener (for now, just send the entire ingredient list to backend)
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                connect.sendIngredientsToBackend(ingredients);
            }
        });
    
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(saveButton);
    
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
    
        JOptionPane.showMessageDialog(frame, panel, "Ingredient Management", JOptionPane.PLAIN_MESSAGE);
    }  
    
    // ########################################################
    // EMPLOYEE MANAGEMENT
    // ########################################################

    /* function done in DBConnection instead.
    private void initializeEmployees() { //DONE: call populateEmployees(employees) from DBConnection class instead
        HashMap<String, Object> zophous = new HashMap<>();
        zophous.put("id", 1);
        zophous.put("username", "Zophous");
        zophous.put("pin", 1111);
        zophous.put("manager", true);
        employees.add(zophous);
    
        HashMap<String, Object> fishy = new HashMap<>();
        fishy.put("id", 2);
        fishy.put("username", "Fishy");
        fishy.put("pin", 3474);
        fishy.put("manager", false);
        employees.add(fishy);
    
        HashMap<String, Object> timtak = new HashMap<>();
        timtak.put("id", 3);
        timtak.put("username", "Timtak");
        timtak.put("pin", 2222);
        timtak.put("manager", false);
        employees.add(timtak);
    
        HashMap<String, Object> smiles = new HashMap<>();
        smiles.put("id", 4);
        smiles.put("username", "Smiles");
        smiles.put("pin", 3333);
        smiles.put("manager", false);
        employees.add(smiles);
    }    
    */

    private void populateEmployeeTableModel() {
        tableModel.setRowCount(0);
        for (HashMap<String, Object> employee : employees) {
            Integer id = (Integer) employee.get("id");
            String username = (String) employee.get("username");
            Integer pin = (Integer) employee.get("pin");
            Boolean isManager = (Boolean) employee.get("manager");
            tableModel.addRow(new Object[]{id, username, pin, isManager ? "Yes" : "No"});
        }
    }

    private void showAddEmployeeForm() {
        JTextField usernameField = new JTextField(10);
        JTextField pinField = new JTextField(5);
        JCheckBox managerCheckBox = new JCheckBox("Manager");
    
        JPanel panel = new JPanel(new GridLayout(3, 2));
        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("PIN:"));
        panel.add(pinField);
        panel.add(managerCheckBox);
    
        int result = JOptionPane.showConfirmDialog(frame, panel, "Add New Employee", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String username = usernameField.getText();
            Integer pin = Integer.parseInt(pinField.getText());
            Boolean isManager = managerCheckBox.isSelected();
    
            Integer id = employees.size() + 1;
    
            HashMap<String, Object> newEmployee = new HashMap<>();
            newEmployee.put("id", id);
            newEmployee.put("username", username);
            newEmployee.put("pin", pin);
            newEmployee.put("manager", isManager);
            employees.add(newEmployee);
    
            populateEmployeeTableModel();
        }
    }    

    private void showEditEmployeeForm(int rowIndex) {
        HashMap<String, Object> employee = employees.get(rowIndex);
    
        JTextField usernameField = new JTextField((String) employee.get("username"), 10);
        JTextField pinField = new JTextField(employee.get("pin").toString(), 5);
        JCheckBox managerCheckBox = new JCheckBox("Manager", (Boolean) employee.get("manager"));
    
        JPanel panel = new JPanel(new GridLayout(3, 2));
        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("PIN:"));
        panel.add(pinField);
        panel.add(managerCheckBox);
    
        int result = JOptionPane.showConfirmDialog(frame, panel, "Edit Employee", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String username = usernameField.getText();
            Integer pin = Integer.parseInt(pinField.getText());
            Boolean isManager = managerCheckBox.isSelected();
    
            employee.put("username", username);
            employee.put("pin", pin);
            employee.put("manager", isManager);
    
            populateEmployeeTableModel();
        }
    }

    /*DONE: call sendMenuToBackend() in DBConnection
    private void sendEmployeesToBackend() {
        System.out.println("Sending employees to backend...");
        for (HashMap<String, Object> employee : employees) {
            System.out.println(employee);
        }
    }*/
    
    private void showEmployeeManagement() {
        String[] columnNames = {"ID", "Username", "PIN", "Manager"};
    
        tableModel = new DefaultTableModel(columnNames, 0); 
        populateEmployeeTableModel();
    
        JTable employeeTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(employeeTable);
    
        JButton addButton = new JButton("Add New Employee");
        JButton editButton = new JButton("Edit Selected Employee");
        JButton saveButton = new JButton("Save");
    
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAddEmployeeForm();
            }
        });
    
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = employeeTable.getSelectedRow();
                if (selectedRow != -1) {
                    showEditEmployeeForm(selectedRow);
                } else {
                    JOptionPane.showMessageDialog(frame, "Please select an employee to edit.");
                }
            }
        });

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                connect.sendEmployeesToBackend(employees);
            }
        });
    
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(saveButton);
    
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
    
        JOptionPane.showMessageDialog(frame, panel, "Employee Management", JOptionPane.PLAIN_MESSAGE);
    }    

    // ########################################################
    // ORDER DISPLAY
    // ########################################################

    /* function done in DBConnection instead.
    private void initializeOrders() { //DONE: call populateOrders(orders) from DBConnection class instead
        HashMap<String, Object> order1 = new HashMap<>();
        order1.put("id", 1);
        order1.put("server", "Zophous");
        order1.put("price", 12.99);
        order1.put("type", "Plate");
        order1.put("timestamp", "2024-10-10 12:30:00");
        orders.add(order1);
    
        HashMap<String, Object> order2 = new HashMap<>();
        order2.put("id", 2);
        order2.put("server", "Fishy");
        order2.put("price", 10.99);
        order2.put("type", "Bowl + Med Drink");
        order2.put("timestamp", "2024-10-10 12:35:00");
        orders.add(order2);
    
        HashMap<String, Object> order3 = new HashMap<>();
        order3.put("id", 3);
        order3.put("server", "Timtak");
        order3.put("price", 15.99);
        order3.put("type", "Double Plate");
        order3.put("timestamp", "2024-10-10 12:40:00");
        orders.add(order3);
    }
    */

    private void displayOrders(JPanel orderPanel) {
        orderPanel.removeAll();

        orderPanel.add(new JLabel("Orders"));
    
        for (HashMap<String, Object> order : orders) {
            String type = (String) order.get("type");
            Double price = (Double) order.get("price");
            orderPanel.add(new JLabel(type + ": $" + price));
        }
    
        orderPanel.revalidate();
        orderPanel.repaint();
    }

    public static void main(String[] args) {
        new ManagerFrame();
    }
}
