package app.controllers;

import app.entities.Bottom;
import app.entities.Topping;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.CupCakeMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;

public class CupcakeController {

    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        // Existing route for loading the homepage
        app.post("/homepage", ctx -> loadBottomsAndToppings(ctx, connectionPool));

        // New route for handling form submission
        app.post("/order", ctx -> handleOrder(ctx, connectionPool));
    }


    public static void loadBottomsAndToppings(Context ctx, ConnectionPool connectionPool) {
        try {
            List<Bottom> bottoms = CupCakeMapper.loadBottoms(connectionPool);
            List<Topping> toppings = CupCakeMapper.loadToppings(connectionPool);

            System.out.println("Bottoms loaded: " + bottoms.size());
            System.out.println("Toppings loaded: " + toppings.size());

            ctx.attribute("bottoms", bottoms);
            ctx.attribute("toppings", toppings);
            ctx.render("homepage.html");
        } catch (Exception e) {
            System.err.println("Failed to load cupcake components");
            e.printStackTrace();
        }
    }


    // Method to handle the order form submission
    public static void handleOrder(Context ctx, ConnectionPool connectionPool) {
        // You can process the order here
        System.out.println("Order received");
        // Extract form data, validate, process the order, etc.

        // For demonstration, simply show a confirmation message
        ctx.result("Order processed successfully");
    }
}
