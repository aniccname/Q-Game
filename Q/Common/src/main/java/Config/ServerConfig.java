package Config;

public record ServerConfig(
		int port,
		int numWaitingPeriods,
		int waitingPeriodLengthInSeconds,
		int waitForNameInSeconds,
		boolean quiet,
		RefereeConfig refereeConfig
) {
	public static class ServerConfigBuilder {
		private int port = 7777;
		private int numWaitingPeriods = 2;
		private int waitingPeriodLengthInSeconds = 20;
		private int waitForNameInSeconds = 3;
		private boolean quiet = true;
		private RefereeConfig refereeConfig
				= new RefereeConfig.RefereeConfigBuilder().build();

		public ServerConfig build() {
			return new ServerConfig(
					port,
					numWaitingPeriods,
					waitingPeriodLengthInSeconds,
					waitForNameInSeconds,
					quiet,
					refereeConfig
			);
		}

		public ServerConfigBuilder port(int port) {
			this.port = port;
			return this;
		}

		public ServerConfigBuilder numWaitingPeriods(int numWaitingPeriods) {
			this.numWaitingPeriods = numWaitingPeriods;
			return this;
		}

		public ServerConfigBuilder waitingPeriodLengthInSeconds(
				int waitingPeriodLengthInSeconds
		) {
			this.waitingPeriodLengthInSeconds = waitingPeriodLengthInSeconds;
			return this;
		}

		public ServerConfigBuilder waitForNameInSeconds(int waitForNameInSeconds) {
			this.waitForNameInSeconds = waitForNameInSeconds;
			return this;
		}

		public ServerConfigBuilder refereeConfig(RefereeConfig refereeConfig) {
			this.refereeConfig = refereeConfig;
			return this;
		}

		public ServerConfigBuilder quiet(boolean quiet) {
			this.quiet = quiet;
			return this;
		}
	}
}
