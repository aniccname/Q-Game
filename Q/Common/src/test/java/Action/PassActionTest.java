package Action;

import Config.ScoringConfig;
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
import static org.junit.Assert.assertTrue;

public class PassActionTest {
  private GameState gs1;

  private PassAction ea1;

  @Before
  public void init() {
    gs1 = new GameState(new Random(1), List.of("Alice", "Bob"), new ScoringConfig(5, 5));
    ea1 = new PassAction();
  }
  @Test
  public void testAccept() {
    assertTrue(ea1.accept(new ActionChecker(), gs1));
  }
}