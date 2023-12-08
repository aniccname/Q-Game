package Player.Strategy;

import java.util.List;

import Action.IAction;
import Map.Tile.ITile;
import Referee.IShareableInfo;

/**
 * Represents a strategy used by a player.
 */
public interface IStrategy {
	/**
	 * Determines the action to take given the current game state and the
	 * player's hand.
	 *
	 * @param gameInfo the current publicly available information about the
	 *                 state of the game
	 * @param hand the tiles currently held by the player
	 * @return the action to take
	 */
	IAction takeTurn(IShareableInfo gameInfo, List<ITile> hand);
}
