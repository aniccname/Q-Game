package Networking;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonStreamParser;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import Action.IAction;
import Map.Tile.ITile;
import Player.IPlayer;
import Referee.IShareableInfo;
import Serialization.Deserializers.JPubDeserializer;
import Serialization.JPub;
import Serialization.JTile;
import Serialization.Serializers.IActionSerializer;

/**
 * Represents a proxy referee for the client.
 */
public class ProxyReferee {

  /**
   * Relays the referee commands sent through the given socket to the given player.
   * @param s The socket connected to the server.
   * @param player The player to receive information from.
   * @return If the player has won the game or not.
   * @throws IOException if the connection fails.
   */
  public boolean playGame(Socket s, IPlayer player) throws IOException {
    JsonStreamParser serverMessages = new JsonStreamParser(new InputStreamReader(s.getInputStream()));
    PrintStream output = new PrintStream(s.getOutputStream());
    while (serverMessages.hasNext()) {
      try {
        JsonArray message = serverMessages.next().getAsJsonArray();
        String methodName = message.get(0).getAsString();
        JsonArray args = message.get(1).getAsJsonArray();
        switch (methodName) {
          case "setup" :
            player.setup(getISharable(args.get(0)), getJTiles(args.get(1)));
            returnVoid(output);
            break;
          case "take-turn" :
            IAction action = player.takeAction(getISharable(args.get(0)));
            output.println(action.accept(new IActionSerializer(), null));
            break;
          case "new-tiles" :
            player.newTiles(getJTiles(args.get(0)));
            returnVoid(output);
            break;
          case "win" :
            boolean won = args.get(0).getAsBoolean();
            player.win(won);
            returnVoid(output);
            return won;
        }
      } catch (RuntimeException e) {
        continue;
      }
    }
    return false;
  }

  private List<ITile> getJTiles(JsonElement e) {
    JTile[] tiles = new Gson().fromJson(e, JTile[].class);
    return Arrays.stream(tiles).map(JTile::convert).collect(Collectors.toList());
  }

  private IShareableInfo getISharable(JsonElement e) {
    Gson gson = new GsonBuilder().registerTypeAdapter(JPub.class, new JPubDeserializer()).create();
    return gson.fromJson(e, JPub.class).convert();
  }

  private void returnVoid(PrintStream o) {
    o.println(new JsonPrimitive("void"));
  }
}
