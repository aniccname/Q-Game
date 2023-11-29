package Serialization;

import Config.ScoringConfig;

public class RefereeStateConfig {
	public final int qbo;
	public final int fbo;

	public RefereeStateConfig(int qbo, int fbo) {
		this.qbo = qbo;
		this.fbo = fbo;
	}

	public ScoringConfig convert() {
		return new ScoringConfig(qbo, fbo);
	}
}
