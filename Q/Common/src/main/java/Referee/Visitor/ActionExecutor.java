package Referee.Visitor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import Action.ExchangeAction;
import Action.PassAction;
import Action.PlaceAction;
import Map.Coord;
import Map.Tile.ITile;
import Referee.IGameState;
import Referee.IPlayerState;

/**
 * An IActionExectuor is able to perform proposed moves are valid with a visitor (referee) on the
 * current game state.
 */
public class ActionExecutor implements IActionExecutor {
	/**
	 * Implemented from the IActionExecutor visitor pattern.
	 * This method will visit the exchange action generating a list of tiles to swap which the player
	 * will receive, if they choose the exchange action
	 * EFFECT: this method will switch the next players turn, and swap tiles from the ref
	 *
	 * @param gameState the game state  modified for the exchange
	 * @param action the user (player's) exchange action taken
	 * @return the player's new tiles
	 */
	public List<ITile> visitExchange(ExchangeAction action, IGameState gameState) {
		IPlayerState activePlayer = gameState.getActivePlayer();

		List<ITile> oldTiles = activePlayer.getTiles();
		List<ITile> newTiles = gameState.pickRefTiles(oldTiles.size());

		activePlayer.removeTiles(oldTiles);
		activePlayer.acceptTiles(newTiles);
		gameState.acceptRefTiles(oldTiles);

		return newTiles;
	}

	/**
	 * Implemented from the IActionExecutor visitor pattern.
	 * The pass action means the player passes his turn
	 * EFFECT: this method will visit the pass action switching to the next player's turn
	 *
	 * @param gameState the game state modified for the exchange
	 * @param action the user (player's) pass action taken
	 * @return an empty list
	 */
	public List<ITile> visitPass(PassAction action, IGameState gameState) {
		return List.of();
	}

	/**
	 * Implemented from the IActionExecutor visitor pattern.
	 * The place action means the player will place a tile down on his turn.
	 * EFFECT: this method will place the tile in place action down on the game state map
	 *
	 * @param gameState the game state modified for the exchange
	 * @param action the user (player's) place action taken
	 * @return the new tiles in the player's hand
	 */
	public List<ITile> visitPlace(PlaceAction action, IGameState gameState) {
		for (Map.Entry<Coord, ITile> placement : action.getPlacements()) {
			gameState.placeTileByOther(placement.getKey(), placement.getValue());
		}

		// get the list of tiles that were placed from player
		List<ITile> tiles = action.getPlacements().stream()
				.map(Map.Entry::getValue).collect(Collectors.toList());

		// remove the tiles placed from user
		gameState.getActivePlayer().removeTiles(tiles);

		List<ITile> newTiles = gameState.pickRefTiles(
				Math.min(tiles.size(), gameState.getRefTileCount()));

		// add new tiles from ref
		gameState.getActivePlayer().acceptTiles(newTiles);

		return newTiles;
	}

}