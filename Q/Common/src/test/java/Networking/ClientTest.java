package Networking;

import com.google.gson.JsonPrimitive;
import com.google.gson.JsonStreamParser;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import Player.IPlayer;
import Player.Player;
import Player.Strategy.DagStrategy;

import static org.junit.Assert.*;

/**
 * Tests for the client/
 */
public class ClientTest {
  Socket serverSocket;
  Client client;
  ExecutorService executorService;

  int port;

  @Test
  public void testClient() throws IOException, ExecutionException, InterruptedException {
    port = 7777;
    executorService = Executors.newSingleThreadExecutor();
    ServerSocket listener = new ServerSocket(port);
    Future<Socket> futureSource = executorService.submit(listener::accept);
    client = new Client(new MockRefereeProxy());
    client.connect(new Player("Jon", new DagStrategy()), "localhost", port);
    serverSocket = futureSource.get();
    JsonStreamParser jp = new JsonStreamParser(new InputStreamReader(serverSocket.getInputStream()));
    assertEquals(new JsonPrimitive("Jon"), jp.next());
  }

}