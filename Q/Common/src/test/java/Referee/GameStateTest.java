package Referee;

import Map.Tile.ITile;
import org.junit.Before;

import java.util.AbstractMap;
import java.util.List;
import java.util.Random;
import java.util.Map;

import Action.ExchangeAction;
import Action.IAction;
import Action.PassAction;
import Action.PlaceAction;
import Map.Coord;
import Map.Tile.EmptyTile;
import Map.Tile.ITile.TileColor;
import Map.Tile.ITile.Shape;
import Map.Tile.Tile;
import Map.IMap;
import Referee.Visitor.ActionChecker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class GameStateTest {
  private IGameState igs;
  private IGameState igs2;
  private IAction exchange;
  private IAction pass;
  private IAction placeGreenStarx0yn1;
  private IAction placeOrangeCirclex1y0;

  @Before
  public void init() {
    igs = new GameState(new Random(1), List.of("Alice", "Bob"));
    exchange = new ExchangeAction();
    pass = new PassAction();

    Map.Entry<Coord, ITile> placement1 =
            new AbstractMap.SimpleEntry<>(new Coord(0, 1),
                    new Tile(Shape.Star, TileColor.Green));

    Map.Entry<Coord, ITile> placement2 =
            new AbstractMap.SimpleEntry<>(new Coord(1, 0),
                    new Tile(Shape.Circle, TileColor.Orange));
    placeGreenStarx0yn1 = new PlaceAction(List.of(placement1));
    placeOrangeCirclex1y0 = new PlaceAction(List.of(placement2));

    igs2 = new GameState(new Random(1), List.of("bob", "blerner", "mattias"));
  }

  @org.junit.Test
  public void doActionExchange() {
    IPlayerState alice = igs.getActivePlayer();
    List<ITile> hand = alice.getTiles();
    assertEquals(igs.getActivePlayer().getName(), "Alice");

    igs.doAction(exchange);

    assertEquals(igs.getActivePlayer().getName(), "Bob");
    assertNotEquals(hand, alice.getTiles());
  }

  @org.junit.Test
  public void doActionPass() {
    assertEquals(igs.getActivePlayer().getName(), "Alice");

    igs.doAction(pass);

    assertEquals(igs.getActivePlayer().getName(), "Bob");
  }

  @org.junit.Test
  public void doActionPlace() {
    igs.doAction(placeOrangeCirclex1y0);

    assertEquals(igs.getMap().getTile(new Coord(1, 0)), new Tile(Shape.Circle, TileColor.Orange));
  }

  @org.junit.Test
  public void getMap() {
    IMap map = igs.getMap();
    assertNotEquals(map.getTile(new Coord(0, 0)), new EmptyTile());
    assertEquals(map.getTile(new Coord(0, 1)), new EmptyTile());
    assertEquals(map.getTile(new Coord(0, -1)), new EmptyTile());
    assertEquals(map.getTile(new Coord(1, 0)), new EmptyTile());
    assertEquals(map.getTile(new Coord(-1, 0)), new EmptyTile());
  }

  @org.junit.Test
  public void validActionPass() {
    assertTrue(igs.validAction(new ActionChecker(), pass));
  }
  @org.junit.Test
  public void validActionExchange() {
    assertTrue(igs.validAction(new ActionChecker(), exchange));
  }

  @org.junit.Test
  public void validActionPlaceSuccess() {
    // purple clover valid with seed of 1
    assertEquals("Alice", igs.getActivePlayer().getName());
    igs.getActivePlayer().acceptTiles(List.of(new Tile(Shape.Clover, TileColor.Purple)));
    assertTrue(igs.validAction(new ActionChecker(), placeOrangeCirclex1y0));
  }

  @org.junit.Test
  public void validActionPlaceFailBadNeighbor() {
    // green star is not valid origin neighbor with seed of 1
    assertFalse(igs.validAction(new ActionChecker(), placeGreenStarx0yn1));
  }

  @org.junit.Test
  public void validActionPlaceNoNeighbor() {
    Map.Entry<Coord, ITile> placement =
            new AbstractMap.SimpleEntry<>(new Coord(10, 10),
                    new Tile(Shape.Star, TileColor.Green));
    IAction farAway = new PlaceAction(List.of(placement));
    assertFalse(igs.validAction(new ActionChecker(), farAway));
  }

  @org.junit.Test
  public void getRefTileCount() {
    // igs has two players with 6 tiles, and 1 tile placed on the board
    int totalTiles = 1080;
    int numPlayers = 2;
    int tilesPerPlayer = 6;
    int tilesOnBoard = 1;
    assertEquals(igs.getRefTileCount(), totalTiles - numPlayers * tilesPerPlayer - tilesOnBoard);

  }

  @org.junit.Test
  public void getScores() {
    List<Integer> initialScores = igs.getScores();
    assertEquals(0, initialScores.get(0).intValue());
    assertEquals(0, initialScores.get(1).intValue());
    igs.getActivePlayer().acceptTiles(List.of(new Tile(Shape.Clover, TileColor.Purple)));
    assertTrue(igs.validAction(new ActionChecker(), placeOrangeCirclex1y0));
    igs.doAction(placeOrangeCirclex1y0);

    List<Integer> updatedScores = igs.getScores();

    assertEquals(3, updatedScores.get(0).intValue());
    assertEquals(0, updatedScores.get(1).intValue());
  }
}