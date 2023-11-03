package Map;

import java.awt.Color;
import java.awt.FlowLayout;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;

import Map.Tile.EmptyTile;
import Map.Tile.ITile;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * An instance of a map of the Q game. The map is represented by a Hashmap that places the tile
 * relative to the starting tile.
 *
 * The board is able to perform a few actions that are specific to the board.
 * - placeTile -> which places a tile that must be adjacent to another tile no matter the validity
 * - validNeighbors -> creates a set of coordinates for a provided tile where a user can place it
 *
 * Since these actions are specific to the board, there is no need for an external rule book that
 * enforces these rules on the board.
 */
public class GameMap implements IMap {
  private final Map<Coord, ITile> board;


  /**
   * Creates a map given a mapping of coordinates to tiles.
   * Helper constructor used in generating copy's of game maps / testing.
   *
   * @param map a given mapping of coordinates to tiles
   */
  public GameMap(Map<Coord, ITile> map) {
    if (map == null) {
      throw new IllegalArgumentException("Starting map must not be null");
    }
    this.board = new HashMap<>(map);
  }

  /**
   * Creates a map starting at the given referee's tile
   *
   * @param tile referee's starting tile
   */
  public GameMap(ITile tile) {
    if (null == tile) {
      throw new IllegalArgumentException("Starting tile must not be null");
    }
    if (tile.isEmpty()) {
      throw new IllegalArgumentException("Starting tile must not be empty.");
    }

    this.board = new HashMap<>();
    this.board.put(new Coord(0, 0), tile);
  }

  /**
   * Places the provided tile at the specified coordinate only if it is adjacent to
   * any other tile.
   *
   * @param coord coordinate to place tile at
   * @param tile tile to place at coordinate
   */
  public void placeTileByOther(Coord coord, ITile tile) {
    if (null == tile) {
      throw new IllegalArgumentException("Tile must not be null");
    }
    if (null == coord) {
      throw new IllegalArgumentException("Coordinate must not be null");
    }
    if (Arrays.stream(coord.getCardinalNeighbors()).noneMatch(board::containsKey)) {
      throw new RuntimeException("Tile must have cardinal neighbors.");
    }
    if (board.containsKey(coord)) {
      throw new RuntimeException("Cannot place tile on top of another.");
    }

    this.board.put(coord, tile);
  }

  /**
   * Determines the valid spots where to place the specified tile.
   * Valid spots are computed in the following manner
   * 1. finding potential neighbors for the tile
   * 2. filtering the potential neighbors for spots that are empty
   * 3. checking neighboring tiles to see that the colors and shapes match in row and column
   *
   * @param tile tile to find valid spots
   * @return a set representing the valid coordinates where the tile can be placed
   */
  public Set<Coord> validSpots(ITile tile) {
    if (null == tile) {
      throw new IllegalArgumentException("Tile must not be null");
    }
    List<Map.Entry<Coord, ITile>> potentialNeighbors = board.entrySet().stream()
            .filter((x) -> x.getValue().validNeighbor(tile)).collect(Collectors.toList());

    Set<Coord> emptyNeighboringSpots = potentialNeighbors.stream()
            .flatMap((x) -> Arrays.stream(x.getKey().getCardinalNeighbors()))
            .filter((x) -> !board.containsKey(x))
            .collect(Collectors.toSet());

    Set<Coord> validSpots = emptyNeighboringSpots.stream()
            .filter((c) -> checkNeighboringTiles(c, tile))
            .collect(Collectors.toSet());

    return validSpots;
  }

  /**
   * Checks if a coordinate is a valid spot for the given tile
   *
   * @param coord coordinate of spot to check
   * @param tile tile to check
   * @return whether a tile would be valid at the given spot
   */
  private boolean checkNeighboringTiles(Coord coord, ITile tile) {
    if (null == tile) {
      throw new IllegalArgumentException("Tile must not be null");
    }
    if (null == coord) {
      throw new IllegalArgumentException("Coordinate must not be null");
    }
    Coord[] neighbors = coord.getCardinalNeighbors();

    return getTile(neighbors[Coord.UP]).validNeighbor(tile)
        && getTile(neighbors[Coord.DOWN]).validNeighbor(tile)
        && getTile(neighbors[Coord.UP]).validNeighbor(getTile(neighbors[Coord.DOWN]))
        && getTile(neighbors[Coord.LEFT]).validNeighbor(tile)
        && getTile(neighbors[Coord.RIGHT]).validNeighbor(tile)
        && getTile(neighbors[Coord.LEFT]).validNeighbor(getTile(neighbors[Coord.RIGHT]));
  }

  @Override
  public ITile getTile(Coord coord) {
    if (null == coord) {
      throw new IllegalArgumentException("Coordinate must not be null");
    }
    return board.getOrDefault(coord, new EmptyTile());
  }

  @Override
  public IMap copyMap() {
    return new GameMap(new HashMap<>(board));
  }

  @Override
  public JComponent render() {
    int minX = board.keySet().stream().mapToInt(Coord::getX).min().orElse(0);
    int minY = board.keySet().stream().mapToInt(Coord::getY).min().orElse(0);
    int maxX = board.keySet().stream().mapToInt(Coord::getX).max().orElse(0);
    int maxY = board.keySet().stream().mapToInt(Coord::getY).max().orElse(0);

    JPanel map = new JPanel();
    map.setLayout(new BoxLayout(map, BoxLayout.Y_AXIS));
    for (int y = minY; y <= maxY; y++) {
      JPanel row = new JPanel();
      row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
      for (int x = minX; x <= maxX; x++) {
        JComponent tileImg = getTile(new Coord(x, y)).render();
        tileImg.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        row.add(tileImg);
      }
      map.add(row);
    }

    return map;
  }

  @Override
  public Map<Coord, ITile> getMap() {
    return new HashMap<>(board);
  }
}
