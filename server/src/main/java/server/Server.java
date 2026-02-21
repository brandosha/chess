package server;

import com.google.gson.Gson;

import dataaccess.DatabaseMemory;
import datamodel.http.FailureResponse;
import datamodel.http.InvalidRequestException;
import handler.DataHandler;
import handler.GameHandler;
import handler.UserHandler;
import io.javalin.Javalin;
import io.javalin.http.ExceptionHandler;
import service.AlreadyTakenException;
import service.UnauthorizedException;

public class Server {

    private final Javalin javalin;
    private final Gson gson = new Gson();

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));
        var db = new DatabaseMemory();
        
        var userHandler = new UserHandler(db);
        javalin.post("/user", userHandler::register);
        javalin.post("/session", userHandler::login);
        javalin.delete("/session", userHandler::logout);

        var gameHandler = new GameHandler(db);
        javalin.post("/game", gameHandler::createGame);
        javalin.get("/game", gameHandler::listGames);
        javalin.put("/game", gameHandler::joinGame);

        var dataHandler = new DataHandler(db);
        javalin.delete("/db", dataHandler::clearDb);

        javalin.exception(InvalidRequestException.class, excHandler(400));
        javalin.exception(UnauthorizedException.class, excHandler(401));
        javalin.exception(AlreadyTakenException.class, excHandler(403));
        javalin.exception(Exception.class, excHandler(500));

    }

    private ExceptionHandler<Exception> excHandler(int status) {
        return (err, ctx) -> {
            System.err.println(err);
            ctx.status(status);
            ctx.result(gson.toJson(new FailureResponse(err)));
        };
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
