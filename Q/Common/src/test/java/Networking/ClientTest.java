package Networking;

import com.google.gson.JsonPrimitive;
import com.google.gson.JsonStreamParser;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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

  int port = 7777;

  @Test
  public void testClient() throws IOException, ExecutionException, InterruptedException {
    executorService = Executors.newFixedThreadPool(2);
    ServerSocket listener = new ServerSocket(port);
    Future<Socket> futureSource = executorService.submit(listener::accept);
    client = new Client(new ProxyReferee(new Player("Jon", new DagStrategy())), true);
    //Deadlock is happening between the server socket and the client, where the listener is being created,
    // then the client is being launched connecting and then playing the game and waiting for the next message (that the first message is their name). How to make this work though
    Future<?> client_playing = executorService.submit(() -> client.connectAndPlay( "localhost", port));
    serverSocket = futureSource.get();
    JsonStreamParser jp = new JsonStreamParser(new InputStreamReader(serverSocket.getInputStream()));
    assertEquals(new JsonPrimitive("Jon"), jp.next());
    listener.close();
  }

}