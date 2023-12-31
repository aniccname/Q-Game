package Referee;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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

		Map<String, PlayerSafetyAdapter> playerNames =
				createPlayerNamesMap(playerSafetyAdapters);

		return playGame(playerNames, refereeConfig, executorService);
	}

	private GameResult playGame(
			Map<String, PlayerSafetyAdapter> playerNames,
			RefereeConfig refereeConfig,
			ExecutorService executorService
	) {
		assholes = new ArrayList<>();

		IGameState gameState = refereeConfig.gameState().orElseGet(() ->
			createGameState(
				playerNames.values().stream()
						.flatMap(player -> player.name().stream())
						.collect(Collectors.toList())
			)
		);

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

	private void playRound(Map<String, PlayerSafetyAdapter> playerNames, IGameState gameState,
												 Optional<IObserver> observer) {
		placementThisRound = false;

		int numTurns = gameState.getPlayerStates().size();

		for (int i = 0; i < numTurns && !shouldGameEnd; i++) {
			PlayerSafetyAdapter activePlayer =
					playerNames.get(gameState.getActivePlayer().getName());
			takeTurn(gameState, activePlayer);
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
	private Map<String, PlayerSafetyAdapter> createPlayerNamesMap(List<PlayerSafetyAdapter> players) {
		Map<String, PlayerSafetyAdapter> playerNames = new LinkedHashMap<>();
		for (PlayerSafetyAdapter player : players) {
			player.name().ifPresent(name -> playerNames.put(name, player));
		}
		return playerNames;
	}

	private IGameState createGameState(List<String> names) {
		return new GameState(names, new ScoringConfig.ScoringConfigBuilder().build());
	}

	private void announceSetup(IGameState gameState, Map<String, PlayerSafetyAdapter> playerNames) {
		// for every representation of the player
		// lets setup the player client
		for (IPlayerState playerState : gameState.getPlayerStates()) {
			boolean playerBehaved = playerNames.get(playerState.getName())
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
			kickPlayer(activePlayerState, gameState);
			return;
		}

		IAction action = possibleAction.get();

		// validate the action taken and throw exception if fail
		if (!gameState.validAction(new ActionChecker(), action)) {
			kickPlayer(activePlayerState, gameState);
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
			if (!playerBehaved) kickPlayer(activePlayerState, gameState);
		}
	}

	private List<String> announceGameEnd(IGameState gameState, Map<String, PlayerSafetyAdapter> playerNames) {
		// calculate the highest score a player has
		int winningScore = gameState.getPlayerStates().stream()
				.mapToInt(IPlayerState::getScore).max().orElse(0);

		List<String> winners = new ArrayList<>();

		// send a message to the players determining if they win
		// using the highest score calculated above
		for (IPlayerState playerState : gameState.getPlayerStates()) {
			PlayerSafetyAdapter player = playerNames.get(playerState.getName());
			boolean playerBehaved = player.win(playerState.getScore() == winningScore);
			if (!playerBehaved) {
				assholes.add(playerState.getName());
			} else if (playerState.getScore() == winningScore) {
				winners.add(playerState.getName());
			}
		}

		return winners;
	}

	private void kickPlayer(IPlayerState player, IGameState gameState) {
		assholes.add(player.getName());
		gameState.removePlayer(player);
	}
}
