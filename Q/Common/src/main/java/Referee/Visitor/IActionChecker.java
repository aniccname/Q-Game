package Referee.Visitor;

import Action.ExchangeAction;
import Action.PassAction;
import Action.PlaceAction;
import Referee.IGameState;

/**
 * An IActionChecker is able to check whether the proposed moves are valid with a visitor (referee)
 * on the current game state.
 */
public interface IActionChecker extends IVisitor<IGameState, Boolean> {
  /**
   * Checks if the player can exchange their tiles.
   *
   * @param gameState the game state to check the action against
   * @param action the exchange action to check
   * @return indication if player can exchange their tiles
   */
  Boolean visitExchange(ExchangeAction action, IGameState gameState);

  /**
   * Indicates that player can pass.
   *
   * @param gameState the game state to check the action against
   * @param action the pass action to check
   * @return true since every player can pass their turn
   */
  Boolean visitPass(PassAction action, IGameState gameState);

  /**
   * Checks if the proposed tile placement is valid.
   *
   * @param gameState the game state to check the action against
   * @param action the place action to check
   * @return whether tile placement is valid
   */
  Boolean visitPlace(PlaceAction action, IGameState gameState);
}
