package Referee;

import java.util.List;

public class GameResult {
	public final List<String> winners;
	public final List<String> assholes;

	public GameResult(List<String> winners, List<String> assholes) {
		this.winners = winners;
		this.assholes = assholes;
	}
}
