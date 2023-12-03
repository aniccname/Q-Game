package Serialization.Serializers;

import Action.ExchangeAction;
import Action.PassAction;
import Action.PlaceAction;
import Map.Coord;
import Map.Tile.ITile;
import Referee.Visitor.IVisitor;
import Serialization.JCoordinate;
import Serialization.JTile;
import Serialization.OnePlacement;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class IActionSerializer implements IVisitor<Void, JsonElement> {
	@Override
	public JsonElement visitExchange(ExchangeAction action, Void additionalArg) {
		return new JsonPrimitive("replace");
	}

	@Override
	public JsonElement visitPass(PassAction action, Void additionalArg) {
		return new JsonPrimitive("pass");
	}

	@Override
	public JsonElement visitPlace(PlaceAction action, Void additionalArg) {
		return new Gson().toJsonTree(
				action.getPlacements().stream().map(
						placement -> new OnePlacement(
								new JCoordinate(placement.getKey()),
								new JTile(placement.getValue())
						)
				).toArray()
		);
	}
}
