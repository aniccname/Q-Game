package Serialization;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Map.Tile.ITile;
import Map.Tile.Tile;
import Referee.GameState;
import Referee.IGameState;

public class JPub {
	public final JMap jmap;
	private final int tileCount;
	private final JPlayer activePlayer;
	private final int[] otherScores;

	public JPub(JMap jmap, int tileCount, JPlayer activePlayer, int[] otherScores) {
		this.jmap = jmap;
		this.tileCount = tileCount;
		this.activePlayer = activePlayer;
		this.otherScores = otherScores;
	}

	public IGameState convert() {
		return new GameState(
				jmap.convert(),
				Collections.nCopies(tileCount, new Tile(ITile.Shape.Star, ITile.TileColor.Purple)),
				List.of(activePlayer.convert())
		);
	}
}
