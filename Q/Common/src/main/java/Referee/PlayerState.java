package Referee;

import java.util.ArrayList;
import java.util.List;

import Map.Tile.ITile;

/**
 * Represents an implementation of the player state.
 * The player state is obtained from the IPlayer client and it holds all the information a player
 * needs to play the Q game.
 */
public class PlayerState implements IPlayerState {
  private final String name; // the name of the player
  //This isn;t final since i'm getting some ide warnings about it not being assigned correctly.
  private Object id; //Unique identifier for this player
  private final List<ITile> playerTiles; // the list of tiles the player holds
  private int score = 0; // the current score of the player

  /**
   * Creates a player state with the given name
   */
  public PlayerState(PlayerID pid) {
    this.id = pid.id();
    this.name = pid.name();
    this.playerTiles = new ArrayList<>();
  }

  public PlayerState(String s) {
    this(new PlayerID(new Object(), s));
  }

  /**
   * Creates a copy of the given player state
   * @param state the player state to copy
   */
  public PlayerState(IPlayerState state) {
    this.name = state.getName();
    this.playerTiles = new ArrayList<>(state.getTiles());
    this.score = state.getScore();
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public List<ITile> getTiles() {
    return new ArrayList<>(playerTiles);
  }

  @Override
  public int getScore() {
    return score;
  }

  @Override
  public void addScore(int points) {
    score += points;
  }

  @Override
  public void removeTiles(List<ITile> tiles) {
    for (ITile tile : tiles) {
      playerTiles.remove(tile);
    }
  }

  @Override
  public void acceptTiles(List<ITile> tiles) {
    this.playerTiles.addAll(tiles);
  }

  @Override
  public Object id() {
    return this.id;
  }
}
