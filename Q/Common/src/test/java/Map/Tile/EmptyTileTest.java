package Map.Tile;

import org.junit.Test;

import static org.junit.Assert.*;

import javax.swing.JDialog;
import javax.swing.JFrame;

public class EmptyTileTest {
  private final ITile empty = new EmptyTile();

  @Test(expected = UnsupportedOperationException.class)
  public void getShape() {
    empty.getShape();
  }

  @Test(expected = UnsupportedOperationException.class)
  public void getColor() {
    empty.getColor();
  }

  @Test
  public void validNeighbor() {
    assertTrue(empty.validNeighbor(empty));
  }

  @Test
  public void isEmpty() {
    assertTrue(empty.isEmpty());
  }
}