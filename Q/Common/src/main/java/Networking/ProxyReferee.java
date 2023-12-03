package Networking;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonStreamParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
  private final IPlayer player;
  private boolean isGameOver = false;
  private final Map<String, PlayerCommand> commands = new HashMap<>();

  public ProxyReferee(IPlayer player) {
    this.player = player;
    this.commands.put("setup", new SetupCommand());
    this.commands.put("take-turn", new TakeTurnCommand());
    this.commands.put("new-tiles", new NewTilesCommand());
    this.commands.put("win", new WinCommand());
  }

  /**
   * Relays the referee commands received through the input stream
   * to the given player, and sends the player's response to the output
   * stream. Begins by sending the player's name in the output stream.
   */
  public void playGame(InputStream in, OutputStream out) {
    PrintStream output = new PrintStream(out);
    output.println("  " + new JsonPrimitive(player.name()) + "  ");
    output.flush();
    JsonStreamParser serverMessages = new JsonStreamParser(new InputStreamReader(in));
    while (serverMessages.hasNext() && !isGameOver) {
      try {
        JsonArray message = serverMessages.next().getAsJsonArray();
        String methodName = message.get(0).getAsString();
        JsonArray args = message.get(1).getAsJsonArray();
        commands.get(methodName).execute(player, output, args);
        output.flush();
      } catch (JsonParseException e) {
        return;
      }
    }
  }

  static List<ITile> getJTiles(JsonElement e) {
    JTile[] tiles = new Gson().fromJson(e, JTile[].class);
    return Arrays.stream(tiles).map(JTile::convert).collect(Collectors.toList());
  }

  static IShareableInfo getISharable(JsonElement e) {
    Gson gson = new GsonBuilder().registerTypeAdapter(JPub.class, new JPubDeserializer()).create();
    return gson.fromJson(e, JPub.class).convert();
  }

  static void returnVoid(PrintStream o) {
    o.println(new JsonPrimitive("void"));
  }

  private interface PlayerCommand {
    void execute(IPlayer player, PrintStream output, JsonArray args);
  }

  private static class SetupCommand implements PlayerCommand {
    @Override
    public void execute(IPlayer player, PrintStream output, JsonArray args) {
      player.setup(getISharable(args.get(0)), getJTiles(args.get(1)));
      returnVoid(output);
    }
  }

  private static class TakeTurnCommand implements PlayerCommand {
    @Override
    public void execute(IPlayer player, PrintStream output, JsonArray args) {
      IAction action = player.takeAction(getISharable(args.get(0)));
      output.println(action.accept(new IActionSerializer(), null));
    }
  }

  private static class NewTilesCommand implements PlayerCommand {
    @Override
    public void execute(IPlayer player, PrintStream output, JsonArray args) {
      player.newTiles(getJTiles(args.get(0)));
      returnVoid(output);
    }
  }

  private class WinCommand implements PlayerCommand {
    @Override
    public void execute(IPlayer player, PrintStream output, JsonArray args) {
      player.win(args.get(0).getAsBoolean());
      returnVoid(output);
      isGameOver = true;
    }
  }
}
