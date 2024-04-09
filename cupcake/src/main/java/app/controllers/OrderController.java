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
        app.post("placeorder", ctx -> placeOrder(ctx, connectionPool));  // Tilføjer rute for at placere ordre
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

    }

    public static int addOrder(int userId, ConnectionPool connectionPool) throws DatabaseException {
        // Logic to add order to the database and return order ID
        Order order = OrderMapper.addOrder(userId, connectionPool);
        return order.getOrderId();
    }

    public static void addOrderline(Context ctx, int orderId, ConnectionPool connectionPool) throws SQLException {
        // Antager, at indkøbskurven er gemt som en liste af ShoppingCartLine objekter i sessionen
        List<ShoppingCartLine> shoppingCartLines = ctx.sessionAttribute("ShoppingCartLineList");

        if (shoppingCartLines != null) {
            for (ShoppingCartLine line : shoppingCartLines) {
                // Her antages det, at OrderMapper har en metode til at tilføje en ordrelinje
                OrderMapper.createOrderLine(connectionPool, orderId, line.getBottom().getBottomId(), line.getTopping().getToppingId(), line.getQuantity());
            }
        }
    }

    public static void placeOrder(Context ctx, ConnectionPool connectionPool) {
        User currentUser = ctx.sessionAttribute("currentUser");
        int totalPrice = calculateTotalPrice(ctx);

        if (currentUser.getBalance() >= totalPrice) {
            try {
                int updatedBalance = currentUser.getBalance() - totalPrice;
                UserMapper.updateBalance(currentUser.getUserid(), updatedBalance, connectionPool);

                int orderId = addOrder(currentUser.getUserid(), connectionPool); // Opretter en ordre og returnerer ID
                addOrderline(ctx, orderId, connectionPool);

                currentUser.setBalance(updatedBalance);
                ctx.sessionAttribute("currentUser", currentUser);

                ctx.attribute("totalPrice", totalPrice);

                ctx.attribute("orderId", orderId); // Til reference eller bekræftelse
                ctx.render("confirmation.html");
            } catch (DatabaseException | SQLException e) {
                e.printStackTrace();
                ctx.attribute("message", "Fejl ved opdatering af saldo eller oprettelse af ordre.");
                ctx.render("shoppingcart.html");
            }
        } else {
            ctx.attribute("message", "Ikke nok saldo til at gennemføre købet.");
            ctx.render("shoppingcart.html");
        }
    }


    public static int calculateTotalPrice(Context ctx) {
        List<ShoppingCartLine> shoppingCartLines = ctx.sessionAttribute("ShoppingCartLineList");
        if (shoppingCartLines == null) {
            return 0; // Ingen varer i kurven
        }
        int totalPrice = 0;
        for (ShoppingCartLine line : shoppingCartLines) {
            int lineTotal = line.getQuantity() * (line.getBottom().getPrice() + line.getTopping().getPrice());
            totalPrice += lineTotal;
        }
        return totalPrice;
    }

}
