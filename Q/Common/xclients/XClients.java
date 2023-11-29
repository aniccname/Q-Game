import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import Networking.Client;
import Networking.ProxyReferee;
import Player.Player;
import Player.IPlayer;
import Serialization.ClientConfig;
import Serialization.Deserializers.JActorSpecDeserializer;
import Serialization.Deserializers.JRowDeserializer;
import Serialization.JRow;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonStreamParser;

public class XClients {
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		int port = Integer.parseInt(args[0]);

		Gson gson = new GsonBuilder()
				.registerTypeAdapter(JRow.class, new JRowDeserializer())
				.registerTypeAdapter(Player.class, new JActorSpecDeserializer())
				.create();
		JsonStreamParser parser = new JsonStreamParser(new InputStreamReader(System.in));

		ClientConfig config = gson.fromJson(parser.next(), ClientConfig.class);
		ExecutorService service = Executors.newFixedThreadPool(config.players.length);
		List<Future<?>> futures = new ArrayList<>();
		for (IPlayer player : config.players) {
			futures.add(
					service.submit(() ->
							new Client(new ProxyReferee(player), config.quiet).connectAndPlay(config.host, port))
			);
			Thread.sleep(config.wait * 1000L);
		}
		for (Future<?> future : futures) {
			try {
				future.get();
			} catch(Exception e) {
				future.cancel(true);
			}
		}
		service.shutdownNow();
	}
}
