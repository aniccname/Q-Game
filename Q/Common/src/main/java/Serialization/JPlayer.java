package Serialization;

import java.util.Arrays;
import java.util.stream.Collectors;

import Referee.IPlayerState;
import Referee.PlayerState;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
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

	public JsonElement serialize() {
		JsonObject jPlayer = new JsonObject();
		jPlayer.add("name", new JsonPrimitive(name));
		jPlayer.add("score", new JsonPrimitive(score));
		JsonElement jtiles = new Gson().toJsonTree(tiles);
		jPlayer.add("tile*", jtiles);
		return jPlayer;
	}

}
