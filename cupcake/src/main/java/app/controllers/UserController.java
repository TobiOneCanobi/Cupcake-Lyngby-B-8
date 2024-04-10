package app.controllers;


import app.entities.User;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.UserMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.regex.Pattern;


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
        app.get("confirmation", ctx -> ctx.render("confirmation.html"));
        app.get("/loadcupcakes", ctx -> CupcakeController.loadBottomsAndToppings(ctx, connectionPool));
        app.get("usersaldo", ctx -> loadUserSaldo(ctx, connectionPool));
    }


    private static void loadUserSaldo(Context ctx, ConnectionPool connectionPool)
    {
        try
        {
            User user = ctx.sessionAttribute("currentUser");
            int userId = user.getUserid();
            int balance = UserMapper.getBalance(userId, connectionPool);

            ctx.attribute("balance", balance);
            ctx.render("shoppingcart.html");
        } catch (DatabaseException e)
        {
            ctx.attribute("message", "Noget gik galt. Prøv evt. igen");
            ctx.render("shoppingcart.html");
        }
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
                ctx.sessionAttribute("userRole", user.getRole());

                ctx.redirect("/loadcupcakes");
            } else
            {
                ctx.attribute("message", "noget er gået galt");
                ctx.render("loginpage.html");
            }
        } catch (DatabaseException e)
        {
            // Handle database error
            ctx.attribute("message", "forkert email eller password");
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
        } else if (!Pattern.matches(".*[\\p{Lu}\\p{N}æøåÆØÅ].*", password1) || password1.length() < 4)
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
