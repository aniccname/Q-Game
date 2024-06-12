package Networking;
import Config.RefereeConfig;
import Config.ServerConfig;
import Referee.GameResult;
import Referee.MockReferee;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.List;
import java.util.Optional;
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
  final int port = 7781;
  @Before
  public void setup() {
    testServer = new Server(new MockReferee(),
            new ServerConfig(port, 2, 10, 1, true,
                    new RefereeConfig(Optional.empty(), 3, Optional.empty())));
  }

  @Test
  public void noSignups() throws IOException {
    assertEquals(new GameResult(List.of(), List.of()), testServer.run(7780));
  }

  @Test
  public void oneSignup() throws IOException, ExecutionException, InterruptedException {
    ExecutorService serverExecutor = Executors.newSingleThreadExecutor();
    Future<GameResult> gameToRun = serverExecutor.submit(() -> testServer.run(port));
    connectPlayer("Lonely Larry", port);
    assertEquals(new GameResult(List.of(), List.of()), gameToRun.get());
  }

  @Test
  public void twoSignups() throws IOException, ExecutionException, InterruptedException {
    int port = 7890;
    ExecutorService serverExecutor = Executors.newSingleThreadExecutor();
    Future<GameResult> gameToRun = serverExecutor.submit(() -> testServer.run(port));
    connectPlayer("Alice", port);
    connectPlayer("Bob", port);
    assertEquals(new GameResult(List.of("Alice", "Bob"), List.of()), gameToRun.get());
  }

  @Test
  public void fiveSignupts()
          throws IOException, ExecutionException, InterruptedException {
    int port = 7783;
    ExecutorService serverExecutor = Executors.newSingleThreadExecutor();
    Future<GameResult> gameToRun = serverExecutor.submit(() -> testServer.run(port));
    connectPlayer("Alice", port);
    connectPlayer("Bob", port);
    connectPlayer("Charlie", port);
    connectPlayer("Daniel", port);
    connectPlayer("Eric", port);
    assertEquals(new GameResult(List.of("Alice", "Bob", "Charlie", "Daniel"), List.of()), gameToRun.get());
  }

  private void connectPlayer(String name, int port) throws IOException {
    try(Socket socket = new Socket("localhost", port)) {
      new PrintStream(socket.getOutputStream()).println("\"" + name + "\"");
    }
  }
}
