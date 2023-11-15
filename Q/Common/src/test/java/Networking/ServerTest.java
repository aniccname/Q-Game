package Networking;
import Referee.GameResult;
import Referee.MockReferee;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;

/**
 * Test class for Server.
 */
public class ServerTest {
  Server testServer;

  @Before
  public void setup() {
    testServer = new Server(new MockReferee(), 1000);
  }

  @Test
  public void noSignups() throws IOException {
    assertEquals(new GameResult(List.of(), List.of()), testServer.run(7777));
  }

  @Test
  public void oneSignup() throws IOException, ExecutionException, InterruptedException {
    ExecutorService serverExecutor = Executors.newSingleThreadExecutor();
    Future<GameResult> gameToRun = serverExecutor.submit(() -> testServer.run(7777));
    connectPlayer("Lonely Larry");
    assertEquals(new GameResult(List.of(), List.of()), gameToRun.get());
  }

  @Test
  public void twoSignups() throws IOException, ExecutionException, InterruptedException {
    ExecutorService serverExecutor = Executors.newSingleThreadExecutor();
    Future<GameResult> gameToRun = serverExecutor.submit(() -> testServer.run(7777));
    connectPlayer("Alice");
    connectPlayer("Bob");
    assertEquals(new GameResult(List.of("Alice", "Bob"), List.of()), gameToRun.get());
  }

  @Test
  public void fiveSignupts()
          throws IOException, ExecutionException, InterruptedException {
    ExecutorService serverExecutor = Executors.newSingleThreadExecutor();
    Future<GameResult> gameToRun = serverExecutor.submit(() -> testServer.run(7777));
    connectPlayer("Alice");
    connectPlayer("Bob");
    connectPlayer("Charlie");
    connectPlayer("Daniel");
    connectPlayer("Eric");
    assertEquals(new GameResult(List.of("Alice", "Bob", "Charlie", "Daniel"), List.of()), gameToRun.get());
  }

  private void connectPlayer(String name) throws IOException {
    try(Socket socket = new Socket("localhost", 7777)) {
      new PrintStream(socket.getOutputStream()).println("\"" + name + "\"");
    }
  }
}
