package Networking;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.ServerSocket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Config.ServerConfig;
import Referee.GameResult;
import Referee.MockReferee;

import static org.junit.Assert.*;

public class QGameWebSocketServerTest {
  private QGameWebSocketServer server;
  private final String hostname = "localhost";
  private final ExecutorService es = Executors.newCachedThreadPool();
  private final int port = 7788;
  private final int WAIT_FOR_LENGTH = 3;

  private WebSocket connectPlayer()  {
    return connectPlayer(new BufferedWebSocketClient());
  }

  private WebSocket connectPlayer(WebSocket.Listener listener) {
    try {
      return HttpClient
              .newHttpClient()
              .newWebSocketBuilder()
              .buildAsync(new URI("ws://" + hostname + ":" + port), listener)
              .join();
    }
    catch (URISyntaxException e ) {
      throw new IllegalStateException("Invalid up hostname/port in connectPlayer() test.");
    }
  }

  @Before
  public void init() {
  }

  @Test
  public void noConnections() throws InterruptedException {
    ServerConfig config = new ServerConfig.ServerConfigBuilder().port(7788)
            .numWaitingPeriods(1)
            .waitingPeriodLengthInSeconds(2).
            waitForNameInSeconds(WAIT_FOR_LENGTH).quiet(false).build();
    this.server = new QGameWebSocketServer(new MockReferee(), config, hostname);
    es.submit(this.server::run);
    //Sleeping for the lenght of the waiting periods plus some butter time.
    Thread.sleep(2 * 1000 + 50);
    assertEquals(GameResult.EMPTY_RESULT, this.server.getResult().get());
  }

  @Test
  public void singleConnection() throws InterruptedException {
    ServerConfig config = new ServerConfig.ServerConfigBuilder().port(7788)
            .numWaitingPeriods(2)
            .waitingPeriodLengthInSeconds(1).
            waitForNameInSeconds(WAIT_FOR_LENGTH).quiet(false).build();
    this.server = new QGameWebSocketServer(new MockReferee(), config, hostname);
    es.submit(this.server::run);
    WebSocket player1 = connectPlayer();
    player1.sendText("Name", true);
    Thread.sleep(2 * 1000 + 50);
    assertEquals(GameResult.EMPTY_RESULT, this.server.getResult().get());
  }

  @Test
  public void twoConnectionsNameTimeout() throws InterruptedException {
    ServerConfig config = new ServerConfig.ServerConfigBuilder().port(7788)
            .numWaitingPeriods(2)
            .waitingPeriodLengthInSeconds(1).
            waitForNameInSeconds(WAIT_FOR_LENGTH).quiet(false).build();
    this.server = new QGameWebSocketServer(new MockReferee(), config, hostname);
    es.submit(this.server::run);
    //Just connect the players and don't send anything.
    WebSocket _player1 = connectPlayer();
    WebSocket _player2 = connectPlayer();
    Thread.sleep(1 * 1000 + 50);
    //Entered the second waiting period
    assertEquals(Optional.empty(), this.server.getResult());
    Thread.sleep(1 * 1000 + 50);
    //Final waiting period is over, and there are still players who are pending connection (3 seconds haven't yet ellapsed), so game is not yet over.
    assertEquals(Optional.empty(), this.server.getResult());
    //One final roll call takes 3 seconds to wait for the name timeout.
    Thread.sleep(3 * 1000 + 50);
    assertEquals(GameResult.EMPTY_RESULT, this.server.getResult().get());
  }

  @Test
  public void nameTimeout() throws InterruptedException {
    ServerConfig config = new ServerConfig.ServerConfigBuilder().port(7788)
            .numWaitingPeriods(2)
            .waitingPeriodLengthInSeconds(2).
            waitForNameInSeconds(3).quiet(false).build();
    this.server = new QGameWebSocketServer(new MockReferee(), config, hostname);
    es.submit(this.server::run);
    //Just connect the players and don't send anything.
    WebSocket player1 = connectPlayer();
    WebSocket player2 = connectPlayer();
    Thread.sleep(1 * 1000 + 50);
    //Entered the second waiting period
    assertEquals(Optional.empty(), this.server.getResult());
    Thread.sleep(3 * 1000 + 50);
    assertEquals(GameResult.EMPTY_RESULT, this.server.getResult().get());
  }

