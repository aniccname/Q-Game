package Serialization;

import com.google.gson.annotations.SerializedName;

public class ServerConfig {
	public final int port;
	@SerializedName("server-tries") public final int serverTries;
	@SerializedName("server-wait") public final int serverWait;
	@SerializedName("wait-for-signup") public final int waitForSignup;
	public final boolean quiet;
	@SerializedName("ref-spec") public final RefereeConfig refSpec;

	public ServerConfig(int port, int serverTries, int serverWait, int waitForSignup, boolean quiet,
											RefereeConfig refSpec) {
		this.port = port;
		this.serverTries = serverTries;
		this.serverWait = serverWait;
		this.waitForSignup = waitForSignup;
		this.quiet = quiet;
		this.refSpec = refSpec;
	}

	public Config.ServerConfig convert() {
		return new Config.ServerConfig(
				port,
				serverTries,
				serverWait,
				waitForSignup,
				quiet,
				refSpec.convert()
		);
	}
}
