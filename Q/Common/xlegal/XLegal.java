import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import Action.IAction;
import Action.PlaceAction;
import Map.IMap;
import Map.Coord;
import Map.Tile.ITile;
import Referee.IGameState;
import Referee.Visitor.ActionChecker;
import Serialization.Deserializers.JPubDeserializer;
import Serialization.JMap;
import Serialization.JPub;
import Serialization.OnePlacement;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonStreamParser;

public class XLegal {
	public static void main(String[] args) {
		Gson gson = new GsonBuilder()
				.registerTypeAdapter(JPub.class, new JPubDeserializer())
				.create();
		JsonStreamParser parser = new JsonStreamParser(new InputStreamReader(System.in));
		JPub jPub = gson.fromJson(parser.next(), JPub.class);
		OnePlacement[] placements = gson.fromJson(parser.next(), OnePlacement[].class);

		IGameState gameState = jPub.convert();
		List<Map.Entry<Coord, ITile>> placementList =
				Arrays.stream(placements).map(OnePlacement::convert).collect(Collectors.toList());

		IAction action = new PlaceAction(placementList);

		if (gameState.validAction(new ActionChecker(), action)) {
			gameState.doAction(action);
			IMap map = gameState.getMap();
			System.out.println(gson.toJson(new JMap(map)));
		} else {
			System.out.println(gson.toJson(false));
		}
	}
}
