import java.time.LocalDate;
import java.time.LocalDateTime;
import java.sql.*;

public class Order {
    public int id;
    public int server;
    public double price;
    public int type;
    public Timestamp timestamp;

    public Order(int id, int server, double price, int type) {
        this.id = id;
        this.server = server;
        this.price = price;
        this.type = type;
        LocalDateTime time = LocalDateTime.now();
        timestamp = Timestamp.valueOf(time);
    }
}
