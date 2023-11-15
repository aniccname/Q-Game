package Networking;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonStreamParser;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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

  ProxyReferee proxy;
  Socket sourceSocket;
  Socket proxySocket;
  ExecutorService executorService;
  IPlayer player;
  int port;
  PrintStream sourceStream;

  @Before
  public void setup() throws IOException, ExecutionException, InterruptedException {
    port = 7777;
    executorService = Executors.newSingleThreadExecutor();
    ServerSocket listener = new ServerSocket(port);
    Future<Socket> futureSource = executorService.submit(listener::accept);
    proxySocket = new Socket("localhost", port);
    sourceSocket = futureSource.get();
    sourceStream = new PrintStream(sourceSocket.getOutputStream());
    proxy = new ProxyReferee();
    player = new Player("Jeven", new DagStrategy());
    executorService.submit(() -> proxy.playGame(proxySocket, player));
  }

  @Test
  public void testSetup() throws InterruptedException, IOException {
    IShareableInfo gs = new GameState(List.of("Jeven"));
    JsonArray message = new JsonArray();
    message.add("setup");
    JsonArray args = new JsonArray();
    args.add(new JPub(gs).serialize());
    args.add(new JsonArray());
    message.add(args);
    this.sourceStream.println(message);
    Thread.sleep(500);
    JsonStreamParser jp = new JsonStreamParser(new InputStreamReader(this.sourceSocket.getInputStream()));
    assertEquals(new JsonPrimitive("void"), jp.next());
    proxySocket.close();
    executorService.shutdown();
  }

  @Test
  public void testTakeTurn() throws InterruptedException, IOException {
    PlayerState jevenInfo = new PlayerState("Jeven");
    Tile gc = new Tile(ITile.TileColor.Green, ITile.Shape.Clover);
    jevenInfo.acceptTiles(List.of(gc));
    IShareableInfo gs = new GameState(new GameMap(new Tile(ITile.TileColor.Blue, ITile.Shape.Star)),
            List.of(), List.of(jevenInfo));
    JsonArray message = new JsonArray();
    message.add("setup");
    JsonArray args = new JsonArray();
    args.add(new JPub(gs).serialize());
    JsonArray tiles = new JsonArray();
    tiles.add(new Gson().toJsonTree(new JTile(gc)));
    args.add(tiles);
    message.add(args);
    this.sourceStream.println(message);
    Thread.sleep(500);
    JsonStreamParser jp = new JsonStreamParser(new InputStreamReader(this.sourceSocket.getInputStream()));
    assertEquals(new JsonPrimitive("void"), jp.next());
    message = new JsonArray();
    message.add( new JsonPrimitive( "take-turn"));
    args = new JsonArray();
    args.add(new JPub(gs).serialize());
    message.add(args);
    this.sourceStream.println(message);
    Thread.sleep(500);
    assertEquals(new JsonPrimitive("pass"), jp.next());
    proxySocket.close();
    executorService.shutdown();
  }

  @Test
  public void testNewTiles() throws InterruptedException, IOException {
    PlayerState jevenInfo = new PlayerState("Jeven");
    IShareableInfo gs = new GameState(new GameMap(new Tile(ITile.TileColor.Blue, ITile.Shape.Star)),
            List.of(), List.of(jevenInfo));
    JsonArray message = new JsonArray();
    message.add("setup");
    JsonArray args = new JsonArray();
    args.add(new JPub(gs).serialize());
    args.add(new JsonArray());
    message.add(args);
    this.sourceStream.println(message);
    Thread.sleep(500);
    JsonStreamParser jp = new JsonStreamParser(new InputStreamReader(this.sourceSocket.getInputStream()));
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
    JsonArray message = new JsonArray();
    message.add("win");
    JsonArray args = new JsonArray();
    args.add(new JsonPrimitive(true));
    message.add(args);
    this.sourceStream.println(message);
    Thread.sleep(500);
    JsonStreamParser jp = new JsonStreamParser(new InputStreamReader(this.sourceSocket.getInputStream()));
    assertEquals(new JsonPrimitive("void"), jp.next());
    executorService.shutdown();
  }

}