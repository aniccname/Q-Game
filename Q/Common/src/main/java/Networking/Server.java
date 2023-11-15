package Networking;

import com.google.gson.Gson;
import com.google.gson.JsonStreamParser;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import Referee.IReferee;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import Player.IPlayer;
import Referee.GameResult;

/**
 * Represents a Server that <ul>
 *   <li>Waits for at least 2 and at most 4 players.
 *   If at 20 seconds there are at least 2 players, starts a QGame. If 2 players have not joined by
 *   40 seconds, does not run a game
 *   <li>Once the game is over, prints out the winners and misbehaving players of a game, as
 *   specified by the referee.
 *   </ul>
 */
public class Server {
  private final IReferee referee;
  volatile boolean acceptingSignups = true;
  private static final int waitForNameInSeconds = 3;
  private final int waitBetweenSignupsInMilliseconds;

  private static final int minPlayers = 2;
  private static final int maxPlayers = 4;
  private static final int signupBlockDurationInSeconds = 1;

  /**
   * Constructs a new Server with the given referee.
   * @param referee the referee that will run the game.
   */
  public Server(IReferee referee, int waitBetweenSignupsInMilliseconds) {
    this.referee = referee;
    this.waitBetweenSignupsInMilliseconds = waitBetweenSignupsInMilliseconds;
  }

  public Server(IReferee referee) {
    this(referee, 20 * 1000);
  }

  /**
   * Sets up and runs a Q game.
   * @param port The port to listen on.
   * @return The result of the Q game.
   */
  public GameResult run(int port) throws IOException {
    ExecutorService threadPool = Executors.newCachedThreadPool();
    try (
            ServerSocket listener = new ServerSocket(port)
    ) {
      List<IPlayer> players = new ArrayList<>(acceptSignups(listener, threadPool, maxPlayers));
      if (players.size() < minPlayers) {
        players.addAll(acceptSignups(listener, threadPool, maxPlayers - players.size()));
      }
      if (players.size() < minPlayers) {
        return new GameResult(List.of(), List.of());
      }
      return referee.playGame(players);
    } finally {
      threadPool.shutdown();
    }
  }

  private List<IPlayer> acceptSignups(ServerSocket listener,
                                      ExecutorService executorService,
                                      int maxSignups) throws SocketException {
    listener.setSoTimeout(signupBlockDurationInSeconds);
    acceptingSignups = true;
    Timer waitTime = new Timer();
    waitTime.schedule(new StopAcceptingSignups(), waitBetweenSignupsInMilliseconds);
    List<IPlayer> players = new ArrayList<>();
    while (acceptingSignups && players.size() < maxSignups) {
      this.acceptOneSignup(listener, executorService).ifPresent(players::add);
    }
    waitTime.cancel();
    return players;
  }

  private Optional<IPlayer> acceptOneSignup(ServerSocket listener, ExecutorService executorService) {
    Socket prospectivePlayer;
    try {
      prospectivePlayer = listener.accept();
    } catch (IOException e) {
      return Optional.empty();
    }
    return this.getPlayerWithTimeout(prospectivePlayer, executorService);
  }

  private IPlayer getPlayer(Socket player) throws IOException {
    JsonStreamParser parser = new JsonStreamParser(new InputStreamReader(player.getInputStream()));
    Gson gson = new Gson();
    String name = gson.fromJson(parser.next(), String.class);
    return new ProxyPlayer(player, name);
  }

  private Optional<IPlayer> getPlayerWithTimeout(Socket player, ExecutorService executorService) {
    Future<IPlayer> future = executorService.submit(() -> this.getPlayer(player));
    try {
      return Optional.ofNullable(future.get(Server.waitForNameInSeconds, TimeUnit.SECONDS));
    } catch (Exception e) {
      future.cancel(true);
      return Optional.empty();
    }
  }

  /**
   * A timer task to close accepting signups.
   */
  private class StopAcceptingSignups extends TimerTask {

    @Override
    public void run() {
      Server.this.acceptingSignups = false;
    }
  }

}
