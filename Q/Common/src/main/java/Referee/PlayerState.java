package Referee;

import java.util.ArrayList;
import java.util.List;

import Map.Tile.ITile;
import Player.IPlayer;

/**
 * Represents an implementation of the player state.
 * The player state is obtained from the IPlayer client and it holds all the information a player
 * needs to play the Q game.
 */
public class PlayerState implements IPlayerState {
  private final String name; // the name of the player
  private final List<ITile> playerTiles; // the list of tiles the player holds
  private int score = 0; // the current score of the player

  /**
   * Creates a player state with the given name
   */
  public PlayerState(String name) {
    this.name = name;
    this.playerTiles = new ArrayList<>();
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
}
