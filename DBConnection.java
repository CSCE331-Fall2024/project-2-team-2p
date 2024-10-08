import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

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
     * @param user 
     * @param pin
     * @return true if the credentials are valid and the manager status matches, false otherwise.
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
                if (isManager == manager) {
                    employee = user;
                    result.close();
                    stmt.close();
                    return true;
                }
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
     * @param orderType 0 if bowl, 1 if plate, 2 if bigger plate
     * @param entrees All entrees that make up this order, do not include sides here
     * @return Completed order object
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
     * @param order order object that is created using createOrder method
     * @param entrees All entrees that make up this order, do not include sides here
     * @param sides do not include entrees here
     */
    public void placeOrder(Order order, String[] entrees, String[] sides) {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO orders (id, server, price, type, timestamp) VALUES (?, ?, ?, ?, ?)");
            stmt.setInt(1, order.id);
            stmt.setInt(2, order.server);
            stmt.setDouble(3, order.price);
            stmt.setInt(4, order.type);
            stmt.setDate(5, order.timestamp);
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
     * Closes db connection
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
}
