package Observer;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import Config.RefereeConfig;
import Config.ScoringConfig;
import Map.GameMap;
import Map.IMap;
import Map.Tile.ITile;
import Map.Tile.Tile;
import Player.IPlayer;
import Player.Player;
import Player.Strategy.DagStrategy;
import Referee.GameState;
import Referee.IGameState;
import Referee.IPlayerState;
import Referee.PlayerState;
import Referee.Referee;
import org.junit.Test;

public class ObserverTest {
	@Test
	public void testFilesAreSaved() {
		IPlayer dag = new Player("Dag", new DagStrategy());

		List<IPlayer> players = List.of(dag);

		IPlayerState dagState = new PlayerState("Dag");
		dagState.acceptTiles(List.of(new Tile(ITile.Shape.Clover, ITile.TileColor.Blue)));

		IMap map = new GameMap(new Tile(ITile.Shape.Clover, ITile.TileColor.Red));

		IGameState gamestate = new GameState(map, List.of(), List.of(dagState), new ScoringConfig(10, 2));

		IObserver observer = new Observer();

		File tmpDir = new File("Tmp");
		emptyDir(tmpDir);

		new Referee().playGame(players, new RefereeConfig(Optional.of(gamestate), 1, Optional.of(observer)));

		assertEquals(tmpDir.listFiles().length, 2);

		emptyDir(tmpDir);
	}

	@Test
	public void testMoreFilesAreSaved() {
		IPlayer dag = new Player("Dag", new DagStrategy());

		List<IPlayer> players = List.of(dag);

		IPlayerState dagState = new PlayerState("Dag");
		dagState.acceptTiles(List.of(
				new Tile(ITile.Shape.Clover, ITile.TileColor.Blue),
				new Tile(ITile.Shape.Circle, ITile.TileColor.Green)
		));

		IMap map = new GameMap(new Tile(ITile.Shape.Clover, ITile.TileColor.Red));

		IGameState gamestate = new GameState(map, List.of(), List.of(dagState), new ScoringConfig(10, 5));

		IObserver observer = new Observer();

		File tmpDir = new File("Tmp");
		emptyDir(tmpDir);

		new Referee().playGame(players, new RefereeConfig(Optional.of(gamestate), 1, Optional.of(observer)));

		assertEquals(tmpDir.listFiles().length, 3);

		emptyDir(tmpDir);
	}

	private void emptyDir(File dir) {
		File[] files = dir.listFiles();
		if (files != null) {
			Arrays.stream(files).forEach(File::delete);
		}
	}
}