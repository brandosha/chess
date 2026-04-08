package websocket;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import io.javalin.websocket.WsContext;

public class WsClients {

  class Client {
    final WsContext ctx;
    final Set<String> channels = new HashSet<>();

    public Client(WsContext ctx) {
      this.ctx = ctx;
    }
  }

  Map<String, Client> clients = new HashMap<>();
  Map<String, Set<Client>> channels = new HashMap<>();

  public synchronized void addClient(WsContext ctx) {
    clients.put(ctx.sessionId(), new Client(ctx));
  }

  public synchronized void subscribeClient(WsContext ctx, String channel) {
    var sid = ctx.sessionId();
    clients.putIfAbsent(sid, new Client(ctx));
    var client = clients.get(sid);

    channels.putIfAbsent(channel, new HashSet<>());
    channels.get(channel).add(client);
    client.channels.add(channel);
  }

  public void broadcast(String channel, String msg) {
    var subscribed = channels.get(channel);
    if (subscribed != null) {
      for (Client client : subscribed) {
        send(client, msg);
      }
    }
  }

  public void broadcast(String channel, String msg, WsContext except) {
    var subscribed = channels.get(channel);
    if (subscribed != null) {
      for (Client client : subscribed) {
        if (client.ctx.equals(except)) { continue; } 
        send(client, msg);
      }
    }
  }

  public synchronized void unsubscribeClient(WsContext ctx, String channel) {
    var sid = ctx.sessionId();
    var client = clients.get(sid);
    if (client == null) { return; }
    
    var subscribed = channels.get(channel);
    if (subscribed != null) {
      subscribed.remove(client);
    }
    client.channels.remove(channel);
  }

  public void removeClient(WsContext ctx) {
    var sid = ctx.sessionId();
    var client = clients.remove(sid);
    if (client == null) { return; }

    for (String channel: client.channels) {
      unsubscribeClient(ctx, channel);
    }
  }

  private void send(Client client, String msg) {
    var ctx = client.ctx;
    if (ctx.session.isOpen()) {
      ctx.send(msg);
    } else {
      for (String channel : client.channels) {
        unsubscribeClient(ctx, channel);
      }
    }
  }
}
