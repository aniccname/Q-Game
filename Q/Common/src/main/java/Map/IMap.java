package Map;

import java.util.Map;
import java.util.Set;

import javax.swing.JComponent;

import Map.Tile.ITile;
import com.google.gson.JsonElement;

/**
 * An IMap is a representation of a map for the Q game.
 */
public interface IMap {

  /**
   * Place the tile at the coordinate only if it adjacent to another tile.
   *
   * @param coord coordinate to place tile at
   * @param tile tile to place at coordinate
   */
  void placeTileByOther(Coord coord, ITile tile);

  /**
   * Determines valid locations for the given tile to be placed on the current board.
   *
   * @param tile tile to find valid spots
   * @return set of valid coordinates for tile
   */
  Set<Coord> validSpots(ITile tile);

  /**
   * Gets the tile at the specified coordinate.
   *
   * @param coord coordinate of tile to find
   * @return the tile
   */
  ITile getTile(Coord coord);

  /**
   * Returns a copy of the game map.
   *
   * @return game map copy
   */
  IMap copyMap();

  /**
   * Renders the current state of the map
   *
   * @return the rendered map
   */
  JComponent render();

  /**
   * Returns a copy of the underlying representation as a Java map
   */
  Map<Coord, ITile> getMap();
}
