package Map.Tile;

import Map.Tile.ITile.TileColor;
import Map.Tile.ITile.Shape;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import javax.swing.JDialog;
import javax.swing.JFrame;

public class TileTest {
  private ITile empty;
  private ITile greenStar;
  private ITile greenSquare;
  private ITile redStar;
  private ITile blueSquare;
  private ITile blueStar;

  @Before
  public void init() {
    empty = new EmptyTile();
    greenStar = new Tile(Shape.Star, TileColor.Green);
    greenSquare = new Tile(Shape.Square, TileColor.Green);
    redStar = new Tile(Shape.Star, TileColor.Red);
    blueSquare = new Tile(Shape.Square, TileColor.Blue);
    blueStar = new Tile(Shape.Star, TileColor.Blue);
  }

  @Test
  public void getShape() {
    assertEquals(greenStar.getShape(), Shape.Star);
    assertEquals(greenSquare.getShape(), Shape.Square);
    assertEquals(redStar.getShape(), Shape.Star);
    assertEquals(blueSquare.getShape(), Shape.Square);
  }

  @Test
  public void getColor() {
    assertEquals(greenStar.getColor(), TileColor.Green);
    assertEquals(greenSquare.getColor(), TileColor.Green);
    assertEquals(redStar.getColor(), TileColor.Red);
    assertEquals(blueSquare.getColor(), TileColor.Blue);
  }

  @Test
  public void validNeighbor() {
    assertTrue(greenStar.validNeighbors(greenStar, empty));
    assertTrue(greenStar.validNeighbors(greenSquare, empty));
    assertTrue(greenStar.validNeighbors(redStar, blueStar));
    assertTrue(blueSquare.validNeighbors(blueStar, empty));
    assertTrue(blueStar.validNeighbors(redStar, blueStar));
    assertFalse(greenStar.validNeighbors(blueSquare, empty));
    assertFalse(blueSquare.validNeighbors(empty, redStar));
  }

  @Test
  public void isEmpty() {
    assertTrue(empty.isEmpty());
    assertFalse(blueSquare.isEmpty());
  }
}