package Serialization;

import java.util.Optional;
import Observer.Observer;

import com.google.gson.annotations.SerializedName;

public class RefereeConfig {
	public final JState state0;
	public final boolean quiet;
	@SerializedName("config-s")
	public final RefereeStateConfig configS;
	@SerializedName("per-turn")
	public final int perTurn;
	public final boolean observe;

	public RefereeConfig(JState state0, boolean quiet, RefereeStateConfig configS, int perTurn,
											 boolean observe) {
		this.state0 = state0;
		this.quiet = quiet;
		this.configS = configS;
		this.perTurn = perTurn;
		this.observe = observe;
	}

	public Config.RefereeConfig convert() {
		return new Config.RefereeConfig(
				Optional.of(state0.convert(configS.convert())),
				perTurn,
				observe ? Optional.of(new Observer()) : Optional.empty()
		);
	}
}
