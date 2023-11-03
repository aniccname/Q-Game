package Map;

import org.junit.Before;
import org.junit.Test;

import Map.Coord;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CoordTest{

  private Coord origin;
  private Coord c10;
  private Coord c11;
  private Coord c01;
  private Coord cNeg10;
  private Coord cNeg1Neg1;
  private Coord c0Neg1;

  @Before
  public void init() {
    origin = new Coord(0, 0);
    c10 = new Coord(1, 0);
    c01 = new Coord(0, 1);
    c11 = new Coord(1, 1);
    cNeg1Neg1 = new Coord(-1, -1);
    c0Neg1 = new Coord(0, -1);
    cNeg10 = new Coord(-1, 0);
  }

  @Test
  public void testGetX() {
    assertEquals(origin.getX(), 0);
    assertEquals(c11.getX(), 1);
    assertEquals(c10.getX(), 1);
    assertEquals(c01.getX(), 0);
    assertEquals(cNeg1Neg1.getX(), -1);
    assertEquals(c0Neg1.getX(), 0);
    assertEquals(cNeg10.getX(), -1);
  }
  @Test
  public void testGetY() {
    assertEquals(origin.getY(), 0);
    assertEquals(c11.getY(), 1);
    assertEquals(c10.getY(), 0);
    assertEquals(c01.getY(), 1);
    assertEquals(cNeg1Neg1.getY(), -1);
    assertEquals(c0Neg1.getY(), -1);
    assertEquals(cNeg10.getY(), 0);
  }

  private void testEqualNeighbors(Coord[] neighbors,
                                  Coord... testList) {
    for (int i = 0; i < neighbors.length; i += 1) {
      Coord neighbor = neighbors[i];
      Coord test = testList[i];
      assertEquals(neighbor.getX(), test.getX());
      assertEquals(neighbor.getY(), test.getY());
    }
  }

  @Test
  public void testGetCardinalNeighbors() {
    testEqualNeighbors(origin.getCardinalNeighbors(),
            cNeg10, c10, c0Neg1, c01);

    testEqualNeighbors(c0Neg1.getCardinalNeighbors(),
            new Coord(-1, -1),
            new Coord(1, -1),
            new Coord(0, -2),
            new Coord(0 ,0));

  }

  @Test
  public void testTestEquals() {
    assertTrue(origin.equals(new Coord(0, 0)));
    assertFalse(origin.equals(new Coord(-1000123123, 10000)));
    assertFalse(origin.equals(c10));
    assertTrue(c0Neg1.equals(new Coord(0, -1)));
    assertTrue(origin.equals(origin));
  }

  @Test
  public void testTestHashCode() {
    assertEquals(origin.hashCode(), new Coord(0,0).hashCode());
  }
}