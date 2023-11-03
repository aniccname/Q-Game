package Referee;

import static org.junit.Assert.assertEquals;

import java.util.List;

import Map.GameMap;
import Map.IMap;
import Map.Tile.ITile;
import Map.Tile.Tile;
import Player.IPlayer;
import Player.Player;
import Player.Strategy.DagStrategy;
import Player.Strategy.LdasgStrategy;
import Serialization.JState;
import com.google.gson.Gson;
import org.junit.Test;

public class RefereeTest {
	@Test
	public void testPlayGame() {
		IPlayer dag = new Player("Dag", new DagStrategy());
		IPlayer ldasg = new Player("Ldasg", new LdasgStrategy());

		List<IPlayer> players = List.of(dag, ldasg);

		IPlayerState dagState = new PlayerState("Dag");
		IPlayerState ldasgState = new PlayerState("Ldasg");

		IMap map = new GameMap(new Tile(ITile.Shape.Clover, ITile.TileColor.Red));
		IGameState gameState = new GameState(map, List.of(), List.of(dagState, ldasgState));

		System.out.println(new Gson().toJson(new JState(gameState).serialize()));

		GameResult result = new Referee().playGame(players, gameState);

		assertEquals(result.winners, List.of("Dag", "Ldasg"));
		assertEquals(result.assholes, List.of());
	}

	@Test
	public void testPlayGamePlace() {
		IPlayer dag = new Player("Dag", new DagStrategy());

		List<IPlayer> players = List.of(dag);

		IPlayerState dagState = new PlayerState("Dag");
		dagState.acceptTiles(List.of(new Tile(ITile.Shape.Clover, ITile.TileColor.Blue)));

		IMap map = new GameMap(new Tile(ITile.Shape.Clover, ITile.TileColor.Red));

		IGameState gamestate = new GameState(map, List.of(), List.of(dagState));

		new Referee().playGame(players, gamestate);

		assertEquals(9, gamestate.getActivePlayer().getScore());
	}

	@Test
	public void testPlayGameExchange() {
		IPlayer dag = new Player("Dag", new DagStrategy());

		List<IPlayer> players = List.of(dag);

		IPlayerState dagState = new PlayerState("Dag");
		dagState.acceptTiles(List.of(new Tile(ITile.Shape.Star, ITile.TileColor.Blue)));

		IMap map = new GameMap(new Tile(ITile.Shape.Clover, ITile.TileColor.Red));

		IGameState gamestate = new GameState(map, List.of(), List.of(dagState));

		new Referee().playGame(players, gamestate);

		assertEquals(0, gamestate.getActivePlayer().getScore());
	}
}
