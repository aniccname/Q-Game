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
public class ProxyPlayer extends AProxyPlayer {
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
  protected JsonElement invoke(JsonElement elem) {
    out.println(elem.toString());
    return this.in.next();
  }
}
