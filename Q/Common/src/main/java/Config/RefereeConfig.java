package Config;

import java.util.Optional;

import Observer.IObserver;
import Referee.IGameState;

public record RefereeConfig(
		Optional<IGameState> gameState,
		int playerTimeoutInSeconds,
		Optional<IObserver> observer
) {
	public static class RefereeConfigBuilder {
		private Optional<IGameState> gameState = Optional.empty();
		private int playerTimeoutInSeconds = 5;
		private Optional<IObserver> observer = Optional.empty();

		public RefereeConfig build() {
			return new RefereeConfig(
					gameState,
					playerTimeoutInSeconds,
					observer
			);
		}

		public RefereeConfigBuilder gameState(IGameState gameState) {
			this.gameState = Optional.of(gameState);
			return this;
		}

		public RefereeConfigBuilder playerTimeoutInSeconds(int playerTimeoutInSeconds) {
			this.playerTimeoutInSeconds = playerTimeoutInSeconds;
			return this;
		}

		public RefereeConfigBuilder observer(IObserver observer) {
			this.observer = Optional.of(observer);
			return this;
		}
	}
}
