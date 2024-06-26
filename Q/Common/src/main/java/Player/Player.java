package Player;

import java.util.ArrayList;
import java.util.List;

import Action.IAction;
import Map.Tile.ITile;
import Player.Strategy.IStrategy;
import Referee.IShareableInfo;

/**
 * A player client implementation that follows a strategy, has a name, and list fo tiles
 * that they will use to play the Q game.
 */
public class Player implements IPlayer {
	private final String name;
	private final Object id;
	private final IStrategy strategy;
	protected List<ITile> tiles;

	/**
	 * Creates a new player client with a name and given strategy that the player should play with.
	 *
	 * @param name the name of given player (assume names are distinct between players)
 	 * @param strategy the strategy to give to player how they should play the their tiles
	 */
	public Player(String name, IStrategy strategy) {
		this.name = name;
		this.strategy = strategy;
		this.id = new Object();
	}

	/**
	 * Return the name of the player that will be used.
	 *
	 * @return the name to the given player
	 */
	public String name() {
		return name;
	}

	/**
	 * Setup a player with a given list of tiles.
	 * Note: the players tiles must be generated by the referee
	 *
	 * @param map the initial map of the game.
	 * @param tiles the initial tiles in the player's hand.
	 */
	public void setup(IShareableInfo map, List<ITile> tiles) {
		this.tiles = new ArrayList<>(tiles);
	}

	/**
	 * Send the message to the referee that the player is taking an action.
	 * Takes an action using the initialized strategy, and then update the tiles in the player
	 * clients hand based on the hand updater visitor pattern
	 *
	 * @param publicState the current public state of the game.
	 * @return the action that we will take
	 */
	public IAction takeAction(IShareableInfo publicState) {
		IAction action = strategy.takeTurn(publicState, tiles);
		tiles = action.accept(new HandUpdater(tiles), publicState);
		return action;
	}

	/**
	 * Accept new tiles that are given by the referee.
	 *
	 * @param tiles the new tiles added to the player's hand.
	 */
	public void newTiles(List<ITile> tiles) {
		this.tiles.addAll(tiles);
	}

	/**
	 * Informs the player if they won or lost the game.
	 *
	 * @param won true if the player won the game, false otherwise.
	 */
	public void win(boolean won) {
		// TODO: as we are not sure how to represent a win in the player yet
	}

	@Override
	public void watchTurn(IShareableInfo publicState) {
		//Nothing! This is an AI we don't care about it.
	}

	@Override
	public void error(String reason) {
		//Nothing! Since this is an AI player, we don't need to respond to this.
	}

	@Override
	public Object id() {
		return this.id;
	}
}
