package Serialization;

import Map.Coord;
import Map.Tile.ITile;
import Serialization.JTile;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.util.AbstractMap;
import java.util.Map;

public class JCell {
  private final int columnIndex;
  private final JTile jTile;

  public JCell(int columnIndex, JTile jTile) {
    this.columnIndex = columnIndex;
    this.jTile = jTile;
  }

  public Map.Entry<Coord, ITile> convert(int rowIndex) {
    return new AbstractMap.SimpleEntry<>(
        new Coord(columnIndex, rowIndex),
        jTile.convert()
    );
  }

  public JsonElement serialize() {
    JsonArray jsonArray = new JsonArray();
    jsonArray.add(columnIndex);
    jsonArray.add(new Gson().toJsonTree(jTile));
    return jsonArray;
  }
}
