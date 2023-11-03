package Action;

import Map.Tile.ITile;
import Referee.IPlayerState;
import Referee.Visitor.ActionChecker;
import Referee.Visitor.ActionExecutor;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import Player.IPlayer;
import Referee.GameState;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class ExchangeActionTest {
  private GameState gs1;

  private ExchangeAction ea1;

  @Before
  public void init() {
    gs1 = new GameState(new Random(1), List.of("Alice", "Bob"));
    ea1 = new ExchangeAction();
  }
  @Test
  public void testAccept() {
    IPlayerState player = gs1.getActivePlayer();
    List<ITile> oldTiles = player.getTiles();

    assertTrue(ea1.accept(new ActionChecker(), gs1));
    ea1.accept(new ActionExecutor(), gs1);

    assertNotEquals(oldTiles, player.getTiles());
  }
}