package app.persistence;

import app.entities.Order;
import app.exceptions.DatabaseException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderMapper
{
    public static List<Order> loadOrdersAdmin(ConnectionPool connectionPool) throws DatabaseException
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
                "    public.users u ON o.user_id = u.user_id\n" +
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
            e.printStackTrace();
            throw new DatabaseException("Fejl ved indlæsning af ordrer.", e.getMessage());
        }
        return listOfOrders;
    }

    public static List<Order> loadOrdersCustomer(ConnectionPool connectionPool, int userId) throws DatabaseException
    {
        List<Order> listOfOrders = new ArrayList<>();

        String sql = "SELECT " +
                "    ol.quantity, " +
                "    t.type AS topping_type, " +
                "    b.type AS bottom_type, " +
                "    t.price AS topping_price, " +
                "    b.price AS bottom_price, " +
                "    (t.price + b.price) * ol.quantity AS total_price " +
                "FROM " +
                "    public.orderline ol " +
                "JOIN " +
                "    public.topping t ON ol.topping_id = t.topping_id " +
                "JOIN " +
                "    public.bottom b ON ol.bottom_id = b.bottom_id " +
                "JOIN " +
                "    public.orders o ON ol.order_id = o.order_id " +
                "WHERE " +
                "    o.user_id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setInt(1, userId);

            ResultSet rs = ps.executeQuery();
            while (rs.next())
            {
                String toppingType = rs.getString("topping_type");
                String bottomType = rs.getString("bottom_type");
                int quantity = rs.getInt("quantity");
                int toppingPrice = rs.getInt("topping_price");
                int bottomPrice = rs.getInt("bottom_price");
                int totalPrice = rs.getInt("total_price");

                Order order = new Order(toppingType, bottomType, quantity, toppingPrice, bottomPrice, totalPrice);
                listOfOrders.add(order);
            }
        } catch (SQLException e)
        {
            e.printStackTrace();
            throw new DatabaseException("Fejl ved indlæsning af ordrer.", e.getMessage());
        }
        return listOfOrders;
    }


    public static Order createOrder(int userId, ConnectionPool connectionPool) throws DatabaseException
    {
        Order newOrder = null;

        String sql = "INSERT INTO orders (user_id) VALUES (?)";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS))
        {
            ps.setInt(1, userId);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 1)
            {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next())
                {
                    int newOrderId = rs.getInt(1);
                    newOrder = new Order(newOrderId, userId);
                }
            } else
            {
                throw new DatabaseException("Error inserting order for user ID: " + userId);
            }
        } catch (SQLException e)
        {
            throw new DatabaseException("Database connection error", e.getMessage());
        }
        return newOrder;
    }


    public static void createOrderLine(ConnectionPool connectionPool, int orderId, int bottomId, int toppingId, int quantity) throws SQLException
    {
        String insertOrderLineSQL = "INSERT INTO public.orderline (order_id, bottom_id, topping_id, quantity) VALUES (?, ?, ?, ?);";

        try (Connection conn = connectionPool.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertOrderLineSQL))
        {
            pstmt.setInt(1, orderId);
            pstmt.setInt(2, bottomId);
            pstmt.setInt(3, toppingId);
            pstmt.setInt(4, quantity);

            pstmt.executeUpdate();
        } catch (SQLException e)
        {
            e.printStackTrace();
            throw e;
        }
    }
}
