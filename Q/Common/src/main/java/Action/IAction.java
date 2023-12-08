package Action;

import Referee.Visitor.IVisitor;

/**
 * Represents a player taking an action in the game.
 * NOTE: IAction is used to implement the visitor pattern, where the referee visits the action
 * and the action accepts the referee visiting it and modifies the game state if necessary.
 */
public interface IAction {
  /**
   * Accepts a visitor into this action. The visitor can change the game state using this action.
   *
   * @param <R> return type
   * @param additionalArg additional argument to pass to the visitor
   * @param visitor entity being allowed in
   */
  <T, R> R accept(IVisitor<T, R> visitor, T additionalArg);
}
