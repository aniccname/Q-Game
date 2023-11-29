package Referee;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import Action.IAction;
import Config.ScoringConfig;
import Map.Coord;
import Map.GameMap;
import Map.IMap;
import Map.Tile.ITile;
import Map.Tile.Tile;
import Referee.Visitor.ActionExecutor;
import Referee.Visitor.ActionScorer;
import Referee.Visitor.IActionChecker;
import Referee.Visitor.PlacesEntireHand;

/**
 * The IGameState represents the game (and its information) at a specific moment.
 *<p>
 *
 * This interface is supposed to represent the interface between the referee-player how they
 * are able to communicate with each other.
 * This specific implementation implements the IGameState interface and will use the
 * ActionExecutor, ActionChecker, and ActionScorer to perform, validate, and score actions
 * respectively.
 *
 */
public class GameState implements IGameState {
  public static final int FULL_HAND_TILE_COUNT = 6; // the max number of tiles a player can have
  private static final int MAX_EACH_TILE_COUNT = 30; // the max number of each tile in the game

  private final IMap map; // the game map
  private final List<ITile> refTiles; // the list of referee's tiles
  private final PlayerOrder order; // the order which the players make moves
  private final ScoringConfig scoringConfig; // the scoring configuration of the game

  /**
   * Creates a game state with a new randomly generated tile at the center, and a list of
   * player (users) in the game
   *
   * @param players the list of names of the players that the game starts with
   */
  public GameState(List<String> players, ScoringConfig scoringConfig) {
    this(new Random(), players, scoringConfig);
  }

  /**
   * Creates GameState using given (seeded) Random, so the tile place in the center is random.
   * NOTE: the players are assumed to be passed in the game state with all the players connected
   *       to the game
   *
   * @param random seeded random
   * @param players the list of names of the players that the game starts with
   */
  public GameState(Random random, List<String> players, ScoringConfig scoringConfig) {
    if (players.isEmpty()) {
      throw new IllegalArgumentException("The game state must start with at least 1 player");
    }
    this.refTiles = generateTiles();
    Collections.shuffle(refTiles, random);

    // convert the list of users to player data's
    List<IPlayerState> playerStates =
        players.stream().map(PlayerState::new).collect(Collectors.toList());
    for (IPlayerState playerState : playerStates) {
      playerState.acceptTiles(pickRefTiles(FULL_HAND_TILE_COUNT));
    }

    // create the order and active player from the list of player data
    this.order = new PlayerOrder(playerStates);

    // place a random tile in the center of the game map
    this.map = new GameMap(pickRefTiles(1).get(0));

    this.scoringConfig = scoringConfig;
  }

  /**
   * Constructs a copy of the given game state.
   * @param state the game state to copy
   */
  public GameState(GameState state) {
    this(
        state.map.copyMap(),
        new ArrayList<>(state.refTiles),
        state.getPlayerStates().stream()
            .map(PlayerState::new).collect(Collectors.toList()),
        state.scoringConfig
    );
  }

  /**
   * Constructor for beginning a game from a starting position, where a map
   * already exists and players already have hands and scores
   *
   * @param map the IMap to initialize the game state with
   * @param players the current states of the players
   */
  public GameState(
      IMap map,
      List<ITile> refTiles,
      List<IPlayerState> players,
      ScoringConfig scoringConfig
  ) {
    this.map = map.copyMap();
    this.refTiles = new ArrayList<>(refTiles);
    this.order = new PlayerOrder(players);
    this.scoringConfig = scoringConfig;
  }

  /**
   * Generates a list of tiles for the game Q.
   * There are 30 tiles of each possible combination of the six valid tile colors and six valid
   * shapes, for a total of 1080 tiles.
   *
   * @return the list of tiles
   */
  private List<ITile> generateTiles() {
    List<ITile> result = new ArrayList<>();

    for (ITile.Shape shape : ITile.Shape.values()) {
      for (ITile.TileColor color : ITile.TileColor.values()) {
        for (int i = 0; i < MAX_EACH_TILE_COUNT; ++i) {
          result.add(new Tile(shape, color));
        }
      }
    }

    return result;
  }

