package Networking;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import Config.RefereeConfig;
import Config.ScoringConfig;
import Config.ServerConfig;
import Player.IPlayer;

import Referee.GameResult;
import Referee.GameState;
import Referee.IReferee;
import Referee.PlayerID;

//TODO Investigate whether I can create a new socket with the INetSocketAddr of the socket.
public class QGameWebSocketServer extends WebSocketServer {
  private volatile Map<WebSocket, WebsocketProxyPlayer> connections = new HashMap<>();
  /* Is there a way to only do it with one map? Yes. Maybe just filter based off of names that
    aren't equal to null? But this would mean that if there existed someone in the players tab
    who hasn't had a chance to send their name yet (they haven't hit the timeout) they would be
    removed. As such, I think the best way to go about it would be for the Future task in
    SignupPlayer() to remove the WebSocket/Proxy pair from players (renamed to connections) if the
    name is still null after the countdown, and for the gameStart() method to see if there are any
    pending players (WebSocketProxyPlayer where name is null) and then to wait the name timeout
    seconds and try again (not sure if I should use a future or Thread.sleep)
   */
  private final IReferee referee;
  private final ExecutorService es;
  private final Timer t;
  private final ServerConfig config;
  private GameResult result;
  private int waitingPeriod = 1;
  private volatile boolean gameStarted = false;
  private static final int MIN_PLAYERS = 2;
  private static final int MAX_PLAYERS = 4;
  private static final String ERROR = "ERROR";
  /** Lock for result (since it's getting assigned) */
  private final Lock lock;
  /**
   * A task to stop accepting signups if the number of waiting periods have elapsed,
   * otherwise waits for more signups.
   */
  private class StopAcceptingSignups extends TimerTask {

    @Override
    public void run() {
      //Determines how many players have sent their name back (and have thus been confirmed for the game).
      long confirmedPlayers = connections.values().stream().filter((pp) -> pp.name != null).count();
      if (!gameStarted) {
        if (confirmedPlayers >= MIN_PLAYERS) {
          gameStarted = true;
          playGame();
        }
        //If this is the final waiting period, the game is over.
        else if (waitingPeriod >= config.numWaitingPeriods()) {
          //There are enough pending connections to start a game, wait for them and try again.
          if (connections.size() >= MIN_PLAYERS) {
            printlnIfLoud("Pending players, scheduling again after the waiting period");
            scheduleTimeout(config.waitForNameInSeconds());
          } else {
            printlnIfLoud("Not enough players, game is over. ");
            lock.lock();
            result = GameResult.EMPTY_RESULT;
            gameStarted = true;
            lock.unlock();
            printlnIfLoud(result.toString());
          }
        } else {
          waitingPeriod += 1;
          printlnIfLoud("waiting period " + waitingPeriod + " elapsed. Trying again.");
          scheduleGameStart();
        }
      }
    }
  }

  /**
   * Creates a new QGameWebSocketServer at the given hostname and port (from the config). Uses the
   * referee to play with the minimum to maximum amount of players.
   * @param referee The referee to play the game with.
   * @param config The config to run the server with.
   * @param hostname The hostname this server will run on.
   */
  public QGameWebSocketServer(IReferee referee, ServerConfig config, int port, String hostname) {
    super(new InetSocketAddress(hostname, port));
    this.config = config;
    this.referee = referee;
    this.es = Executors.newCachedThreadPool();
    this.t = new Timer();
    this.lock = new ReentrantLock();
    printlnIfLoud("Server created at " + hostname + ":" + config.port());
  }

  public QGameWebSocketServer(IReferee referee, String hostname) {
    this(referee, new ServerConfig.ServerConfigBuilder().build(), hostname);
  }

  public QGameWebSocketServer(IReferee referee, ServerConfig config, String hostname) {
    this(referee, config, config.port(), hostname);
  }

  /**
   * Schedules the timeout task in timeoutInSecond seconds
   * @param timeoutInSeconds how many seconds to schedule the timout.
   */
  private void scheduleTimeout(long timeoutInSeconds) {
    t.schedule(new StopAcceptingSignups(), timeoutInSeconds * 1000L);
  }

  private void scheduleGameStart() {
    this.scheduleTimeout(config.waitingPeriodLengthInSeconds());
  }

