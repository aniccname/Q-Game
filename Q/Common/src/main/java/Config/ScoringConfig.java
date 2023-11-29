package Config;

public record ScoringConfig(
		int qPoints,
		int wholeHandBonus
) {
	public static class ScoringConfigBuilder {
		private int qPoints = 6;
		private int wholeHandBonus = 6;

		public ScoringConfig build() {
			return new ScoringConfig(qPoints, wholeHandBonus);
		}

		public ScoringConfigBuilder qPoints(int qPoints) {
			this.qPoints = qPoints;
			return this;
		}

		public ScoringConfigBuilder wholeHandBonus(int wholeHandBonus) {
			this.wholeHandBonus = wholeHandBonus;
			return this;
		}
	}
}
