package Networking;


import java.net.URI;
import java.net.http.WebSocket;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BufferedWebSocketClient implements WebSocket.Listener {

  private final Queue<String> messages = new ArrayDeque<>();
  private final Lock lock = new ReentrantLock();
  private final Condition anyMessages = lock.newCondition();

//
//  public void onOpen(ServerHandshake serverHandshake) {
//    System.out.println("Client open!");
//  }
//
//  @Override
//  public void onMessage(String s) {
//    this.messages.add(s);
//    anyMessages.signal();
//  }
//
//  @Override
//  public void onClose(int i, String s, boolean b) {
//    System.out.println("Closed!");
//  }
//
//  @Override
//  public void onError(Exception e) {
//    System.out.println("BufferedWebSocketClient threw error " + e.toString());
//  }
//

  /**
   * Gets the next message that this WebSocket received.
   * @return the next message that this web socket received.
   */
  public String getMessage() {
    lock.lock();
    while (this.messages.isEmpty()) {
      try {
        anyMessages.await();
      } catch (InterruptedException e) {
        //I have no idea what to do here.
      }
    }
    lock.lock();
    return this.messages.remove();
  }
@Override
public void onOpen(WebSocket webSocket) {
  System.out.println("Client open!");
  WebSocket.Listener.super.onOpen(webSocket);
}

  @Override
  public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
    System.out.println("onText received " + data);
    this.messages.add(data.toString());
    lock.lock();
    anyMessages.signal();
    lock.unlock();
    return WebSocket.Listener.super.onText(webSocket, data, last);
  }

  @Override
  public void onError(WebSocket webSocket, Throwable error) {
    System.out.println("Bad day! " + webSocket.toString());
    WebSocket.Listener.super.onError(webSocket, error);
  }

  @Override
  public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
    System.out.println("Connection closed due to: " + statusCode + " (" + reason + ")");
    return WebSocket.Listener.super.onClose(webSocket, statusCode, reason);
  }
}
