package Player.Strategy;

import java.util.Optional;
import java.util.Set;

import Map.Coord;
import Map.IMap;
import Map.Tile.ITile;

/**
 * Chooses the smallest (according to lexographic ordering) tile that can extend
 * the current map. Breaks ties using row-column ordering (smaller y values
 * first, then smaller x values). If no tiles can be placed, exchanges if
 * possible. If exchanging is not possible, passes.
 */
public class DagStrategy extends AbstractStrategy {
	@Override
	protected Optional<Coord> findPlace(IMap map, ITile tile) {
		Set<Coord> possibleCoords = map.validSpots(tile);
		return possibleCoords.stream().sorted().findFirst();
	}
}
