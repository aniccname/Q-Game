package Action;

import Config.ScoringConfig;
import Referee.PlayerID;
import Referee.Visitor.ActionChecker;
import Referee.Visitor.ActionExecutor;
import org.junit.Before;
import org.junit.Test;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;

import Map.Coord;
import Map.Tile.ITile;
import Map.Tile.Tile;
import Player.IPlayer;
import Referee.GameState;
import Referee.IGameState;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PlaceActionTest {

  private PlaceAction a1;
  private PlaceAction a2;
  private PlaceAction a3;
  private IGameState gs1;

  @Before
  public void init() {
    gs1 = new GameState(new Random(1), List.of(new PlayerID("Alice"), new PlayerID("Bob")), new ScoringConfig(1, 0));

    ITile tile = gs1.getMap().getTile(new Coord(0, 0));
    gs1.getActivePlayer().acceptTiles(List.of(tile, tile, tile));

    a1 = new PlaceAction(List.of(
        new AbstractMap.SimpleEntry<>(new Coord(0, 0), tile)
    ));
    a2 = new PlaceAction(List.of(
        new AbstractMap.SimpleEntry<>(new Coord(1, 0), tile),
        new AbstractMap.SimpleEntry<>(new Coord(2, 0), tile)
    ));
    a3 = new PlaceAction(List.of(
        new AbstractMap.SimpleEntry<>(new Coord(2, 1), tile)
    ));
  }

  @Test
  public void accept() {
    // check the validity of the place tile
    assertFalse(a1.accept(new ActionChecker(), gs1));
    assertTrue(a2.accept(new ActionChecker(), gs1));
    assertFalse(a3.accept(new ActionChecker(), gs1));

    // place the tile on the board gs1 and check the score
    a2.accept(new ActionExecutor(), gs1);

    // check to see than next action can be performed
    assertTrue(a3.accept(new ActionChecker(), gs1));
  }
}