import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DBConnection {
    private Connection conn = null;
    private static final String user = "team_2p";
    private static final String password = "pawmo";
    private static final String dbName = "team_2p_db";
    private String dbConnectionString = "jdbc:postgresql://csce-315-db.engr.tamu.edu/" + dbName;
    public boolean manager = false;
    private String employee;

    public DBConnection(boolean manager) {
        this.manager = manager;
        try {
            conn = DriverManager.getConnection(dbConnectionString, user, password);
        } 
        catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
    }

    /***
     * Verifies whether an employees username and pin are correct and whether they are a manager or not
     * @author Myles
     * @param user 
     * @param pin
     * @return true if the credentials are valid and the manager status matches, false otherwise.
     * @throws SQLException
     */
    public boolean verifyCredentials(String user, int pin) {
        ResultSet result = null;
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("SELECT * FROM employees WHERE username = ? AND pin = ?");
            stmt.setString(1, user);
            stmt.setInt(2, pin);
            result = stmt.executeQuery();
            
            if (result.next()) {
                boolean isManager = result.getBoolean("manager");
                manager = (isManager) ? true : false;
                employee = user;
                result.close();
                stmt.close();
                return true;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            result.close();
            stmt.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    /***
     * Creates order object which can then be used in the placeOrder method
     * @author Myles
     * @param orderType 0 if bowl, 1 if plate, 2 if bigger plate
     * @param entrees All entrees that make up this order, do not include sides here
     * @return Completed order object
     * @throws SQLException
     */
    public Order createOrder(int orderType, String[] entrees) {
        ResultSet result = null;
        int id = 1;
        double price = 0;
        try {
            Statement stmt = conn.createStatement();
            result = stmt.executeQuery("SELECT MAX(id) FROM orders");
            if (result.next()) {
                id = result.getInt(1) + 1;
            }
            stmt.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        } 

        // calculate price 
        if (orderType == 0) {
            price += 8.99;
        }
        else if (orderType == 1) {
            price += 9.99;
        }
        else {
            price += 11.99;
        }
        try {
            PreparedStatement stmt = null;
            for (int i=0; i<entrees.length; ++i) {
                stmt = conn.prepareStatement("SELECT price FROM menuitems WHERE name = ?");
                stmt.setString(1, entrees[i]);
                result = stmt.executeQuery();
                if (result.next()) {
                    price += result.getDouble("price");
                }
            }
            stmt.close();
            result.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        Order order = new Order(id, getServerID(employee), price, orderType);

        return order;
    }

    /**
     * Writes order to database as well as updates ingredients and menuitemsorders tables
     * @author Myles 
     * @param order order object that is created using createOrder method
     * @param entrees All entrees that make up this order, do not include sides here
     * @param sides do not include entrees here
     * @throws SQLException
     */
    public void placeOrder(Order order, String[] entrees, String[] sides) {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO orders (id, server, price, type, timestamp) VALUES (?, ?, ?, ?, ?)");
            stmt.setInt(1, order.id);
            stmt.setInt(2, order.server);
            stmt.setDouble(3, order.price);
            stmt.setInt(4, order.type);
            stmt.setTimestamp(5, order.timestamp);
            stmt.executeUpdate(); 
            stmt.close(); 
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        ArrayList<Integer> menuitemskeys = update_menuitemsorders_table(entrees, sides, order.id);
        update_ingredients_table(menuitemskeys);
    }

    /**
     * gets the maximum id for a specified table
     * @author Myles
     * @param tableName
     * @return integer of the max id
     * @throws SQLException
     */
    public int getMaxID(String tableName) {
        PreparedStatement stmt = null;
        ResultSet result = null;
        int maxID = -1;
        String query = "SELECT MAX(id) FROM " + tableName;
        try {
            stmt = conn.prepareStatement(query);
            result = stmt.executeQuery();
            if (result.next()) {
                maxID = result.getInt(1);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return maxID;
    }

    /**
     * based on a string query, executes it and populates an Array of Hashmaps with the result
     * @author Myles
     * @param query 
     * @param array 
     * @return ArrayList of a Hashmap with results of the query 
     * @throws SQLException
     */
    public ArrayList<HashMap<String, Object>> executeQuery(String query, ArrayList<HashMap<String, Object>> array) {
        Statement stmt = null;
        ArrayList<HashMap<String, Object>> resultList = new ArrayList<>();

        try {
            stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery(query);

            ResultSetMetaData metaData = result.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (result.next()) {
                HashMap<String, Object> row = new HashMap<>();
                
                // For each column in the row, add it to the HashMap
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    Object value = result.getObject(i);
                    row.put(columnName, value);
                }
                resultList.add(row);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        
        return resultList;
    }

    /**
     * @author Myles
     * Closes db connection
     * @throws SQLException
     */
    public void close() {
        try {
            conn.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int getServerID(String employee) {
        ResultSet result = null;
        PreparedStatement stmt = null;
        int server = 1;
        try {
            stmt = conn.prepareStatement("SELECT id FROM employees WHERE username = ?");
            stmt.setString(1, employee);
            result = stmt.executeQuery();
            if (result.next()) {
                server = result.getInt("id");
            }
            stmt.close();
            result.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return server;
    }

    private ArrayList<Integer> update_menuitemsorders_table(String[] entrees, String[] sides, int orderid) {
        PreparedStatement stmt = null;
        ResultSet result = null;
        ArrayList<Integer> menuitemskeys = new ArrayList<>();
        try {
            for (int i=0; i<entrees.length; ++i) {
                stmt = conn.prepareStatement("SELECT id FROM menuitems WHERE name = ?");
                stmt.setString(1, entrees[i]);
                result = stmt.executeQuery();
                if (result.next()) {
                    menuitemskeys.add(result.getInt("id"));
                }
            }
            for (int i=0; i<sides.length; ++i) {
                stmt = conn.prepareStatement("SELECT id FROM menuitems WHERE name = ?");
                stmt.setString(1, sides[i]);
                result = stmt.executeQuery();
                if (result.next()) {
                    menuitemskeys.add(result.getInt("id"));
                }
            }
            stmt.close();
            result.close();

            // add menuitemskeys to menuitemsorders table with corresponding orderid
            Statement maxStatement = conn.createStatement();
            int id = 1;
            for (int menuitemkey : menuitemskeys) {
                result = maxStatement.executeQuery("SELECT MAX(id) FROM menuitemsorders");
                if (result.next()) {
                    id = result.getInt(1) + 1; 
                }
                stmt = conn.prepareStatement("INSERT INTO menuitemsorders (id, menuitemkey, orderkey) VALUES (?, ?, ?)");
                stmt.setInt(1, id);
                stmt.setInt(2, menuitemkey); 
                stmt.setInt(3, orderid); 
                stmt.executeUpdate(); 
                stmt.close(); 
            }
            maxStatement.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        
        return menuitemskeys;
    }

    private void update_ingredients_table(ArrayList<Integer> menuitemskeys) {
        HashMap<Integer, Integer> ingredientMap = new HashMap<>();
        PreparedStatement stmt = null;
        ResultSet result = null;

        try {
            StringBuilder sql = new StringBuilder(
                "SELECT ingredientkey, quantity FROM ingredientsmenuitems WHERE menuitemkey IN ("
            );

            for (int i = 0; i < menuitemskeys.size(); i++) {
                sql.append("?");
                if (i < menuitemskeys.size() - 1) {
                    sql.append(", ");
                }
            }
            sql.append(");");
            stmt = conn.prepareStatement(sql.toString());
            for (int i = 0; i < menuitemskeys.size(); i++) {
                stmt.setInt(i + 1, menuitemskeys.get(i));
            }

            result = stmt.executeQuery();

            while (result.next()) {
                int ingredientId = result.getInt("ingredientkey"); 
                int quantity = result.getInt("quantity");         
                ingredientMap.merge(ingredientId, quantity, Integer::sum);
            }

            // Update stock in ingredients table
            result.close();
            stmt.close();

            for (HashMap.Entry<Integer, Integer> entry : ingredientMap.entrySet()) {
                int ingredientId = entry.getKey();
                int usedQuantity = entry.getValue();

                stmt = conn.prepareStatement("SELECT stock FROM ingredients WHERE id = ?");
                stmt.setInt(1, ingredientId);
                result = stmt.executeQuery();

                if (result.next()) {
                    int currentQuantity = result.getInt("quantity");
                    int newQuantity = currentQuantity - usedQuantity;

                    stmt = conn.prepareStatement("UPDATE ingredients SET stock = ? WHERE id = ?");
                    stmt.setInt(1, newQuantity);
                    stmt.setInt(2, ingredientId);
                    stmt.executeUpdate();
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Pulls menu items from database
     * @author Matthew Fisher
     * @param menuItems vector to fill in with menu items
     */
    public void populateMenuItems(ArrayList<HashMap<String, Object>> menuItems){
        ResultSet result = null;
        PreparedStatement stmt = null;
        try {
            String sql = "select * from menuitems";
            stmt = conn.prepareStatement(sql);
            result = stmt.executeQuery();
 
            while (result.next()) {
                int id = result.getInt("id");
                String name = result.getString("name");
                double price = result.getDouble("price");
                int entree = result.getInt("entree");
                HashMap<String, Object> currentMenuItem = new HashMap<>();
                currentMenuItem.put("id", id);
                currentMenuItem.put("Name", name);
                currentMenuItem.put("Additional Cost", price);
                currentMenuItem.put("Entree", entree);
                menuItems.add(currentMenuItem);
            }
            
            stmt.close();
            result.close();
        }
        catch (SQLException e) {
             System.out.println(e);
        }
    }

    /**
     * Sends updated menu items to database
     * @author Matthew Fisher
     * @param menuItems vector with menu items
     * @param ingredientsmenuitems vectore with ingredientsmenuitems
     */
    public void sendMenuToBackend(ArrayList<HashMap<String, Object>> menuItems, HashMap<Integer, ArrayList<Integer>> ingredientsmenuitems){
        System.out.println("Sending menu to backend...");
        PreparedStatement stmt = null;
        try {
            // Insert new menu items
            String insertMenuItemSQL = "INSERT INTO menuitems (id, name, price, entree) VALUES (?, ?, ?, ?)" +
            "ON CONFLICT (id) " +
            "DO UPDATE SET name = EXCLUDED.name, price = EXCLUDED.price, entree = EXCLUDED.entree";
            stmt = conn.prepareStatement(insertMenuItemSQL);
            
            for (HashMap<String, Object> menuItem : menuItems) {
                stmt.setInt(1, (Integer) menuItem.get("id"));
                stmt.setString(2, (String) menuItem.get("Name"));
                stmt.setDouble(3, (Double) menuItem.get("Additional Cost"));
                stmt.setInt(4, (Integer) menuItem.get("Entree"));
                stmt.executeUpdate();
            }

            // update ingredientsmenuitems table
            String query = "INSERT INTO ingredientsmenuitems (id, ingredientkey, menuitemkey, quantity) VALUES (?, ?, ?, ?)";
            stmt = conn.prepareStatement(query);

            int id = this.getMaxID("ingredientsmenuitems") + 1;

            for (HashMap.Entry<Integer, ArrayList<Integer>> menuitem : ingredientsmenuitems.entrySet()) {
                ArrayList<Integer> ingredientkeys =  menuitem.getValue();
                for (Integer key : ingredientkeys) {
                    stmt.setInt(1, id);
                    stmt.setInt(2, key);
                    stmt.setInt(3, menuitem.getKey());
                    stmt.setInt(4, 100);
                    stmt.executeUpdate();
                    ++id;
                }
            }
            stmt.close();
            System.out.println("Menu Items sent to backend successfully.");
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Pulls ingredients from database
     * @author Matthew Fisher
     * @param ingredients vector to fill in with ingredients
     */
    public void populateIngredients(ArrayList<HashMap<String, Object>> ingredients){
        ResultSet result = null;
        PreparedStatement stmt = null;
        try {
            String sql = "select * from ingredients";
            stmt = conn.prepareStatement(sql);
            result = stmt.executeQuery();
 
            while (result.next()) {
                int id = result.getInt("id");
                String name = result.getString("name");
                int stock = result.getInt("stock");
                int threshold = result.getInt("threshold");
                double price = result.getDouble("price");
                String unit = result.getString("unit");
                HashMap<String, Object> currentIngredient = new HashMap<>();
                currentIngredient.put("id", id);
                currentIngredient.put("name", name);
                currentIngredient.put("stock", stock);
                currentIngredient.put("threshold", threshold);
                currentIngredient.put("price", price);
                currentIngredient.put("unit", unit);
                ingredients.add(currentIngredient);
            }

            stmt.close();
            result.close();
        }
        catch (SQLException e) {
             System.out.println(e);
        }
    }

    /**
     * Sends updated ingredients to database
     * @author Matthew Fisher
     * @param ingredients vector with menu items
     */
    public void sendIngredientsToBackend(ArrayList<HashMap<String, Object>> ingredients) {
        System.out.println("Sending ingredients to backend...");
        PreparedStatement stmt = null;
    
        try {
            // Insert new ingredients
            String insertIngredientSQL = "INSERT INTO ingredients (id, name, stock, threshold, price, unit) VALUES (?, ?, ?, ?, ?, ?) " +
            "ON CONFLICT (id) " +
            "DO UPDATE SET name = EXCLUDED.name, stock = EXCLUDED.stock, threshold = EXCLUDED.threshold, price = EXCLUDED.price, unit = EXCLUDED.unit";
            stmt = conn.prepareStatement(insertIngredientSQL);
    
            for (HashMap<String, Object> ingredient : ingredients) {
                stmt.setInt(1, (Integer) ingredient.get("id"));
                stmt.setString(2, (String) ingredient.get("name"));
                stmt.setInt(3, (Integer) ingredient.get("stock"));
                stmt.setInt(4, (Integer) ingredient.get("threshold"));
                stmt.setDouble(5, (Double) ingredient.get("price"));
                stmt.setString(6, (String) ingredient.get("unit"));
                stmt.executeUpdate();
            }

            stmt.close();
            System.out.println("Ingredients sent to backend successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Pulls employees from database
     * @author Matthew Fisher
     * @param employees vector to fill in with employees
     */
    public void populateEmployees(ArrayList<HashMap<String, Object>> employees){
        ResultSet result = null;
        PreparedStatement stmt = null;
        try {
            String sql = "SELECT * FROM employees";
            stmt = conn.prepareStatement(sql);
            result = stmt.executeQuery();
 
            while (result.next()) {
                int id = result.getInt("id");
                String username = result.getString("username");
                int pin = result.getInt("pin");
                boolean manager = result.getBoolean("manager");
                HashMap<String, Object> currentEmployee = new HashMap<>();
                currentEmployee.put("id", id);
                currentEmployee.put("username", username);
                currentEmployee.put("pin", pin);
                currentEmployee.put("manager", manager);
                employees.add(currentEmployee);
            }
            
            stmt.close();
            result.close();
        }
        catch (SQLException e) {
             System.out.println(e);
        }
    }

    /**
     * Sends updated employees database
     * @author Matthew Fisher
     * @param employees vector with employees
     */
    public void sendEmployeesToBackend(ArrayList<HashMap<String, Object>> employees) {
        System.out.println("Sending employees to backend...");
        PreparedStatement stmt = null;
    
        try {
            // Insert new employees
            String insertEmployeeSQL = "INSERT INTO employees (id, username, pin, manager) VALUES (?, ?, ?, ?) " +
            "ON CONFLICT (id) " +
            "DO UPDATE SET username = EXCLUDED.username, pin = EXCLUDED.pin, manager = EXCLUDED.manager";
            stmt = conn.prepareStatement(insertEmployeeSQL);
    
            for (HashMap<String, Object> employee : employees) {
                stmt.setInt(1, (Integer) employee.get("id"));
                stmt.setString(2, (String) employee.get("username"));
                stmt.setInt(3, (Integer) employee.get("pin"));
                stmt.setBoolean(4, (Boolean) employee.get("manager"));
                stmt.executeUpdate();
            }

            stmt.close();
            System.out.println("Employees sent to backend successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Pulls orders from database
     * @author Matthew Fisher
     * @param orders vector to fill in with orders
     */
    public void populateOrders(ArrayList<HashMap<String, Object>> orders){
        ResultSet result = null;
        PreparedStatement stmt = null;
        try {
            String sql = "select * from orders LIMIT 30";
            stmt = conn.prepareStatement(sql);
            result = stmt.executeQuery();
 
            while (result.next()) {
                int id = result.getInt("id");
                int server = result.getInt("server");
                double price = result.getDouble("price");
                int type = result.getInt("type");
                Timestamp timestamp = result.getTimestamp("timestamp");
                HashMap<String, Object> currentOrder = new HashMap<>();
                currentOrder.put("id", id);
                currentOrder.put("server", server);
                currentOrder.put("price", price);
                currentOrder.put("type", type);
                currentOrder.put("timestamp", timestamp);
                orders.add(currentOrder);
            }
            
            stmt.close();
            result.close();
        }
        catch (SQLException e) {
             System.out.println(e);
        }
    }
    
}

    // public static void main(String[] args) {
    //     DBConnection connect = new DBConnection(false);
    //     connect.verifyCredentials("Smiles", 3333);
    //     String[] entrees = new String[3];
    //     String[] sides = new String[1];
    //     entrees[0] = "Orange Chicken";
    //     entrees[1] = "Honey Walnut Shrimp";
    //     entrees[2] = "Teriyaki Chicken";
    //     sides[0] = "Fried Rice";
    //     Order order = connect.createOrder(2, entrees);
    //     connect.placeOrder(order, entrees, sides);
    //     connect.close();
    // }

