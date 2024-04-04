package app.persistence;

import app.entities.Order;
import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderMapper
{
    public static List<Order> loadOrders(ConnectionPool connectionPool) throws DatabaseException
    {
        List<Order> listOfOrders = new ArrayList<>();

        String sql = "SELECT\n" +
                "    o.order_id,\n" +
                "    u.email AS user_email,\n" +
                "    t.type AS topping_type,\n" +
                "    b.type AS bottom_type,\n" +
                "    ol.quantity,\n" +
                "    (t.price + b.price) * ol.quantity AS total_price\n" +
                "FROM\n" +
                "    public.\"orders\" o\n" +
                "JOIN\n" +
                "    public.users u ON o.users_id = u.users_id\n" +
                "JOIN\n" +
                "    public.orderline ol ON o.order_id = ol.order_id\n" +
                "JOIN\n" +
                "    public.topping t ON ol.topping_id = t.topping_id\n" +
                "JOIN\n" +
                "    public.bottom b ON ol.bottom_id = b.bottom_id";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ResultSet rs = ps.executeQuery();
            while (rs.next())
            {
                int orderId = rs.getInt("order_id");
                String userEmail = rs.getString("user_email");
                String toppingType = rs.getString("topping_type");
                String bottomType = rs.getString("bottom_type");
                int quantity = rs.getInt("quantity");
                int totalPrice = (int) rs.getDouble("total_price");

                Order order = new Order(orderId, userEmail, toppingType, bottomType, quantity, totalPrice);
                listOfOrders.add(order);
            }
        } catch (SQLException e)
        {
            e.printStackTrace(); // Tilføj denne linje for at logge fejlen
            throw new DatabaseException("Fejl ved indlæsning af ordrer.", e.getMessage());
        }

        return listOfOrders;
    }


}
