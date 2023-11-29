import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

import Networking.Server;
import Player.Player;
import Referee.Referee;
import Referee.GameResult;
import Serialization.Deserializers.JActorSpecDeserializer;
import Serialization.Deserializers.JRowDeserializer;
import Serialization.JRow;
import Serialization.ServerConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonStreamParser;

public class XServer {
	public static void main(String[] args) throws IOException {
		int port = Integer.parseInt(args[0]);

		Gson gson = new GsonBuilder()
				.registerTypeAdapter(JRow.class, new JRowDeserializer())
				.registerTypeAdapter(Player.class, new JActorSpecDeserializer())
				.create();
		JsonStreamParser parser = new JsonStreamParser(new InputStreamReader(System.in));

		ServerConfig config = gson.fromJson(parser.next(), ServerConfig.class);

		GameResult result = new Server(new Referee(), config.convert()).run(port);

		System.out.println(gson.toJson(List.of(
				result.winners.stream().sorted().collect(Collectors.toList()),
				result.assholes
		)));
	}
}
