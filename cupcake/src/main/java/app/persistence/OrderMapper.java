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
    public static List<Order> loadOrder(ConnectionPool connectionPool) throws DatabaseException
    {
        List<Order> listOfOrders = new ArrayList<>();

        String sql = "SELECT * FROM public.orders";

        try (Connection connection = connectionPool.getConnection(); PreparedStatement ps = connection.prepareStatement(sql))
        {
            ResultSet rs = ps.executeQuery();
            while (rs.next())
            {
                int orderId = rs.getInt("order_id");
                int userId = rs.getInt("users_id");

                listOfOrders.add(new Order(orderId, userId));
            }
        } catch (SQLException e)
        {
            throw new DatabaseException("Fejl!!!!", e.getMessage());
        }
        return listOfOrders;
    }

}
