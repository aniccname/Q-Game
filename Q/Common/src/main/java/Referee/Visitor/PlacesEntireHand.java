package Referee.Visitor;

import java.util.List;

import Action.ExchangeAction;
import Action.PassAction;
import Action.PlaceAction;
import Map.Tile.ITile;

/**
 * Determines whether the action places the player's entire hand.
 */
public class PlacesEntireHand implements IVisitor<List<ITile>, Boolean> {
	@Override
	public Boolean visitExchange(ExchangeAction action, List<ITile> hand) {
		return false;
	}

	@Override
	public Boolean visitPass(PassAction action, List<ITile> hand) {
		return false;
	}

	@Override
	public Boolean visitPlace(PlaceAction action, List<ITile> hand) {
		return action.getPlacements().size() == hand.size();
	}
}
