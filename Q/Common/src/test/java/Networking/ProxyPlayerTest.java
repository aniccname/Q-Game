package Networking;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonStreamParser;
import com.google.gson.stream.JsonReader;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.AbstractMap;
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
import Serialization.JPub;
import Serialization.JTile;
import Serialization.OnePlacement;

import static org.junit.Assert.*;

/**
 * Testing class for player.
 */
public class ProxyPlayerTest {
  private final ScoringConfig defaultConfig = new ScoringConfig(6, 10);

  IPlayer proxy;
  Socket sourceSocket;
  Socket proxySocket;
  ExecutorService executorService;

  int port = 7778;

  @Before
  public void setup() throws IOException, ExecutionException, InterruptedException {
    executorService = Executors.newSingleThreadExecutor();
    ServerSocket listener = new ServerSocket(port);
    Future<Socket> futureSource = executorService.submit(listener::accept);
    proxySocket = new Socket("localhost", port);
    var inputStream = proxySocket.getInputStream();
    sourceSocket = futureSource.get();
    var sourceStream = new PrintStream(sourceSocket.getOutputStream());
    sourceStream.println(new JsonPrimitive("Jave"));
    sourceStream.flush();
    proxy = new ProxyPlayer(inputStream, proxySocket.getOutputStream());
    listener.close();
  }

  @After
  public void cleanup() throws IOException {
    proxySocket.close();
    executorService.shutdown();
  }

  @Test(timeout = 1000L)
  public void testName() {
    assertEquals("Jave", proxy.name());
  }

  @Test(timeout = 1000L)
  public void testSetup() throws InterruptedException, IOException, ExecutionException {
    IShareableInfo gb = new GameState(new GameMap(new Tile(ITile.TileColor.Green, ITile.Shape.Star)),
            List.of(), List.of(new PlayerState("Jave"), new PlayerState("Janual")), defaultConfig);
    Future<?> future = executorService.submit(() -> this.proxy.setup(gb, List.of()));
    Thread.sleep(500);
    JsonStreamParser jparser =
            new JsonStreamParser(new InputStreamReader(this.sourceSocket.getInputStream()));
    JsonArray result = (JsonArray) jparser.next();
    assertEquals(new JsonPrimitive("setup"), result.get(0));
    assertTrue(result.get(1) instanceof JsonArray);
    assertEquals(new JsonArray(), result.get(1).getAsJsonArray().get(1));
    new PrintStream(this.sourceSocket.getOutputStream()).println(new JsonPrimitive("void"));
    future.get();
  }

  @Test(timeout = 1000L)
  public void testTakeTurnPass() throws InterruptedException, IOException, ExecutionException {
    IShareableInfo gb = new GameState(new GameMap(new Tile(ITile.TileColor.Green, ITile.Shape.Star)),
            List.of(), List.of(new PlayerState("Jave"), new PlayerState("Janual")), defaultConfig);
    Future<IAction> future = executorService.submit(() -> this.proxy.takeAction(gb));
    Thread.sleep(500);
    JsonStreamParser jparser =
            new JsonStreamParser(new InputStreamReader(this.sourceSocket.getInputStream()));
    JsonArray result = (JsonArray) jparser.next();
    assertEquals(new JsonPrimitive("take-turn"), result.get(0));
    assertEquals(1, result.get(1).getAsJsonArray().size());
    new PrintStream(this.sourceSocket.getOutputStream()).println(new JsonPrimitive("pass"));
    assertTrue(future.get() instanceof PassAction);
  }

  @Test(timeout = 1000L)
  public void testTakeTurnExchange() throws InterruptedException, IOException, ExecutionException {
    IShareableInfo gb = new GameState(new GameMap(new Tile(ITile.TileColor.Green, ITile.Shape.Star)),
            List.of(), List.of(new PlayerState("Jave"), new PlayerState("Janual")), defaultConfig);
    Future<IAction> future = executorService.submit(() -> this.proxy.takeAction(gb));
    Thread.sleep(500);
    JsonStreamParser jparser =
            new JsonStreamParser(new InputStreamReader(this.sourceSocket.getInputStream()));
    JsonArray result = (JsonArray) jparser.next();
    assertEquals(new JsonPrimitive("take-turn"), result.get(0));
    assertEquals(1, result.get(1).getAsJsonArray().size());
    new PrintStream(this.sourceSocket.getOutputStream()).println(new JsonPrimitive("replace"));
    assertTrue(future.get() instanceof ExchangeAction);
  }

