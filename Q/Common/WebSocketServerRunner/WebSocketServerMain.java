import org.java_websocket.server.DefaultSSLWebSocketServerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import Config.RefereeConfig;
import Config.ServerConfig;
import Networking.QGameWebSocketServer;
import Referee.Referee;

/**
 * A Java program to read form the command line and start WebSocketServer
 * Input read in the format of port rounds timeout name-timeout
 */
public class WebSocketServerMain {
  private static String getStoreType(String path) {
    return path.substring(path.lastIndexOf(".") + 1).toUpperCase();
  }

  private static Optional<KeyStore> initializeKeyStore(String storetype, String storepass, String storepath) {
    KeyStore ks = null;
    try {
      ks = KeyStore.getInstance(storetype);
      File kf = new File(storepath);
      ks.load(new FileInputStream(kf), storepass.toCharArray());
    } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {
      System.out.println("Error loading KeyStore. Received error " + e.toString());
      return Optional.empty();
    }
    return Optional.of(ks);
  }

  private static Optional<KeyManagerFactory> initializeKeyManager(KeyStore ks, String algo, String storepass) {
    KeyManagerFactory kmf = null;
    try {
      kmf = KeyManagerFactory.getInstance(algo);
      kmf.init(ks, storepass.toCharArray());
    } catch (NoSuchAlgorithmException e) {
      System.out.println("Error loading KeyManager using the  " + algo + " algorithm. Received error " + e.toString());
    } catch (KeyStoreException | UnrecoverableKeyException e) {
      System.out.println("Error initializing Key and Trust managers. Received error " + e.toString());
    }
    return Optional.ofNullable(kmf);
  }

  private static Optional<TrustManagerFactory> initializeTrustManager(KeyStore ks, String algo) {
    TrustManagerFactory tmf = null;
    try {
      tmf = TrustManagerFactory.getInstance(algo);
      tmf.init(ks);
    } catch (NoSuchAlgorithmException e) {
      System.out.println("Error loading TrustManager using the  " + algo + " algorithm. Received error " + e.toString());
    } catch (KeyStoreException e) {
      System.out.println("Error initializing Trust manager. Received error " + e.toString());
    }
  return Optional.ofNullable(tmf);
  }

  private static Optional<SSLContext> initializeSSLContext(KeyManagerFactory kmf, TrustManagerFactory tmf) {
    SSLContext sslContext = null;
    try {
      sslContext = SSLContext.getInstance("TLS");
      sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
    } catch (NoSuchAlgorithmException e) {
      System.out.println("Unexpected SSLContext error. TLS not recognized as valid SSL protocol.");
    } catch (KeyManagementException e) {
      System.out.println("Failed to initialize the server's SSL context. Received error " + e.toString());
    }
    return Optional.ofNullable(sslContext);
  }

  /*
   * The loading of certificates for using wss was based off of this example:
   *  https://github.com/TooTallNate/Java-WebSocket/blob/master/src/main/example/SSLServerExample.java.
   */
  private static Optional<SSLContext> getSSLContext(String storetype, String storepass, String storepath) {
    String ALGO = "PKIX";
    var ks = initializeKeyStore(storetype, storepass, storepath);
    if (ks.isEmpty()) {
      return Optional.empty();
    }
    var kmf = initializeKeyManager(ks.get(), ALGO, storepass);
    if (kmf.isEmpty()) {
      return Optional.empty();
    }
    var tmf = initializeTrustManager(ks.get(), ALGO);
    if (tmf.isEmpty()) {
      return Optional.empty();
    }
    return initializeSSLContext(kmf.get(), tmf.get());
  }

