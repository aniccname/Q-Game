package Referee;

import java.util.List;

import Action.IAction;
import Map.Coord;
import Map.Tile.ITile;
import Referee.Visitor.IActionChecker;

/**
 * The IGameState represents the game (and its information) at a specific moment.
 *<p>
 * This is meant to be implemented alongside a referee that will hold the game state
 * and monitor scoring and valid player actions (placements, exchanges, ...).
 *
 * This interface is supposed to represent the interface between the referee-player how they
 * are able to communicate with each other.
 *
 * This interface extends IShareableInfo interface to provide the player with info that they
 * may need when it's their turn.
 */
public interface IGameState extends IShareableInfo {
  /**
   * Performs the requested action with the referee's approval.
   * An action can be one of the following
   * - exchange
   * - place
   * - pass
   *
   * @param action the proposed action to perform with the referee approval
   * @return the new tiles added to the player's hand
   */
  List<ITile> doAction(IAction action);

  /**
   * Checks whether the given action is valid.
   *
   * @param checker entity in charge of checking action
   * @param action proposed action
   * @return indication whether proposed is valid
   */
  boolean validAction(IActionChecker checker, IAction action);

  /**
   * Selects a number of random tiles from the pool of tiles.
   *
   * @param numTiles number of tiles to select
   * @return list of selected tiles
   */
  List<ITile> pickRefTiles(int numTiles);

  /**
   * Gets the state of the active player.
   *
   * @return the state of the active player
   */
  IPlayerState getActivePlayer();

  /**
   * Removes the given player from the game.
   */
  void removePlayer(IPlayerState player);

  /**
   * Adds the given tiles to the pool of referee tiles.
   *
   * @param tiles the tiles to add to the pool of referee tiles
   */
  void acceptRefTiles(List<ITile> tiles);

  /**
   * Place the tile at the coordinate only if it adjacent to another tile on the game map.
   *
   * @param coord coordinate to place tile at
   * @param tile tile to place at coordinate
   */
  void placeTileByOther(Coord coord, ITile tile);

  /**
   * Returns the states of all players.
   * @return the states of all players
   */
  List<IPlayerState> getPlayerStates();

  /**
   * Creates a copy of the game state.
   */
  IGameState copy();
}
