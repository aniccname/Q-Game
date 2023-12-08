package Action;

import Referee.Visitor.IVisitor;

/**
 * The active players' proposal to exchange the all its tiles with some of the referee's.
 */
public class ExchangeAction implements IAction {
  /**
   * Allows the visitor to specifically visit the exchange action by seeing the game state.
   *
   * @param additionalArg additional argument to pass to the visitor
   * @param visitor entity being allowed in
   * @return <R> the return type of the Visitor
   * @param <R> of type R that specifies what the visitor returns when visiting
   */
  public <T, R> R accept(IVisitor<T, R> visitor, T additionalArg) {
    return visitor.visitExchange(this, additionalArg);
  }
}
