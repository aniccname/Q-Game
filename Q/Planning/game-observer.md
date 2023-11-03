TO: Q Game CEOs  
FROM: Derek Kaplan and Matt Goldgirsh  
DATE: 10/25/2023  
SUBJECT: Game Observer Communication Protocol

The following function calls are required by the protocol we have designed:

- This method is called on the referee by an observer to subscribe to updates about the game.
	```
	subscribe(Observer observer)
	```
	Once an observer subscribes to the game, the referee will send them updates about the game.
- This method is called on an observer to inform them about the current state of the game when they initially subscribe.
	```
	setupObserver(IShareableInfo gameState)
	```
	When an observer subscribes to the game, the referee will respond with the current state of the game.
- This method is called on an observer to inform them that a player has taken an action:
	```
	informTurnTaken(String playerName, IAction action, IShareableInfo gameState)
	```
	After each player takes their turn, the referee will notify all observers of the action and of the new public state of the game.
- This method is called on an observer to inform them who won the game
	```
	announceWin(String playerName)
	```
	When the game ends, the referee will notify all observers the name of the winner.

In the below diagram, an observer subscribes to a game after the initial setup, but before any turns are taken. The referee informs them of the initial game state. Then, after a player takes a turn, the referee informs the observer about the details of the turn and the updated public state of the game. This pattern will continue in future turns (not depicted).

![[observer.png]]