package Map.Tile;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;

/**
 * An EmptyTile is the absence of a tile.
 */
public class EmptyTile implements ITile {
  private static final BufferedImage IMAGE;

  static {
    try {
      IMAGE = ImageIO.read(EmptyTile.class.getResource("/empty.png"));
    } catch (IOException e) {
      throw new RuntimeException("Could not load empty tile image.");
    }
  }

  @Override
  public Shape getShape() {
    throw new UnsupportedOperationException("Empty tiles do not have a shape.");
  }

  @Override
  public TileColor getColor() {
    throw new UnsupportedOperationException("Empty tiles do not have a color.");
  }

  @Override
  public boolean validNeighbor(ITile tile) {
    if (null == tile) {
      throw new IllegalArgumentException("Tile must not be null");
    }
    return true;
  }

  @Override
  public boolean isEmpty() {
    return true;
  }

  @Override
  public JComponent render() {
    return new JLabel(new ImageIcon(IMAGE.getScaledInstance(SIDE_LENGTH, SIDE_LENGTH, Image.SCALE_SMOOTH)));
  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    return true;
  }

  @Override
  public int compareTo(ITile o) {
    throw new UnsupportedOperationException("Empty tiles cannot be compared.");
  }
}
