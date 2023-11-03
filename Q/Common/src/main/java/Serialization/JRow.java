package Serialization;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import Map.Tile.ITile;
import Map.Coord;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

public class JRow {
  private final int rowIndex;
  private final List<JCell> jCellList;

  public JRow(int rowIndex, JCell... jCells) {
    this.rowIndex = rowIndex;
    this.jCellList = Arrays.asList(jCells);
  }

  public List<Map.Entry<Coord, ITile>> convert() {
    List<Map.Entry<Coord, ITile>> row = new ArrayList<>();
    for (JCell jCell : jCellList) {
      row.add(jCell.convert(rowIndex));
    }
    return row;
  }

  public JsonElement serialize() {
    JsonArray jsonArray = new JsonArray();
    jsonArray.add(rowIndex);
    for (JCell jCell : jCellList) {
      jsonArray.add(jCell.serialize());
    }
    return jsonArray;
  }
}
