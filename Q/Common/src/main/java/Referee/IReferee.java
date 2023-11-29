package Referee;

import java.util.List;

import Config.RefereeConfig;
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
	GameResult playGame(List<IPlayer> players, RefereeConfig refereeConfig);
}
