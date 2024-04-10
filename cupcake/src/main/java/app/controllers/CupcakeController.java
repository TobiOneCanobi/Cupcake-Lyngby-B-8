package app.controllers;

import app.entities.Bottom;
import app.entities.ShoppingCartLine;
import app.entities.Topping;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.CupCakeMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.ArrayList;
import java.util.List;

public class CupcakeController
{
    public static void addRoutes(Javalin app, ConnectionPool connectionPool)
    {
        app.post("add-to-cart", ctx -> addToCart(ctx, connectionPool));
        app.get("shoppingcart", ctx -> showShoppingCart(ctx, connectionPool));
    }

    public static void loadBottomsAndToppings(Context ctx, ConnectionPool connectionPool)
    {
        try
        {
            List<Bottom> bottomlist = CupCakeMapper.loadBottoms(connectionPool);
            List<Topping> toppinglist = CupCakeMapper.loadToppings(connectionPool);

            ctx.sessionAttribute("bottoms", bottomlist);
            ctx.sessionAttribute("toppings", toppinglist);
            ctx.render("homepage.html");
        } catch (Exception e)
        {
            ctx.status(500).result("Server Error");
            e.printStackTrace();
        }
    }

    public static void addToCart(Context ctx, ConnectionPool connectionPool) throws DatabaseException
    {
        try
        {
            int bottomId = Integer.parseInt(ctx.formParam("bottomId"));
            int toppingId = Integer.parseInt(ctx.formParam("toppingId"));
            int quantity = Integer.parseInt(ctx.formParam("quantity"));

            Bottom selectedBottom = CupCakeMapper.findBottomById(connectionPool, bottomId);
            Topping selectedTopping = CupCakeMapper.findToppingById(connectionPool, toppingId);

            ShoppingCartLine shoppingCartLine = new ShoppingCartLine(quantity, selectedBottom, selectedTopping);
            List<ShoppingCartLine> shoppingCartLineList = ctx.sessionAttribute("ShoppingCartLineList");

            if (shoppingCartLineList == null)
            {
                shoppingCartLineList = new ArrayList<>();
            }

            shoppingCartLineList.add(shoppingCartLine);

            ctx.sessionAttribute("ShoppingCartLineList", shoppingCartLineList);

            ctx.attribute("message", "tilføjet " + quantity + " cupcakes med bund: " + selectedBottom.getType() +
                    " og top: " + selectedTopping.getType() + " for en total price af: " + shoppingCartLine.getTotal()
                    + " kr");
            ctx.render("homepage.html");

        } catch (Exception e)
        {
            e.printStackTrace();
            ctx.attribute("message", "An error occurred while adding to cart. Please try again.");
            ctx.render("homepage.html");
        }
    }

    public static void showShoppingCart(Context ctx, ConnectionPool connectionPool)
    {
        List<ShoppingCartLine> shoppingCartLines = ctx.sessionAttribute("ShoppingCartLineList");

        if (shoppingCartLines == null)
        {
            shoppingCartLines = new ArrayList<>();
        }

        int totalPrice = calculateTotalPrice(ctx);

        ctx.attribute("ShoppingCartLineList", shoppingCartLines);
        ctx.attribute("totalPrice", totalPrice);
        ctx.render("shoppingcart.html");
    }

    public static int calculateTotalPrice(Context ctx)
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