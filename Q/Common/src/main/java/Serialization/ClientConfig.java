package Serialization;

import Player.Player;

public class ClientConfig {
	public final int port;
	public final String host;
	public final int wait;
	public final boolean quiet;
	public final Player[] players;

	public ClientConfig(int port, String host, int wait, boolean quiet, Player[] players) {
		this.port = port;
		this.host = host;
		this.wait = wait;
		this.quiet = quiet;
		this.players = players;
	}
}
