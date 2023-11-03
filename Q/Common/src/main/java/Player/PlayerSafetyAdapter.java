package Player;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import Action.IAction;
import Map.IMap;
import Map.Tile.ITile;
import Referee.IShareableInfo;

public class PlayerSafetyAdapter {
	private final IPlayer player;
	private final ExecutorService executorService;
	private final int timeoutInSeconds;

	public PlayerSafetyAdapter(
			IPlayer player,
			ExecutorService executorService,
			int timeoutInSeconds
	) {
		this.player = player;
		this.executorService = executorService;
		this.timeoutInSeconds = timeoutInSeconds;
	}

	public Optional<String> name() {
		return callWithTimeout(player::name);
	}

	public boolean setup(IMap map, List<ITile> tiles) {
		return callWithTimeout(() -> {
			player.setup(map, tiles);
			return true;
		}).orElse(false);
	}

	public Optional<IAction> takeAction(IShareableInfo publicState) {
		return callWithTimeout(() -> player.takeAction(publicState));
	}

	public boolean newTiles(List<ITile> tiles) {
		return callWithTimeout(() -> {
			player.newTiles(tiles);
			return true;
		}).orElse(false);
	}

	public boolean win(boolean won) {
		return callWithTimeout(() -> {
			player.win(won);
			return true;
		}).orElse(false);
	}

	private <T> Optional<T> callWithTimeout(Callable<T> toRun) {
		Future<T> future = executorService.submit(toRun);
		try {
			return Optional.ofNullable(future.get(timeoutInSeconds, TimeUnit.SECONDS));
		} catch (Exception e) {
			return Optional.empty();
		}
	}
}
