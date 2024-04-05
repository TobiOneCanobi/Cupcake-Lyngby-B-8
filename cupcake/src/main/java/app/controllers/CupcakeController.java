package app.controllers;

import app.entities.Bottom;
import app.entities.Topping;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.CupCakeMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;

public class CupcakeController
{

    public static void addRoutes(Javalin app, ConnectionPool connectionPool)
    {

        // New route for handling form submission
       // app.post("/order", ctx -> handleOrder(ctx, connectionPool));
    }


    public static void loadBottomsAndToppings(Context ctx, ConnectionPool connectionPool)
    {
        try
        {
            List<Bottom> bottomlist = CupCakeMapper.loadBottoms(connectionPool);
            List<Topping> toppinglist = CupCakeMapper.loadToppings(connectionPool);




            ctx.attribute("bottoms", bottomlist);
            ctx.attribute("toppings", toppinglist);
            ctx.render("homepage.html");
        } catch (Exception e)
        {
            ctx.status(500).result("Server Error");
            e.printStackTrace();
        }
    }


    // Method to handle the order form submission
    public static void handleOrder(Context ctx, ConnectionPool connectionPool)
    {
        // You can process the order here
        System.out.println("Order received");
        // Extract form data, validate, process the order, etc.

        // For demonstration, simply show a confirmation message
        ctx.result("Order processed successfully");
    }
}
