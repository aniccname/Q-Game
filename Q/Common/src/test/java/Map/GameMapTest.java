package Map;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.swing.JDialog;
import javax.swing.JFrame;

import Map.Tile.ITile;
import Map.Tile.Tile;
import Map.Tile.ITile.Shape;
import Map.Tile.ITile.TileColor;

import Map.Tile.EmptyTile;

public class GameMapTest {
  private ITile tile1;
  private GameMap gm1;
  private Coord c0_0;
  private Coord c0_1;
  private GameMap gm2;
  private GameMap gm3;
  private GameMap gm4;

  @Before
  public void init() {
    tile1 = new Tile(Shape.Square, TileColor.Blue);
    gm1 = new GameMap(tile1);
    c0_0 = new Coord(0, 0);
    c0_1 = new Coord(0, 1);

    // example we got from course website
    gm2 = createBoard(new GameMap(new Tile(Shape.Circle, TileColor.Green)),
            new Tile[] {
                    new Tile(Shape.Circle, TileColor.Red),
                    new Tile(Shape.Diamond, TileColor.Red),
                    new Tile(Shape.EightStar, TileColor.Red),
                    new Tile(Shape.Star, TileColor.Red),
                    new Tile(Shape.Clover, TileColor.Red),
                    new Tile(Shape.Clover, TileColor.Green)
            },
            "left", "up", "up", "up", "up", "right");

    gm3 = createBoard(new GameMap(new Tile(Shape.Circle, TileColor.Green)),
            new Tile[] {
                    new Tile(Shape.Circle, TileColor.Red),
                    new Tile(Shape.Diamond, TileColor.Red),
                    new Tile(Shape.EightStar, TileColor.Red),
                    new Tile(Shape.Star, TileColor.Red),
                    new Tile(Shape.Clover, TileColor.Red),
            },
            "left", "up", "up", "up", "up");

    createBoard(gm3, new Coord(0,0),
      new Tile[] {
        new Tile(Shape.Circle, TileColor.Blue),
        new Tile(Shape.Diamond, TileColor.Blue),
        new Tile(Shape.EightStar, TileColor.Blue),
        new Tile(Shape.Star, TileColor.Blue)
      }, "right", "up", "up", "up"
    );


    gm4 = createBoard(new GameMap(new Tile(Shape.Circle, TileColor.Green)),
            new Tile[] {
                    new Tile(Shape.Circle, TileColor.Red),
                    new Tile(Shape.Diamond, TileColor.Red),
                    new Tile(Shape.EightStar, TileColor.Red),
                    new Tile(Shape.Star, TileColor.Red),
                    new Tile(Shape.Clover, TileColor.Red),
            },
            "up", "right", "right", "right", "right");

    createBoard(gm4, new Coord(0,0),
            new Tile[] {
                    new Tile(Shape.Circle, TileColor.Blue),
                    new Tile(Shape.Diamond, TileColor.Blue),
                    new Tile(Shape.EightStar, TileColor.Blue),
                    new Tile(Shape.Star, TileColor.Blue)
            }, "down", "right", "right", "right"
    );
  }

  /**
   * Creates the board of tiles based on the place tile method implemented in the map.
   * NOTE: the first tile in the list is placed after the first direction is performed.
   * NOTE: when not specified the starting coord is (0, 0)
   *
   * @param gm the game map to start with (with a starting tile at 0,0)
   * @param tiles the tiles to places onto the gm
   * @param directions the directions which to place tiles adjacent next to each other (4 cardinal)
   * @return the updated game map with the adjustments made
   */
  private GameMap createBoard(GameMap gm, Tile[] tiles, String... directions) {
    return createBoard(gm, new Coord(0, 0), tiles, directions);
  }

  /**
   * Creates the board of tiles based on the place tile method implemented in the map.
   * NOTE: the first tile in the list is placed after the first direction is performed.
   *
   * @param gm the game map to start with (with a starting tile at 0,0)
   * @param coord the starting coordinate to add to
   * @param tiles the tiles to places onto the gm
   * @param directions the directions which to place tiles adjacent next to each other (4 cardinal)
   * @return the updated game map with the adjustments made
   */
  private GameMap createBoard(GameMap gm, Coord coord, Tile[] tiles, String... directions) {
    int x = coord.getX();
    int y = coord.getY();
    for (int i = 0; i < tiles.length; i += 1) {
      Tile tile = tiles[i];
      String dir = directions[i].trim().toLowerCase();

      switch (dir) {
        case "up":
          y += 1;
          break;
        case "down":
          y -= 1;
          break;
        case "left":
          x -= 1;
          break;
        case "right":
          x += 1;
          break;
        default:
          throw new IllegalArgumentException("unsupported direction");
      }
      gm.placeTileByOther(new Coord(x, y), tile);
    }
    return gm;
  }

  @Test
  public void constructor() {
    GameMap gm = new GameMap(tile1);
    assertEquals(gm.getTile(new Coord(0, 0)).getColor(), tile1.getColor());
    assertEquals(gm.getTile(new Coord(0, 0)).getShape(), tile1.getShape());

    assertEquals(gm2.getTile(new Coord(0, 0)).getColor(), TileColor.Green);
    assertEquals(gm2.getTile(new Coord(0, 0)).getShape(), Shape.Circle);
  }

