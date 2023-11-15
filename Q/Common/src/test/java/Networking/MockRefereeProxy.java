package Networking;

import java.io.IOException;
import java.net.Socket;

import Player.IPlayer;

public class MockRefereeProxy extends ProxyReferee {

  @Override
  public boolean playGame(Socket s, IPlayer player) throws IOException {
    return true;
  }
}
