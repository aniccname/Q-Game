TO: Q Game CEOs

FROM: Smit Patel and Matt Goldgirsh

DATE: 10/05/2023

SUBJECT: The Player Interface Outline

We designed the player interface with interactions to the referee in 
mind. It should be able to obtain shared information about the game 
state. It should allow a user or an AI to create and propose actions.

Wish List

```java
/**
 * An IPlayer represents a competitor in the game. 
 * The strategy component of a player is in a separate interface.
 * 
 * The players would follow a different decision maker process depending
 * on other components.
 */
public interface IPlayer {
  /**
   * Accept information about the gamestate through a
   * ShareableInfo provided by the referee.
   * 
   * It should know about the tiles placed on the board, the turn order,
   * scores, and remaining tiles.
   * 
   * @param info game state information
   */
  void accept(ShareableInfo info);

  /**
   * Sends the action proposed by this player to the referee
   * 
   * The action will be created following a strategy provided by another 
   * component. 
   * 
   * @param ref referee the IAction will be sent to
   */
  void provideAction(IReferee ref);

  /**
   * Gets the name of this player.
   * 
   * @return player name
   */
  String getName();

  /**
   * Gets the age of this player measured in years.
   * 
   * @return age in years
   */
  int getAgeInYears();
}


```

