import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import Action.PlaceAction;
import Map.Tile.ITile;
import Map.Coord;
import Referee.GameState;
import Referee.IGameState;
import Referee.IPlayerState;
import Referee.PlayerState;
import Referee.Visitor.ActionScorer;
import Serialization.Deserializers.JRowDeserializer;
import Serialization.JMap;
import Serialization.JRow;
import Serialization.OnePlacement;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonStreamParser;

public class XScore {
	public static void main(String[] args) {
		Gson gson = new GsonBuilder()
				.registerTypeAdapter(JRow.class, new JRowDeserializer())
				.create();
		JsonStreamParser parser = new JsonStreamParser(new InputStreamReader(System.in));

		JMap jmap = new JMap(gson.fromJson(parser.next(), JRow[].class));

		OnePlacement[] placements = gson.fromJson(parser.next(), OnePlacement[].class);
		List<Map.Entry<Coord, ITile>> placementList =
				Arrays.stream(placements).map(OnePlacement::convert).collect(Collectors.toList());

		PlaceAction action = new PlaceAction(placementList);
		System.out.println(gson.toJson(
				new ActionScorer(false, GameState.Q_POINTS, GameState.WHOLE_HAND_BONUS)
						.visitPlace(action, jmap.convert())
		));
	}
}
