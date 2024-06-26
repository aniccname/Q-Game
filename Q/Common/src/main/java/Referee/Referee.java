package Referee;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import Action.IAction;
import Action.PassAction;
import Action.PlaceAction;
import Config.RefereeConfig;
import Config.ScoringConfig;
import Map.Tile.ITile;
import Observer.IObserver;
import Player.IPlayer;
import Player.PlayerSafetyAdapter;
import Referee.Visitor.ActionChecker;
import Referee.Visitor.PlacesEntireHand;

/**
 * The referee that will play a game to completion, rule check, and kick players if they
 * throw an exception or take more than TIMEOUT_IN_SECONDS to respond.
 */
public class Referee implements IReferee {
	private boolean shouldGameEnd = false;
	private boolean placementThisRound = false;
	private List<String> assholes;

	/**
	 * Plays a game to completion with a list of players.
	 *
	 * @param players The players in the game. Assume the players are sorted by age already.
	 */
	public GameResult playGame(List<IPlayer> players, RefereeConfig refereeConfig) {
		ExecutorService executorService = Executors.newCachedThreadPool();

		List<PlayerSafetyAdapter> playerSafetyAdapters =
				createPlayerSafetyAdapters(players, executorService, refereeConfig);

		Map<Object, PlayerSafetyAdapter> playerNames =
				createPlayerNamesMap(playerSafetyAdapters);

		return playGame(playerNames, refereeConfig, executorService);
	}

	private GameResult playGame(
			Map<Object, PlayerSafetyAdapter> playerNames,
			RefereeConfig refereeConfig,
			ExecutorService executorService
	) {
		assholes = new ArrayList<>();

		IGameState gameState = refereeConfig.gameState().orElseGet(() ->
			createGameState(
				playerNames.values().stream()
						.map(player ->  new PlayerID(player.id(), player.name().orElseThrow()))
						.collect(Collectors.toList())));

		announceSetup(gameState, playerNames);
		refereeConfig.observer().ifPresent(
				observer -> updateObserver(observer, gameState)
		);
		shouldGameEnd = false;

		// while main loop that plays a game to completion
		while (!shouldGameEnd) {
			playRound(playerNames, gameState, refereeConfig.observer());
			shouldGameEnd |= gameState.getPlayerStates().isEmpty() || !placementThisRound;
		}

		List<String> winners = announceGameEnd(gameState, playerNames);
		executorService.shutdownNow();
		return new GameResult(winners, assholes);
	}

	private void playTurn(Map<Object, PlayerSafetyAdapter> playerNames, IGameState gameState) {
		AtomicReference<PlayerSafetyAdapter> activePlayer = new AtomicReference<>(null);
		gameState.getPlayerStates().forEach(gs -> {
			if (gs.equals(gameState.getActivePlayer())) {
				activePlayer.set(playerNames.get(gs.id()));
			} else {
				watchTurn(gameState, gs, playerNames);
			}
		});
		takeTurn(gameState, activePlayer.get());
	}

	private void playRound(Map<Object, PlayerSafetyAdapter> playerNames, IGameState gameState,
												 Optional<IObserver> observer) {
		placementThisRound = false;

		int numTurns = gameState.getPlayerStates().size();

		for (int i = 0; i < numTurns && !shouldGameEnd; i++) {
			playTurn(playerNames, gameState);
			observer.ifPresent(o -> updateObserver(o, gameState));
		}
	}

	private void updateObserver(IObserver observer, IGameState gameState) {
		observer.receiveState(gameState.copy());
	}

	private List<PlayerSafetyAdapter> createPlayerSafetyAdapters(
			List<IPlayer> players,
			ExecutorService executorService,
			RefereeConfig refereeConfig
	) {
		return players.stream()
				.map(player -> new PlayerSafetyAdapter(
						player,
						executorService,
						refereeConfig.playerTimeoutInSeconds()
				))
				.collect(Collectors.toList());
	}

