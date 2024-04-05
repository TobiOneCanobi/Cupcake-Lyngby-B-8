package app.controllers;

import app.entities.Order;
import app.persistence.ConnectionPool;
import app.persistence.OrderMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;

public class OrderController
{

    public static void addRoutes(Javalin app, ConnectionPool connectionPool)
    {
        app.get("orderoverview", ctx -> orderoverview(ctx, connectionPool));
    }

    public static void orderoverview(Context ctx, ConnectionPool connectionPool)
    {
        try
        {
            List<Order> orderList = OrderMapper.loadOrders(connectionPool);
            ctx.attribute("orders", orderList);
            ctx.render("adminoverview.html");
        } catch (Exception e)
        {
            ctx.attribute("message", "Failed to get orders");
            e.printStackTrace();
            ctx.render("adminoverview.html");
        }
    }
}
