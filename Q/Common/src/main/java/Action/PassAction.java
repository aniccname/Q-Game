package Action;

import Referee.IGameState;
import Referee.Visitor.IVisitor;

/**
 * A PassAction is a request by the active player to pass.
 */
public class PassAction implements IAction {

  /**
   * Allows the visitor to specifically visit the pass action by seeing the game state. For pass
   * the visit does nothing.
   *
   * @param additionalArg additional argument to pass to the visitor
   * @param visitor entity being allowed in
   * @return <R> the return type of the Visitor
   * @param <R> of type R that specifies what the visitor returns when visiting
   */
  public <T, R> R accept(IVisitor<T, R> visitor, T additionalArg) {
    return visitor.visitPass(this, additionalArg);
  }

}
