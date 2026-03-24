package client;

import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import client.server.ServerFacade;
import client.server.ServerResponseException;
import datamodel.http.CreateGameRequest;
import datamodel.http.JoinGameRequest;
import datamodel.http.LoginRequest;
import datamodel.http.RegisterRequest;
import server.Server;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade sf;
    private static int port;

    @BeforeAll
    public static void init() {
        server = new Server();
        port = server.run(0);
        System.out.println("Started test HTTP server on " + port);

        sf = new ServerFacade("localhost", port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    public void clearDatabase() throws IOException, InterruptedException, ServerResponseException {
        sf.clear();
    }


    @Test
    public void registerPositive() throws Exception {
        var request = new RegisterRequest("user-register-pos", "password", "a@a.com");

        var response = sf.register(request);

        Assertions.assertEquals("user-register-pos", response.username);
        Assertions.assertNotNull(response.authToken);
    }

    @Test
    public void registerNegative() throws Exception {
        var request = new RegisterRequest("user-register-neg", "password", "a@a.com");
        sf.register(request);

        var ex = Assertions.assertThrows(ServerResponseException.class, () -> sf.register(request));

        Assertions.assertEquals(403, ex.response.statusCode());
    }

    @Test
    public void loginPositive() throws Exception {
        var username = "user-login-pos";
        var password = "password";

        sf.register(new RegisterRequest(username, password, "a@a.com"));

        var response = sf.login(new LoginRequest(username, password));

        Assertions.assertEquals(username, response.username);
        Assertions.assertNotNull(response.authToken);
    }

    @Test
    public void loginNegative() throws Exception {
        sf.register(new RegisterRequest("user-login-neg", "password", "a@a.com"));

        var ex = Assertions.assertThrows(
            ServerResponseException.class,
            () -> sf.login(new LoginRequest("user-login-neg", "wrong-password"))
        );

        Assertions.assertEquals(401, ex.response.statusCode());
    }

    @Test
    public void logoutPositive() throws Exception {
        var registerResponse = sf.register(new RegisterRequest("user-logout-pos", "password", "a@a.com"));

        sf.logout(registerResponse.authToken);
    }

    @Test
    public void logoutNegative() {
        var ex = Assertions.assertThrows(ServerResponseException.class, () -> sf.logout("bad-auth-token"));

        Assertions.assertEquals(401, ex.response.statusCode());
    }

    @Test
    public void createGamePositive() throws Exception {
        var registerResponse = sf.register(new RegisterRequest("user-create-pos", "password", "a@a.com"));

        var response = sf.createGame(new CreateGameRequest("test-game"), registerResponse.authToken);

        Assertions.assertTrue(response.gameID > 0);
    }

    @Test
    public void createGameNegative() {
        var ex = Assertions.assertThrows(
            ServerResponseException.class,
            () -> sf.createGame(new CreateGameRequest("test-game"), "bad-auth-token")
        );

        Assertions.assertEquals(401, ex.response.statusCode());
    }

    @Test
    public void listGamesPositive() throws Exception {
        var registerResponse = sf.register(new RegisterRequest("user-list-pos", "password", "a@a.com"));
        sf.createGame(new CreateGameRequest("list-game"), registerResponse.authToken);

        var response = sf.listGames(registerResponse.authToken);

        Assertions.assertEquals(1, response.games.size());
        var onlyGame = response.games.iterator().next();
        Assertions.assertEquals("list-game", onlyGame.gameName);
    }

    @Test
    public void listGamesNegative() {
        var ex = Assertions.assertThrows(ServerResponseException.class, () -> sf.listGames("bad-auth-token"));

        Assertions.assertEquals(401, ex.response.statusCode());
    }

    @Test
    public void joinGamePositive() throws Exception {
        var registerResponse = sf.register(new RegisterRequest("user-join-pos", "password", "a@a.com"));
        var createResponse = sf.createGame(new CreateGameRequest("join-game"), registerResponse.authToken);

        sf.joinGame(new JoinGameRequest("WHITE", createResponse.gameID), registerResponse.authToken);

        var gamesResponse = sf.listGames(registerResponse.authToken);
        var joinedGame = gamesResponse.games.iterator().next();
        Assertions.assertEquals("user-join-pos", joinedGame.whiteUsername);
    }

    @Test
    public void joinGameNegative() throws Exception {
        var registerResponse = sf.register(new RegisterRequest("user-join-neg", "password", "a@a.com"));
        var createResponse = sf.createGame(new CreateGameRequest("join-game"), registerResponse.authToken);

        var ex = Assertions.assertThrows(
                ServerResponseException.class,
                () -> sf.joinGame(new JoinGameRequest("GREEN", createResponse.gameID), registerResponse.authToken)
        );

        Assertions.assertEquals(400, ex.response.statusCode());
    }

}
