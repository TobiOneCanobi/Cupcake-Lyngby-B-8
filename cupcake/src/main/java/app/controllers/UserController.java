package app.controllers;

import app.entities.User;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.UserMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.regex.Pattern;

import static app.controllers.CupcakeController.loadBottomsAndToppings;

public class UserController
{

    public static void addRoutes(Javalin app, ConnectionPool connectionPool)
    {
        app.post("login", ctx -> login(ctx, connectionPool));
        app.get("return", ctx -> ctx.render("loginpage.html"));
        app.get("logout", ctx -> logout(ctx));
        app.get("createuser", ctx -> ctx.render("createuser.html"));
        app.post("createuser", ctx -> createUser(ctx, connectionPool));
        app.get("homepage", ctx -> ctx.render("homepage.html"));
        app.get("shoppingcart", ctx -> ctx.render("shoppingcart.html"));
        app.get("confirmation", ctx -> ctx.render("confirmation.html"));
        app.get("/loadcupcakes", ctx -> loadBottomsAndToppings(ctx, connectionPool));

    }

    public static void login(Context ctx, ConnectionPool connectionPool)
    {

        String email = ctx.formParam("email");
        String password = ctx.formParam("password");

        try
        {
            User user = UserMapper.login(email, password, connectionPool);
            if (user != null)
            {
                ctx.sessionAttribute("currentUser", user);
                ctx.sessionAttribute("userEmail", email);
                ctx.redirect("/loadcupcakes"); // Redirect to load cupcakes and then to the homepage
            } else
            {
                // Handle login failure
                ctx.attribute("loginError", "Invalid username or password");
                ctx.render("loginpage.html");
            }
        } catch (DatabaseException e)
        {
            // Handle database error
            ctx.attribute("loginError", "An error occurred. Please try again.");
            ctx.render("loginpage.html");
        }

    }

    private static void createUser(Context ctx, ConnectionPool connectionPool)
    {
        // Hent form parametre
        String email = ctx.formParam("email");
        String password1 = ctx.formParam("password1");
        String password2 = ctx.formParam("password2");
        if (!email.contains("@"))
        {
            ctx.attribute("message", "Din email skal indeholde '@'! Prøv igen.");
            ctx.render("createuser.html");
        } else if (!password1.equals(password2))
        {
            ctx.attribute("message", "Dine to passwords matcher ikke! Prøv igen");
            ctx.render("createuser.html");
        }
        //  else if (!password1.matches(".*[A-Z].*") || password1.length() < 4)
        else if (!Pattern.matches(".*[\\p{Lu}\\p{N}æøåÆØÅ].*", password1) || password1.length() < 4)
        {
            ctx.attribute("message", " kan kun havde normale bogstav og tal, skal mindst være 4 bogstaver langt");
            ctx.render("createuser.html");

        } else if (password1.equals(password2))
        {
            try
            {
                UserMapper.createuser(email, password1, 0, "customer", connectionPool);
                ctx.attribute("message", "Du er hermed oprettet med email: " + email +
                        ". Nu skal du logge på.");
                ctx.render("loginpage.html");
            } catch (DatabaseException e)
            {
                ctx.attribute("message", "Dit email er allerede i brug. Prøv igen, eller log ind");
                ctx.render("createuser.html");
            }
        } else
        {
            ctx.attribute("noget gik galt, PANIC!");
            ctx.render("createuser.html");
        }
    }

    private static void logout(Context ctx)
    {
        ctx.req().getSession().invalidate();
        ctx.redirect("/");
    }
}
