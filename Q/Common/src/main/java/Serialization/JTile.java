package Serialization;

import java.util.HashMap;
import java.util.Map;

import Map.Tile.ITile;
import Map.Tile.Tile;

public class JTile {
  private final String color;
  private final String shape;

  public JTile(String color, String shape) {
    this.color = color;
    this.shape = shape;
  }

  public JTile(ITile tile) {
    this.color = tile.getColor().toString().toLowerCase();
    if (tile.getShape() == ITile.Shape.EightStar) {
      this.shape = "8star";
    } else {
      this.shape = tile.getShape().toString().toLowerCase();
    }
  }

  public ITile convert() {
    Map<String, ITile.TileColor> colorMap = new HashMap<>() {{
      put("red", ITile.TileColor.Red);
      put("green", ITile.TileColor.Green);
      put("blue", ITile.TileColor.Blue);
      put("yellow", ITile.TileColor.Yellow);
      put("orange", ITile.TileColor.Orange);
      put("purple", ITile.TileColor.Purple);
    }};

    Map<String, ITile.Shape> shapeMap = new HashMap<>() {{
      put("star", ITile.Shape.Star);
      put("8star", ITile.Shape.EightStar);
      put("square", ITile.Shape.Square);
      put("circle", ITile.Shape.Circle);
      put("clover", ITile.Shape.Clover);
      put("diamond", ITile.Shape.Diamond);
    }};
    return new Tile(shapeMap.get(shape), colorMap.get(color));
  }
}
