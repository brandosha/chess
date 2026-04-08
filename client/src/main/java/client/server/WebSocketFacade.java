package client.server;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.function.Consumer;

import com.google.gson.Gson;

import jakarta.websocket.ContainerProvider;
import jakarta.websocket.DeploymentException;
import jakarta.websocket.Endpoint;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.MessageHandler;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

public class WebSocketFacade extends Endpoint {

  final String hostname;
  final int port;

  final Gson gson = new Gson();
  Session session;
  Consumer<ServerMessage> listener;

  public WebSocketFacade(String hostname, int port) {
    this.hostname = hostname;
    this.port = port;
  }

  public void connect() throws DeploymentException, IOException {
    WebSocketContainer container = ContainerProvider.getWebSocketContainer();
    this.session = container.connectToServer(this, uri("/ws"));
    this.session.addMessageHandler(new MessageHandler.Whole<String>() {
      @Override
      public void onMessage(String message) {
        System.err.println("[ws] Recieved " + message);
        var msg = gson.fromJson(message, ServerMessage.class);
        listener.accept(msg);
      }
    });
  }

  private void sendCommand(UserGameCommand cmd) throws IOException {
    var json = gson.toJson(cmd);
    System.err.println("[ws] Sending " + json);
    session.getBasicRemote().sendText(json);
  }

  public void connectToGame(int gameID, String authToken, Consumer<ServerMessage> listener) throws IOException {
    this.listener = listener;
    var cmd = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
    sendCommand(cmd);
  }

  public void disconnect() throws IOException {
    session.close();
  }

  private URI uri(String path) {
    try {
      return new URI("ws", null, hostname, port, path, null, null);
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void onOpen(Session arg0, EndpointConfig arg1) {
  }

  
  
}
