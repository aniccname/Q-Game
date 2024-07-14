import org.java_websocket.server.DefaultSSLWebSocketServerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.io.FileInputStream;
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
  public static void main(String[] args) throws IOException {
    if (args.length != 4) {
      System.out.println("Invalid number of arguments. Please supply the port number, " +
              "number of waiting periods, length of each waiting period, and turn timeout in seconds.");
      return;
    }

    int port = Integer.parseInt(args[0]);
    int rounds = Integer.parseInt(args[1]);
    int roundLength = Integer.parseInt(args[2]);
    int playerTimeout = Integer.parseInt(args[3]);
    int NAME_TIMEOUT = 3;

    var refConfig = new RefereeConfig.RefereeConfigBuilder().playerTimeoutInSeconds(playerTimeout).build();
    var config = new ServerConfig.ServerConfigBuilder().port(port).quiet(false).waitingPeriodLengthInSeconds(roundLength)
            .numWaitingPeriods(rounds).waitForNameInSeconds(NAME_TIMEOUT).refereeConfig(refConfig).build();

    QGameWebSocketServer server = new QGameWebSocketServer(new Referee(), config, port,"localhost");
    Scanner scanner = new Scanner(new InputStreamReader(System.in));
    System.out.println("Please input the path to the keystore:");
    String STOREPATH = scanner.nextLine();
    System.out.println("Please input the type of the keystore:");
    String STORETYPE = scanner.nextLine();
    System.out.println("Please enter the password to the keystore:");
    String STOREPASS = scanner.nextLine();

    getSSLContext(STORETYPE, STOREPASS, STOREPATH)
            .ifPresentOrElse((ssl) -> {
              System.out.println("Certificate successfully loaded. Starting server with wss support");
              server.setWebSocketFactory(new DefaultSSLWebSocketServerFactory(ssl));},
                    () -> System.out.println("Unable to load certificate and initialize SSL context." +
                            " Starting server without certificate validation."));

    server.run();
  }
}
