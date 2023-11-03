package Serialization;

import Map.Coord;

public class JCoordinate implements Comparable<JCoordinate>{

  private final int row;
  private final int column;

  public JCoordinate(int row, int column) {
    this.row = row;
    this.column = column;
  }

  public JCoordinate(Coord coord) {
    this.row = coord.getY();
    this.column = coord.getX();
  }

  @Override
  public int compareTo(JCoordinate o) {
    if (this.row - o.row == 0) {
      return this.column - o.column;
    }
    else {
      return this.row - o.row;
    }
  }

  public Coord convert() {
    return new Coord(column, row);
  }
}
