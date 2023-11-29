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
import Referee.IShareableInfo;

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

	public JPub(IShareableInfo pub) {
		this(new JMap(pub.getMap()), pub.getRefTileCount(), new JPlayer(pub.activePlayer()),
            pub.getScores().subList(1, pub.getScores().size()).stream().mapToInt((x) -> x).toArray());
	}

	public IGameState convert() {
		return new GameState(
				jmap.convert(),
				Collections.nCopies(tileCount, new Tile(ITile.Shape.Star, ITile.TileColor.Purple)),
				List.of(activePlayer.convert()),
				new ScoringConfig.ScoringConfigBuilder().build()
		);
	}

	public JsonElement serialize() {
		JsonObject jpub = new JsonObject();
		jpub.add("map", this.jmap.serialize());
		jpub.add("tile*", new JsonPrimitive(this.tileCount));
		JsonArray players = new JsonArray();
		players.add(new Gson().toJsonTree(this.activePlayer));
		for (int score : this.otherScores) {
			players.add(score);
		}
		jpub.add("players", players);
		return jpub;
	}
}
