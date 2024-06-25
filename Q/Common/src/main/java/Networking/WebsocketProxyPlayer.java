package Networking;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;


import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketListener;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import Action.IAction;
import Map.Tile.ITile;
import Player.IPlayer;
import Referee.IShareableInfo;

/**
 * A proxy for a player on the other side of the websocket. Follows the Websocket protocol.
 */
public class WebsocketProxyPlayer extends AProxyPlayer {
  private boolean isNameMessage = true;
  //Should this be null or just the empty string or something like that? Probably null to make it fail asap.
  private final WebSocket ws;

  //TOOD: THis wont' work since all of the communication will come from the server, and as such it needs to be passed in. As such, WebSocketProxyPlayer has to exist in a HashSet inside the server, and have a message that is the equivalent to (message received).

  //TODO: This might just need to be a queue that has an associated condition variable in case the queue is empty.
  private Queue<String> inputs = new ArrayDeque<>();
  private Lock lock = new ReentrantLock();
  private Condition anyInputs = lock.newCondition();


  /**
   * Returns the next input from the remote client as a JSON element
   * @return The next JSON input from the client.
   * @throws IllegalStateException if one of the inputs given to this Proxy is not well formed JSON.
   */
  private JsonElement nextInput() throws IllegalStateException {
    this.lock.lock();
    while (this.inputs.isEmpty()) {
      try {
        this.anyInputs.await();
      } catch (InterruptedException e) {
        //I have no idea what to do here.
      }
    }
    this.lock.lock();
    //TODO Implement busy waiting
    return JsonParser.parseString(this.inputs.remove());
  }

  /**
   * Send an input to this Proxy player
   * @param s the input from the remote player
   */
  public void sendInput(String s) {
    System.out.println("Received input " + s + " from " + ws.getRemoteSocketAddress().toString());
    if (this.isNameMessage) {
      this.name = s;
      this.isNameMessage = false;
    }
    else {
      this.lock.lock();
      this.inputs.add(s);
      this.anyInputs.signal();
      this.lock.unlock();
    }
  }

  public WebsocketProxyPlayer(WebSocket ws) {
    this.ws = ws;
  }

  @Override
  protected JsonElement invoke(JsonElement elem) {
    this.ws.send(elem.toString());
    return this.nextInput();
  }

  @Override
  public void error(String reason) {
    JsonArray msg = new JsonArray();
    msg.add(this.error);
    JsonArray rsn = new JsonArray();
    rsn.add(reason);
    msg.add(rsn);
    this.ws.send(msg.toString());
    this.ws.close();
  }
}
