package Serialization;

import java.util.Arrays;
import java.util.stream.Collectors;

import Referee.IPlayerState;
import Referee.PlayerState;
import com.google.gson.annotations.SerializedName;

public class JPlayer {
	private final int score;
	private final String name;
	@SerializedName("tile*")
	private final JTile[] tiles;

	public JPlayer(int score, String name, JTile[] tiles) {
		this.score = score;
		this.name = name;
		this.tiles = tiles;
	}

	public JPlayer(IPlayerState playerState) {
		this.score = playerState.getScore();
		this.name = playerState.getName();
		this.tiles = playerState.getTiles().stream()
				.map(JTile::new).toArray(JTile[]::new);
	}

	public PlayerState convert() {
		PlayerState playerState = new PlayerState(name);
		playerState.addScore(score);
		playerState.acceptTiles(Arrays.stream(tiles).map(JTile::convert).collect(Collectors.toList()));
		return playerState;
	}
}
