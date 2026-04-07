package client.server;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import com.google.gson.Gson;

import jakarta.websocket.ContainerProvider;
import jakarta.websocket.DeploymentException;
import jakarta.websocket.Endpoint;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.MessageHandler;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;
import websocket.messages.ServerMessage;

public class WebSocketFacade extends Endpoint {

  final String hostname;
  final int port;

  final Gson gson = new Gson();
  Session session;

  public WebSocketFacade(String hostname, int port) {
    this.hostname = hostname;
    this.port = port;

    try {
      WebSocketContainer container = ContainerProvider.getWebSocketContainer();
      this.session = container.connectToServer(this, uri("/ws"));
      this.session.addMessageHandler(new MessageHandler.Whole<String>() {
        @Override
        public void onMessage(String message) {
          var msg = gson.fromJson(message, ServerMessage.class);
          System.out.println(msg);
        }
      });
    } catch (DeploymentException | IOException | IllegalStateException e) {
    }
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
