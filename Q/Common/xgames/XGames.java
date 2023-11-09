import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import Player.Player;
import Referee.Referee;
import Referee.GameResult;
import Serialization.Deserializers.JActorSpecDeserializer;
import Serialization.Deserializers.JRowDeserializer;
import Serialization.JRow;
import Serialization.JState;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonStreamParser;

public class XGames {
	public static void main(String[] args) {
		Gson gson = new GsonBuilder()
				.registerTypeAdapter(JRow.class, new JRowDeserializer())
				.registerTypeAdapter(Player.class, new JActorSpecDeserializer())
				.create();
		JsonStreamParser parser = new JsonStreamParser(new InputStreamReader(System.in));

		JState jState = gson.fromJson(parser.next(), JState.class);
		Player[] jActors = gson.fromJson(parser.next(), Player[].class);

		GameResult result = new Referee().playGame(
				Arrays.stream(jActors).collect(Collectors.toList()),
				jState.convert()
		);

		System.out.println(gson.toJson(List.of(
				result.winners.stream().sorted().collect(Collectors.toList()),
				result.assholes
		)));
	}
}