  /**
   * Performs the requested action on the game map.
   * NOTE: the method validAction must be called before using to have the referee check
   * whether the action is valid
   *
   * This method uses the IActionExecutor for the action to accept
   * this and perform the action.
   *
   * NOTE: an action executor will use the actionScorer within it to compute the score the
   * action produces. By accepting an actionExecutor in the action we will get the points generated
   * by the action.
   * The active player will add the points from the action to their score.
   *
   * @param action the proposed action to perform with the referee approval
   * @return the new tiles added to the player's hand
   */
  public List<ITile> doAction(IAction action) {
    boolean placesEntireHand =
        action.accept(new PlacesEntireHand(), getActivePlayer().getTiles());
    List<ITile> tiles = action.accept(new ActionExecutor(), this);

    getActivePlayer().addScore(
        action.accept(
            new ActionScorer(placesEntireHand, scoringConfig),
            this.getMap()
        )
    );

    order.next();
    return tiles;
  }

  @Override
  public IMap getMap() {
    return map.copyMap();
  }

  @Override
  public boolean isStartOfRound() {
    return order.isStartOfRound();
  }

  @Override
  public IPlayerState activePlayer() {
    return this.order.current();
  }

  /**
   * Uses the referee (IActionChecker) to determine whether the action is valid.
   * Note: this method uses the visitor pattern to accept the referee into the action alongside
   * with the game state to make sure the action is valid.
   *
   * @param checker entity in charge of checking action
   * @param action proposed action to check
   * @return the boolean result of whether the action is valid.
   */
  public boolean validAction(IActionChecker checker, IAction action) {
    return action.accept(checker, this);
  }

  /**
   * Picks a number of tiles from the ref and generates a list of these random tiles.
   *
   * @param numTiles number of tiles to select
   * @return a list of random tiles picked from the ref's tiles
   */
  public List<ITile> pickRefTiles(int numTiles) {
    List<ITile> result = new ArrayList<>();
    for (int i = 0; i < numTiles; i++) {
      result.add(refTiles.remove(0));
    }
    return result;
  }

  /**
   * Returns the active player's internal information so that we can accept tiles into
   * the active player's hand.
   *
   * @return the IPlayerState representing the active player's internal information.
   */
  public IPlayerState getActivePlayer() {
    return order.current();
  }

  /**
   * Adds a list of tiles into the ref tiles hand.
   * NOTE: should only be called when we remove tiles from the ref's hand and then
   * add them back into the ref's hand.
   *
   * @param tiles the tiles to add to the pool of referee tiles
   */
  public void acceptRefTiles(List<ITile> tiles) {
    this.refTiles.addAll(tiles);
  }

  @Override
  public int getRefTileCount() {
    return this.refTiles.size();
  }

  @Override
  public List<String> showOrder() {
    return order.getPlayerStates().stream().map(IPlayerState::getName).collect(Collectors.toList());
  }

  @Override
  public List<Integer> getScores() {
    return order.getPlayerStates().stream()
            .map(IPlayerState::getScore).collect(Collectors.toList());
  }

  @Override
  public void placeTileByOther(Coord coord, ITile tile) {
    map.placeTileByOther(coord, tile);
  }

  /**
   * Returns a copy of the list internal player information that the game state holds.
   *
   * @return a list of internal player information that the game state holds.
   */
  public List<IPlayerState> getPlayerStates() {
    return order.getPlayerStates();
  }

  @Override
  public void removePlayer(IPlayerState player) {
    // kick the player from the order
    order.kickPlayer(player);
    // the ref will accept the players tiles
    acceptRefTiles(player.getTiles());
  }

  @Override
  public IGameState copy() {
    return new GameState(this);
  }
}
