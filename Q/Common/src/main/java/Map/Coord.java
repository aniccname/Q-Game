package Map;

import java.util.Objects;

/**
 * A Coord is a representation of a location on the board.
 * <p>
 * A Coord of (0, 0) is the center of the board.
 * Positive x-coordinates are to the right of the origin, while positive y-coordinates are above the
 * origin; the negative versions of the x and y coordinates are in the opposite directions, which
 * are left and below respectively.
 * NOTE: every coordinate point is immutable
 */
public class Coord implements Comparable<Coord> {
  private final int x; // the x-pos in relation to the map
  private final int y; // the y-pos in relation to the map

  // denotes the order of the cardinal neighbors method
  // LEFT - 0th index
  // RIGHT - 1st index
  // DOWN - 2nd index
  // UP - 3rd index
  public static final int LEFT = 0;
  public static final int RIGHT = 1;
  public static final int DOWN = 2;
  public static final int UP = 3;

  /**
   * Constructs a coordinate pair.
   *
   * @param x the x-coordinate of the pair
   * @param y the y-coordinate of the pair
   */
  public Coord(int x, int y) {
    this.x = x;
    this.y = y;
  }

  /**
   * Returns the x-coordinate of this Coord.
   *
   * @return x-coordinate
   */
  public int getX() {
    return x;
  }

  /**
   * Returns the y-coordinate of this Coord.
   *
   * @return y-coordinate
   */
  public int getY() {
    return y;
  }

  /**
   * Returns this Coord's immediate neighbors in the cardinal directions.
   * The order is as follows:
   *  0. Left
   *  1. Right
   *  2. Down
   *  3. Up
   *
   * @return array of neighboring Coords
   */
  public Coord[] getCardinalNeighbors() {
    return new Coord[]{
      new Coord(x - 1, y),
      new Coord(x + 1, y),
      new Coord(x, y - 1),
      new Coord(x, y + 1)
    };
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Coord coord = (Coord) o;
    return x == coord.x && y == coord.y;
  }

  @Override
  public int hashCode() {
    return Objects.hash(x, y);
  }

  @Override
  public String toString() {
    return "Coord{" +
        "x=" + x +
        ", y=" + y +
        '}';
  }

  @Override
  public int compareTo(Coord o) {
    int yCompare = Integer.compare(this.y, o.y);
    if (yCompare != 0) {
      return yCompare;
    }
    return Integer.compare(this.x, o.x);
  }
}
