package Player;

import java.util.List;

import Action.IAction;
import Map.Tile.ITile;
import Referee.IShareableInfo;

/**
 * Represents a user that will be controlling the player in the game.
 */
public interface IPlayer {
  /**
   * Gets the name of the player.
   *
   * @return player's name
   */
  String name();

  /**
   * Informs the player about the initial map of the game and the player's hand.
   * @param map the initial map of the game.
   * @param tiles the initial tiles in the player's hand.
   */
  void setup(IShareableInfo map, List<ITile> tiles);

  /**
   * Determines the action the player will take based on the current public
   * state of the game.
   *
   * @param publicState the current public state of the game.
   * @return the action created from taking the strategy.
   */
  IAction takeAction(IShareableInfo publicState);

  /**
   * Informs the player about the new tiles added to their hand.
   * @param tiles the new tiles added to the player's hand.
   */
  void newTiles(List<ITile> tiles);

  /**
   * Informs the player if they won or lost the game.
   * @param won true if the player won the game, false otherwise.
   */
  void win(boolean won);


  /**
   * Informs the player about an opponents' turn.
   * @param publicState the state of the game during the opponents turn.
   * @param activePlayerIndx the index of the active player (the player whose turn it is).
   */
  void watchTurn(IShareableInfo publicState, int activePlayerIndx);

  /**
   * Informs the player about an opponents' turn.
   * @param publicState the state of the game during the opponents turn.
   */
  void watchTurn(IShareableInfo publicState);

  /**
   * Informs the player that they have been removed from the game due to the given reason.
   * @param reason the reason behind the caused error.
   */
  void error(String reason);

  /**
   * Returns a unique identifier for this Player.
   * This is shared with the corresponding IPlayerState
   * @return an Object acting as the unique identifier for this Player.
   */
  Object id();
}
