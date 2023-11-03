package Referee;

import java.util.List;

import Player.IPlayer;

/**
 * A referee, which runs a game of Q.
 */
public interface IReferee {
	/**
	 * Runs a game of Q to completion.
	 *
	 * @param players The players in the game.
	 */
	GameResult playGame(List<IPlayer> players);


	/**
	 * Runs a game of Q to completion, starting at the given game state.
	 *
	 * @param players The players in the game.
	 * @param gameState The game state to start at.
	 */
	GameResult playGame(List<IPlayer> players, IGameState gameState);
}
