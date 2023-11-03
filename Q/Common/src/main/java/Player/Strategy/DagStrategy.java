package Player.Strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import Map.Coord;
import Map.IMap;
import Map.Tile.ITile;
import java.util.Map;

/**
 * Chooses the smallest (according to lexographic ordering) tile that can extend
 * the current map. Breaks ties using row-column ordering (smaller y values
 * first, then smaller x values). If no tiles can be placed, exchanges if
 * possible. If exchanging is not possible, passes.
 */
public class DagStrategy extends AbstractStrategy {
	@Override
	protected Optional<Coord> findPlace(
			IMap map, ITile tile, List<Coord> placements
	) {
		Set<Coord> possibleCoords = map.validSpots(tile);
		return possibleCoords.stream().filter(
				coord -> {
					List<Coord> coords = new ArrayList<>(placements);
					coords.add(coord);
					return sameRowOrColumn(coords);
				}
				)
				.sorted().findFirst();
	}
}
