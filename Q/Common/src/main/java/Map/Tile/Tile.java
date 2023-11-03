package Map.Tile;

import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.LookupOp;
import java.util.Objects;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;

/**
 * A Tile is a piece on the board. Each tile has a shape and a color.
 */
public class Tile implements ITile {
  private final Shape shape;
  private final TileColor color;

  /**
   * Constructs a tile with the specified shape and color.
   *
   * @param shape shape of the tile
   * @param color color of the tile
   */
  public Tile(Shape shape, TileColor color) {
    this.shape = shape;
    this.color = color;
  }

  @Override
  public Shape getShape() {
    return this.shape;
  }

  @Override
  public TileColor getColor() {
    return this.color;
  }

  @Override
  public boolean validNeighbor(ITile tile) {
    if (null == tile) {
      throw new IllegalArgumentException("Tile must not be null.");
    }
    if (tile.isEmpty()) {
      return true;
    }
    return this.color == tile.getColor() || this.shape == tile.getShape();
  }

  @Override
  public boolean isEmpty() {
    return false;
  }

  @Override
  public JComponent render() {
    BufferedImageOp lookup = new LookupOp(new ColorMapperNonWhite(this.color.color), null);
    BufferedImage colorAdjustedImage = lookup.filter(this.shape.redImage, null);
    return new JLabel(new ImageIcon(colorAdjustedImage));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Tile tile = (Tile) o;
    return shape == tile.shape && color == tile.color;
  }

  @Override
  public int hashCode() {
    return Objects.hash(shape, color);
  }

  @Override
  public String toString() {
    return "Tile{" +
        "shape=" + shape +
        ", color=" + color +
        '}';
  }

  @Override
  public int compareTo(ITile o) {
    int shapeCompare = this.shape.compareTo(o.getShape());
    if (shapeCompare != 0) {
      return shapeCompare;
    }
    return this.color.compareTo(o.getColor());
  }
}
