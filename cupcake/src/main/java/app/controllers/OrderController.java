package app.controllers;

import app.entities.*;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.CupCakeMapper;
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
        app.get("orderoverview", ctx -> orderoverview(ctx, connectionPool));
        app.get("orderoverviewcustommer", ctx -> orderoverviewCustommer(ctx, connectionPool));
        app.post("placeorder", ctx -> placeOrder(ctx, connectionPool));

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


    public static int addOrder(int userId, ConnectionPool connectionPool) throws DatabaseException
    {
        Order order = OrderMapper.addOrder(userId, connectionPool);
        return order.getOrderId();
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
        int totalPrice = calculateTotalPrice(ctx);

        if (currentUser.getBalance() >= totalPrice)
        {
            try
            {
                int updatedBalance = currentUser.getBalance() - totalPrice;
                UserMapper.updateBalance(currentUser.getUserid(), updatedBalance, connectionPool);

                int orderId = addOrder(currentUser.getUserid(), connectionPool);
                addOrderline(ctx, orderId, connectionPool);

                currentUser.setBalance(updatedBalance);
                ctx.sessionAttribute("currentUser", currentUser);

                ctx.attribute("orderId", orderId);
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

    private static int calculateTotalPrice(Context ctx)
    {
        List<ShoppingCartLine> shoppingCartLines = ctx.sessionAttribute("ShoppingCartLineList");
        if (shoppingCartLines == null)
        {
            return 0;
        }
        int totalPrice = 0;
        for (ShoppingCartLine line : shoppingCartLines)
        {
            int lineTotal = line.getQuantity() * (line.getBottom().getPrice() + line.getTopping().getPrice());
            totalPrice += lineTotal;
        }
        return totalPrice;
    }
}
