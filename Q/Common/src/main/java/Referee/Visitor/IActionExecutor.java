package Referee.Visitor;

import java.util.List;

import Action.ExchangeAction;
import Action.PassAction;
import Action.PlaceAction;
import Map.Tile.ITile;
import Referee.IGameState;

/**
 * An IActionExectuor is able to perform proposed moves are valid with a visitor (referee) on the
 * current game state.
 */
public interface IActionExecutor extends IVisitor<IGameState, List<ITile>> {
  /**
   * Exchanges the active player's tiles.
   *
   * @param gameState the game state to mutate with the executer
   * @param action to which mutate the game state by
   */
  List<ITile> visitExchange(ExchangeAction action, IGameState gameState);

  /**
   * Lets the active player pass its move.
   *
   * @param gameState the game state to mutate with the executer
   * @param action to which mutate the game state by
   */
  List<ITile> visitPass(PassAction action, IGameState gameState);

  /**
   * Places the active player's tile on the board at the coordinate they specified.
   *
   * @param gameState the game state to mutate with the executer
   * @param action to which mutate the game state by
   */
  List<ITile> visitPlace(PlaceAction action, IGameState gameState);
}