	/**
	 * Creates a map of player names to their client player class
	 *
	 * @param players the list of players to create a map from
	 * @return the mapping of name to iplayer client.
	 */
	private Map<Object, PlayerSafetyAdapter> createPlayerNamesMap(List<PlayerSafetyAdapter> players) {
		Map<Object, PlayerSafetyAdapter> playerNames = new LinkedHashMap<>();
		for (PlayerSafetyAdapter player : players) {
			player.name().ifPresent(name -> playerNames.put(player.id(), player));
		}
		return playerNames;
	}

	private IGameState createGameState(List<PlayerID> pids) {
		return new GameState(pids, new ScoringConfig.ScoringConfigBuilder().build());
	}

	private void announceSetup(IGameState gameState, Map<Object, PlayerSafetyAdapter> playerNames) {
		// for every representation of the player
		// lets setup the player client
		for (IPlayerState playerState : gameState.getPlayerStates()) {
			boolean playerBehaved = playerNames.get(playerState.id())
					.setup(gameState, playerState.getTiles());
			if (!playerBehaved) {
				gameState.removePlayer(playerState);
				assholes.add(playerState.getName());
			}
		}
	}

	private void takeTurn(IGameState gameState, PlayerSafetyAdapter activePlayer) {
		IPlayerState activePlayerState = gameState.getActivePlayer();

		// the active player will try taking an action
		Optional<IAction> possibleAction = activePlayer.takeAction(gameState);
		if (possibleAction.isEmpty()) {
			kickPlayer(activePlayerState, gameState, activePlayer,
							"no action received within the allotted period of time.");
			return;
		}

		IAction action = possibleAction.get();

		// validate the action taken and throw exception if fail
		if (!gameState.validAction(new ActionChecker(), action)) {
			kickPlayer(activePlayerState, gameState, activePlayer,
							"given action is not a valid action. Cheating is not allowed here!");
			return;
		}

		this.shouldGameEnd |=
				action.accept(new PlacesEntireHand(), activePlayerState.getTiles());
		this.placementThisRound |= action instanceof PlaceAction;

		// if action is valid take the action
		List<ITile> newTiles = gameState.doAction(action);

		if (!(action instanceof PassAction)) {
			// give the active player new tiles
			boolean playerBehaved = activePlayer.newTiles(newTiles);
			if (!playerBehaved) kickPlayer(activePlayerState, gameState, activePlayer,
							"Server didn't receive acknowledgement to receiving new tiles in time.");
		}
	}

	private void watchTurn(IGameState gameState, IPlayerState watchingPlayer, Map<Object, PlayerSafetyAdapter> playerNames) {
		PlayerSafetyAdapter adapter = playerNames.get(watchingPlayer.id());
		if (!adapter.watchTurn(gameState)) {
			kickPlayer(watchingPlayer, gameState, adapter,
							"Server didn't receive acknowledgement to watching turn in time.");
		}
	}

	private List<String> announceGameEnd(IGameState gameState, Map<Object, PlayerSafetyAdapter> playerNames) {
		// calculate the highest score a player has
		int winningScore = gameState.getPlayerStates().stream()
				.mapToInt(IPlayerState::getScore).max().orElse(0);

		List<String> winners = new ArrayList<>();

		// send a message to the players determining if they win
		// using the highest score calculated above
		for (IPlayerState playerState : gameState.getPlayerStates()) {
			PlayerSafetyAdapter player = playerNames.get(playerState.id());
			boolean playerBehaved = player.win(playerState.getScore() == winningScore);
			if (!playerBehaved) {
				assholes.add(playerState.getName());
			} else if (playerState.getScore() == winningScore) {
				winners.add(playerState.getName());
			}
		}

		return winners;
	}

	private void kickPlayer(IPlayerState player, IGameState gameState,
													PlayerSafetyAdapter offender, String reason) {
		assholes.add(player.getName());
		offender.error("Kicked due to " + reason);
		gameState.removePlayer(player);
	}
}
