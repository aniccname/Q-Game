package Observer;

import Referee.IGameState;

/**
 * Represents an observer that observes 1 QGame from start to finish.
 */
public interface IObserver {
  /**
   * Informs this observer about the given state.
   * @param state the new state of the game's map.
   */
  void receiveState(IGameState state);

  /**
   * Informs this observer that the observed game is over!
   */
  void gameOver();
}
