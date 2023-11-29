package Networking;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Optional;

/**
 * Represents the connection of a client to the server.
 */
public class Client {
  private final ProxyReferee refereeProxy;
  private final boolean quiet;
  private final static int MAX_WAIT_SECONDS = 5;

  public Client(ProxyReferee refereeProxy, boolean quiet) {
    this.refereeProxy = refereeProxy;
    this.quiet = quiet;
  }

  /**
   * Connects to a server and plays a game.
   */
  public void connectAndPlay(String hostname, int port) {
    Optional<Socket> socket = connect(hostname, port);
    if (socket.isEmpty()) {
      return;
    }

    InputStream in;
    OutputStream out;
    try {
      in = socket.get().getInputStream();
      out = socket.get().getOutputStream();
    } catch (IOException e) {
      printIfNotQuiet("Unable to communicate with server: " + e);
      closeSocket(socket.get());
      return;
    }

    refereeProxy.playGame(in, out);
    closeSocket(socket.get());
  }

  private Optional<Socket> connect(String hostname, int port) {
    long startTime = System.currentTimeMillis();
    while (System.currentTimeMillis() - startTime < 1000L * MAX_WAIT_SECONDS) {
      try {
        return Optional.of(new Socket(hostname, port));
      } catch (UnknownHostException | SecurityException | IllegalArgumentException e) {
        printIfNotQuiet("Unable to connect to server: " + e);
        return Optional.empty();
      } catch (IOException e) {
        printIfNotQuiet("Server not available, trying again");
        try {
          Thread.sleep(500);
        } catch (InterruptedException e2) {
          printIfNotQuiet("Interrupted while sleeping: " + e2);
        }
      }
    }
    return Optional.empty();
  }


  private void closeSocket(Socket socket) {
    try {
      socket.close();
    } catch (IOException e) {
      printIfNotQuiet("Unable to close socket: " + e);
    }
  }

  private void printIfNotQuiet(Object object) {
    if (!quiet) {
      System.err.println(object);
    }
  }
}