  @Test(timeout = 1000L)
  public void testTakeTurnPlace() throws InterruptedException, IOException, ExecutionException {
    IShareableInfo gb = new GameState(new GameMap(new Tile(ITile.TileColor.Green, ITile.Shape.Star)),
            List.of(), List.of(new PlayerState("Jave"), new PlayerState("Janual")), defaultConfig);
    Future<IAction> future = executorService.submit(() -> this.proxy.takeAction(gb));
    Thread.sleep(500);
    JsonStreamParser jparser =
            new JsonStreamParser(new InputStreamReader(this.sourceSocket.getInputStream()));
    JsonArray result = (JsonArray) jparser.next();
    assertEquals(new JsonPrimitive("take-turn"), result.get(0));
    assertEquals(1, result.get(1).getAsJsonArray().size());
    JsonArray placements = new JsonArray();
    placements.add(new Gson().toJsonTree(new OnePlacement(new JCoordinate(new Coord(0, 1)),
            new JTile(new Tile(ITile.TileColor.Green, ITile.Shape.Clover)))));
    new PrintStream(this.sourceSocket.getOutputStream()).println(placements);
    PlaceAction action = (PlaceAction) future.get();
    assertEquals(List.of(
            new AbstractMap.SimpleEntry<>(new Coord(0, 1),
                    new Tile(ITile.TileColor.Green, ITile.Shape.Clover))),
            action.getPlacements());
  }

  @Test(timeout = 1000L)
  public void testNewTiles() throws InterruptedException, IOException, ExecutionException {
    Future<?> future = executorService.submit(() -> this.proxy.newTiles(List.of()));
    Thread.sleep(500);
    JsonStreamParser jparser =
            new JsonStreamParser(new InputStreamReader(this.sourceSocket.getInputStream()));
    JsonArray result = (JsonArray) jparser.next();
    assertEquals(new JsonPrimitive("new-tiles"), result.get(0));
    assertEquals(1, result.get(1).getAsJsonArray().size());
    assertEquals(0, result.get(1).getAsJsonArray().get(0).getAsJsonArray().size());
    new PrintStream(this.sourceSocket.getOutputStream()).println(new JsonPrimitive("void"));
    future.get();
  }

  @Test(timeout = 1000L)
  public void testWin() throws InterruptedException, IOException, ExecutionException {
    Future<?> future = executorService.submit(() -> this.proxy.win(false));
    Thread.sleep(500);
    JsonStreamParser jparser =
            new JsonStreamParser(new InputStreamReader(this.sourceSocket.getInputStream()));
    JsonArray result = (JsonArray) jparser.next();
    assertEquals(new JsonPrimitive("win"), result.get(0));
    assertEquals(1, result.get(1).getAsJsonArray().size());
    assertEquals(new JsonPrimitive(false), result.get(1).getAsJsonArray().get(0));
    new PrintStream(this.sourceSocket.getOutputStream()).println(new JsonPrimitive("void"));
    future.get();
  }

  @Test(timeout = 1000L)
  public void testWatchTurn() throws InterruptedException, IOException, ExecutionException {
    IShareableInfo gb = new GameState(new GameMap(new Tile(ITile.TileColor.Green, ITile.Shape.Star)),
            List.of(), List.of(new PlayerState("Jave"), new PlayerState("Janual")), defaultConfig);
    Future<?> future = executorService.submit(() -> this.proxy.watchTurn(gb));
    Thread.sleep(500);
    JsonStreamParser jparser =
            new JsonStreamParser(new InputStreamReader(this.sourceSocket.getInputStream()));
    JsonArray result = (JsonArray) jparser.next();
    assertEquals(new JsonPrimitive("watch-turn"), result.get(0));
    assertTrue(result.get(1) instanceof JsonArray);
    assertEquals(new JPub(gb, proxy.id()).serialize(), result.get(1).getAsJsonArray().get(0));
    new PrintStream(this.sourceSocket.getOutputStream()).println(new JsonPrimitive("void"));
    future.get();
  }

  @Test(timeout = 1000L)
  public void testError() throws InterruptedException, IOException, ExecutionException {
    Future<?> future = executorService.submit(() -> this.proxy.error("reason"));
    Thread.sleep(500);
    JsonStreamParser jparser =
            new JsonStreamParser(new InputStreamReader(this.sourceSocket.getInputStream()));
    JsonArray result = (JsonArray) jparser.next();
    assertEquals(new JsonPrimitive("ERROR"), result.get(0));
    assertTrue(result.get(1) instanceof JsonArray);
    assertEquals(new JsonPrimitive("reason"), result.get(1).getAsJsonArray().get(0));
    assertTrue(this.proxySocket.isClosed());
    future.get();
  }


}