package Referee;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * A PlayerOrder is the order of players in the Q game. The order is by the age of the player in
 * descending order. PlayerOrder is a circular queue, where players who complete their turn got to
 * the end.
 */
public class PlayerOrder {
  private final List<IPlayerState> playerStates;
  private int currentPlayerIndex;

  /**
   * Create the order of the players. The players are ordered by age in descending order.
   *
   * @param playerStates list of players to order
   */
  public PlayerOrder(List<IPlayerState> playerStates) {
    this.playerStates = new LinkedList<>(playerStates);
    this.currentPlayerIndex = 0;
  }

  /**
   * Get the order of the players as a list of their names.
   * Order is by age of players in descending order.
   *
   * @return list of players names in order
   */
  public List<IPlayerState> getPlayerStates() {
    return new ArrayList<>(playerStates);
  }

  /**
   * Kicks the player out of the turn queue.
   *
   * @param playerState player to remove
   */
  public void kickPlayer(IPlayerState playerState) {
    int kickedIndex = playerStates.indexOf(playerState);
    playerStates.remove(kickedIndex);
    if (currentPlayerIndex > kickedIndex) {
      currentPlayerIndex -= 1;
    }
    if (currentPlayerIndex == playerStates.size()) {
      currentPlayerIndex = 0;
    }
  }

  /**
   * Moves on to the next player in the list.
   * @return the new current player
   */
  public IPlayerState next() {
    currentPlayerIndex += 1;
    if (currentPlayerIndex == playerStates.size()) {
      currentPlayerIndex = 0;
    }
    return this.current();
  }

  /**
   * @return the current active player
   */
  public IPlayerState current() {
    return playerStates.get(currentPlayerIndex);
  }

  /**
   * Determines whether it is the start of the round.
   * @return boolean representing whether it is the start of the round
   */
  public boolean isStartOfRound() {
    return this.currentPlayerIndex == 0;
  }
}
