package Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import Action.ExchangeAction;
import Action.PassAction;
import Action.PlaceAction;
import Map.Tile.ITile;
import Referee.IShareableInfo;
import Referee.Visitor.IVisitor;

/**
 * Implementation of the visitor pattern that updates the player (clients)
 * hand, through the action that the player takes.
 */
public class HandUpdater implements IVisitor<IShareableInfo, List<ITile>> {
	private final List<ITile> originalHand; // the original hand that the player holds

	public HandUpdater(List<ITile> originalHand) {
		this.originalHand = originalHand;
	}

	/**
	 * When the player exchanges their tiles their hand should be an empty arraylist
	 * signifying that they gave their tiles to the referee.
	 * NOTE: it is the referee's job to put tiles into the players hand again
	 *
	 * @param action the exchange action visited
	 * @param gameState public information that the player should know when their move is made
	 * @return a list of tiles representing their new hand after the action
	 */
	public List<ITile> visitExchange(ExchangeAction action, IShareableInfo gameState) {
		return new ArrayList<>();
	}

	/**
	 * When a player passes, their hand stays the same as it was before, so we return the original
	 * hand of the player.
	 *
	 * @param action the pass action visited
	 * @param gameState public information that the player should know when their move is made
	 * @return a list of tiles representing their new hand after the action
	 */
	public List<ITile> visitPass(PassAction action, IShareableInfo gameState) {
		return originalHand;
	}

	/**
	 * When a player places tiles the hand updater should update the players hand so that
	 * it represents the removed tiles by the placement
	 *
	 * @param action the pass action visited
	 * @param gameState public information that the player should know when their move is made
	 * @return a list of tiles representing their new hand after the action
	 */
	public List<ITile> visitPlace(PlaceAction action, IShareableInfo gameState) {
		List<ITile> newHand = new ArrayList<>(originalHand);

		// find all the tiles that was used in the placement
		// remove in the new hand
		for(ITile tile : action.getPlacements().stream()
				.map(Map.Entry::getValue).collect(Collectors.toList())) {
			newHand.remove(tile);
		}

		return newHand;
	}
}
