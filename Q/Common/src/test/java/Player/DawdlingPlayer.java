package Player;

import Action.IAction;
import Player.Strategy.IStrategy;
import Referee.IShareableInfo;

public class DawdlingPlayer extends Player {
  private final long waitInMilliseconds;
  /**
   * Creates a new player client with a name and given strategy that the player should play with.
   *
   * @param name     the name of given player (assume names are distinct between players)
   * @param strategy the strategy to give to player how they should play the their tiles
   * @param waitInMilliseconds how long this player waits before responding to
   */
  public DawdlingPlayer(String name, IStrategy strategy, long waitInMilliseconds) {
    super(name, strategy);
    this.waitInMilliseconds = waitInMilliseconds;
  }

  public IAction takeAction(IShareableInfo publicState) {
    try {
      Thread.sleep(this.waitInMilliseconds);
    } catch (InterruptedException e) {
      throw new IllegalStateException("Dawdling player interrupted while waiting.");
    }
    return super.takeAction(publicState);
  }
}
