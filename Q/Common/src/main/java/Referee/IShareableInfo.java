package Referee;

import java.util.List;

import Map.IMap;

/**
 * Represents the shareable game state information that the player should be able to view.
 */
public interface IShareableInfo {
  /**
   * Returns the copy of map (for anti-tampering reasons).
   *
   * The referee will be able to check the validity of a
   * move by using the `validSpots()` method of the board
   * to determine if the tile can be placed at the spot the
   * player requested. The referee will be able to
   * determine if the player can exchange tiles depending
   * on the number of tiles the referee has.
   *
   */
  IMap getMap();

  /**
   * Returns the number of tiles in the referee's possession for this game.
   *
   * @return referee's tile count
   */
  int getRefTileCount();

  /**
   * Returns a list of names in the order of their turns. Begins with the name of the next player.
   *
   * @return list of names as Strings
   */
  List<String> showOrder();

  /**
   * Returns the list of player scores in turn order.
   *
   * @return list scores in turn order.
   */
  List<Integer> getScores();

  /**
   * Returns whether the game is at the start of a round.
   * @return whether the game is at the start of a round
   */
  boolean isStartOfRound();

  /**
   * Returns the active player (the player whose turn it is)
   * @return the player which has the current turn.
   */
  IPlayerState activePlayer();

  /**
   * Returns the states of all players.
   * @return the states of all players
   */
  List<IPlayerState> getPlayerStates();


}
