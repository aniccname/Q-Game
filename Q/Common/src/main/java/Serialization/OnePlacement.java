package Serialization;

import java.util.AbstractMap;
import java.util.Map;

import Map.Coord;
import Map.Tile.ITile;
import com.google.gson.annotations.SerializedName;

public class OnePlacement {
	private final JCoordinate coordinate;
	@SerializedName("1tile")
	private final JTile tile;

	public OnePlacement(JCoordinate coordinate, JTile tile) {
		this.coordinate = coordinate;
		this.tile = tile;
	}

	public Map.Entry<Coord, ITile> convert() {
		return new AbstractMap.SimpleEntry<>(coordinate.convert(), tile.convert());
	}
}
