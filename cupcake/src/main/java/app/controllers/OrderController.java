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
        app.post("totalPrice", ctx -> totalPrice(ctx, connectionPool));
        app.post("add-to-cart", ctx -> addToCart(ctx, connectionPool));
        app.post("addorder", ctx -> addOrder(ctx, connectionPool));

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


    public static void addToCart (Context ctx, ConnectionPool connectionPool) throws DatabaseException {

        try
        {
            // Parse form parameters
            int bottomId = Integer.parseInt(ctx.formParam("bottomId"));
            int toppingId = Integer.parseInt(ctx.formParam("toppingId"));
            int quantity = Integer.parseInt(ctx.formParam("quantity"));

            Bottom selectedBottom = CupCakeMapper.findBottomById(connectionPool, bottomId);
            Topping selectedTopping = CupCakeMapper.findToppingById(connectionPool, toppingId);

            ShoppingCartLine shoppingCartLine = new ShoppingCartLine(quantity, selectedBottom, selectedTopping);
            List<ShoppingCartLine> shoppingCartLineList = ctx.sessionAttribute("ShoppingCartLineList");

            if (shoppingCartLineList == null) {
                shoppingCartLineList = new ArrayList<>();
            }

            shoppingCartLineList.add(shoppingCartLine);

            ctx.sessionAttribute("ShoppingCartLineList", shoppingCartLineList);

            ctx.attribute("message", "tilføjet "+ quantity + " cupcakes med bund: " + selectedBottom.getType() + " og top: " + selectedTopping.getType() + " for en total price af: " + shoppingCartLine.getTotal());
            ctx.render("homepage.html");

        } catch (Exception e)
        {
            e.printStackTrace();
            ctx.attribute("message", "An error occurred while adding to cart. Please try again.");
            ctx.render("homepage.html");

        }
    }


    public static void totalPrice(Context ctx, ConnectionPool connectionPool)
    {

        int bottomPrice = Integer.parseInt(ctx.formParam("bottomPrice"));
        int toppingPrice = Integer.parseInt(ctx.formParam("toppingPrice"));
        int quantity = Integer.parseInt(ctx.formParam("quantity"));
        int total = 0;

        total = (bottomPrice + toppingPrice) * quantity;
        ctx.attribute("total", total);
//        return total;

    }


    public static void addOrder(Context ctx, ConnectionPool connectionPool)
    {
        User user = ctx.sessionAttribute("currentUser");
        try
        {
            Order order = OrderMapper.addOrder(user.getUserid(), connectionPool);
            ctx.attribute("order", order);
            addOrderline(ctx, connectionPool);
            ctx.render("confirmation.html");
        } catch (DatabaseException e)
        {
            ctx.attribute("message", "Noget gik galt. Prøv evt. igen");
            ctx.render("confirmation.html");
        }
    }


    public static void addOrderline(Context ctx, ConnectionPool connectionPool)
    {
        User user = ctx.sessionAttribute("currentUser");

        int userId = user.getUserid();

        List<ShoppingCartLine> shoppingCartLines = ctx.sessionAttribute("ShoppingCartLineList");
        try {
            int orderId = OrderMapper.createOrder(connectionPool, userId);

            for (ShoppingCartLine line : shoppingCartLines) {
                OrderMapper.createOrderLine(connectionPool, orderId, line);
            }

            // Optionally clear the shopping cart from the session after successful order creation
             ctx.sessionAttribute("ShoppingCartLineList", null);
            // Provide feedback to the user
            ctx.attribute("message", "Your order has been successfully placed.");
        } catch (SQLException e) {
            ctx.attribute("message", "Error placing the order. Please try again.");
        }

    }


}
