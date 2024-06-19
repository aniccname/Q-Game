package Networking;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonStreamParser;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import Config.ScoringConfig;
import Map.GameMap;
import Map.Tile.ITile;
import Map.Tile.Tile;
import Player.IPlayer;
import Player.Player;
import Player.Strategy.DagStrategy;
import Referee.GameState;
import Referee.IShareableInfo;
import Referee.PlayerState;
import Serialization.JPub;
import Serialization.JTile;

import static org.junit.Assert.*;

/**
 * A test class for the proxy referee.
 */
public class ProxyRefereeTest {
  private final ScoringConfig defaultConfig = new ScoringConfig(6, 10);

  ProxyReferee proxy;
  Socket sourceSocket;
  Socket proxySocket;
  ExecutorService executorService;
  IPlayer player;
  int port = 7779;
  PrintStream sourceStream;
  String name = "Jeven";

  ServerSocket listener;


  @Before
  public void setup() throws IOException, ExecutionException, InterruptedException {
    executorService = Executors.newSingleThreadExecutor();
    listener = new ServerSocket(port);
    Future<Socket> futureSource = executorService.submit(listener::accept);
    proxySocket = new Socket("localhost", port);
    sourceSocket = futureSource.get();
    sourceStream = new PrintStream(sourceSocket.getOutputStream());
    player = new Player(name, new DagStrategy());
    proxy = new ProxyReferee(player);
    InputStream in = proxySocket.getInputStream();
    OutputStream out = proxySocket.getOutputStream();
    executorService.submit(() -> proxy.playGame(in, out));
  }

  @After
  public void cleanup() throws IOException {
    proxySocket.close();
    executorService.shutdown();
    listener.close();
  }

  @Test
  public void testSetup() throws InterruptedException, IOException {
    JsonStreamParser jp = new JsonStreamParser(new InputStreamReader(this.sourceSocket.getInputStream()));
    assertEquals(new JsonPrimitive("Jeven"), jp.next());
    IShareableInfo gs = new GameState(List.of("Jeven"), defaultConfig);
    JsonArray message = new JsonArray();
    message.add("setup");
    JsonArray args = new JsonArray();
    args.add(new JPub(gs, name).serialize());
    args.add(new JsonArray());
    message.add(args);
    this.sourceStream.println(message);
    Thread.sleep(500);
    assertEquals(new JsonPrimitive("void"), jp.next());
    proxySocket.close();
    executorService.shutdown();
  }

  @Test
  public void testTakeTurn() throws InterruptedException, IOException {
    JsonStreamParser jp = new JsonStreamParser(new InputStreamReader(this.sourceSocket.getInputStream()));
    assertEquals(new JsonPrimitive("Jeven"), jp.next());
    PlayerState jevenInfo = new PlayerState("Jeven");
    Tile gc = new Tile(ITile.TileColor.Green, ITile.Shape.Clover);
    jevenInfo.acceptTiles(List.of(gc));
    IShareableInfo gs = new GameState(new GameMap(new Tile(ITile.TileColor.Blue, ITile.Shape.Star)),
            List.of(), List.of(jevenInfo), defaultConfig);
    JsonArray message = new JsonArray();
    message.add("setup");
    JsonArray args = new JsonArray();
    args.add(new JPub(gs, name).serialize());
    JsonArray tiles = new JsonArray();
    tiles.add(new Gson().toJsonTree(new JTile(gc)));
    args.add(tiles);
    message.add(args);
    this.sourceStream.println(message);
    Thread.sleep(500);
    assertEquals(new JsonPrimitive("void"), jp.next());
    message = new JsonArray();
    message.add( new JsonPrimitive( "take-turn"));
    args = new JsonArray();
    args.add(new JPub(gs, name).serialize());
    message.add(args);
    this.sourceStream.println(message);
    Thread.sleep(500);
    assertEquals(new JsonPrimitive("pass"), jp.next());
    proxySocket.close();
    executorService.shutdown();
  }

  @Test
  public void testNewTiles() throws InterruptedException, IOException {
    JsonStreamParser jp = new JsonStreamParser(new InputStreamReader(this.sourceSocket.getInputStream()));
    assertEquals(new JsonPrimitive("Jeven"), jp.next());
    PlayerState jevenInfo = new PlayerState("Jeven");
    IShareableInfo gs = new GameState(new GameMap(new Tile(ITile.TileColor.Blue, ITile.Shape.Star)),
            List.of(), List.of(jevenInfo), defaultConfig);
    JsonArray message = new JsonArray();
    message.add("setup");
    JsonArray args = new JsonArray();
    args.add(new JPub(gs, name).serialize());
    args.add(new JsonArray());
    message.add(args);
    this.sourceStream.println(message);
    Thread.sleep(500);
    assertEquals(new JsonPrimitive("void"), jp.next());
    message = new JsonArray();
    message.add("new-tiles");
    args = new JsonArray();
    args.add(new JsonArray());
    message.add(args);
    this.sourceStream.println(message);
    Thread.sleep(500);
    assertEquals(new JsonPrimitive("void"), jp.next());
  }

  @Test
  public void testWin() throws InterruptedException, IOException {
    JsonStreamParser jp = new JsonStreamParser(new InputStreamReader(this.sourceSocket.getInputStream()));
    assertEquals(new JsonPrimitive("Jeven"), jp.next());
    JsonArray message = new JsonArray();
    message.add("win");
    JsonArray args = new JsonArray();
    args.add(new JsonPrimitive(true));
    message.add(args);
    this.sourceStream.println(message);
    Thread.sleep(500);
    assertEquals(new JsonPrimitive("void"), jp.next());
    executorService.shutdown();
  }

  @Test
  public void testWatchTurn() throws InterruptedException, IOException {
    JsonStreamParser jp = new JsonStreamParser(new InputStreamReader(this.sourceSocket.getInputStream()));
    assertEquals(new JsonPrimitive("Jeven"), jp.next());
    IShareableInfo gs = new GameState(List.of("Jeven"), defaultConfig);
    JsonArray message = new JsonArray();
    message.add("watch-turn");
    JsonArray args = new JsonArray();
    args.add(new JPub(gs, name).serialize());
    args.add(new JsonArray());
    message.add(args);
    this.sourceStream.println(message);
    Thread.sleep(500);
    assertEquals(new JsonPrimitive("void"), jp.next());
    executorService.shutdown();
  }

}