package Networking;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import Config.RefereeConfig;
import Config.ScoringConfig;
import Config.ServerConfig;
import Player.IPlayer;

import Player.Player;
import Referee.GameState;
import Referee.IReferee;

//TODO Investigate whether I can create a new socket with the INetSocketAddr of the socket.
public class QGameWebSocketServer extends WebSocketServer {
  private Map<WebSocket, WebsocketProxyPlayer> players = new HashMap<>();
  private final IReferee referee;
  private final ExecutorService es;

  public QGameWebSocketServer(IReferee referee, String hostname, int port) {
    super(new InetSocketAddress(hostname, port));
    this.referee = referee;
    System.out.println("Server created at " + hostname + ":" + port);
    this.es = Executors.newSingleThreadExecutor();
  }
  @Override
  public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
    System.out.println("Socket connected from " + webSocket.getRemoteSocketAddress().getHostString());
    //new Socket(new Proxy(Proxy.Type.HTTP, webSocket.getRemoteSocketAddress()))
    if (players.isEmpty()) {
      this.players.put(webSocket, new WebsocketProxyPlayer(webSocket));
    }
    else {
      this.players.put(webSocket, new WebsocketProxyPlayer(webSocket));
      System.out.println("Starting game");
      List<IPlayer> players = new ArrayList<>(this.players.values());
      this.es.submit(() -> referee.playGame(players,
              new RefereeConfig.RefereeConfigBuilder()
                      .gameState(new GameState(players.stream().map(IPlayer::name).toList(),
                              new ScoringConfig.ScoringConfigBuilder().build())).build()));
    }
  }

  @Override
  public void onClose(WebSocket webSocket, int i, String s, boolean b) {

  }

  @Override
  public void onMessage(WebSocket webSocket, String s) {
    this.players.get(webSocket).sendInput(s);
  }

  @Override
  public void onError(WebSocket webSocket, Exception e) {

  }

  @Override
  public void onStart() {

  }
}
