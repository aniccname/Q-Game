package Player.Strategy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.AbstractMap;
import java.util.List;
import java.util.Random;

import Action.ExchangeAction;
import Action.IAction;
import Action.PassAction;
import Action.PlaceAction;
import Config.ScoringConfig;
import Map.Coord;
import Map.Tile.ITile;
import Map.Tile.Tile;
import Referee.GameState;
import Referee.IGameState;
import org.junit.Before;
import org.junit.Test;

public class DagStrategyTest {
	private IGameState igs;

	@Before
	public void init() {
		igs = new GameState(new Random(1), List.of("Alice", "Bob"), new ScoringConfig(5, 10));
	}

	@Test
	public void testPlacement() {
		ITile tile = igs.getMap().getTile(new Coord(0, 0));
		List<ITile> hand = List.of(
				new Tile(tile.getShape(), ITile.TileColor.Green),
				new Tile(tile.getShape(), ITile.TileColor.Red)
		);

		IAction action = new DagStrategy().takeTurn(igs, hand);
		assertTrue(action instanceof PlaceAction);
		PlaceAction placeAction = (PlaceAction) action;

		assertEquals(placeAction.getPlacements(), List.of(
				new AbstractMap.SimpleEntry<>(new Coord(0, -1), hand.get(1)),
				new AbstractMap.SimpleEntry<>(new Coord(0, -2), hand.get(0))
		));
	}

	@Test
	public void testExchange() {
		List<ITile> hand = List.of(new Tile(ITile.Shape.Star, ITile.TileColor.Green));

		IAction action = new DagStrategy().takeTurn(igs, hand);
		assertTrue(action instanceof ExchangeAction);
	}

	@Test
	public void testPass() {
		igs.pickRefTiles(igs.getRefTileCount());
		List<ITile> hand = List.of(new Tile(ITile.Shape.Star, ITile.TileColor.Green));

		IAction action = new DagStrategy().takeTurn(igs, hand);
		assertTrue(action instanceof PassAction);
	}
}
