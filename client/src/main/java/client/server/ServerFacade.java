package client.server;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;

import com.google.gson.Gson;

import datamodel.http.CreateGameRequest;
import datamodel.http.CreateGameResponse;
import datamodel.http.LoginRequest;
import datamodel.http.LoginResponse;
import datamodel.http.RegisterRequest;
import datamodel.http.RegisterResponse;

public class ServerFacade {

  public static final ServerFacade local = new ServerFacade("localhost", 8080);
  
  private static final HttpClient httpClient = HttpClient.newHttpClient();
  private static final Gson gson = new Gson();

  final String hostname;
  final int port;

  public ServerFacade(String hostname, int port) {
    this.hostname = hostname;
    this.port = port;

    System.out.println(this.uri("/test"));
  }

  public RegisterResponse register(RegisterRequest registerRequest) throws ServerResponseException, IOException, InterruptedException {
    var uri = this.uri("/user");
    var body = gson.toJson(registerRequest);
    var req = HttpRequest.newBuilder(uri).POST(BodyPublishers.ofString(body)).build();

    var res = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
    if (res.statusCode() == 200) {
      return gson.fromJson(res.body(), RegisterResponse.class);
    } else {
      throw ServerResponseException.fromResponse(res);
    }
  }

  public LoginResponse login(LoginRequest loginRequest) throws ServerResponseException, IOException, InterruptedException {
    var uri = this.uri("/session");
    var body = gson.toJson(loginRequest);
    var req = HttpRequest.newBuilder(uri).POST(HttpRequest.BodyPublishers.ofString(body)).build();

    var res = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
    if (res.statusCode() == 200) {
      return gson.fromJson(res.body(), LoginResponse.class);
    } else {
      throw ServerResponseException.fromResponse(res);
    }
  }

  public void logout(String authToken) throws ServerResponseException, IOException, InterruptedException {
    var uri = this.uri("/session");
    var req = HttpRequest.newBuilder(uri).DELETE().header("Authorization", authToken).build();

    var res = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
    if (res.statusCode() != 200) {
      throw ServerResponseException.fromResponse(res);
    }
  }

  public CreateGameResponse createGame(CreateGameRequest createRequest, String authToken) throws ServerResponseException, IOException, InterruptedException {
    var uri = this.uri("/game");
    var body = gson.toJson(createRequest);
    var req = HttpRequest.newBuilder(uri)
              .POST(HttpRequest.BodyPublishers.ofString(body))
              .header("Authorization", authToken)
              .build();

    var res = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
    if (res.statusCode() == 200) {
      return gson.fromJson(res.body(), CreateGameResponse.class);
    } else {
      throw ServerResponseException.fromResponse(res);
    }
  }

  private URI uri(String path) {
    try {
      return new URI("http", null, hostname, port, path, null, null);
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }

  private URI uri(String path, String query) {
    try {
      return new URI("http", "", hostname, port, path, query, "");
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }

}
