package Serialization;

import Config.ScoringConfig;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.Collections;
import java.util.List;

import Map.Tile.ITile;
import Map.Tile.Tile;
import Referee.GameState;
import Referee.IGameState;
import Referee.IPlayerState;
import Referee.IShareableInfo;

public class JPub {
	public final JMap jmap;
	private final int tileCount;
	private final String playingPlayerName;
	private final List<IPlayerState> players;

	public JPub(JMap jmap, int tileCount, String playingPlayerName, List<IPlayerState> players) {
		this.jmap = jmap;
		this.tileCount = tileCount;
		this.playingPlayerName = playingPlayerName;
		this.players = players;
	}

	public JPub(IShareableInfo pub, String playingPlayerName) {
		this(new JMap(pub.getMap()), pub.getRefTileCount(), playingPlayerName,
            pub.getPlayerStates());
	}

	public IGameState convert() {
		return new GameState(
				jmap.convert(),
				Collections.nCopies(tileCount, new Tile(ITile.Shape.Star, ITile.TileColor.Purple)),
				players,
				new ScoringConfig.ScoringConfigBuilder().build()
		);
	}

	public JsonElement serialize() {
		JsonObject jpub = new JsonObject();
		jpub.add("map", this.jmap.serialize());
		jpub.add("tile*", new JsonPrimitive(this.tileCount));
		JsonArray players = new JsonArray();
		this.players.forEach((ps) -> {
			if (ps.getName().equals(this.playingPlayerName)) {
				players.add(new JPlayer(ps).serialize());
			} else {
				players.add(new JOpponent(ps).serialize());
			}
		});
		jpub.add("players", players);
		return jpub;
	}
}
