package Player.Strategy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

import Action.ExchangeAction;
import Action.IAction;
import Action.PassAction;
import Action.PlaceAction;
import Config.ScoringConfig;
import Map.Coord;
import Map.IMap;
import Map.GameMap;
import Map.Tile.ITile;
import Map.Tile.Tile;
import Referee.GameState;
import Referee.IGameState;
import Referee.PlayerState;
import org.junit.Before;
import org.junit.Test;

public class LdasgStrategyTest {
		private IGameState igs;

		@Before
		public void init() {
			ITile tile = new Tile(ITile.Shape.Star, ITile.TileColor.Purple);
			IMap map = new GameMap(Map.of(
					new Coord(0,0), new Tile(ITile.Shape.Clover, ITile.TileColor.Purple),
					new Coord(1,0), new Tile(ITile.Shape.Clover, ITile.TileColor.Purple),
					new Coord(0,1), new Tile(ITile.Shape.Star, ITile.TileColor.Purple)
			));

			igs = new GameState(map, List.of(tile), List.of(new PlayerState("Test Player")), new ScoringConfig(10, 8));
		}

		@Test
		public void testPlacement() {
			List<ITile> hand = List.of(
					new Tile(ITile.Shape.Square, ITile.TileColor.Green),
					new Tile(ITile.Shape.Square, ITile.TileColor.Purple)
			);

			IAction action = new LdasgStrategy().takeTurn(igs, hand);
			assertTrue(action instanceof PlaceAction);
			PlaceAction placeAction = (PlaceAction) action;

			assertEquals(placeAction.getPlacements(), List.of(
					new AbstractMap.SimpleEntry<>(new Coord(1, 1), hand.get(1)),
					new AbstractMap.SimpleEntry<>(new Coord(2, 1), hand.get(0))
			));
		}

		@Test
		public void testExchange() {
			List<ITile> hand = List.of(new Tile(ITile.Shape.Square, ITile.TileColor.Green));

			IAction action = new DagStrategy().takeTurn(igs, hand);
			assertTrue(action instanceof ExchangeAction);
		}

		@Test
		public void testPass() {
			igs.pickRefTiles(igs.getRefTileCount());
			List<ITile> hand = List.of(new Tile(ITile.Shape.Square, ITile.TileColor.Green));

			IAction action = new DagStrategy().takeTurn(igs, hand);
			assertTrue(action instanceof PassAction);
		}
}
