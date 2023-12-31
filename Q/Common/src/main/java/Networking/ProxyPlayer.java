package Networking;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonStreamParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;

import Action.IAction;
import Map.Tile.ITile;
import Player.IPlayer;
import Referee.IShareableInfo;
import Serialization.Deserializers.JChoiceDeserializer;
import Serialization.JPub;
import Serialization.JTile;

/**
 * A player proxy that informs a remote player about method calls and returns the remote players
 * responses (if any exist). Throws an exception if the remote players response is invalid.
 */
public class ProxyPlayer implements IPlayer {
  private final String name;
  private final JsonStreamParser in;
  private final PrintStream out;

  /**
   * Creates a new proxy player, communicating with a remote player through the
   * given input and output streams. Reads a string from the input stream and
   * uses that as the name of this player
   * @param in the input stream to read from
   * @param out the output stream to write to
   */
  public ProxyPlayer(InputStream in, OutputStream out) {
    this.in = new JsonStreamParser(new InputStreamReader(in));
    this.out = new PrintStream(out);
    this.name = new Gson().fromJson(this.in.next(), String.class);
  }

  @Override
  public String name() {
    return this.name;
  }

  @Override
  public void setup(IShareableInfo map, List<ITile> tiles) {
    JsonElement methodCall =
            serializeMethodCall("setup", new JPub(map).serialize(), this.getJTiles(tiles));
    JsonElement response = this.invoke(methodCall);
    this.assertVoid(response);
  }

  private JsonElement getJTiles(List<ITile> tiles) {
    JsonArray jTiles = new JsonArray();
    for(ITile tile: tiles) {
      jTiles.add(new Gson().toJsonTree(new JTile(tile)));
    }
    return jTiles;
  }

  private JsonElement serializeMethodCall(String name, JsonElement... args) {
    JsonArray methodCall = new JsonArray();
    methodCall.add(name);
    JsonArray arguments = new JsonArray();
    for (JsonElement arg : args) {
      arguments.add(arg);
    }
    methodCall.add(arguments);
    return methodCall;
  }

  private void assertVoid(JsonElement response) {
    if (!response.getAsString().equals("void")) {
      throw new RuntimeException("Expected response \"void\". Got " + response.getAsString());
    }
  }

  @Override
  public IAction takeAction(IShareableInfo publicState) {
    JsonElement methodCall =
            this.serializeMethodCall("take-turn", new JPub(publicState).serialize());
    JsonElement response = this.invoke(methodCall);
    Gson gson = new GsonBuilder().registerTypeAdapter(IAction.class, new JChoiceDeserializer()).create();
    return gson.fromJson(response, IAction.class);
  }

  @Override
  public void newTiles(List<ITile> tiles) {
    JsonElement methodCall = this.serializeMethodCall("new-tiles", this.getJTiles(tiles));
    JsonElement response = this.invoke(methodCall);
    this.assertVoid(response);
  }

  @Override
  public void win(boolean won) {
    JsonElement methodCall = this.serializeMethodCall("win", new JsonPrimitive(won));
    JsonElement response = this.invoke(methodCall);
    this.assertVoid(response);
  }

  private JsonElement invoke(JsonElement elem) {
    out.println(elem.toString());
    return in.next();
  }
}
