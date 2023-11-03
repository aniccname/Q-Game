package Serialization;

import java.util.Arrays;
import java.util.stream.Collectors;

import Referee.IPlayerState;
import Referee.PlayerState;
import com.google.gson.annotations.SerializedName;

public class JPlayer {
	private final int score;
	@SerializedName("tile*")
	private final JTile[] tiles;

	public JPlayer(int score, JTile[] tiles) {
		this.score = score;
		this.tiles = tiles;
	}

	public JPlayer(IPlayerState playerState) {
		this.score = playerState.getScore();
		this.tiles = playerState.getTiles().stream()
				.map(JTile::new).toArray(JTile[]::new);
	}

	public PlayerState convert() {
		return this.convert("Test Player");
	}

	public PlayerState convert(String name) {
		PlayerState playerState = new PlayerState(name);
		playerState.addScore(score);
		playerState.acceptTiles(Arrays.stream(tiles).map(JTile::convert).collect(Collectors.toList()));
		return playerState;
	}
}
