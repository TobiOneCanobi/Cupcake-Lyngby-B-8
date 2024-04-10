package app.controllers;

import app.entities.*;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.OrderMapper;
import app.persistence.UserMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderController
{

    public static void addRoutes(Javalin app, ConnectionPool connectionPool)
    {
        app.get("orderoverviewadmin", ctx -> orderOverviewAdmin(ctx, connectionPool));
        app.get("orderoverviewcustomer", ctx -> orderOverviewCustomer(ctx, connectionPool));
        app.post("placeorder", ctx -> placeOrder(ctx, connectionPool));
    }

    public static void orderOverviewAdmin(Context ctx, ConnectionPool connectionPool)
    {
        try
        {
            List<Order> orderList = OrderMapper.loadOrdersAdmin(connectionPool);
            ctx.attribute("orders", orderList);
            ctx.render("adminoverview.html");
        } catch (Exception e)
        {
            ctx.attribute("message", "Failed to get orders");
            e.printStackTrace();
            ctx.render("adminoverview.html");
        }
    }

    public static void orderOverviewCustomer(Context ctx, ConnectionPool connectionPool)
    {
        User user = ctx.sessionAttribute("currentUser");

        try
        {
            int userId = user.getUserid();
            List<Order> orderListCustommer = OrderMapper.loadOrdersCustomer(connectionPool, userId);
            ctx.attribute("custommerorders", orderListCustommer);
            ctx.render("custommeroverview.html");
        } catch (Exception e)
        {
            ctx.attribute("message", "Failed to get orders");
            e.printStackTrace();
            ctx.render("custommeroverview.html");
        }
    }


    public static void addOrderline(Context ctx, int orderId, ConnectionPool connectionPool) throws SQLException
    {
        List<ShoppingCartLine> shoppingCartLines = ctx.sessionAttribute("ShoppingCartLineList");

        if (shoppingCartLines != null)
        {
            for (ShoppingCartLine line : shoppingCartLines)
            {
                OrderMapper.createOrderLine(connectionPool, orderId, line.getBottom().getBottomId(), line.getTopping().getToppingId(), line.getQuantity());
            }
        }
    }


    public static void placeOrder(Context ctx, ConnectionPool connectionPool)
    {
        User currentUser = ctx.sessionAttribute("currentUser");
        int totalPrice = CupcakeController.calculateTotalPrice(ctx);

        if (currentUser.getBalance() >= totalPrice)
        {
            try
            {
                int updatedBalance = currentUser.getBalance() - totalPrice;
                UserMapper.updateBalance(currentUser.getUserid(), updatedBalance, connectionPool);

                Order newOrder = OrderMapper.createOrder(currentUser.getUserid(), connectionPool);

                addOrderline(ctx, newOrder.getOrderId(), connectionPool);

                currentUser.setBalance(updatedBalance);
                ctx.sessionAttribute("currentUser", currentUser);

                List<?> shoppingCartLineList = new ArrayList<>();
                ctx.sessionAttribute("ShoppingCartLineList", shoppingCartLineList);

                ctx.attribute("orderId", newOrder.getOrderId());
                ctx.render("confirmation.html");
            } catch (DatabaseException | SQLException e)
            {
                e.printStackTrace();
                ctx.attribute("message", "Fejl ved opdatering af saldo eller oprettelse af ordre.");
                ctx.render("shoppingcart.html");
            }
        } else
        {
            ctx.attribute("message", "Ikke nok saldo til at gennemføre købet.");
            ctx.render("shoppingcart.html");
        }
    }
}
