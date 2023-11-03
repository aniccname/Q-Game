package Referee;

import java.util.List;

import Map.Tile.ITile;

/**
 * An IPlayerState represents the information a player may have when playing the Q game.
 */
public interface IPlayerState {
  /**
   * Returns the name of the player.
   *
   * @return string representing the name of the player
   */
  String getName();

  /**
   * Returns a copy of the list of tiles that the player is currently holding.
   *
   * @return the list of tiles that the player is holding.
   */
  List<ITile> getTiles();

  /**
   * Determines the score of the player.
   *
   * @return an integer representing the score of the player.
   */
  int getScore();

  /**
   * Adds the given points to the player's score.
   *
   * @param points the points to add to the player's score.
   */
  void addScore(int points);

  /**
   * Removes the given tiles from the player's hand.
   *
   * @param tiles the tiles to remove from the player's hand.
   */
	void removeTiles(List<ITile> tiles);

  /**
   * Adds the given tiles to the player's hand.
   *
   * @param tiles the tiles to add to the player's hand.
   */
  void acceptTiles(List<ITile> tiles);
}
