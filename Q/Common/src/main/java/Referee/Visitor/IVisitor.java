package Referee.Visitor;

import Action.ExchangeAction;
import Action.PassAction;
import Action.PlaceAction;

/**
 * Represents a visitor that will explore actions providing some result <T>.
 *
 * @param <R> the type of return for the visitor (what the visitor is doing when accepted)
 */
public interface IVisitor<T, R> {
  /**
   * Visits the Exchange action.
   *
   * @param action the exchange action visited
   * @param additionalArg the additional argument to the visit
   * @return T representing the end state of the visit
   */
  R visitExchange(ExchangeAction action, T additionalArg);

  /**
   * Visits the Pass action.
   *
   * @param action the exchange action visited
   * @param additionalArg the additional argument to the visit
   * @return T representing the end state of the visit
   */
  R visitPass(PassAction action, T additionalArg);

  /**
   * Visits the Place action.
   *
   * @param action the exchange action visited
   * @param additionalArg the additional argument to the visit
   * @return T representing the end state of the visit
   */
  R visitPlace(PlaceAction action, T additionalArg);
}
