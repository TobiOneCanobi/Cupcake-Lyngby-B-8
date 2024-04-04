package app.controllers;

import app.entities.Order;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.OrderMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;

public class OrderController {

    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.get("orderoverview", ctx -> ctx.render("adminoverview.html"));
        app.post("orderoverview", ctx -> orderoverview(ctx, connectionPool));
    }

    public static void orderoverview(Context ctx, ConnectionPool connectionPool) {
        try {
            List<Order> orders = OrderMapper.loadOrders(connectionPool);
            ctx.attribute("orders", orders);
            ctx.render("adminoverview.html");
        } catch (DatabaseException e) {
            ctx.attribute("message", "Failed to get orders");
        }
    }
}