  @Test(expected = IllegalArgumentException.class)
  public void constructorEmptyTile() {
    GameMap gm = new GameMap(new EmptyTile());
  }

  @Test(expected = IllegalArgumentException.class)
  public void constructorNullTile() {
    GameMap gm = new GameMap((ITile) null);
  }

  @Test
  public void placeTileByOther() {
    gm1.placeTileByOther(c0_1, tile1);
    assertEquals(gm1.getTile(c0_1), tile1);
  }

  @Test(expected = RuntimeException.class)
  public void placeTileOnOrigin() {
    gm1.placeTileByOther(c0_0, tile1);
  }

  @Test(expected = RuntimeException.class)
  public void placeTileOnOther() {
    gm2.placeTileByOther(new Coord(0, 4), new Tile(Shape.Clover, TileColor.Red));
  }

  @Test(expected = RuntimeException.class)
  public void placeTileByItself() {
    gm2.placeTileByOther(new Coord(3, 3), new Tile(Shape.Clover, TileColor.Yellow));
  }

  @Test
  public void validSpots() {
    assertTrue(gm2.validSpots(new Tile(Shape.Star, TileColor.Green))
            .contains(new Coord(0, 3)));
    assertFalse(gm2.validSpots(new Tile(Shape.Star, TileColor.Purple))
            .contains(new Coord(0, 3)));
  }

  @Test
  public void validSpots3Horiz() {
    gm3.placeTileByOther(new Coord(1, 4), new Tile(Shape.Clover, TileColor.Blue));
    // valid placement between two neighbors
    assertTrue(gm3.validSpots(new Tile(Shape.Clover, TileColor.Green)).contains(new Coord(0, 4)));

    // invalid for both neighbors
    assertFalse(gm3.validSpots(new Tile(Shape.Star, TileColor.Green)).contains(new Coord(0, 4)));
  }

  // trying to place tile in middle of two tiles that individually are valid neighbors, but not both
  @Test
  public void validSpotsDiffHoriz() {
    gm3.placeTileByOther(new Coord(1, 4), new Tile(Shape.Star, TileColor.Green));
    assertFalse(gm3.validSpots(new Tile(Shape.Clover, TileColor.Green)).contains(new Coord(0, 4)));
  }
  @Test
  public void validSpots3Vert() {
    gm4.placeTileByOther(new Coord(4, -1), new Tile(Shape.Clover, TileColor.Blue));
    // valid placement between two neighbors
    assertTrue(gm4.validSpots(new Tile(Shape.Clover, TileColor.Green)).contains(new Coord(4, 0)));

    // invalid for both neighbors
    assertFalse(gm4.validSpots(new Tile(Shape.Star, TileColor.Green)).contains(new Coord(4, 0)));
  }

  // trying to place tile in middle of two tiles that individually are valid neighbors, but not both
  @Test
  public void validSpotsDiffVert() {
    gm4.placeTileByOther(new Coord(4, -1), new Tile(Shape.Star, TileColor.Green));
    assertFalse(gm4.validSpots(new Tile(Shape.Clover, TileColor.Green)).contains(new Coord(4, 0)));
  }

  @Test
  public void validSpots4neighbors() {
    gm3.placeTileByOther(new Coord(1, 4), new Tile(Shape.Clover, TileColor.Blue));
    gm3.placeTileByOther(new Coord(0, 4), new Tile(Shape.Clover, TileColor.Green));
    gm3.placeTileByOther(new Coord(0, 2), new Tile(Shape.EightStar, TileColor.Green));
    assertTrue(gm3.validSpots(new Tile(Shape.Star, TileColor.Green)).contains(new Coord(0, 3)));

  }

  @Test
  public void validSpots3neighbors() {
    gm3.placeTileByOther(new Coord(1, 4), new Tile(Shape.Clover, TileColor.Blue));
    gm3.placeTileByOther(new Coord(0, 4), new Tile(Shape.Clover, TileColor.Green));
    assertTrue(gm3.validSpots(new Tile(Shape.Star, TileColor.Green)).contains(new Coord(0, 3)));
  }

  @Test
  public void invalidSpot3neighbors() {
    createBoard(gm2, new Coord(0, 4),new Tile[]{
        new Tile(Shape.Clover, TileColor.Purple),
        new Tile(Shape.Star, TileColor.Purple)
      }, "right", "down");
    assertFalse(gm2.validSpots(new Tile(Shape.Star, TileColor.Blue)).contains(new Coord(0, 3)));
  }


  @Test
  public void invalidSpot4neighbors() {
    gm3.placeTileByOther(new Coord(1, 4), new Tile(Shape.Clover, TileColor.Blue));
    gm3.placeTileByOther(new Coord(0, 4), new Tile(Shape.Clover, TileColor.Blue));
    gm3.placeTileByOther(new Coord(0, 2), new Tile(Shape.EightStar, TileColor.Green));
    assertFalse(gm3.validSpots(new Tile(Shape.Star, TileColor.Green)).contains(new Coord(0, 3)));

  }

  @Test
  public void neighborsMatchLineDoesnt() {
    GameMap map = new GameMap(new Tile(Shape.Clover, TileColor.Green));
    map.placeTileByOther(new Coord(0, 1), new Tile(Shape.Clover, TileColor.Purple));
    assertFalse(map.validSpots(new Tile(Shape.Star, TileColor.Purple)).contains(new Coord(0, 2)));
  }
}