  private void printlnIfLoud(String s) {
    if (!config.quiet()) {
      System.out.println(s);
    }
  }
  /**
   * Plays the game and updates the result, waiting for any pending connections to resolve.
   */
  private void playGame() {
    this.lock.lock();
    this.gameStarted = true;
    List<IPlayer> players = new ArrayList<>(this.connections.values());
    //Wait for
    if (players.stream().anyMatch((p) -> p.name() == null)) {
      try {
        Thread.sleep(1000L * config.waitForNameInSeconds());
        //This gives enough time for the getPlayer() method to remove the ProxyPlayer if the remote user hasn't responded.
        players = new ArrayList<>(this.connections.values());
      } catch (InterruptedException e) {
        //I don't know what to do here.
      }
    }
    if (this.connections.size() >= MIN_PLAYERS) {
      printlnIfLoud("Starting game");
      this.result = referee.playGame(players,
              new RefereeConfig.RefereeConfigBuilder()
                      .gameState(new GameState(players.stream()
                              .map(p -> new PlayerID(p.id(), p.name())).toList(),
                              new ScoringConfig.ScoringConfigBuilder().build()))
                      .playerTimeoutInSeconds(config.refereeConfig().playerTimeoutInSeconds())
                      .build());
    } else {
      this.result = GameResult.EMPTY_RESULT;
    }
    printlnIfLoud("Game ended with result " + result.toString());
    this.connections.keySet().forEach(WebSocket::close);
    this.lock.unlock();
  }
  /**Adds the player, and if they are the final player plays the game**/
  private void addPlayer(WebSocket s) {
    var player = new WebsocketProxyPlayer(s);
    this.connections.put(s, player);
    es.submit(() -> {removePlayerIfUnresponsive(s, player);
      if(this.connections.size() == MAX_PLAYERS && !this.gameStarted) playGame();});
  }

  /**
   * Creates the ProxyPlayer of the given WebSocket if the user has sent a name in the connections map.
   * in the required amount of seconds (specified by the config).
   * @param s The WebSocket that is connected to the player.
   */
  private void removePlayerIfUnresponsive(WebSocket s, WebsocketProxyPlayer player) {
    String name;
    try {
      Thread.sleep(1000L * config.waitForNameInSeconds());
      name = player.name();
    } catch (InterruptedException e) {
      //I have no idea when this would execute.
      throw new IllegalStateException("Unable to retrieve name from WebsocketProxyPlayer," +
              " error caused by " + e.toString());
    }
    //Player did not send name in time, remove the connection and close the websocket.
    if (name == null) {
      printlnIfLoud("Did not receive name " + name + " from " + s.getRemoteSocketAddress() + " in time.");
      this.connections.remove(s);
      s.close();
    }
    else {
      printlnIfLoud("Received name " + name + " from " + s.getRemoteSocketAddress() + " in time.");
    }
  }
  @Override
  public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
    printlnIfLoud("Socket connected from " + webSocket.getRemoteSocketAddress().getHostString());
    if (!this.gameStarted && this.connections.size() < MAX_PLAYERS) {
     addPlayer(webSocket);
    } else  if (this.gameStarted){
      printlnIfLoud("ERROR: Game has already started!");
      webSocket.send(error("Game has already started.").toString());
      webSocket.close();
    } else {
      printlnIfLoud("ERROR: Server is full!");
      webSocket.send(error("Server is full, try again later!").toString());
      webSocket.close();
    }
  }

  private JsonElement error(String message) {
    JsonArray response = new JsonArray();
    response.add(ERROR);
    JsonArray msg = new JsonArray();
    msg.add(message);
    response.add(msg);
    return response;
  }

  @Override
  public void onClose(WebSocket webSocket, int i, String s, boolean b) {
    printlnIfLoud("Socket " + webSocket.toString() + " closed with code " + i);
    //TODO: Should this remove the webSocket from the connections map?
  }

  @Override
  public void onMessage(WebSocket webSocket, String s) {
    if (this.connections.containsKey(webSocket)) {
      this.connections.get(webSocket).sendInput(s);
    }
  }

  @Override
  public void onError(WebSocket webSocket, Exception e) {

  }

  @Override
  public void onStart() {
    scheduleGameStart();
  }

  /**
   * Gets the result of the game that was played, if one has been played.
   * @return Optional.empty() if the game has not started, Optional.of(GameResult)
   * if the game has concluded. Code will wait until the game is over if there is a game in progress.
   */
  public Optional<GameResult> getResult() {
    this.lock.lock();
    printlnIfLoud("Getting result!");
    var r = Optional.ofNullable(result);
    this.lock.unlock();
    return r;
  }
}
