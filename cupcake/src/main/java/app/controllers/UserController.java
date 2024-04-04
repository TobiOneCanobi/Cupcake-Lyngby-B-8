package app.controllers;

import app.entities.User;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.UserMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class UserController
{

    public static void addRoutes(Javalin app, ConnectionPool connectionPool)
    {
        app.post("login", ctx -> login(ctx, connectionPool));
        app.get("return", ctx -> ctx.render("loginpage.html"));
        app.get("logout", ctx -> logout(ctx));
        app.get("createuser", ctx -> ctx.render("createuser.html"));
        app.post("createuser", ctx -> createUser(ctx, connectionPool));

    }

    public static void login(Context ctx, ConnectionPool connectionPool)
    {
        // Hent form parametre
        String email = ctx.formParam("email");
        String password = ctx.formParam("password");

        try
        {
            User user = UserMapper.login(email, password, connectionPool);
            ctx.sessionAttribute("currentUser", user);
            // Hvis ja, send videre til forsiden med login besked
            ctx.attribute("message", "Du er nu logget ind");
            ctx.render("homepage.html"); // skal render til vores main page
        }
        catch (DatabaseException e)
        {
            // Hvis nej, send tilbage til login side med fejl besked
            ctx.attribute("message", e.getMessage() );
            ctx.render("loginpage.html");
        }

    }
    private static void createUser(Context ctx, ConnectionPool connectionPool)
    {
        // Hent form parametre
        String email = ctx.formParam("email");
        String password1 = ctx.formParam("password1");
        String password2 = ctx.formParam("password2");

        if (password1.equals(password2))
        {
            try
            {
                UserMapper.createuser(email, password1,0, "customer",connectionPool);
                ctx.attribute("message", "Du er hermed oprettet med email: " + email +
                        ". Nu skal du logge på.");
                ctx.render("loginpage.html");
            }

            catch (DatabaseException e)
            {
                ctx.attribute("message", "Dit email er allerede i brug. Prøv igen, eller log ind");
                ctx.render("createuser.html");
            }
        } else
        {
            ctx.attribute("message", "Dine to passwords matcher ikke! Prøv igen");
            ctx.render("createuser.html");
        }
    }

    private static void logout(Context ctx)
    {
        ctx.req().getSession().invalidate();
        ctx.redirect("/");
    }
}
