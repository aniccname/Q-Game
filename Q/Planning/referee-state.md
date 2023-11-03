TO: Q Game CEOs

FROM: Derek Kaplan and Matt Goldgirsh

DATE: 10/16/2023

SUBJECT: The Referee Interface Outline

We have designed an interface for the referee. The referee will start the game from a given game state, and return a message to be sent to the active player. When the player takes the action, the `takeAction` method will be called with the player's action. The referee will update the game state based on that action, and then return a message to be sent to the next active player. This will continue until the game is over (which can be determined through calling `Message.gameInfo.isGameOver()`), at which point the referee will no longer accept actions. The referee also allows the current game state to be saved, if the game is to be paused and picked up from the current state at a later point in time by a different referee.

```java
public interface IReferee {
	/**
	 * Begins the game from the given game state. Returns a message to be
	 * sent to the active player, informing them that it is their turn,
	 * and about the current state of the game.
	 */
	public Message startGame(IGameState gamestate);

	/**
	 * Performs the given action on behalf of the active player. If
	 * the action is illegal, kicks the player from the game. Updates
	 * the active player. Returns a message to be sent to the new active
	 * player, informing them that it is their turn,
	 * and about the current state of the game.
	 */
	public Message takeAction(IAction action);

	/**
	 * Returns a copy of the current state of the game, so that the game
	 * can be started from here by another referee.
	 */
	public IGameState saveState();
}

public class Message {
	/** 
	 * The current publicly available info about the state of the game
	 */
	public IShareableInfo gameInfo;
	
	/**
	 * The active player's hand
	 */
	public List<ITile> hand;	
}
```