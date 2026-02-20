package server;

import com.google.gson.Gson;

import dataaccess.UserDaoMemory;
import datamodel.http.FailureResponse;
import handler.UserHandler;
import io.javalin.Javalin;
import service.AlreadyTakenException;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        var userDao = new UserDaoMemory();
        var userHandler = new UserHandler(userDao);
        javalin.post("/user", userHandler::register);

        Gson gson = new Gson();

        javalin.exception(AlreadyTakenException.class, (err, ctx) -> {
            System.err.println("AlreadyTakenException " + err);
            ctx.status(403);
            ctx.result(gson.toJson(new FailureResponse(err)));
        });

        // General exception handler
        javalin.exception(Exception.class, (err, ctx) -> {
            System.err.println("Exception " + err);
            ctx.status(500);
            ctx.result(gson.toJson(new FailureResponse(err)));
        });

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