  @Test
  public void nameTimeoutAfterThresholdHit() throws InterruptedException {
    ServerConfig config = new ServerConfig.ServerConfigBuilder().port(7788)
            .numWaitingPeriods(1)
            .waitingPeriodLengthInSeconds(1).
            waitForNameInSeconds(2).quiet(false).build();
    this.server = new QGameWebSocketServer(new MockReferee(), config, hostname);
    es.submit(this.server::run);
    //Just connect the players and don't send anything.
    WebSocket player1 = connectPlayer();
    player1.sendText("name1", true);
    WebSocket player2 = connectPlayer();
    player2.sendText("name2", true);
    WebSocket player3 = connectPlayer();
    //Waits for the waiting period to start for the game to run. Don't need to wait longer because result is locked during the execution of the game.
    Thread.sleep(1 * 1000 + 50);
    var result = this.server.getResult().get();
    assertTrue(result.equals(new GameResult(List.of("name1", "name2"), List.of())) ||
            result.equals(new GameResult(List.of("name2", "name1"), List.of())));
  }

  @Test
  public void nameSentAfterTimeout() throws InterruptedException {
    ServerConfig config = new ServerConfig.ServerConfigBuilder().port(7788)
            .numWaitingPeriods(1)
            .waitingPeriodLengthInSeconds(1).
            waitForNameInSeconds(2).quiet(false).build();
    this.server = new QGameWebSocketServer(new MockReferee(), config, hostname);
    es.submit(this.server::run);
    //Just connect the players and don't send anything.
    WebSocket player1 = connectPlayer();
    player1.sendText("name1", true);
    WebSocket player2 = connectPlayer();
    Thread.sleep(1 * 1000 + 50);
    //Player 2 hasn't sent it's name yet after the 1st waiting period, meaning the game ends.
    assertEquals(Optional.empty(), this.server.getResult());
    //Waits an additional waitForNameInSeconds worth of time to wait until player 2 times out for sending a name
    Thread.sleep(2 * 1000 + 100);
    assertEquals(GameResult.EMPTY_RESULT, this.server.getResult().get());
  }

  @Test
  public void maxOutConnections() throws InterruptedException {
    ServerConfig config = new ServerConfig.ServerConfigBuilder().port(7788)
            .numWaitingPeriods(1)
            .waitingPeriodLengthInSeconds(4).
            waitForNameInSeconds(1).quiet(false).build();
    this.server = new QGameWebSocketServer(new MockReferee(), config, hostname);
    es.submit(this.server::run);
    WebSocket player1 = connectPlayer();
    player1.sendText("name1", true);
    WebSocket player2 = connectPlayer();
    player2.sendText("name2", true);
    WebSocket player3 = connectPlayer();
    player3.sendText("name3", true);
    WebSocket player4 = connectPlayer();
    player4.sendText("name4", true);
    //Game is waiting to make sure player4 sends their stuff back.
    assertEquals(Optional.empty(), this.server.getResult());
    //Give the game time to play
    Thread.sleep(1 * 1000 + 100);
    GameResult result = this.server.getResult().get();
    assertEquals(result.winners.size(), 4);
    assertEquals(result.assholes.size(), 0);
  }

