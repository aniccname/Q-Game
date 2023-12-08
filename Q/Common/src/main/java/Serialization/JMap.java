package Serialization;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Map.Coord;
import Map.IMap;
import Map.Tile.ITile;
import Map.GameMap;
import Serialization.JRow;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

public class JMap {
  final List<JRow> jRowList;

  public JMap(JRow... jRows) {
    this.jRowList = Arrays.asList(jRows);
  }

  public JMap(IMap map) {
    Map<Coord, ITile> board = map.getMap();

    int minX = board.keySet().stream().mapToInt(Coord::getX).min().orElse(0);
    int minY = board.keySet().stream().mapToInt(Coord::getY).min().orElse(0);
    int maxX = board.keySet().stream().mapToInt(Coord::getX).max().orElse(0);
    int maxY = board.keySet().stream().mapToInt(Coord::getY).max().orElse(0);

    List<JRow> jRowList = new ArrayList<>();
    for (int y = minY; y <= maxY; y++) {
      List<JCell> jCellList = new ArrayList<>();
      for (int x = minX; x <= maxX; x++) {
        ITile tile = map.getTile(new Coord(x, y));
        if (!tile.isEmpty()) {
          JCell jCell = new JCell(x, new JTile(tile));
          jCellList.add(jCell);
        }
      }
      JRow jRow = new JRow(y, jCellList.toArray(new JCell[0]));
      jRowList.add(jRow);
    }

    this.jRowList = jRowList;
  }

  public IMap convert() {
    List<Map.Entry<Coord, ITile>> mapComponents = new ArrayList<>();
    for (JRow jRow : jRowList) {
      mapComponents.addAll(jRow.convert());
    }

    Map<Coord, ITile> intermediateMap = new HashMap<>();
    for (Map.Entry<Coord, ITile> entry : mapComponents) {
      intermediateMap.put(entry.getKey(), entry.getValue());
    }

    return new GameMap(intermediateMap);
  }

  public JsonElement serialize() {
    JsonArray jRowArray = new JsonArray();
    for (JRow jRow : jRowList) {
      jRowArray.add(jRow.serialize());
    }
    return jRowArray;
  }
}
