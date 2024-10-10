import java.sql.*;

public class DBConnection {
    private Connection conn = null;
    private static final String user = "team_2p";
    private static final String password = "pawmo";
    private static final String dbName = "team_2p_db";
    String dbConnectionString = "jdbc:postgresql://csce-315-db.engr.tamu.edu/" + dbName;
    public boolean manager = false;

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
}
