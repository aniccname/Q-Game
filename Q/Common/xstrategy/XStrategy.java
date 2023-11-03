import java.io.InputStreamReader;

import Action.IAction;
import Player.Strategy.DagStrategy;
import Player.Strategy.IStrategy;
import Player.Strategy.LdasgStrategy;
import Referee.IGameState;
import Serialization.Deserializers.JPubDeserializer;
import Serialization.JPub;
import Serialization.Serializers.IActionSerializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonStreamParser;

public class XStrategy {
	public static void main(String[] args) {
		Gson gson = new GsonBuilder()
				.registerTypeAdapter(JPub.class, new JPubDeserializer())
				.create();
		JsonStreamParser parser = new JsonStreamParser(new InputStreamReader(System.in));

		String jStrategy = gson.fromJson(parser.next(), String.class);
		IStrategy strategy;

		switch (jStrategy) {
			case "dag":
				strategy = new DagStrategy();
				break;
			case "ldasg":
				strategy = new LdasgStrategy();
				break;
			default:
				throw new RuntimeException("Unknown strategy: " + jStrategy);
		}

		JPub jPub = gson.fromJson(parser.next(), JPub.class);
		IGameState gameState = jPub.convert();

		IAction action = strategy.takeTurn(gameState, gameState.getActivePlayer().getTiles());
		JsonElement jAction = action.accept(new IActionSerializer(), null);
		System.out.println(gson.toJson(jAction));
	}
}

