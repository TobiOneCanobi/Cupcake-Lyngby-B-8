package app;

import app.config.ThymeleafConfig;
import app.controllers.CupcakeController;
import app.controllers.OrderController;
import app.controllers.UserController;
import app.entities.Order;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.OrderMapper;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinThymeleaf;

import java.util.List;

public class Main
{
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";
    private static final String URL = "jdbc:postgresql://localhost:5432/%s?currentSchema=public";
    private static final String DB = "cupcakes";
    private static final ConnectionPool connectionPool = ConnectionPool.getInstance(USER, PASSWORD, URL, DB);


    public static void main(String[] args) throws DatabaseException
    {
        // Initializing Javalin and Jetty webserver
        Javalin app = Javalin.create(config ->
        {
            config.staticFiles.add("/public");
            config.fileRenderer(new JavalinThymeleaf(ThymeleafConfig.templateEngine()));
        }).start(7070);

        // Routing
        app.get("/", ctx -> ctx.render("loginpage.html"));
        UserController.addRoutes(app, connectionPool);
        OrderController.addRoutes(app, connectionPool);
        CupcakeController.addRoutes(app, connectionPool);

    }
}
