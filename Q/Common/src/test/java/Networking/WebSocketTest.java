package Networking;

import org.java_websocket.WebSocketAdapter;

import java.net.ServerSocket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Referee.Referee;

public class WebSocketTest {
  private static int port = 8887;

  public static void main(String[] args) throws Exception {
    ExecutorService es = Executors.newCachedThreadPool();
    es.submit(() -> new QGameWebSocketServer(new Referee(), "localhost", port).run());
    //TODO: This means it's the server that's fucking up
    System.out.println("Opened up server!");
    CountDownLatch latch = new CountDownLatch(1);

    WebSocket ws1 = HttpClient
            .newHttpClient()
            .newWebSocketBuilder()
            .buildAsync(new URI("ws://localhost:8887"), new WebSocketClient(latch))
            .join();
    System.out.println("Client John connected!");
    ws1.sendText("John", true);
    System.out.println("John sent name");

    WebSocket ws2 = HttpClient
            .newHttpClient()
            .newWebSocketBuilder()
            .buildAsync(new URI("ws://localhost:8887"), new WebSocketClient(latch))
            .join();
    System.out.println("Client Jave connected!");
    ws2.sendText("Jave", true);
    System.out.println("Jave sent name");

    //TODO: Figure out of communication works with the proxy socket

  }

  private static class WebSocketClient implements WebSocket.Listener {
    private final CountDownLatch latch;

    public WebSocketClient(CountDownLatch latch) {
      System.out.println("WebSocket client created!");
      this.latch = latch;
    }

    @Override
    public void onOpen(WebSocket webSocket) {
      System.out.println("onOpen using subprotocol " + webSocket.getSubprotocol());
      WebSocket.Listener.super.onOpen(webSocket);
    }

    @Override
    public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
      System.out.println("onText received " + data);
      latch.countDown();
      return WebSocket.Listener.super.onText(webSocket, data, last);
    }

    @Override
    public void onError(WebSocket webSocket, Throwable error) {
      System.out.println("Bad day! " + webSocket.toString());
      WebSocket.Listener.super.onError(webSocket, error);
    }

    @Override
    public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
      System.out.println("It's closed!");
      return WebSocket.Listener.super.onClose(webSocket, statusCode, reason);
    }
  }
}