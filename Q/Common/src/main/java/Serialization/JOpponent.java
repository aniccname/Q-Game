package Serialization;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.annotations.SerializedName;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

import Map.Tile.ITile;
import Map.Tile.Tile;
import Referee.IPlayerState;
import Referee.PlayerState;

public class JOpponent {
  private final int score;
  private final String name;
  @SerializedName("tile*")
  private final  int numTiles;

  public JOpponent(int score, String name, int numtiles) {
    this.score = score;
    this.name = name;
    this.numTiles = numtiles;
  }

  public JOpponent(IPlayerState playerState) {
    this(playerState.getScore(), playerState.getName(),playerState.getTiles().size());
  }

  public IPlayerState convert() {
    PlayerState playerState = new PlayerState(name);
    playerState.addScore(score);
    playerState.acceptTiles(Collections.nCopies(numTiles, new Tile(ITile.Shape.Star, ITile.TileColor.Purple)));
    return playerState;
  }

  public JsonElement serialize(){
    JsonObject jOpponent = new JsonObject();
    jOpponent.add("name", new JsonPrimitive(name));
    jOpponent.add("score", new JsonPrimitive(score));
    jOpponent.add("tile#", new JsonPrimitive(numTiles));
    return jOpponent;
  }
}
