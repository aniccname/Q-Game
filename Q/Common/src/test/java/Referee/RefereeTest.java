package Referee;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.function.Function;

import Config.RefereeConfig;
import Config.ScoringConfig;
import Map.GameMap;
import Map.IMap;
import Map.Tile.ITile;
import Map.Tile.Tile;
import Player.IPlayer;
import Player.Player;
import Player.DawdlingPlayer;
import Player.Strategy.DagStrategy;
import Player.Strategy.LdasgStrategy;
import Serialization.JState;
import com.google.gson.Gson;
import org.junit.Test;
public class RefereeTest {

	private RefereeConfig makeConfig (IGameState gs) {
		 return new RefereeConfig.RefereeConfigBuilder().gameState(gs).build();
	}
	private final ScoringConfig defaultConfig = new ScoringConfig(6, 10);
	@Test
	public void testPlayGame() {
		IPlayer dag = new Player("Dag", new DagStrategy());
		IPlayer ldasg = new Player("Ldasg", new LdasgStrategy());

		List<IPlayer> players = List.of(dag, ldasg);

		IPlayerState dagState = new PlayerState(new PlayerID(dag.id(), "Dag"));
		IPlayerState ldasgState = new PlayerState(new PlayerID(dag.id(), "Ldasg"));

		IMap map = new GameMap(new Tile(ITile.Shape.Clover, ITile.TileColor.Red));
		IGameState gameState = new GameState(map, List.of(), List.of(dagState, ldasgState), defaultConfig);

		System.out.println(new Gson().toJson(new JState(gameState).serialize()));

		GameResult result = new Referee().playGame(players, makeConfig(gameState));

		assertEquals(result.winners, List.of("Dag", "Ldasg"));
		assertEquals(result.assholes, List.of());
	}

	@Test
	public void testPlayGamePlace() {
		IPlayer dag = new Player("Dag", new DagStrategy());

		List<IPlayer> players = List.of(dag);

		IPlayerState dagState = new PlayerState(new PlayerID(dag.id(), "Dag"));
		dagState.acceptTiles(List.of(new Tile(ITile.Shape.Clover, ITile.TileColor.Blue)));

		IMap map = new GameMap(new Tile(ITile.Shape.Clover, ITile.TileColor.Red));

		IGameState gamestate = new GameState(map, List.of(), List.of(dagState), defaultConfig);

		new Referee().playGame(players, makeConfig(gamestate));

		assertEquals(
				3 + defaultConfig.wholeHandBonus(),
				gamestate.getActivePlayer().getScore()
		);
	}

	@Test
	public void testPlayGameExchange() {
		IPlayer dag = new Player("Dag", new DagStrategy());

		List<IPlayer> players = List.of(dag);

		IPlayerState dagState = new PlayerState(new PlayerID(dag.id(), "Dag"));
		dagState.acceptTiles(List.of(new Tile(ITile.Shape.Star, ITile.TileColor.Blue)));

		IMap map = new GameMap(new Tile(ITile.Shape.Clover, ITile.TileColor.Red));

		IGameState gamestate = new GameState(map, List.of(), List.of(dagState), defaultConfig);

		new Referee().playGame(players, makeConfig(gamestate));

		assertEquals(0, gamestate.getActivePlayer().getScore());
	}

	@Test
	public void testTimeoutThenPlace() {
		IPlayer dawdler = new DawdlingPlayer("Dawdler", new DagStrategy(), 1500);
		IPlayer dag = new Player("Dag", new DagStrategy());

		List<IPlayer> players = List.of(dawdler, dag);

		IPlayerState dawdlerState = new PlayerState(new PlayerID(dawdler.id(), "Dawdler"));
		IPlayerState dagState = new PlayerState(new PlayerID(dag.id(), "Dag"));
		dagState.acceptTiles(List.of(new Tile(ITile.Shape.Star, ITile.TileColor.Blue),
						new Tile(ITile.Shape.Circle, ITile.TileColor.Purple)));
		dawdlerState.acceptTiles(List.of(new Tile(ITile.Shape.Circle, ITile.TileColor.Purple)));

		IMap map = new GameMap(new Tile(ITile.Shape.Clover, ITile.TileColor.Blue));

		IGameState gamestate = new GameState(map, List.of(), List.of(dawdlerState, dagState), defaultConfig);

		var config =  new RefereeConfig.RefereeConfigBuilder()
						.gameState(gamestate).playerTimeoutInSeconds(1).build();
		var result = new Referee().playGame(players, config);

		assertEquals(List.of("Dag"), result.winners);
		assertEquals(List.of("Dawdler"), result.assholes);
		assertEquals(3, gamestate.getActivePlayer().getScore());
	}
}
