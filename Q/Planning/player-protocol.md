TO: Q Game CEOs  
FROM: Derek Kaplan and Matt Goldgirsh  
DATE: 10/11/2023  
SUBJECT: Player-Referee Communication Protocol

The following function calls are required by the protocol we have designed:

- This method is called on the player by the referee to inform a player of the tiles currently in their hand.
  ```
  informPlayerOfHand(List<ITile> hand)
  ```
  The referee will call this method on all players at the start of the game to inform them of their initial hands. After a player takes a turn, the referee will call this method on that player to inform them of their updated hand.
- This method is called on the player by the referee to inform a player of the current public information about the state of the game
  ```
  informPlayerOfGamestate(IShareableInfo gamestate)
  ```
  The referee will call this method on all players at the start of the game, and after every turn, so that all players always know the current gamestate.
- This method is called on the player by the referee to inform a player that it is their turn
  ```
  informPlayerOfTurn()
  ```
  The referee will call this method on each player at the start of their turn.
- This method is called on the referee by the player to take their turn
  ```
  takeAction(IAction)
  ```
  A player will call this method after `informPlayerOfTurn()` is called on them.
- This method is called on the player by the referee to inform them that they have been kicked out of the game.
  ```
  informPlayerKicked()
  ```
  This method is called by the referee when a player attempts to take an illegal action.
- This method is called on the player by the referee to inform them the game is over and who won the game.
  ```
  informGameOver(String winnerName)
  ```
  The referee calls this method on all players at the end of the game.

In the sample interaction, a game begins with two players. The referee informs them of their initial hands and of the initial gamestate. It then informs player 1 that it is their turn, player 1 takes an action, and the referee informs them of their updated hand and all players of the new gamestate. It then informs player 2 that it is their turn, player 2 attempts to take an illegal action, and the referee informs them that they have been kicked out of the game. The referee then informs player 1 of the updated gamestate. Since player 1 is the only remaining player, they have won, so the referee informs them the game is over and that player 1 is the winner.

![[communication.png]]