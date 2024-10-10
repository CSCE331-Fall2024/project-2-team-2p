import java.time.LocalDate;
import java.sql.*;

public class Order {
    public int id;
    public int server;
    public double price;
    public int type;
    public Date timestamp;

    public Order(int id, int server, double price, int type) {
        this.id = id;
        this.server = server;
        this.price = price;
        this.type = type;
        LocalDate time = LocalDate.now();
        timestamp = Date.valueOf(time);
    }
}
