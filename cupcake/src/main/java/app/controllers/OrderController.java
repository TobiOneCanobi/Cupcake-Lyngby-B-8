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
        app.get("orderoverviewcustommer", ctx -> orderoverviewCustommer(ctx, connectionPool));
        app.post("totalPrice", ctx -> totalPrice(ctx, connectionPool));

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


    public static void orderoverviewCustommer(Context ctx, ConnectionPool connectionPool)
    {
        try
        {
            List<Order> orderListCustommer = OrderMapper.loadOrdersCustomer(connectionPool);
            ctx.attribute("custommerorders", orderListCustommer);
            ctx.render("custommeroverview.html");
        } catch (Exception e)
        {
            ctx.attribute("message", "Failed to get orders");
            e.printStackTrace();
            ctx.render("custommeroverview.html");
        }
    }



    public static void totalPrice(Context ctx, ConnectionPool connectionPool){

        int bottomPrice = Integer.parseInt(ctx.formParam("bottomPrice"));
        int toppingPrice = Integer.parseInt(ctx.formParam("toppingPrice"));
        int quantity = Integer.parseInt(ctx.formParam("quantity"));
        int total = 0;

        total = (bottomPrice + toppingPrice) * quantity;
        ctx.attribute("total", total);
//        return total;

    }







}
