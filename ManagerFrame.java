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
    private HashMap<Integer, ArrayList<Integer>> ingredientsmenuitems = new HashMap<>();
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

    public ManagerFrame(String username, int pin) {
        // initializing data structures for the popups
        menuItems = new ArrayList<>();
        ingredients = new ArrayList<>();
        employees = new ArrayList<>();
        orders = new ArrayList<>();

        //connect to database
        connect = new DBConnection(true); //true for manager view
        connect.verifyCredentials(placeholdUsername, placeholdPin);

        /* functions done in DBConnection instead. TODO: ensure correctness
        */
        connect.populateMenuItems(menuItems);
        connect.populateIngredients(ingredients);
        connect.populateEmployees(employees);
        connect.populateOrders(orders);


        // Set up the frame
        frame = new JFrame("Panda Express POS System - Manager");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setBackground(new java.awt.Color(255, 51, 0));

        // Main panel
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        JPanel orderPanel = new JPanel();
        orderPanel.setLayout(new BoxLayout(orderPanel, BoxLayout.Y_AXIS));
        orderPanel.setBackground(new java.awt.Color(255, 51, 0));

        displayOrders(orderPanel);

        mainPanel.add(orderPanel, BorderLayout.WEST);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());

        graphLabel = new JLabel(new ImageIcon("placeholder_graph.png"));  // Placeholder graph
        centerPanel.add(graphLabel, BorderLayout.CENTER);

        JButton orderIngredientsButton = new JButton("Order Ingredients");
        orderIngredientsButton.setBackground(new java.awt.Color(255, 51, 0));
        centerPanel.add(orderIngredientsButton, BorderLayout.SOUTH);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new GridLayout(3, 1));
        rightPanel.setBackground(new java.awt.Color(255, 51, 0));

        menuButton = new JButton("Menu Management");
        menuButton.setBackground(new java.awt.Color(255, 51, 0));
        inventoryButton = new JButton("Inventory Management");
        inventoryButton.setBackground(new java.awt.Color(255, 51, 0));
        employeesButton = new JButton("Employee Management");
        employeesButton.setBackground(new java.awt.Color(255, 51, 0));

        // ####################################################
        // ACTION LISTENERS TO TRIGGER SUB VIEWS
        // ####################################################

        orderIngredientsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean status = connect.orderIngredients();
                if (status) {
                    JOptionPane.showMessageDialog(frame, "Ingredients Ordered Successfully", "Popup", JOptionPane.INFORMATION_MESSAGE);
                }
                else {
                    JOptionPane.showMessageDialog(frame, "Error Ordering Ingredients", "Popup", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        
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

    private void populateTableModel() {
        tableModel.setRowCount(0); // Clear existing rows
        for (HashMap<String, Object> menuItem : menuItems) {
            String name = (String) menuItem.get("Name");
            Double additionalCost = (Double) menuItem.get("Additional Cost");
            Integer isEntree = (Integer) menuItem.get("Entree");
            tableModel.addRow(new Object[]{name, additionalCost, (isEntree == 1) ? "Yes" : "No"});
        }
    }

    // Show form to add a new menu item
    private void showAddMenuItemForm() {
        JTextField nameField = new JTextField(10);
        JTextField costField = new JTextField(5);
        JCheckBox entreeCheckBox = new JCheckBox("Entree?");

        JPanel panel = new JPanel();
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(Box.createHorizontalStrut(15)); // Spacer
        panel.add(new JLabel("Additional Cost:"));
        panel.add(costField);
        panel.add(Box.createHorizontalStrut(15)); // Spacer
        panel.add(entreeCheckBox);

        JPanel ingredientsPanel = new JPanel();
        ingredientsPanel.setLayout(new GridLayout(0, 3));  
        panel.add(new JLabel("Select Ingredients:"));
        HashMap<Integer, JCheckBox> checkboxes = new HashMap<>();
        for (HashMap<String, Object> ingredient : ingredients) {
            JCheckBox checkBox = new JCheckBox((String) ingredient.get("name"));
            checkboxes.put((Integer) ingredient.get("id"), checkBox);
            ingredientsPanel.add(checkBox);
        }
        panel.add(ingredientsPanel);

        int result = JOptionPane.showConfirmDialog(frame, panel, "Add New Menu Item", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText();
            Double additionalCost = Double.parseDouble(costField.getText());
            Integer isEntree = entreeCheckBox.isSelected() ? 1 : 0;

            HashMap<String, Object> newItem = new HashMap<>();
            newItem.put("id", menuItems.size()+1);
            newItem.put("Name", name);
            newItem.put("Additional Cost", additionalCost);
            newItem.put("Entree", isEntree);
            menuItems.add(newItem);

            Integer maxID = connect.getMaxID("menuitems") + 1;

            ArrayList<Integer> newIngredients = new ArrayList<>();
            for (HashMap.Entry<Integer, JCheckBox> entry : checkboxes.entrySet()) {
                if (entry.getValue().isSelected()) {
                    newIngredients.add(entry.getKey());
                }
            }
            ingredientsmenuitems.put(maxID, newIngredients);

            populateTableModel();
        }
    }

    // Show form to edit an existing menu item
    private void showEditMenuItemForm(int rowIndex) {
        HashMap<String, Object> menuItem = menuItems.get(rowIndex);

        JTextField nameField = new JTextField((String) menuItem.get("Name"), 10);
        JTextField costField = new JTextField(menuItem.get("Additional Cost").toString(), 5);
        JCheckBox entreeCheckBox = new JCheckBox("Entree", (Boolean) ((Integer) menuItem.get("Entree") == 1) ? true : false );

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
            Integer isEntree = entreeCheckBox.isSelected() ? 1 : 0;

            menuItem.put("Name", name);
            menuItem.put("Additional Cost", additionalCost);
            menuItem.put("Entree", isEntree);

            populateTableModel();
        }
    }

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
                connect.sendMenuToBackend(menuItems, ingredientsmenuitems); 
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

    private void  populateIngredientTableModel() {
        tableModel.setRowCount(0);
        for (HashMap<String, Object> ingredient : ingredients) {
            String name = (String) ingredient.get("name");
            Integer threshold = (Integer) ingredient.get("threshold");
            Double price = (Double) ingredient.get("price");
            String unit = (String) ingredient.get("unit");
            Integer quantity = (Integer) ingredient.get("stock");
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

            int id = ingredients.size() + 1;

            HashMap<String, Object> newIngredient = new HashMap<>();
            newIngredient.put("id", id);
            newIngredient.put("name", name);
            newIngredient.put("threshold", threshold);
            newIngredient.put("price", price);
            newIngredient.put("unit", unit);
            newIngredient.put("stock", quantity);
            ingredients.add(newIngredient);
    
            populateIngredientTableModel();
        }
    }    

    private void showEditIngredientForm(int rowIndex) {
        HashMap<String, Object> ingredient = ingredients.get(rowIndex);
    
        JTextField nameField = new JTextField((String) ingredient.get("name"), 10);
        JTextField thresholdField = new JTextField(ingredient.get("threshold").toString(), 10);
        JTextField priceField = new JTextField(ingredient.get("price").toString(), 5);
        JTextField unitField = new JTextField((String) ingredient.get("unit"), 5);
        JTextField quantityField = new JTextField(ingredient.get("stock").toString(), 5);
    
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
            
            ingredient.put("id", ingredients.size() + 1);
            ingredient.put("name", name);
            ingredient.put("threshold", threshold);
            ingredient.put("price", price);
            ingredient.put("unit", unit);
            ingredient.put("stock", quantity);
    
            populateIngredientTableModel();
        }
    }    

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

    private void displayOrders(JPanel orderPanel) {
        orderPanel.removeAll();

        orderPanel.add(new JLabel("Orders"));
    
        for (HashMap<String, Object> order : orders) {
            String type = order.get("type").toString();
            Double price = (Double) order.get("price");
            orderPanel.add(new JLabel(type + ": $" + price));
        }
    
        orderPanel.revalidate();
        orderPanel.repaint();
    }

    public static void main(String[] args) {
        new ManagerFrame("Zophous", 1111);
    }
}