  /**
   * Runs a server with the given options. Prompts the user for a keystore and password through system.in.
   * @param port the port to host the server on.
   * @param rounds the number of waiting periods this server will try before ending the connections.
   * @param roundLength the length of each waiting period.
   * @param playerTimeout the maximum time allotted for each player to take a turn.
   */
  private static void runServer(int port, int rounds, int roundLength, int playerTimeout,
                                int minPlayers, int maxPlayers, boolean fill) {
    int NAME_TIMEOUT = 3;
    var refConfig = new RefereeConfig.RefereeConfigBuilder().playerTimeoutInSeconds(playerTimeout).build();
    var config = new ServerConfig.ServerConfigBuilder().port(port).quiet(false).waitingPeriodLengthInSeconds(roundLength)
            .numWaitingPeriods(rounds).waitForNameInSeconds(NAME_TIMEOUT).refereeConfig(refConfig).build();
    String hostAddress;
    try {
       hostAddress = InetAddress.getLocalHost().getHostAddress();
    } catch (UnknownHostException e) {
      System.out.println("Unable to get local address. Defaulting to localhost.");
      hostAddress = "localhost";
    }

    QGameWebSocketServer server = new QGameWebSocketServer(new Referee(), config, port, hostAddress,
            minPlayers, maxPlayers, fill);
    Scanner scanner = new Scanner(new InputStreamReader(System.in));
    System.out.println("Please input the path to the keystore:");
    String STOREPATH = scanner.nextLine();
    System.out.println("Please input the password of the keystore:");
    String STORETYPE = getStoreType(STOREPATH);
    String STOREPASS = scanner.nextLine();

    getSSLContext(STORETYPE, STOREPASS, STOREPATH)
            .ifPresentOrElse((ssl) -> {
                      System.out.println("Certificate successfully loaded. Starting server with wss support");
                      server.setWebSocketFactory(new DefaultSSLWebSocketServerFactory(ssl));},
                    () -> System.out.println("Unable to load certificate and initialize SSL context." +
                            " Starting server without certificate validation."));

    server.run();
  }
  private static boolean isHelpArg(String arg) {
    return "-h".equalsIgnoreCase(arg)
            || "-help".equalsIgnoreCase(arg);
  }
  private static void printHelp() {
    System.out.println("usage: server [-f] [port] [num-rounds] [round-length] [max-turn-length] " +
            "[min-players] [max-players]");
    System.out.println("The -f flag fills up all open slots up to max-players with computer players.");
    System.out.println("port is the port the server will be hosted on.");
    System.out.println("num-rounds is the number of waiting periods the server will try before " +
            "ending the game.");
    System.out.println("If at the end of a round at least 2 players are present, the game "+
            "will start. If not, a new round will commence if it's not the last round");
    System.out.println("If it is the last round, the server will report the end of the game. ");
    System.out.println("round-length is the length of each round in seconds.");
    System.out.println("max-turn-length is the maximum length allotted for a player's turn in " +
            "seconds.");
    System.out.println("A player will be disconnected from the game if they do not finish their " +
            "turn within this allotted time. ");
    System.out.println("min-players is the minimum amount of human players required for the game to " +
            "start at the end of a waiting period.");
    System.out.println("If -f/-fill is present, min-players must be at least 1. Otherwise, " +
            "min-players must be at least 2. ");
    System.out.println("max-players is the maximum amount of players able to join the game. " +
            "max-players must be greater than or equal to 2 or min-players, whichever is larger.");
  }
  public static void main(String[] args) throws IOException {
    if (Arrays.stream(args).anyMatch(WebSocketServerMain::isHelpArg)) {
      printHelp();
      return;
    }
    //Remove the flags from the argument list.
    List<String> argList = new ArrayList<>(Arrays.stream(args).toList());
    boolean fillWithAi = argList.contains("-f") || argList.contains("-fill");
    argList.remove("-f"); argList.remove("-fill");
    if (argList.size() != 6) {
      System.out.println("Invalid number of arguments. For instructions please use the -h argument.");
      return;
    }
    var argVals = argList.stream().map(Integer::parseInt).toList();
    runServer(argVals.get(0), argVals.get(1), argVals.get(2),
            argVals.get(3), argVals.get(4), argVals.get(5), fillWithAi);
  }
}
