package Networking;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonStreamParser;

import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import Action.ExchangeAction;
import Action.IAction;
import Action.PassAction;
import Action.PlaceAction;
import Config.ScoringConfig;
import Map.Coord;
import Map.GameMap;
import Map.Tile.ITile;
import Map.Tile.Tile;
import Player.IPlayer;
import Referee.GameState;
import Referee.IShareableInfo;
import Referee.PlayerState;
import Serialization.JCoordinate;
import Serialization.JTile;
import Serialization.OnePlacement;

import static org.junit.Assert.*;

public class WebsocketProxyPlayerTest {

  private final ScoringConfig defaultConfig = new ScoringConfig(6, 10);
  //Source socket sends to the proxyListener, and vice versa.
  private java.net.http.WebSocket sourceSocket;
  private BufferedWebSocketClient sourceListener;
  private ProxyServer proxyServer;
  private ExecutorService executorService;
  private WebsocketProxyPlayer proxyPlayer;
  private final int port = 7778;
  private final String hostname = "localhost";

  private class ProxyServer extends WebSocketServer {

    public ProxyServer(InetSocketAddress addr) {
      super(addr);
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
      proxyPlayer = new WebsocketProxyPlayer(webSocket);
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {

    }

    @Override
    public void onMessage(WebSocket webSocket, String s) {
      proxyPlayer.sendInput(s);
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {

    }

    @Override
    public void onStart() {
      System.out.println("Server started!");
    }
  }

  @Before
  public void init() throws URISyntaxException, InterruptedException {
    this.executorService = Executors.newCachedThreadPool();
    this.proxyServer = new ProxyServer(new InetSocketAddress(hostname, port));
    this.proxyServer.setDaemon(true);
    executorService.submit(() -> this.proxyServer.run());
    this.sourceListener = new BufferedWebSocketClient();

    this.sourceSocket = HttpClient
          .newHttpClient()
          .newWebSocketBuilder()
          .buildAsync(new URI("ws://" + hostname + ":" + port), this.sourceListener)
          .join();

    sourceSocket.sendText("Jove", true);
    Thread.sleep(500);
  }

  @Test(timeout = 1500L)
  public void testName() {
    System.out.println("Testing name!");
    assertEquals("Jove", this.proxyPlayer.name());
  }

  @Test//(timeout = 2000L)
  public void testSetup() throws InterruptedException, IOException, ExecutionException {
    IShareableInfo gb = new GameState(new GameMap(new Tile(ITile.TileColor.Green, ITile.Shape.Star)),
            List.of(), List.of(new PlayerState("Jave"), new PlayerState("Janual")), defaultConfig);
    Future<?> future = executorService.submit(() -> this.proxyPlayer.setup(gb, List.of()));
    Thread.sleep(500);
    JsonArray result = (JsonArray) JsonParser.parseString(this.sourceListener.getMessage());
    assertEquals(new JsonPrimitive("setup"), result.get(0));
    assertTrue(result.get(1) instanceof JsonArray);
    assertEquals(new JsonArray(), result.get(1).getAsJsonArray().get(1));
    this.sourceSocket.sendText(new JsonPrimitive("void").getAsString(), true);
    Thread.sleep(1000);
    future.get();
  }

  @Test(timeout = 1000L)
  public void testTakeTurnPass() throws InterruptedException, IOException, ExecutionException {
    IShareableInfo gb = new GameState(new GameMap(new Tile(ITile.TileColor.Green, ITile.Shape.Star)),
            List.of(), List.of(new PlayerState("Jave"), new PlayerState("Janual")), defaultConfig);
    Future<IAction> future = executorService.submit(() -> this.proxyPlayer.takeAction(gb));
    Thread.sleep(500);
    JsonArray result = (JsonArray) JsonParser.parseString(this.sourceListener.getMessage());
    assertEquals(new JsonPrimitive("take-turn"), result.get(0));
    assertEquals(1, result.get(1).getAsJsonArray().size());
    this.sourceSocket.sendText(new JsonPrimitive("pass").getAsString(), true);
    assertTrue(future.get() instanceof PassAction);
  }

  @Test(timeout = 1000L)
  public void testTakeTurnExchange() throws InterruptedException, IOException, ExecutionException {
    IShareableInfo gb = new GameState(new GameMap(new Tile(ITile.TileColor.Green, ITile.Shape.Star)),
            List.of(), List.of(new PlayerState("Jave"), new PlayerState("Janual")), defaultConfig);
    Future<IAction> future = executorService.submit(() -> this.proxyPlayer.takeAction(gb));
    Thread.sleep(500);
    JsonArray result = (JsonArray) JsonParser.parseString(this.sourceListener.getMessage());
    assertEquals(new JsonPrimitive("take-turn"), result.get(0));
    assertEquals(1, result.get(1).getAsJsonArray().size());
    this.sourceSocket.sendText(new JsonPrimitive("replace").getAsString(), true);
    assertTrue(future.get() instanceof ExchangeAction);
  }

  @Test(timeout = 1000L)
  public void testTakeTurnPlace() throws InterruptedException, IOException, ExecutionException {
    IShareableInfo gb = new GameState(new GameMap(new Tile(ITile.TileColor.Green, ITile.Shape.Star)),
            List.of(), List.of(new PlayerState("Jave"), new PlayerState("Janual")), defaultConfig);
    Future<IAction> future = executorService.submit(() -> this.proxyPlayer.takeAction(gb));
    Thread.sleep(500);
    JsonArray result = (JsonArray) JsonParser.parseString(this.sourceListener.getMessage());
    assertEquals(new JsonPrimitive("take-turn"), result.get(0));
    assertEquals(1, result.get(1).getAsJsonArray().size());
    JsonArray placements = new JsonArray();
    placements.add(new Gson().toJsonTree(new OnePlacement(new JCoordinate(new Coord(0, 1)),
            new JTile(new Tile(ITile.TileColor.Green, ITile.Shape.Clover)))));
    this.sourceSocket.sendText(placements.toString(), true);
    PlaceAction action = (PlaceAction) future.get();
    assertEquals(List.of(
                    new AbstractMap.SimpleEntry<>(new Coord(0, 1),
                            new Tile(ITile.TileColor.Green, ITile.Shape.Clover))),
            action.getPlacements());
  }

  @Test(timeout = 1000L)
  public void testNewTiles() throws InterruptedException, IOException, ExecutionException {
    Future<?> future = executorService.submit(() -> this.proxyPlayer.newTiles(List.of()));
    Thread.sleep(500);
    JsonArray result = (JsonArray) JsonParser.parseString(this.sourceListener.getMessage());
    assertEquals(new JsonPrimitive("new-tiles"), result.get(0));
    assertEquals(1, result.get(1).getAsJsonArray().size());
    assertEquals(0, result.get(1).getAsJsonArray().get(0).getAsJsonArray().size());
    this.sourceSocket.sendText(new JsonPrimitive("void").getAsString(), true);
    future.get();
  }

  @Test(timeout = 1000L)
  public void testWin() throws InterruptedException, IOException, ExecutionException {
    Future<?> future = executorService.submit(() -> this.proxyPlayer.win(false));
    Thread.sleep(500);
    JsonArray result = (JsonArray) JsonParser.parseString(this.sourceListener.getMessage());
    assertEquals(new JsonPrimitive("win"), result.get(0));
    assertEquals(1, result.get(1).getAsJsonArray().size());
    assertEquals(new JsonPrimitive(false), result.get(1).getAsJsonArray().get(0));
    this.sourceSocket.sendText(new JsonPrimitive("void").getAsString(), true);
    future.get();
  }

  @After
  public void cleanup() throws InterruptedException {
    this.sourceSocket.sendClose(java.net.http.WebSocket.NORMAL_CLOSURE, "test done!").join();
    this.proxyServer.stop(100);
    this.sourceSocket.abort();
    this.executorService.shutdown();
  }

}