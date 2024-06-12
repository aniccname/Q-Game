package Networking;

import java.io.IOException;
import java.net.Socket;

import Player.IPlayer;

public class MockRefereeProxy extends ProxyReferee {

  public MockRefereeProxy(IPlayer player) {
    super(player);
  }
}
