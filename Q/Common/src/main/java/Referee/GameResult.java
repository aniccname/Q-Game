package Referee;

import java.util.List;
import java.util.Objects;

public class GameResult {
	public final List<String> winners;
	public final List<String> assholes;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		GameResult that = (GameResult) o;
		return Objects.equals(winners, that.winners) && Objects.equals(assholes, that.assholes);
	}

	@Override
	public int hashCode() {
		return Objects.hash(winners, assholes);
	}

	public GameResult(List<String> winners, List<String> assholes) {
		this.winners = winners;
		this.assholes = assholes;
	}

	@Override
	public String toString() {
		return "[[" +
						this.winners.stream().reduce((name, result) -> name + ", " + result).orElse("[]") +
						"], [" +
						this.assholes.stream().reduce((name, result) -> name + ", " + result).orElse("[]") +
						"]]";
	}

	/**
	 * A GameResult for a game that fails to run.
	 */
	public static GameResult EMPTY_RESULT = new GameResult(List.of(), List.of());

}
