package Map.Tile;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * An ITile is a tile that has a shape and a color that can be placed on the map.
 * NOTE: every ITile is immutable
 */
public interface ITile extends Comparable<ITile> {
  /**
   * Get the shape of the tile.
   * @return Shape of the tile
   */
  Shape getShape();

  /**
   * Get the color of the tile.
   * @return TileColor of the tile
   */
  TileColor getColor();

  /**
   * Indicates whether the given tile would be a valid neighbor of this tile.
   *
   * @param tile tile to check against this tile
   * @return boolean indicating if given tile could be a valid neighbor
   */
  boolean validNeighbor(ITile tile);

  /**
   * Indicates whether this tile is empty.
   *
   * @return boolean indicating
   */
  boolean isEmpty();

  /**
   * Render the tile as an image.
   */
  JComponent render();

  /**
   * A Shape is a valid shape for a tile.
   */
  enum Shape {
    Star("/red_star.png"),
    EightStar("/red_8star.png"),
    Square("/red_square.png"),
    Circle("/red_circle.png"),
    Clover("/red_clover.png"),
    Diamond("/red_diamond.png");

    final BufferedImage redImage;
    Shape(String filename) {
      try {
        this.redImage = ImageIO.read(getClass().getResource(filename));
      } catch (IOException e) {
        throw new RuntimeException("Could not load image: " + filename);
      }
    }
  }

  /**
   * A TileColor is a valid color for a tile.
   */
  enum TileColor {
    Red(Color.red),
    Green(Color.green),
    Blue(Color.blue),
    Yellow(Color.yellow),
    Orange(Color.orange),
    Purple(Color.magenta);

    final Color color;
    TileColor(Color color) {
      this.color = color;
    }
  }

}
