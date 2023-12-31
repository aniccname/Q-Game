package Serialization;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import Config.ScoringConfig;
import Referee.GameState;
import Referee.IGameState;
import Referee.IPlayerState;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

public class JState {
	@SerializedName("map")
	private final List<JRow> rows;
	@SerializedName("tile*")
	private final List<JTile> tiles;
	private final List<JPlayer> players;

	public JState(List<JRow> rows, List<JTile> tiles, List<JPlayer> players) {
		this.rows = rows;
		this.tiles = tiles;
		this.players = players;
	}

	public JState(IGameState gameState) {
		this.rows = new JMap(gameState.getMap()).jRowList;
		this.tiles = gameState.pickRefTiles(gameState.getRefTileCount()).stream()
				.map(JTile::new).collect(Collectors.toList());
		this.players = gameState.getPlayerStates().stream()
				.map(JPlayer::new).collect(Collectors.toList());
	}

	public IGameState convert() {
		return convert(new ScoringConfig.ScoringConfigBuilder().build());
	}

	public IGameState convert(ScoringConfig scoringConfig) {
		List<IPlayerState> players = new ArrayList<>();
		for (JPlayer player : this.players) {
			players.add(player.convert());
		}

		return new GameState(
				new JMap(rows.toArray(new JRow[0])).convert(),
				tiles.stream().map(JTile::convert).collect(java.util.stream.Collectors.toList()),
				players,
				scoringConfig
		);
	}

	public JsonElement serialize() {
		JsonObject object = new JsonObject();
		JsonArray rows = new JsonArray();
		for (JRow row : this.rows) {
			rows.add(row.serialize());
		}
		object.add("map", rows);
		object.add("tile*", new Gson().toJsonTree(tiles));
		object.add("players", new Gson().toJsonTree(players));
		return object;
	}
}
