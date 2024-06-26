import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonStreamParser;

import org.java_websocket.server.WebSocketServer;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import Config.RefereeConfig;
import Config.ServerConfig;
import Networking.QGameWebSocketServer;
import Serialization.Deserializers.JActorSpecDeserializer;
import Serialization.Deserializers.JRowDeserializer;
import Serialization.JRow;
import Player.Player;
import Referee.Referee;

/**
 * A Java program to read form the command line and start WebSocketServer
 */
public class WebSocketServerMain {
  public static void main(String[] args) throws IOException {
    int port = Integer.parseInt(args[0]);

    /**Gson gson = new GsonBuilder()
            .registerTypeAdapter(JRow.class, new JRowDeserializer())
            .registerTypeAdapter(Player.class, new JActorSpecDeserializer())
            .create();
    JsonStreamParser parser = new JsonStreamParser(new InputStreamReader(System.in));

    ServerConfig config = gson.fromJson(parser.next(), ServerConfig.class);**/
  var refConfig = new RefereeConfig.RefereeConfigBuilder().playerTimeoutInSeconds(60).build();
    var config = new ServerConfig.ServerConfigBuilder().port(port).quiet(false).refereeConfig(refConfig).build();

    QGameWebSocketServer server = new QGameWebSocketServer(new Referee(), config, port,"localhost");

    server.run();
  }
}
