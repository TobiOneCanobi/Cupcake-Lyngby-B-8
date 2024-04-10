package app.persistence;

import app.entities.Bottom;
import app.entities.Topping;
import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CupCakeMapper
{
    public static List<Topping> loadToppings(ConnectionPool connectionPool) throws DatabaseException
    {
        List<Topping> listOfToppings = new ArrayList<>();

        String sql = "SELECT * FROM public.topping";

        try (Connection connection = connectionPool.getConnection(); PreparedStatement ps = connection.prepareStatement(sql))
        {
            ResultSet rs = ps.executeQuery();
            while (rs.next())
            {
                int toppingId = rs.getInt("topping_id");
                int price = rs.getInt("price");
                String type = rs.getString("type");
                listOfToppings.add(new Topping(toppingId, price, type));
            }
        } catch (SQLException e)
        {
            throw new DatabaseException("Fejl!!!!", e.getMessage());
        }
        return listOfToppings;
    }

    public static List<Bottom> loadBottoms(ConnectionPool connectionPool) throws DatabaseException
    {
        List<Bottom> listOfBottoms = new ArrayList<>();

        String sql = "SELECT * FROM public.bottom";

        try (Connection connection = connectionPool.getConnection(); PreparedStatement ps = connection.prepareStatement(sql))
        {
            ResultSet rs = ps.executeQuery();
            while (rs.next())
            {
                int bottomId = rs.getInt("bottom_id");
                int price = rs.getInt("price");
                String type = rs.getString("type");
                listOfBottoms.add(new Bottom(bottomId, price, type));
            }
        } catch (SQLException e)
        {
            throw new DatabaseException("Fejl!!!!", e.getMessage());
        }
        return listOfBottoms;
    }

    public static Bottom findBottomById(ConnectionPool connectionPool, int bottomId) throws SQLException
    {
        Bottom bottom = null;
        String sql = "SELECT bottom_id, type, price FROM public.bottom WHERE bottom_id = ?";

        try (Connection conn = connectionPool.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql))
        {
            pstmt.setInt(1, bottomId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next())
            {
                int id = rs.getInt("bottom_id");
                String type = rs.getString("type");
                int price = rs.getInt("price");

                bottom = new Bottom(id, price, type);
            }
        }
        return bottom;
    }

    public static Topping findToppingById(ConnectionPool connectionPool, int toppingId) throws SQLException
    {
        Topping topping = null;
        String sql = "SELECT topping_id, type, price FROM public.topping WHERE topping_id = ?";

        try (Connection conn = connectionPool.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql))
        {
            pstmt.setInt(1, toppingId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next())
            {
                int id = rs.getInt("topping_id");
                String type = rs.getString("type");
                int price = rs.getInt("price");

                topping = new Topping(id, price, type);
            }
        }
        return topping;
    }
}
