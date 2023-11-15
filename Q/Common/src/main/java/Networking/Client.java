package Networking;

import com.google.gson.JsonPrimitive;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

import Player.IPlayer;

/**
 * Represents the connection of a client to the server.
 */
public class Client {
  private final ProxyReferee refereeProxy;

  public Client(ProxyReferee refereeProxy) {
    this.refereeProxy = refereeProxy;
  }

  public boolean connect(IPlayer player, String hostname, int port) {
    try (Socket toServer = new Socket(hostname, port)) {
      new PrintStream(toServer.getOutputStream()).println(new JsonPrimitive(player.name()));
      return refereeProxy.playGame(toServer, player);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}