  @Test
  public void maxOutConnectionsOneNoName() throws InterruptedException {
    ServerConfig config = new ServerConfig.ServerConfigBuilder().port(7788)
            .numWaitingPeriods(1)
            .waitingPeriodLengthInSeconds(4).
            waitForNameInSeconds(1).quiet(false).build();
    this.server = new QGameWebSocketServer(new MockReferee(), config, hostname);
    es.submit(this.server::run);
    WebSocket player1 = connectPlayer();
    player1.sendText("name1", true);
    WebSocket player2 = connectPlayer();
    player2.sendText("name2", true);
    WebSocket player3 = connectPlayer();
    player3.sendText("name3", true);
    WebSocket _player4 = connectPlayer();
    //Game is waiting to make sure player4 sends their stuff back.
    assertEquals(Optional.empty(), this.server.getResult());
    //Give the game time to play
    Thread.sleep(1 * 1000 + 100);
    GameResult result = this.server.getResult().get();
    assertEquals(result.winners.size(), 3);
    assertEquals(result.assholes.size(), 0);
  }

  @Test
  public void maxOutConnectionsThreeNoName() throws InterruptedException {
    ServerConfig config = new ServerConfig.ServerConfigBuilder().port(7788)
            .numWaitingPeriods(1)
            .waitingPeriodLengthInSeconds(4).
            waitForNameInSeconds(1).quiet(false).build();
    this.server = new QGameWebSocketServer(new MockReferee(), config, hostname);
    es.submit(this.server::run);
    WebSocket player1 = connectPlayer();
    player1.sendText("name1", true);
    WebSocket _player2 = connectPlayer();
    WebSocket _player3 = connectPlayer();
    WebSocket _player4 = connectPlayer();
    //Game is waiting to make sure player4 sends their stuff back.
    assertEquals(Optional.empty(), this.server.getResult());
    //Give the game time to wait for the names to TimeOut (and also for all the sockets to close)
    Thread.sleep(1 * 1000 + 50);
    assertEquals(GameResult.EMPTY_RESULT, this.server.getResult().get());
  }

  @Test
  public void FiveConnectionsAllNames() throws InterruptedException {
    ServerConfig config = new ServerConfig.ServerConfigBuilder().port(7788)
            .numWaitingPeriods(1)
            .waitingPeriodLengthInSeconds(4).
            waitForNameInSeconds(1).quiet(false).build();
    this.server = new QGameWebSocketServer(new MockReferee(), config, hostname);
    es.submit(this.server::run);
    WebSocket player1 = connectPlayer();
    player1.sendText("name1", true);
    WebSocket player2 = connectPlayer();
    player2.sendText("name2", true);
    WebSocket player3 = connectPlayer();
    player3.sendText("name3", true);
    WebSocket player4 = connectPlayer();
    player4.sendText("name4", true);
    BufferedWebSocketClient fiveListener = new BufferedWebSocketClient();
    WebSocket player5 = connectPlayer(fiveListener);
    player5.sendText("name5", true);
    Thread.sleep(1000);
    assertEquals(4, this.server.getResult().get().winners.size());
    var result = JsonParser.parseString(fiveListener.getMessage());
    var expected = new JsonArray();
    expected.add("ERROR");
    expected.add("Server is full, try again later!");
    assertEquals(expected, result) ;
  }

  @Test
  public void gameStartedConnection() throws InterruptedException {
    ServerConfig config = new ServerConfig.ServerConfigBuilder().port(7788)
            .numWaitingPeriods(2)
            .waitingPeriodLengthInSeconds(1).
            waitForNameInSeconds(1).quiet(false).build();
    this.server = new QGameWebSocketServer(new MockReferee(), config, hostname);
    es.submit(this.server::run);
    WebSocket player1 = connectPlayer();
    player1.sendText("name2", true);
    WebSocket player2 = connectPlayer();
    player2.sendText("name2", true);
    //Wait until the 1st waiting period is done for the game to start.
    Thread.sleep(1000);
    BufferedWebSocketClient threeListener = new BufferedWebSocketClient();
    WebSocket player3 = connectPlayer(threeListener);
    player3.sendText("name3", true);
    var result = JsonParser.parseString(threeListener.getMessage());
    var expected = new JsonArray();
    expected.add("ERROR");
    expected.add("Game has already started.");
    assertEquals(expected, result);
    assertEquals(2, this.server.getResult().get().winners.size());
  }

  @After
  public void cleanup() throws InterruptedException {
    this.server.stop(1000);
  }

}