package app.persistence;
import app.entities.User;
import app.exceptions.DatabaseException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserMapper
{
    public static User login(String email, String password, ConnectionPool connectionPool) throws DatabaseException
    {
        String sql = "select * from users where email=? and password=?";

        try (Connection connection = connectionPool.getConnection(); PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setString(1, email);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();
            if (rs.next())
            {
                int userId = rs.getInt("users_id");
                int balance = rs.getInt("balance");
                String role = rs.getString("role");
                return new User(userId, email, password, balance, role);
            } else
            {
                throw new DatabaseException("Fejl i login. Prøv igen");
            }
        } catch (SQLException e)
        {
            throw new DatabaseException("DB fejl", e.getMessage());
        }
    }

    public static void createuser(String email, String password, int balance, String role, ConnectionPool connectionPool) throws DatabaseException
    {
        String sql = "insert into users (email, password, balance, role) values (?,?,?,?)";

        try (Connection connection = connectionPool.getConnection(); PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setString(1, email);
            ps.setString(2, password);
            ps.setInt(3, balance);
            ps.setString(4, role);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected != 1)
            {
                throw new DatabaseException("Fejl ved oprettelse af ny bruger");
            }
        } catch (SQLException e)
        {
            String msg = "Der er sket en fejl. Prøv igen";
            if (e.getMessage().startsWith("ERROR: duplicate key value "))
            {
                msg = "Brugernavnet findes allerede. Vælg et andet";
            }
            throw new DatabaseException(msg, e.getMessage());
        }
    }

    public static void updateBalance(int userId, int balance, ConnectionPool connectionPool) throws DatabaseException
    {
        String sql = "update users set balance = ? where user_id = ?";

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
        )
        {
            ps.setInt(1, userId);
            ps.setInt(2, balance);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected != 1)
            {
                throw new DatabaseException("Fejl i opdatering af en task");
            }
        }
        catch (SQLException e)
        {
            throw new DatabaseException("Fejl i opdatering af en task", e.getMessage());
        }
    }
}
